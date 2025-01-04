package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.exception.UnprocessedItemsExceptions;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;

import java.util.List;

abstract class AbstractSyncBatchWriter extends AbstractBatchWriter {

  private final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration;

  public AbstractSyncBatchWriter(final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                 final RequestInterceptor requestInterceptor) {
    super(retryPolicyConfiguration, requestInterceptor);
    this.retryPolicyConfiguration = retryPolicyConfiguration;
  }

  public void execute(final List<? extends TableEntity> entities) {
    List<UnprocessedItem> unprocesseds = UnprocessedItem.unprocessedItems(entities);
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

  protected abstract List<UnprocessedItem> executor(final List<? extends TableEntity> entities);

  private void handleSaveFailure(final List<? extends TableEntity> entities, final Exception initialException) {
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

  private void handleRecoveryOrThrow(final List<? extends TableEntity> entities, final Exception exceptionToHandle) {
    try {
      if (this.isEnableErrorRecoverer()) {
        this.retryPolicyConfiguration.getErrorRecoverer().recover(entities);
        this.logError(entities, exceptionToHandle);
      } else {
        throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
      }
    } catch (final Exception e) {
      this.logError(entities, e);
      throw e;
    }
  }

}
