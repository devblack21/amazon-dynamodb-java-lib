package br.com.devblack21.dynamodb.resilience.backoff;

import br.com.devblack21.dynamodb.resilience.helper.mock.FailureSuccessRunnableMock;
import br.com.devblack21.dynamodb.resilience.helper.mock.SuccessRunnableMock;
import br.com.devblack21.dynamodb.resilience.interceptor.RetryInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

class InfiniteRetrySchedulerTest {

  private BackoffDelayAlgorithm backoffDelayAlgorithm;

  private RetryInterceptor retryInterceptor;

  private InfiniteRetryScheduler executor;

  @BeforeEach
  void setUp() {
    backoffDelayAlgorithm = mock(BackoffDelayAlgorithm.class);
    retryInterceptor = mock(RetryInterceptor.class);
    executor = new InfiniteRetryScheduler(backoffDelayAlgorithm,
      Executors.newScheduledThreadPool(10),
      retryInterceptor);
  }

  @Test
  void testExecuteSuccessOnFirstAttempt() throws ExecutionException, InterruptedException {

    final SuccessRunnableMock runnable = new SuccessRunnableMock();

    executor.execute(runnable);

    runnable.verify(1);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(1)).logRetryStart();
    verify(retryInterceptor, times(1)).logRetryEnd();
  }

  @Test
  void testRetryOnException() throws ExecutionException, InterruptedException {

    final int maxAttempts = 5;

    final FailureSuccessRunnableMock runnable = new FailureSuccessRunnableMock(maxAttempts);

    executor.execute(runnable);

    runnable.verify(maxAttempts);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, times(maxAttempts)).logRetryStart();
    verify(retryInterceptor, times(maxAttempts)).logRetryEnd();
  }

  @Test
  void testRetryOnExceptionWithoutInterceptor() throws ExecutionException, InterruptedException {

    final int maxAttempts = 5;

    final InfiniteRetryScheduler withouInterceptor = schedulerWithoutInterceptor();

    final FailureSuccessRunnableMock runnable = new FailureSuccessRunnableMock(maxAttempts);

    withouInterceptor.execute(runnable);

    runnable.verify(maxAttempts);
    runnable.verifyNoMoreInterations();
    verify(retryInterceptor, never()).logRetryStart();
    verify(retryInterceptor, never()).logRetryEnd();
  }

  private InfiniteRetryScheduler schedulerWithoutInterceptor() {
    return new InfiniteRetryScheduler(
      backoffDelayAlgorithm,
      Executors.newScheduledThreadPool(10),
      null);
  }


}
