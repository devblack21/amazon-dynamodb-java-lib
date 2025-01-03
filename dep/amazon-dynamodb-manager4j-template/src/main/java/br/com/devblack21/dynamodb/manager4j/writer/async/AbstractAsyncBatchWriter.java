package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
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

  public void execute(final List<T> entities) {
    CompletableFuture.supplyAsync(() -> executor(entities), executorService)
      .whenComplete((unprocesseds, throwable) -> {
        if (!unprocesseds.isEmpty()) {
          this.handleSaveFailure(UnprocessedItem.getEntities(unprocesseds), throwable);
        } else {
          this.logSuccess(entities);
        }
      });
  }

  protected abstract List<UnprocessedItem<T>> executor(final List<T> entities);

  private void handleSaveFailure(final List<T> entities, final Throwable initialException) {
    if (this.backoffExecutor != null) {
      try {
        this.backoffExecutor.execute(this::executor, entities);
        this.logSuccess(entities);
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entities, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entities, initialException);
    }
  }

  private void handleRecoveryOrThrow(final List<T> entities, final Throwable exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entities);
    }

    this.logError(entities, exceptionToHandle);
  }

  private void logError(final List<T> entities, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entities, throwable);
    }
  }

  private void logSuccess(final List<T> entities) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entities);
    }
  }
}
