//package br.com.devblack21.dynamodb.manager4j.writer.sync;
//
//import br.com.devblack21.dynamodb.manager4j.factory.BatchSaveClientFactory;
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.writer.BatchSaveManager;
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
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//class BatchSaveManagerSyncTest {
//
//  private DynamoDBMapper dynamoDBMapper;
//  private BackoffSingleWriteExecutor mockBackoffExecutor;
//  private ErrorRecoverer<List<Object>> mockErrorRecoverer;
//  private RequestInterceptor<List<Object>> mockRequestInterceptor;
//  private BatchSaveManager<Object> testWriter;
//  private BatchSaveManager<Object> testWriterWithoutBackoffAndRecoverer;
//
//  @BeforeEach
//  void setUp() {
//    dynamoDBMapper = mock(DynamoDBMapper.class);
//    mockBackoffExecutor = mock(BackoffSingleWriteExecutor.class);
//    mockErrorRecoverer = mock(ErrorRecoverer.class);
//    mockRequestInterceptor = mock(RequestInterceptor.class);
//
//    testWriter = BatchSaveClientFactory.createSyncClient(
//      dynamoDBMapper,
//      mockBackoffExecutor,
//      mockErrorRecoverer,
//      mockRequestInterceptor
//    );
//
//    testWriterWithoutBackoffAndRecoverer = BatchSaveClientFactory.createSyncClient(
//      dynamoDBMapper,
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
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
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
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
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
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
//    verify(mockErrorRecoverer, times(1)).recover(anyList());
//    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));
//  }
//
//  @Test
//  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
//    final Object entity = new Object();
//
//    simulateDynamoDbFailure();
//    simulateBackoffFailure();
//    simulateRecoveryFailure();
//
//    assertThrows(RuntimeException.class, () -> testWriter.batchSave(List.of(entity)));
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
//    verify(mockErrorRecoverer, times(1)).recover(anyList());
//    verify(mockRequestInterceptor, never()).logError(anyList(), any());
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
//
//  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
//    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
//    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
//  }
//
//  private void simulateFailedBatch() {
//
//    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
//    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
//    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
//    final WriteRequest writeRequest = new WriteRequest();
//    writeRequest.setPutRequest(new PutRequest().withItem(Map.of("", new AttributeValue(""))));
//    unprocessedItems.put("TableName", Arrays.asList(writeRequest));
//
//    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
//    failedBatches.add(failedBatch);
//
//    when(dynamoDBMapper.batchSave(anyList()))
//      .thenReturn(failedBatches)
//      .thenReturn(List.of());
//  }
//
//  private void simulateRecoveryFailure() {
//    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any());
//  }
//
//  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
//    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
//    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());
//  }
//
//}
