package br.com.devblack21.dynamodb.resilience.backoff;

public interface RetryableExecutor {

  void execute(final Runnable runnable);

}
