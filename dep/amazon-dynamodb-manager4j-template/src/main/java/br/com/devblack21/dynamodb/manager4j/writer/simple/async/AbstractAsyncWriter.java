package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.configuration.WriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
abstract class AbstractAsyncWriter {

  private final WriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final ExecutorService executorService;
  private final RequestInterceptor requestInterceptor;

  public void execute(final TableEntity entity) {
    CompletableFuture.runAsync(() -> executor(entity), executorService)
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
    if (this.isEnableErrorRecoverer()) {
      this.retryPolicyConfiguration.getErrorRecoverer().recover(entity);
    }

    this.logError(entity, exceptionToHandle);
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
