package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class BatchDeleteManagerSync<T> extends AbstractSyncWriter<T> implements BatchDeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;
  public BatchDeleteManagerSync(final DynamoDBMapper dynamoDBMapper,
                                final BackoffExecutor backoffExecutor,
                                final ErrorRecoverer<T> errorRecoverer,
                                final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
  }


  @Override
  public void batchDelete(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDBMapper.batchDelete(entity);
  }

}
