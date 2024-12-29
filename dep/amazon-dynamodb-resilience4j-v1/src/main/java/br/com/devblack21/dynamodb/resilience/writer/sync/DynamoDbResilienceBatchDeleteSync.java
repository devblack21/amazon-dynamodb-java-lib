package br.com.devblack21.dynamodb.resilience.writer.sync;

import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.backoff.RetryableExecutor;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceBatchDelete;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDbResilienceBatchDeleteSync<T> extends AbstractSyncWriter<T> implements DynamoDbResilienceBatchDelete<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RequestInterceptor<T> requestInterceptor;

  public DynamoDbResilienceBatchDeleteSync(final DynamoDBMapper dynamoDBMapper,
                                           final RetryableExecutor retryableExecutor,
                                           final ErrorRecoverer<T> errorRecoverer,
                                           final RequestInterceptor<T> requestInterceptor) {
    super(retryableExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
    this.requestInterceptor = requestInterceptor;
  }


  @Override
  public void batchDelete(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDBMapper.batchDelete(entity);
    this.logSuccess(entity);
  }

  private void logSuccess(final T entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }

}
