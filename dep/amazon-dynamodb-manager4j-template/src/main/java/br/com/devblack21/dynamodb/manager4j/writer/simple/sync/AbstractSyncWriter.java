package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class AbstractSyncWriter {

  private final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final RequestInterceptor requestInterceptor;

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
    if (this.isEnableErrorRecoverer()) {
      this.retryPolicyConfiguration.getErrorRecoverer().recover(entity);
      this.logError(entity, exceptionToHandle);
    } else {
      this.logError(entity, exceptionToHandle);
      throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
    }
  }

  private void logError(final TableEntity entity, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entity, throwable);
    }
  }

  private void logSuccess(final TableEntity entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }

  private boolean isEnableBackoffExecutor() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableBackoffSingleWriteExecutor();
  }

  private boolean isEnableErrorRecoverer() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableErrorRecoverer();
  }

  private boolean isEnableRetryPolicy() {
    return this.retryPolicyConfiguration != null;
  }

}
