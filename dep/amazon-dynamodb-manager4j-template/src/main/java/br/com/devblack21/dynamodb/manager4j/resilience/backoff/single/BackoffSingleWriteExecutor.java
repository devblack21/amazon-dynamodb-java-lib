package br.com.devblack21.dynamodb.manager4j.resilience.backoff.single;

import java.util.concurrent.ExecutionException;

public interface BackoffSingleWriteExecutor {

  void execute(final Runnable runnable) throws ExecutionException, InterruptedException;

}
