package br.com.devblack21.dynamodb.resilience.writer;

import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.backoff.RetryableExecutor;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDbResilienceDeleteSync<T> extends AbstractSyncWriter<T> implements DynamoDbResilienceDelete<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RequestInterceptor<T> requestInterceptor;

  public DynamoDbResilienceDeleteSync(final DynamoDBMapper dynamoDBMapper,
                                      final RetryableExecutor retryableExecutor,
                                      final ErrorRecoverer<T> errorRecoverer,
                                      final RequestInterceptor<T> requestInterceptor) {
    super(retryableExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
    this.requestInterceptor = requestInterceptor;
  }


  @Override
  public void delete(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDBMapper.delete(entity);
    this.logSuccess(entity);
  }

  private void logSuccess(final T entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }

}
