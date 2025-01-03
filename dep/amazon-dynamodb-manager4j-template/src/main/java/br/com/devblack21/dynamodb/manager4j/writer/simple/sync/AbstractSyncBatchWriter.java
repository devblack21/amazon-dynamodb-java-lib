package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.exception.UnprocessedItemsExceptions;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
abstract class AbstractSyncBatchWriter<T> {

  private final BackoffBatchWriteExecutor<T> backoffExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final RequestInterceptor<T> requestInterceptor;

  public void execute(final List<T> entities) {
    List<UnprocessedItem<T>> unprocesseds = UnprocessedItem.unprocessedItems(entities);
    try {
      unprocesseds = this.executor(entities);

      if (!unprocesseds.isEmpty()) {
        throw new UnprocessedItemsExceptions(unprocesseds);
      } else {
        this.logSuccess(entities);
      }
    } catch (final Exception initialException) {
      this.handleSaveFailure(UnprocessedItem.getEntities(unprocesseds), initialException);
    }
  }

  protected abstract List<UnprocessedItem<T>> executor(final List<T> entities);

  private void handleSaveFailure(final List<T> entities, final Exception initialException) {
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

  private void handleRecoveryOrThrow(final List<T> entities, final Exception exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entities);
      this.logError(entities, exceptionToHandle);
    } else {
      this.logError(entities, exceptionToHandle);
      throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
    }
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
