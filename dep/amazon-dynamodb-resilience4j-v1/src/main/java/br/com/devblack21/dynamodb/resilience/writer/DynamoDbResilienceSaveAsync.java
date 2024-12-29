package br.com.devblack21.dynamodb.resilience.writer;

import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.backoff.RetryableExecutor;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class DynamoDbResilienceSaveAsync<T> implements DynamoDbResilienceSave<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RetryableExecutor retryableExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final ExecutorService executorService;
  private final RequestInterceptor<T> requestInterceptor;

  @Override
  public void save(final T entity) {
    CompletableFuture.runAsync(() -> executeSave(entity), executorService)
      .whenComplete((unused, throwable) -> {
        if (throwable != null) {
          this.handleSaveFailure(entity, throwable);
        }
      });
  }

  private void executeSave(final T entity) {
    this.dynamoDBMapper.save(entity);
    this.logSuccess(entity);
  }

  private void handleSaveFailure(final T entity, final Throwable initialException) {
    if (this.retryableExecutor != null) {
      try {
        this.retryableExecutor.execute(() -> executeSave(entity));
      } catch (final Exception retryException) {
        this.handleRecoveryOrThrow(entity, retryException);
      }
    } else {
      this.handleRecoveryOrThrow(entity, initialException);
    }
  }

  private void handleRecoveryOrThrow(final T entity, final Throwable exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entity);
    } else {
      this.logError(entity, exceptionToHandle);
    }
  }

  private void logSuccess(final T entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }

  private void logError(final T entity, final Throwable throwable) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logError(entity, throwable);
    }
  }


}
