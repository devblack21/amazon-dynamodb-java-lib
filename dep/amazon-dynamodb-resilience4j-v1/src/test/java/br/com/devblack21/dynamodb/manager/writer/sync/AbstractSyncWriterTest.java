package br.com.devblack21.dynamodb.manager.writer.sync;


import br.com.devblack21.dynamodb.manager.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager.interceptor.RequestInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class AbstractSyncWriterTest {

  private BackoffExecutor mockBackoffExecutor;
  private ErrorRecoverer<Object> mockErrorRecoverer;
  private RequestInterceptor<Object> mockRequestInterceptor;
  private AbstractSyncWriter<Object> testWriter;
  private AbstractSyncWriter<Object> testWriterWithoutBackoffAndRecoverer;
  private AbstractSyncWriter<Object> testFailureWriter;

  @BeforeEach
  void setUp() {
    mockBackoffExecutor = mock(BackoffExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = new TestSyncWriterSuccess(
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor
    );

    testFailureWriter = new TestSyncWriterFailure(
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor);

    testWriterWithoutBackoffAndRecoverer = new TestSyncWriterFailure(
      null,
      null,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    testWriter.execute(entity);

    verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
    verify(mockErrorRecoverer, never()).recover(any());
    verify(mockRequestInterceptor, never()).logSuccess(any());
    verify(mockRequestInterceptor, never()).logError(any(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, never()).recover(any());
    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
    verify(mockRequestInterceptor, never()).logSuccess(any());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(any());
    verify(mockRequestInterceptor, times(1)).logError(any(Object.class), any(RuntimeException.class));
    verify(mockRequestInterceptor, never()).logSuccess(any());
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any());

    Assertions.assertThrows(RuntimeException.class, () -> testFailureWriter.execute(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(any());
    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
    verify(mockRequestInterceptor, never()).logSuccess(any());
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.execute(entity));

    verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
    verify(mockErrorRecoverer, never()).recover(any());
    verify(mockRequestInterceptor, times(1)).logError(eq(entity), any());
    verify(mockRequestInterceptor, never()).logSuccess(any());
  }


  private static class TestSyncWriterSuccess extends AbstractSyncWriter<Object> {

    public TestSyncWriterSuccess(final BackoffExecutor backoffExecutor,
                                 final ErrorRecoverer<Object> errorRecoverer,
                                 final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, requestInterceptor);
    }

    @Override
    public void executor(final Object entity) {
    }
  }

  private static class TestSyncWriterFailure extends AbstractSyncWriter<Object> {

    public TestSyncWriterFailure(final BackoffExecutor backoffExecutor,
                                 final ErrorRecoverer<Object> errorRecoverer,
                                 final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, requestInterceptor);
    }

    @Override
    public void executor(final Object entity) {
      throw new RuntimeException();
    }
  }
}


