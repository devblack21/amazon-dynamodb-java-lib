package br.com.devblack21.dynamodb.manager4j.resilience;

import java.util.concurrent.ExecutionException;

public interface BackoffExecutor {

  void execute(final Runnable runnable) throws ExecutionException, InterruptedException;

}
