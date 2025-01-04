package br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch;

import br.com.devblack21.dynamodb.manager4j.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager4j.exception.UnprocessedItemsExceptions;
import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@RequiredArgsConstructor
public final class MaxAttemptsRetryExecutor implements BackoffBatchWriteExecutor {

  private final int maxAttempts;
  private final RetryInterceptor retryInterceptor;

  @Override
  public void execute(final Function<List<? extends TableEntity>, List<UnprocessedItem>> batchFunction, final List<? extends TableEntity> entities)
    throws ExecutionException, InterruptedException {
    execute(batchFunction, entities, 0);
  }

  private void execute(final Function<List<? extends TableEntity>, List<UnprocessedItem>> batchFunction, final List<? extends TableEntity> entities, final int count) {

    this.validate(maxAttempts);

    List<? extends TableEntity> unprocesseds = entities;

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

  private void validate(final int attemptCount) {
    if (attemptCount >= this.maxAttempts) {
      throw new MaxAttemptsRetryException();
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
