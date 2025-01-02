package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.DeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DeleteManagerSync<T> extends AbstractSyncWriter<T> implements DeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;

  public DeleteManagerSync(final DynamoDBMapper dynamoDBMapper,
                           final BackoffExecutor backoffExecutor,
                           final ErrorRecoverer<T> errorRecoverer,
                           final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
  }


  @Override
  public void delete(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDBMapper.delete(entity);
  }

}
