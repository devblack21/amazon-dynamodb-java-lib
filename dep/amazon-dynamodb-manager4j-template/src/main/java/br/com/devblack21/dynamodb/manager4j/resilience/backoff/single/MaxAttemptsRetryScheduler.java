package br.com.devblack21.dynamodb.manager4j.resilience.backoff.single;

import br.com.devblack21.dynamodb.manager4j.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.algorithm.BackoffDelayAlgorithm;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MaxAttemptsRetryScheduler implements BackoffSingleWriteExecutor {

  private final int maxAttempts;
  private final BackoffDelayAlgorithm delayAlgorithm;
  private final ScheduledExecutorService scheduledExecutorService;
  private final RetryInterceptor retryInterceptor;

  @Override
  public void execute(final Runnable runnable) {
    try {
      this.executeWithRetry(runnable, 0);
    } catch (final ExecutionException | InterruptedException e) {
      throw new MaxAttemptsRetryException();
    } finally {
      scheduledExecutorService.shutdown();
    }
  }

  private void executeWithRetry(final Runnable runnable, final int attemptCount) throws ExecutionException, InterruptedException {
    if (attemptCount >= this.maxAttempts) {
      throw new MaxAttemptsRetryException();
    }

    try {
      this.logRetryStart();
      runnable.run();
      this.logRetryEnd();
    } catch (final Exception e) {
      this.logRetryEnd();
      this.scheduleRetry(runnable, attemptCount + 1);
    }
  }

  private void scheduleRetry(final Runnable runnable, final int nextAttemptCount) throws ExecutionException, InterruptedException {
    final long delay = this.delayAlgorithm.delay(nextAttemptCount);

    this.scheduledExecutorService.schedule(() -> {
        try {
          executeWithRetry(runnable, nextAttemptCount);
        } catch (final MaxAttemptsRetryException e) {
          throw e;
        } catch (final Exception e) {
          throw new RuntimeException(e);
        }
      },
      delay, TimeUnit.MILLISECONDS).get();
  }

  private void logRetryStart() {
    if (this.retryInterceptor != null) {
      this.retryInterceptor.logRetryStart();
    }
  }

  private void logRetryEnd() {
    if (this.retryInterceptor != null) {
      this.retryInterceptor.logRetryEnd();
    }
  }

}
