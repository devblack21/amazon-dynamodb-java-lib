package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
abstract class AbstractAsyncBatchWriter<T> {

  private final BackoffBatchWriteExecutor<T> backoffExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final ExecutorService executorService;
  private final RequestInterceptor<T> requestInterceptor;

  public void execute(final List<T> entity) {
    CompletableFuture.supplyAsync(() -> executor(entity), executorService)
      .whenComplete((unprocesseds, throwable) -> {
        if (!unprocesseds.isEmpty()) {
          this.handleSaveFailure(entity, throwable);
        } else {
          this.logSuccess(entity);
        }
      });
  }

  protected abstract List<T> executor(final List<T> entity);

  private void handleSaveFailure(final List<T> entity, final Throwable initialException) {
    if (this.backoffExecutor != null) {
      try {
        this.backoffExecutor.execute(this::executor, entity);
        this.logSuccess(entity);
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entity, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entity, initialException);
    }
  }

  private void handleRecoveryOrThrow(final List<T> entity, final Throwable exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entity);
    }

    this.logError(entity, exceptionToHandle);
  }

  private void logError(final List<T> entity, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entity, throwable);
    }
  }

  private void logSuccess(final List<T> entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }
}
