//package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;
//
//import br.com.devblack21.dynamodb.manager4j.factory.SaveClientFactory;
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.util.concurrent.ExecutionException;
//
//import static org.mockito.Mockito.*;
//
//class SaveManagerSyncTest {
//
//  private DynamoDBMapper dynamoDBMapper;
//  private BackoffSingleWriteExecutor mockBackoffExecutor;
//  private ErrorRecoverer<Object> mockErrorRecoverer;
//  private RequestInterceptor<Object> mockRequestInterceptor;
//  private SaveManager<Object> testWriter;
//  private SaveManager<Object> testWriterWithoutBackoffAndRecoverer;
//
//  @BeforeEach
//  void setUp() {
//    dynamoDBMapper = mock(DynamoDBMapper.class);
//    mockBackoffExecutor = mock(BackoffSingleWriteExecutor.class);
//    mockErrorRecoverer = mock(ErrorRecoverer.class);
//    mockRequestInterceptor = mock(RequestInterceptor.class);
//
//    testWriter = SaveClientFactory.createSyncClient(
//      dynamoDBMapper,
//      mockBackoffExecutor,
//      mockErrorRecoverer,
//      mockRequestInterceptor
//    );
//
//    testWriterWithoutBackoffAndRecoverer = SaveClientFactory.createSyncClient(
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
//    testWriter.save(entity);
//
//    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
//    verify(mockRequestInterceptor, times(1)).logSuccess(entity);
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
//    testWriter.save(entity);
//
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
//    verifyNoInteractions(mockErrorRecoverer);
//    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
//  }
//
//  @Test
//  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
//    final Object entity = new Object();
//
//    simulateDynamoDbFailure();
//    simulateBackoffFailure();
//
//    testWriter.save(entity);
//
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
//    verify(mockErrorRecoverer, times(1)).recover(entity);
//    verify(mockRequestInterceptor, times(1)).logError(any(Object.class), any(RuntimeException.class));
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
//    Assertions.assertThrows(RuntimeException.class, () -> testWriter.save(entity));
//
//
//    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
//    verify(mockErrorRecoverer, times(1)).recover(entity);
//    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
//    verify(mockRequestInterceptor, never()).logSuccess(any());
//  }
//
//  @Test
//  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
//    final Object entity = new Object();
//
//    simulateDynamoDbFailure();
//
//    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.save(entity));
//
//    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
//    verify(mockRequestInterceptor, times(1)).logError(eq(entity), any());
//  }
//
//  private void simulateDynamoDbFailure() {
//    doThrow(RuntimeException.class).when(dynamoDBMapper).save(any(Object.class));
//  }
//
//  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
//    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
//    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
//  }
//
//  private void simulateRecoveryFailure() {
//    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(Object.class));
//  }
//
//  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
//    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
//    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());
//  }
//
//}
