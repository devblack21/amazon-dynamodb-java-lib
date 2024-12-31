package br.com.devblack21.dynamodb.manager4j.resilience;

import br.com.devblack21.dynamodb.manager4j.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MaxAttemptsRetryExecutor implements BackoffExecutor {

  private final int maxAttempts;
  private final RetryInterceptor retryInterceptor;

  public void execute(final Runnable runnable) {
    execute(runnable, 0);
  }

  private void execute(final Runnable runnable, final int count) {
    if (count >= this.maxAttempts) {
      throw new MaxAttemptsRetryException();
    }

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