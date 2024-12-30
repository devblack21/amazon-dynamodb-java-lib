package br.com.devblack21.dynamodb.resilience.backoff;

public interface BackoffExecutor {

  void execute(final Runnable runnable);

}
