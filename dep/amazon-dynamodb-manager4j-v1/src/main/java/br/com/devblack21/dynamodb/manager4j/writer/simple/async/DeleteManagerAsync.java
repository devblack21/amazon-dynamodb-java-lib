package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;


public class DeleteManagerAsync<T> extends AbstractAsyncWriter<T> implements DeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;

  public DeleteManagerAsync(final DynamoDBMapper dynamoDBMapper,
                            final BackoffSingleWriteExecutor backoffExecutor,
                            final ErrorRecoverer<T> errorRecoverer,
                            final ExecutorService executorService,
                            final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
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
