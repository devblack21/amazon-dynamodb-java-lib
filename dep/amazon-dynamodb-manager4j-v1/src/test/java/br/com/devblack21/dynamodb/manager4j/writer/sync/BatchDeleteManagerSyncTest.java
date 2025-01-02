package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.factory.BatchDeleteClientFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class BatchDeleteManagerSyncTest {

  private DynamoDBMapper dynamoDBMapper;
  private BackoffExecutor mockBackoffExecutor;
  private ErrorRecoverer<List<Object>> mockErrorRecoverer;
  private RequestInterceptor<List<Object>> mockRequestInterceptor;
  private BatchDeleteManager<Object> testWriter;
  private BatchDeleteManager<Object> testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    mockBackoffExecutor = mock(BackoffExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchDeleteClientFactory.createSyncClient(
      dynamoDBMapper,
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = BatchDeleteClientFactory.createSyncClient(
      dynamoDBMapper,
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


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(anyList(), any());


  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.batchDelete(List.of(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));

  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    Assertions.assertThrows(RuntimeException.class, () -> testWriter.batchDelete(List.of(entity)));


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
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
