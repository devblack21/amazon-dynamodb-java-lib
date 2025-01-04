package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
abstract class AbstractAsyncBatchWriter {

  private final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final ExecutorService executorService;
  private final RequestInterceptor requestInterceptor;

  public void execute(final List<? extends TableEntity> entities) {
    CompletableFuture.supplyAsync(() -> executor(entities), executorService)
      .whenComplete((unprocesseds, throwable) -> {
        if (!unprocesseds.isEmpty()) {
          this.handleSaveFailure(UnprocessedItem.getEntities(unprocesseds), throwable);
        } else {
          this.logSuccess(entities);
        }
      });
  }

  protected abstract List<UnprocessedItem> executor(final List<? extends TableEntity> entities);

  private void handleSaveFailure(final List<? extends TableEntity> entities, final Throwable initialException) {
    if (this.isEnableBackoffExecutor()) {
      try {
        this.retryPolicyConfiguration.getBackoffBatchWriteExecutor().execute(this::executor, entities);
        this.logSuccess(entities);
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entities, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entities, initialException);
    }
  }

  private void handleRecoveryOrThrow(final List<? extends TableEntity> entities, final Throwable exceptionToHandle) {
    if (this.isEnableErrorRecoverer()) {
      this.retryPolicyConfiguration.getErrorRecoverer().recover(entities);
    }

    this.logError(entities, exceptionToHandle);
  }

  private void logError(final List<? extends TableEntity> entities, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entities, throwable);
    }
  }

  private void logSuccess(final List<? extends TableEntity> entities) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entities);
    }
  }

  private boolean isEnableBackoffExecutor() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableBackoffBatchWriteExecutor();
  }

  private boolean isEnableErrorRecoverer() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableErrorRecoverer();
  }

  private boolean isEnableRetryPolicy() {
    return this.retryPolicyConfiguration != null;
  }

}
