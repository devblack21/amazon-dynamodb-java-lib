package br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public interface BackoffBatchWriteExecutor {

  void execute(final Function<List<? extends TableEntity>, List<UnprocessedItem>> batchFunction, final List<? extends TableEntity> entities) throws ExecutionException, InterruptedException;

}
