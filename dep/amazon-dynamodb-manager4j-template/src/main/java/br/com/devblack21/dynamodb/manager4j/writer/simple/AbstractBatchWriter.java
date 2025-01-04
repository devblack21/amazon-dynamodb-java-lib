package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
abstract class AbstractBatchWriter {

  private final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration;
  private final RequestInterceptor requestInterceptor;


  protected void logError(final List<? extends TableEntity> entities, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entities, throwable);
    }
  }

  protected void logSuccess(final List<? extends TableEntity> entities) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entities);
    }
  }

  protected boolean isEnableBackoffExecutor() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableBackoffBatchWriteExecutor();
  }

  protected boolean isEnableErrorRecoverer() {
    return isEnableRetryPolicy() && this.retryPolicyConfiguration.isEnableErrorRecoverer();
  }

  protected boolean isEnableRetryPolicy() {
    return this.retryPolicyConfiguration != null;
  }

}
