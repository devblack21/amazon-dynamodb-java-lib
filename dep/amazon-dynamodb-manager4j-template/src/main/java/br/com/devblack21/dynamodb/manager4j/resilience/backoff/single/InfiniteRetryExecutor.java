package br.com.devblack21.dynamodb.manager4j.resilience.backoff.single;

import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class InfiniteRetryExecutor implements BackoffSingleWriteExecutor {

  private final RetryInterceptor retryInterceptor;

  public void execute(final Runnable runnable) {
    execute(runnable, 0);
  }

  private void execute(final Runnable runnable, final int count) {
    try {
      this.logRetryStart();
      runnable.run();
      this.logRetryEnd();
    } catch (final Exception e) {
      this.logRetryEnd();
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
