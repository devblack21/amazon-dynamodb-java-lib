package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

abstract class AbstractAsyncBatchWriter extends AbstractBatchWriter {

  private final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final ExecutorService executorService;

  public AbstractAsyncBatchWriter(final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                  final ExecutorService executorService,
                                  final RequestInterceptor requestInterceptor) {
    super(retryPolicyConfiguration, requestInterceptor);
    this.executorService = executorService;
    this.retryPolicyConfiguration = retryPolicyConfiguration;
  }

  public void execute(final List<? extends TableEntity> entities) {
    CompletableFuture.supplyAsync(() -> this.executor(entities), executorService)
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
    try {
      if (this.isEnableErrorRecoverer()) {
        this.retryPolicyConfiguration.getErrorRecoverer().recover(entities);
      }
    } finally {
      this.logError(entities, exceptionToHandle);
    }
  }

}
