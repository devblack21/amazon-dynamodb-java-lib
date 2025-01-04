package br.com.devblack21.dynamodb.manager4j.configuration;


import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SingleWriteRetryPolicyConfiguration {

  private final BackoffSingleWriteExecutor backoffSingleWriteExecutor;
  private final ErrorRecoverer errorRecoverer;

  public boolean isEnableErrorRecoverer() {
    return this.errorRecoverer != null;
  }

  public boolean isEnableBackoffSingleWriteExecutor() {
    return this.backoffSingleWriteExecutor != null;
  }

}
