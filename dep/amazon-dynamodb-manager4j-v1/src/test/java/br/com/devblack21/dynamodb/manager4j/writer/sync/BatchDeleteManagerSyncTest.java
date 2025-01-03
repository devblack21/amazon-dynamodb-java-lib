package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.factory.BatchDeleteClientFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.mockito.Mockito.*;

class BatchDeleteManagerSyncTest {

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchDeleteRequestTransformer<Object> transformer;
  private BackoffBatchWriteExecutor<Object> mockBackoffExecutor;
  private ErrorRecoverer<Object> mockErrorRecoverer;
  private RequestInterceptor<Object> mockRequestInterceptor;
  private BatchDeleteManager<Object> testWriter;
  private BatchDeleteManager<Object> testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = mock(FailedBatchDeleteRequestTransformer.class);
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchDeleteClientFactory.createSyncClient(
      dynamoDBMapper,
      transformer,
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = BatchDeleteClientFactory.createSyncClient(
      dynamoDBMapper,
      transformer,
      null,
      null,
      mockRequestInterceptor
    );
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final Object entity = new Object();

    testWriter.batchDelete(List.of(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(any(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    captureRunnableForRetry();

    testWriter.batchDelete(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<Object> itemsToSave = Arrays.asList("Item1", "Item2");

    simulateFailedBatch();

    testWriter.batchDelete(itemsToSave);

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(any());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateFailedBatch();
    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.batchDelete(List.of(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));

  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    testWriter.batchDelete(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(any());

  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final Object entity = new Object();

    simulateDynamoDbFailure();


    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.batchDelete(List.of(entity)));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
  }

  private void simulateDynamoDbFailure() {
    doThrow(RuntimeException.class).when(dynamoDBMapper).batchDelete(anyList());
  }

  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
   final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> captor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateRecoveryFailure() {
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(Object.class));
  }

  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
   final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> captor = ArgumentCaptor.forClass(Function.class);
    doNothing().when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateFailedBatch() {

    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
    final WriteRequest writeRequest = new WriteRequest();
    writeRequest.setDeleteRequest(new DeleteRequest().withKey(Map.of("a", new AttributeValue("b"))));
    unprocessedItems.put("TableName", List.of(writeRequest));

    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
    failedBatches.add(failedBatch);

    when(transformer.transform(anyList())).thenReturn(List.of("item"));

    when(dynamoDBMapper.batchDelete(anyList()))
      .thenReturn(failedBatches)
      .thenReturn(List.of());
  }

}
