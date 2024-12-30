package br.com.devblack21.dynamodb.resilience.backoff;

import java.util.concurrent.ExecutionException;

public interface BackoffExecutor {

  void execute(final Runnable runnable) throws ExecutionException, InterruptedException;

}
