package br.com.devblack21.dynamodb.resilience.writer;

import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.backoff.RetryableExecutor;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DynamoDbResilienceSaveSync<T> implements DynamoDbResilienceSave<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RetryableExecutor retryableExecutor;
  private final ErrorRecoverer<T> errorRecoverer;
  private final RequestInterceptor<T> requestInterceptor;

  @Override
  public void save(final T entity) {
    try {
      this.executeSave(entity);
    } catch (final Exception initialException) {
      this.handleSaveFailure(entity, initialException);
    }
  }

  private void executeSave(final T entity) {
    this.dynamoDBMapper.save(entity);
    this.logSuccess(entity);
  }

  private void handleSaveFailure(final T entity, final Exception initialException) {
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

  private void handleRecoveryOrThrow(final T entity, final Exception exceptionToHandle) {
    if (this.errorRecoverer != null) {
      this.errorRecoverer.recover(entity);
      this.logError(entity, exceptionToHandle);
    } else {
      this.logError(entity, exceptionToHandle);
      throw new RuntimeException("Failed to save entity after retry attempts.", exceptionToHandle);
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
