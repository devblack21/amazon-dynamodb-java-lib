package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.BatchSaveClientSyncFactory;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BatchSaveManagerSyncTest {

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchPutRequestTransformer<MyItem> transformer;
  private BackoffBatchWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private BatchSaveManager testWriter;
  private BatchSaveManager testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = mock(FailedBatchPutRequestTransformer.class);
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchSaveClientSyncFactory.createClient(
      dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder()
        .backoffBatchWriteExecutor(mockBackoffExecutor)
        .errorRecoverer(mockErrorRecoverer)
        .build(),
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = BatchSaveClientSyncFactory.createClient(
      dynamoDBMapper,
      transformer,
      mockRequestInterceptor
    );
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final MyItem entity = new MyItem("87", "nome");

    testWriter.batchSave(List.of(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("87", "nome");

    simulateDynamoDbFailure();
    captureRunnableForRetry();

    testWriter.batchSave(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<MyItem> itemsToSave = Arrays.asList(new MyItem("1", "Item1"), new MyItem("2", "Item2"));

    simulateFailedBatch();

    testWriter.batchSave(itemsToSave);

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("87", "nome");

    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.batchSave(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("87", "nome");

    simulateFailedBatch();
    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    Assertions.assertThrows(RuntimeException.class, () -> testWriter.batchSave(List.of(entity)));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final MyItem entity = new MyItem("87", "nome");

    simulateDynamoDbFailure();

    assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.batchSave(List.of(entity)));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());

  }


  private void simulateDynamoDbFailure() {
    doThrow(RuntimeException.class).when(dynamoDBMapper).batchSave(anyList());
  }

  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateRecoveryFailure() {
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(anyList());
  }

  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doNothing().when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateFailedBatch() {

    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
    final WriteRequest writeRequest = new WriteRequest();
    writeRequest.setPutRequest(new PutRequest().withItem(Map.of("a", new AttributeValue("b"))));
    unprocessedItems.put("TableName", List.of(writeRequest));

    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
    failedBatches.add(failedBatch);

    when(transformer.transform(anyList())).thenReturn(List.of(new MyItem("1", "1")));

    when(dynamoDBMapper.batchSave(anyList()))
      .thenReturn(failedBatches)
      .thenReturn(List.of());
  }

}
