package br.com.devblack21.dynamodb.resilience.writer;

import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.backoff.RetryableExecutor;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;


public class DynamoDbResilienceBatchSaveAsync<T> extends AbstractAsyncWriter<T> implements DynamoDbResilienceBatchSave<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RequestInterceptor<T> requestInterceptor;

  public DynamoDbResilienceBatchSaveAsync(final DynamoDBMapper dynamoDBMapper,
                                          final RetryableExecutor retryableExecutor,
                                          final ErrorRecoverer<T> errorRecoverer,
                                          final ExecutorService executorService,
                                          final RequestInterceptor<T> requestInterceptor) {
    super(retryableExecutor, errorRecoverer, executorService, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
    this.requestInterceptor = requestInterceptor;
  }

  @Override
  public void batchSave(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDBMapper.batchSave(entity);
    this.logSuccess(entity);
  }

  private void logSuccess(final T entity) {
    if (this.requestInterceptor != null) {
      this.requestInterceptor.logSuccess(entity);
    }
  }

}
