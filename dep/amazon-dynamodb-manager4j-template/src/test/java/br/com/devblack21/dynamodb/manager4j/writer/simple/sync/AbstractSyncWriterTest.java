package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;


import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class AbstractSyncWriterTest {

  private BackoffSingleWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private AbstractSyncWriter testWriter;
  private AbstractSyncWriter testWriterWithoutBackoffAndRecoverer;
  private AbstractSyncWriter testFailureWriter;

  @BeforeEach
  void setUp() {
    mockBackoffExecutor = mock(BackoffSingleWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration = SingleWriteRetryPolicyConfiguration.builder()
      .backoffSingleWriteExecutor(mockBackoffExecutor)
      .errorRecoverer(mockErrorRecoverer)
      .build();

    testWriter = new TestSyncWriterSuccess(
      retryPolicyConfiguration,
      mockRequestInterceptor
    );

    testFailureWriter = new TestSyncWriterFailure(
      retryPolicyConfiguration,
      mockRequestInterceptor);

    testWriterWithoutBackoffAndRecoverer = new TestSyncWriterFailure(
      null,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    testWriter.execute(entity);

    verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
    verify(mockErrorRecoverer, never()).recover(any(MyItem.class));
    verify(mockRequestInterceptor, times(1)).logSuccess(any(MyItem.class));
    verify(mockRequestInterceptor, never()).logError(any(MyItem.class), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, never()).recover(any(MyItem.class));
    verify(mockRequestInterceptor, never()).logError(any(MyItem.class), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(any(MyItem.class));
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(any(MyItem.class));
    verify(mockRequestInterceptor, times(1)).logError(any(MyItem.class), any(RuntimeException.class));
    verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(MyItem.class));

    Assertions.assertThrows(RuntimeException.class, () -> testFailureWriter.execute(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(any(MyItem.class));
    verify(mockRequestInterceptor, times(1)).logError(any(MyItem.class), any());
    verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.execute(entity));

    verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
    verify(mockErrorRecoverer, never()).recover(any(MyItem.class));
    verify(mockRequestInterceptor, times(1)).logError(any(MyItem.class), any());
    verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
  }


  private static class TestSyncWriterSuccess extends AbstractSyncWriter {

    public TestSyncWriterSuccess(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                 final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, requestInterceptor);
    }

    @Override
    public void executor(final TableEntity entity) {
    }
  }

  private static class TestSyncWriterFailure extends AbstractSyncWriter {

    public TestSyncWriterFailure(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                 final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, requestInterceptor);
    }

    @Override
    public void executor(final TableEntity entity) {
      throw new RuntimeException();
    }
  }
}


