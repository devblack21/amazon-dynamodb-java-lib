package br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public interface BackoffBatchWriteExecutor<T> {

  void execute(final Function<List<T>, List<T>> batchFunction, final List<T> entities) throws ExecutionException, InterruptedException;

}
