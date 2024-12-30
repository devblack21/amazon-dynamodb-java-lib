package br.com.devblack21.dynamodb.resilience.writer.sync;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class AbstractSyncWriter<T> {

  private final BackoffExecutor backoffExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final RequestInterceptor<T> requestInterceptor;

  public void execute(final T entity) {
    try {
      this.executor(entity);
    } catch (final Exception initialException) {
      this.handleSaveFailure(entity, initialException);
    }
  }

  public abstract void executor(final T entity);

  private void handleSaveFailure(final T entity, final Exception initialException) {
    if (this.backoffExecutor != null) {
      try {
        this.backoffExecutor.execute(() -> executor(entity));
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entity, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entity, initialException);
    }
  }

  private void handleRecoveryOrThrow(final T entity, final Exception exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entity);
      this.logError(entity, exceptionToHandle);
    } else {
      this.logError(entity, exceptionToHandle);
      throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
    }
  }

  private void logError(final T entity, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entity, throwable);
    }
  }

}
