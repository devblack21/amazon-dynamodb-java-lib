package br.com.devblack21.dynamodb.manager.writer.sync;

import br.com.devblack21.dynamodb.manager.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager.writer.DeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DeleteManagerSync<T> extends AbstractSyncWriter<T> implements DeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final RequestInterceptor<T> requestInterceptor;

  public DeleteManagerSync(final DynamoDBMapper dynamoDBMapper,
                           final BackoffExecutor backoffExecutor,
                           final ErrorRecoverer<T> errorRecoverer,
                           final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
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
