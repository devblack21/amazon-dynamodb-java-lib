package br.com.devblack21.dynamodb.manager4j.resilience.backoff.single;

import br.com.devblack21.dynamodb.manager4j.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MaxAttemptsRetryExecutor implements BackoffSingleWriteExecutor {

  private final int maxAttempts;
  private final RetryInterceptor retryInterceptor;

  public void execute(final Runnable runnable) {
    execute(runnable, 0);
  }

  private void execute(final Runnable runnable, final int attemptCount) {

    this.validate(attemptCount);

    try {
      this.logRetryStart();
      runnable.run();
      this.logRetryEnd();
    } catch (final Exception e) {
      this.logRetryEnd();
      execute(runnable, attemptCount + 1);
    }
  }

  private void validate(final int attemptCount) {
    if (attemptCount >= this.maxAttempts) {
      throw new MaxAttemptsRetryException();
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
