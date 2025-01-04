package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.BatchSaveClientAsyncFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.mockito.Mockito.*;

class BatchSaveManagerAsyncTest {

  private final Integer TIMEOUT = 3;

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchPutRequestTransformer<MyItem> transformer;
  private BackoffBatchWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private BatchSaveManager testWriter;
  private BatchSaveManager testWriterWithoutBackoffAndRecoverer;
  private static ExecutorService executorService;

  @BeforeAll
  static void setUpExecutorService() {
    executorService = Executors.newCachedThreadPool();
  }

  @AfterAll
  static void tearDownExecutorService() {
    executorService.shutdown();
  }

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = mock(FailedBatchPutRequestTransformer.class);
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchSaveClientAsyncFactory.createClient(dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder()
        .backoffBatchWriteExecutor(mockBackoffExecutor)
        .errorRecoverer(mockErrorRecoverer)
        .build(),
      executorService,
      mockRequestInterceptor);


    testWriterWithoutBackoffAndRecoverer = BatchSaveClientAsyncFactory.createClient(dynamoDBMapper,
      transformer,
      executorService,
      mockRequestInterceptor);
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final MyItem entity = new MyItem("12", "");

    when(dynamoDBMapper.batchSave(anyList())).thenReturn(List.of());

    when(transformer.transform(anyList())).thenReturn(List.of());

    testWriter.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
      verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
    });
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("12", "");

    simulateDynamoDbFailure();
    captureRunnableForRetry();

    testWriter.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verifyNoInteractions(mockErrorRecoverer);
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
    });

  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<MyItem> itemsToSave = Arrays.asList(new MyItem("12", ""), new MyItem("12", ""));

    simulateFailedBatch();

    testWriter.batchSave(itemsToSave);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
      verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    });

  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("12", "");

    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    });
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("12", "");

    simulateFailedBatch();
    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    testWriter.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
      verify(mockRequestInterceptor, never()).logSuccess(anyList());
    });
  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final MyItem entity = new MyItem("12", "");

    simulateDynamoDbFailure();

    testWriterWithoutBackoffAndRecoverer.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    });
  }

  private void simulateDynamoDbFailure() {
    doThrow(RuntimeException.class).when(dynamoDBMapper).batchSave(anyList());
  }

  private void simulateFailedBatch() {

    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
    final WriteRequest writeRequest = new WriteRequest();
    writeRequest.setPutRequest(new PutRequest().withItem(Map.of("id", new AttributeValue("11"))));
    unprocessedItems.put("TableName", List.of(writeRequest));

    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
    failedBatches.add(failedBatch);

    when(transformer.transform(anyList())).thenReturn(List.of(new MyItem("11", "")));

    when(dynamoDBMapper.batchSave(anyList()))
      .thenReturn(failedBatches)
      .thenReturn(List.of());
  }

  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateRecoveryFailure() {
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(MyItem.class));
  }

  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doNothing().when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

}
