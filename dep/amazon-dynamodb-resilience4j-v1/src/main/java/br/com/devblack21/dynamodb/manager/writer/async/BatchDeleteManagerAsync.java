package br.com.devblack21.dynamodb.manager.writer.async;

import br.com.devblack21.dynamodb.manager.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;


public class BatchDeleteManagerAsync<T> extends AbstractAsyncWriter<T> implements BatchDeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RequestInterceptor<T> requestInterceptor;

  public BatchDeleteManagerAsync(final DynamoDBMapper dynamoDBMapper,
                                 final BackoffExecutor backoffExecutor,
                                 final ErrorRecoverer<T> errorRecoverer,
                                 final ExecutorService executorService,
                                 final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
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
