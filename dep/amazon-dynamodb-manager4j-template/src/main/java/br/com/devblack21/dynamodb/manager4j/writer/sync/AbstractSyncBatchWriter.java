package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.exception.UnprocessedItemsExceptions;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
abstract class AbstractSyncBatchWriter<T> {

  private final BackoffBatchWriteExecutor<T> backoffExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final RequestInterceptor<T> requestInterceptor;

  public void execute(final List<T> entity) {
    List<T> unprocesseds = entity;
    try {
      unprocesseds = this.executor(entity);

      if (!unprocesseds.isEmpty()) {
        throw new UnprocessedItemsExceptions(unprocesseds);
      } else {
        this.logSuccess(entity);
      }
    } catch (final Exception initialException) {
      this.handleSaveFailure(unprocesseds, initialException);
    }
  }

  public abstract List<T> executor(final List<T> entity);

  private void handleSaveFailure(final List<T> entity, final Exception initialException) {
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

  private void handleRecoveryOrThrow(final List<T> entity, final Exception exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entity);
      this.logError(entity, exceptionToHandle);
    } else {
      this.logError(entity, exceptionToHandle);
      throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
    }
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
