package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

abstract class AbstractSyncWriter extends AbstractSingleWriter {

  private final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration;

  public AbstractSyncWriter(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                            final RequestInterceptor requestInterceptor) {
    super(retryPolicyConfiguration, requestInterceptor);
    this.retryPolicyConfiguration = retryPolicyConfiguration;
  }

  public void execute(final TableEntity entity) {
    try {
      this.executor(entity);
      this.logSuccess(entity);
    } catch (final Exception initialException) {
      this.handleSaveFailure(entity, initialException);
    }
  }

  public abstract void executor(final TableEntity entity);

  private void handleSaveFailure(final TableEntity entity, final Exception initialException) {
    if (this.isEnableBackoffExecutor()) {
      try {
        this.retryPolicyConfiguration.getBackoffSingleWriteExecutor().execute(() -> executor(entity));
        this.logSuccess(entity);
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entity, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entity, initialException);
    }
  }

  private void handleRecoveryOrThrow(final TableEntity entity, final Exception exceptionToHandle) {
    try {
      if (this.isEnableErrorRecoverer()) {
        this.retryPolicyConfiguration.getErrorRecoverer().recover(entity);
        this.logError(entity, exceptionToHandle);
      } else {
        throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
      }
    } catch (final Exception e) {
      this.logError(entity, e);
      throw e;
    }
  }

}
