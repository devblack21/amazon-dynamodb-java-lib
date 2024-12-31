package br.com.devblack21.dynamodb.manager.resilience;

import br.com.devblack21.dynamodb.manager.helper.mock.FailureSuccessRunnableMock;
import br.com.devblack21.dynamodb.manager.helper.mock.SuccessRunnableMock;
import br.com.devblack21.dynamodb.manager.interceptor.RetryInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class InfiniteRetryExecutorTest {


  private RetryInterceptor retryInterceptor;

  private InfiniteRetryExecutor executor;

  @BeforeEach
  void setUp() {
    retryInterceptor = mock(RetryInterceptor.class);
    executor = new InfiniteRetryExecutor(retryInterceptor);
  }

  @Test
  void testExecuteSuccessOnFirstAttempt() {

    final SuccessRunnableMock runnable = new SuccessRunnableMock();

    executor.execute(runnable);

    runnable.verify(1);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(1)).logRetryStart();
    verify(retryInterceptor, times(1)).logRetryEnd();
  }

  @Test
  void testRetryOnException() {

    final int maxAttempts = 5;

    final FailureSuccessRunnableMock runnable = new FailureSuccessRunnableMock(maxAttempts);

    executor.execute(runnable);

    runnable.verify(maxAttempts);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(maxAttempts)).logRetryStart();
    verify(retryInterceptor, times(maxAttempts)).logRetryEnd();
  }

  @Test
  void testRetryOnExceptionWithoutInterceptor() {

    final int maxAttempts = 5;

    final InfiniteRetryExecutor withouInterceptor = new InfiniteRetryExecutor(null);

    final FailureSuccessRunnableMock runnable = new FailureSuccessRunnableMock(maxAttempts);

    withouInterceptor.execute(runnable);

    runnable.verify(maxAttempts);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, never()).logRetryStart();
    verify(retryInterceptor, never()).logRetryEnd();
  }


}
