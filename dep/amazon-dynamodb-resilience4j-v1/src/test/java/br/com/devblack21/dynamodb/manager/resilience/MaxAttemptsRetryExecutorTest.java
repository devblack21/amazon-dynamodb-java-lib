package br.com.devblack21.dynamodb.manager.resilience;

import br.com.devblack21.dynamodb.manager.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager.helper.mock.FailureRunnableMock;
import br.com.devblack21.dynamodb.manager.helper.mock.FailureSuccessRunnableMock;
import br.com.devblack21.dynamodb.manager.helper.mock.SuccessRunnableMock;
import br.com.devblack21.dynamodb.manager.interceptor.RetryInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

class MaxAttemptsRetryExecutorTest {


  private RetryInterceptor retryInterceptor;

  private MaxAttemptsRetryExecutor executor;

  private static final Integer MAX_ATTEMPTS = 5;

  @BeforeEach
  void setUp() {
    retryInterceptor = mock(RetryInterceptor.class);
    executor = new MaxAttemptsRetryExecutor(MAX_ATTEMPTS, retryInterceptor);
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

    final FailureRunnableMock runnable = new FailureRunnableMock();

    Assertions.assertThrows(RuntimeException.class, () -> executor.execute(runnable));

    runnable.verify(MAX_ATTEMPTS);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(MAX_ATTEMPTS)).logRetryStart();
    verify(retryInterceptor, times(MAX_ATTEMPTS)).logRetryEnd();
  }

  @Test
  void testMaxAttemptsExceeded() {

    final FailureRunnableMock runnable = new FailureRunnableMock();

    MaxAttemptsRetryException exception =
      Assertions.assertThrows(MaxAttemptsRetryException.class, () -> executor.execute(runnable));

    assertThat(exception, notNullValue());

    runnable.verify(MAX_ATTEMPTS);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(MAX_ATTEMPTS)).logRetryStart();
    verify(retryInterceptor, times(MAX_ATTEMPTS)).logRetryEnd();
  }

  @Test
  void testWithoutRetryInterceptor() {

    final MaxAttemptsRetryExecutor executorWithoutInterceptor = new MaxAttemptsRetryExecutor(MAX_ATTEMPTS, null);

    final SuccessRunnableMock runnable = new SuccessRunnableMock();

    executorWithoutInterceptor.execute(runnable);

    runnable.verify(1);
    runnable.verifyNoMoreInterations();
  }

  @Test
  void testRetryWithoutRetryInterceptor() {

    final MaxAttemptsRetryExecutor executorWithoutInterceptor = new MaxAttemptsRetryExecutor(MAX_ATTEMPTS, null);

    final FailureRunnableMock runnable = new FailureRunnableMock();

    MaxAttemptsRetryException exception =
      Assertions.assertThrows(MaxAttemptsRetryException.class, () -> executorWithoutInterceptor.execute(runnable));

    assertThat(exception, notNullValue());

    runnable.verify(MAX_ATTEMPTS);
    runnable.verifyNoMoreInterations();
  }

  @Test
  void testRetryWhenSuccessBetweenExecutions() {

    final int attempts = MAX_ATTEMPTS - 2;

    final FailureSuccessRunnableMock runnable = new FailureSuccessRunnableMock(attempts);

    Assertions.assertDoesNotThrow(() -> executor.execute(runnable));

    runnable.verify(attempts);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(attempts)).logRetryStart();
    verify(retryInterceptor, times(attempts)).logRetryEnd();
  }

}
