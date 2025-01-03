package br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch;

import br.com.devblack21.dynamodb.manager4j.exception.MaxAttemptsRetryException;
import br.com.devblack21.dynamodb.manager4j.exception.UnprocessedItemsExceptions;
import br.com.devblack21.dynamodb.manager4j.interceptor.RetryInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@RequiredArgsConstructor
public class MaxAttemptsRetryExecutor<T> implements BackoffBatchWriteExecutor<T> {

  private final int maxAttempts;
  private final RetryInterceptor retryInterceptor;

  @Override
  public void execute(final Function<List<T>, List<T>> batchFunction, final List<T> entities)
    throws ExecutionException, InterruptedException {
    execute(batchFunction, entities, 0);
  }

  private void execute(final Function<List<T>, List<T>> batchFunction, final List<T> entities, final int count) {
    if (count >= this.maxAttempts) {
      throw new MaxAttemptsRetryException();
    }

    List<T> fails = entities;

    try {
      this.logRetryStart();
      fails = batchFunction.apply(entities);
      if (!fails.isEmpty()) {
        throw new UnprocessedItemsExceptions(fails);
      }
      this.logRetryEnd();
    } catch (final Exception e) {
      this.logRetryEnd();
      execute(batchFunction, fails, count + 1);
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
