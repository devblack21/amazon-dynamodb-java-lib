package br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch;

import br.com.devblack21.dynamodb.manager4j.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager4j.exception.UnprocessedItemsExceptions;
import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@RequiredArgsConstructor
public class MaxAttemptsRetryExecutor<T> implements BackoffBatchWriteExecutor<T> {

  private final int maxAttempts;
  private final RetryInterceptor retryInterceptor;

  @Override
  public void execute(final Function<List<T>, List<UnprocessedItem<T>>> batchFunction, final List<T> entities)
    throws ExecutionException, InterruptedException {
    execute(batchFunction, entities, 0);
  }

  private void execute(final Function<List<T>, List<UnprocessedItem<T>>> batchFunction, final List<T> entities, final int count) {
    if (count >= this.maxAttempts) {
      throw new MaxAttemptsRetryException();
    }

    List<T> unprocesseds = entities;

    try {
      this.logRetryStart();
      unprocesseds = UnprocessedItem.getEntities(batchFunction.apply(entities));
      if (!unprocesseds.isEmpty()) {
        throw new UnprocessedItemsExceptions(unprocesseds);
      }
      this.logRetryEnd();
    } catch (final Exception e) {
      this.logRetryEnd();
      execute(batchFunction, unprocesseds, count + 1);
    }
  }


  private void logRetryStart() {
    if (this.retryInterceptor != null) {
      this.retryInterceptor.logRetryStart();
    }
  }

  private void logRetryEnd() {
    if (this.retryInterceptor != null) {
      this.retryInterceptor.logRetryEnd();
    }
  }

}
