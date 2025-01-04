package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

abstract class AbstractAsyncWriter extends AbstractSingleWriter {

  private final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final ExecutorService executorService;

  public AbstractAsyncWriter(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                             final ExecutorService executorService,
                             final RequestInterceptor requestInterceptor) {
    super(retryPolicyConfiguration, requestInterceptor);
    this.executorService = executorService;
    this.retryPolicyConfiguration = retryPolicyConfiguration;
  }
  public void execute(final TableEntity entity) {
    CompletableFuture.runAsync(() -> this.executor(entity), executorService)
      .whenComplete((unused, throwable) -> {
        if (throwable != null) {
          this.handleSaveFailure(entity, throwable);
        } else {
          this.logSuccess(entity);
        }
      });
  }

  public abstract void executor(final TableEntity entity);

  private void handleSaveFailure(final TableEntity entity, final Throwable initialException) {
    if (this.isEnableBackoffExecutor()) {
      try {
        this.retryPolicyConfiguration.getBackoffSingleWriteExecutor().execute(() -> this.executor(entity));
        this.logSuccess(entity);
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entity, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entity, initialException);
    }
  }

  private void handleRecoveryOrThrow(final TableEntity entity, final Throwable exceptionToHandle) {
    try {
      if (this.isEnableErrorRecoverer()) {
        this.retryPolicyConfiguration.getErrorRecoverer().recover(entity);
      }

    } finally {
      this.logError(entity, exceptionToHandle);
    }
  }

}
