package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
abstract class AbstractSingleWriter {

  private final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final RequestInterceptor requestInterceptor;


  protected void logError(final TableEntity entity, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entity, throwable);
    }
  }

  protected void logSuccess(final TableEntity entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }

  protected boolean isEnableBackoffExecutor() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableBackoffSingleWriteExecutor();
  }

  protected boolean isEnableErrorRecoverer() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableErrorRecoverer();
  }

  protected boolean isEnableRetryPolicy() {
    return this.retryPolicyConfiguration != null;
  }

}
