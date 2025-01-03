//package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;
//
//import br.com.devblack21.dynamodb.manager4j.factory.BatchSaveClientFactory;
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import com.amazonaws.services.dynamodbv2.model.AttributeValue;
//import com.amazonaws.services.dynamodbv2.model.PutRequest;
//import com.amazonaws.services.dynamodbv2.model.WriteRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.util.*;
//import java.util.concurrent.ExecutionException;
//import java.util.function.Function;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//class BatchSaveManagerSyncTest {
//
//  private DynamoDBMapper dynamoDBMapper;
//  private FailedBatchPutRequestTransformer<Object> transformer;
//  private BackoffBatchWriteExecutor<Object> mockBackoffExecutor;
//  private ErrorRecoverer<Object> mockErrorRecoverer;
//  private RequestInterceptor<Object> mockRequestInterceptor;
//  private BatchSaveManager<Object> testWriter;
//  private BatchSaveManager<Object> testWriterWithoutBackoffAndRecoverer;
//
//  @BeforeEach
//  void setUp() {
//    dynamoDBMapper = mock(DynamoDBMapper.class);
//    transformer = mock(FailedBatchPutRequestTransformer.class);
//    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
//    mockErrorRecoverer = mock(ErrorRecoverer.class);
//    mockRequestInterceptor = mock(RequestInterceptor.class);
//
//    testWriter = BatchSaveClientFactory.createSyncClient(
//      dynamoDBMapper,
//      transformer,
//      mockBackoffExecutor,
//      mockErrorRecoverer,
//      mockRequestInterceptor
//    );
//
//    testWriterWithoutBackoffAndRecoverer = BatchSaveClientFactory.createSyncClient(
//      dynamoDBMapper,
//      transformer,
//      null,
//      null,
//      mockRequestInterceptor
//    );
//  }
//
//  @Test
//  void shouldExecuteSuccessfullyWithoutErrors() {
//    final Object entity = new Object();
//
//    testWriter.batchSave(List.of(entity));
//
//    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
//    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
//    verify(mockRequestInterceptor, never()).logError(any(), any());
//  }
//
//  @Test
//  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
//    final Object entity = new Object();
//
//    simulateDynamoDbFailure();
//    captureRunnableForRetry();
//
//    testWriter.batchSave(List.of(entity));
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
//    verifyNoInteractions(mockErrorRecoverer);
//    verify(mockRequestInterceptor, never()).logError(anyList(), any());
//  }
//
//  @Test
//  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
//    final List<Object> itemsToSave = Arrays.asList("Item1", "Item2");
//
//    simulateFailedBatch();
//
//    testWriter.batchSave(itemsToSave);
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
//    verify(mockRequestInterceptor, never()).logError(anyList(), any());
//    verify(mockRequestInterceptor, times(1)).logSuccess(any());
//  }
//
//  @Test
//  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
//    final Object entity = new Object();
//
//    simulateDynamoDbFailure();
//    simulateBackoffFailure();
//
//    testWriter.batchSave(List.of(entity));
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
//    verify(mockErrorRecoverer, times(1)).recover(anyList());
//    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));
//  }
//
//  @Test
//  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
//    final Object entity = new Object();
//
//    simulateFailedBatch();
//    simulateDynamoDbFailure();
//    simulateBackoffFailure();
//    simulateRecoveryFailure();
//
//    testWriter.batchSave(List.of(entity));
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
//    verify(mockErrorRecoverer, times(1)).recover(anyList());
//    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
//    verify(mockRequestInterceptor, never()).logSuccess(any());
//  }
//
//  @Test
//  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
//    final Object entity = new Object();
//
//    simulateDynamoDbFailure();
//
//    assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.batchSave(List.of(entity)));
//
//    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
//    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
//
//  }
//
//
//  private void simulateDynamoDbFailure() {
//    doThrow(RuntimeException.class).when(dynamoDBMapper).batchSave(anyList());
//  }
//  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
//    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> captor = ArgumentCaptor.forClass(Function.class);
//    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(captor.capture(), anyList());
//  }
//
//  private void simulateRecoveryFailure() {
//    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(Object.class));
//  }
//
//  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
//   final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> captor = ArgumentCaptor.forClass(Function.class);
//    doNothing().when(mockBackoffExecutor).execute(captor.capture(), anyList());
//  }
//
//  private void simulateFailedBatch() {
//
//    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
//    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
//    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
//    final WriteRequest writeRequest = new WriteRequest();
//    writeRequest.setPutRequest(new PutRequest().withItem(Map.of("a", new AttributeValue("b"))));
//    unprocessedItems.put("TableName", List.of(writeRequest));
//
//    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
//    failedBatches.add(failedBatch);
//
//    when(transformer.transform(anyList())).thenReturn(List.of("item"));
//
//    when(dynamoDBMapper.batchSave(anyList()))
//      .thenReturn(failedBatches)
//      .thenReturn(List.of());
//  }
//
//}
