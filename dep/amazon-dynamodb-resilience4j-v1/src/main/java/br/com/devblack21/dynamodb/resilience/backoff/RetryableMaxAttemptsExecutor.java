package br.com.devblack21.dynamodb.resilience.backoff;

import br.com.devblack21.dynamodb.resilience.exceptions.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.resilience.interceptors.RetryInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RetryableMaxAttemptsExecutor implements RetryableExecutor {

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
