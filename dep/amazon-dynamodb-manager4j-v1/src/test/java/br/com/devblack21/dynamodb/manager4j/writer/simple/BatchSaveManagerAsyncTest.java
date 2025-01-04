package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.BatchSaveClientAsyncFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.templates.AbstractBatchSaveManagerTemplate;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static br.com.devblack21.dynamodb.manager4j.model.MyItem.getMyItem;
import static org.mockito.Mockito.*;

class BatchSaveManagerAsyncTest extends AbstractBatchSaveManagerTemplate {

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
    final MyItem entity = getMyItem();

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
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    captureFunctionForRetry(mockBackoffExecutor);

    testWriter.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verifyNoInteractions(mockErrorRecoverer);
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
    });

  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<MyItem> itemsToSave = Arrays.asList(getMyItem(), getMyItem());

    simulateFailedBatch(dynamoDBMapper, transformer);

    testWriter.batchSave(itemsToSave);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
      verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    });

  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);

    testWriter.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    });
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateFailedBatch(dynamoDBMapper, transformer);
    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);
    simulateRecoveryFailure(mockErrorRecoverer);

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
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);

    testWriterWithoutBackoffAndRecoverer.batchSave(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    });
  }

}
