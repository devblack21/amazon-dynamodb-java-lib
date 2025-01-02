package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.factory.DeleteClientFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.DeleteManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class DeleteManagerSyncTest {

  private DynamoDbTable<Object> dynamoDBMTable;
  private BackoffExecutor mockBackoffExecutor;
  private ErrorRecoverer<Object> mockErrorRecoverer;
  private RequestInterceptor<Object> mockRequestInterceptor;
  private DeleteManager<Object> testWriter;
  private DeleteManager<Object> testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMTable = mock(DynamoDbTable.class);
    mockBackoffExecutor = mock(BackoffExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = DeleteClientFactory.createSyncClient(
      dynamoDBMTable,
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = DeleteClientFactory.createSyncClient(
      dynamoDBMTable,
      null,
      null,
      mockRequestInterceptor
    );
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final Object entity = new Object();

    testWriter.delete(entity);

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(entity);
    verify(mockRequestInterceptor, never()).logError(any(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    captureRunnableForRetry();

    testWriter.delete(entity);

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.delete(entity);


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(entity);
    verify(mockRequestInterceptor, times(1)).logError(any(Object.class), any(RuntimeException.class));

  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    Assertions.assertThrows(RuntimeException.class, () -> testWriter.delete(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(entity);
    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
    verify(mockRequestInterceptor, never()).logSuccess(any());

  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final Object entity = new Object();

    simulateDynamoDbFailure();

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.delete(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(eq(entity), any());

  }

  private void simulateDynamoDbFailure() {
    doThrow(RuntimeException.class).when(dynamoDBMTable).putItem(any(Object.class));
  }

  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
  }

  private void simulateRecoveryFailure() {
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any());
  }

  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());
  }

}
