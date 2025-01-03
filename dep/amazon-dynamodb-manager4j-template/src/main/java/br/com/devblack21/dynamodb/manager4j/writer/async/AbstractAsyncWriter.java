package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
abstract class AbstractAsyncWriter<T> {

  private final BackoffSingleWriteExecutor backoffExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final ExecutorService executorService;
  private final RequestInterceptor<T> requestInterceptor;

  public void execute(final T entity) {
    CompletableFuture.runAsync(() -> executor(entity), executorService)
      .whenComplete((unused, throwable) -> {
        if (throwable != null) {
          this.handleSaveFailure(entity, throwable);
        } else {
          this.logSuccess(entity);
        }
      });
  }

  public abstract void executor(final T entity);

  private void handleSaveFailure(final T entity, final Throwable initialException) {
    if (this.backoffExecutor != null) {
      try {
        this.backoffExecutor.execute(() -> this.executor(entity));
        this.logSuccess(entity);
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entity, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entity, initialException);
    }
  }

  private void handleRecoveryOrThrow(final T entity, final Throwable exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entity);
    }

    this.logError(entity, exceptionToHandle);
  }

  private void logError(final T entity, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entity, throwable);
    }
  }

  private void logSuccess(final T entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }
}