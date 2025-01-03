package br.com.devblack21.dynamodb.manager4j.configuration;


import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WriteRetryPolicyConfiguration {

  private final BackoffSingleWriteExecutor backoffSingleWriteExecutor;
  private final BackoffBatchWriteExecutor backoffBatchWriteExecutor;
  private final ErrorRecoverer errorRecoverer;

  public boolean isEnableErrorRecoverer() {
    return this.errorRecoverer != null;
  }

  public boolean isEnableBackoffSingleWriteExecutor() {
    return this.backoffSingleWriteExecutor != null;
  }

  public boolean isEnableBackoffBatchWriteExecutor() {
    return this.backoffBatchWriteExecutor != null;
  }

}
