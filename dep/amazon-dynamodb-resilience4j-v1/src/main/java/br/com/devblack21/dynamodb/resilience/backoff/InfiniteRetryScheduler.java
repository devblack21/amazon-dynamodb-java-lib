package br.com.devblack21.dynamodb.resilience.backoff;

import br.com.devblack21.dynamodb.resilience.interceptor.RetryInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class InfiniteRetryScheduler implements BackoffExecutor {

  private final BackoffDelayAlgorithm delayAlgorithm;
  private final ScheduledExecutorService scheduledExecutorService;
  private final RetryInterceptor retryInterceptor;

  public void execute(final Runnable runnable) {
    execute(runnable, 0);
  }

  private void execute(final Runnable runnable, final int count) {
    try {
      this.scheduledExecutorService.schedule(() -> {
        this.logRetryStart();
        runnable.run();
        this.logRetryEnd();
      }, delayAlgorithm.delay(count), TimeUnit.MILLISECONDS);
    } catch (final Exception e) {
      execute(runnable, count + 1);
    }
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
