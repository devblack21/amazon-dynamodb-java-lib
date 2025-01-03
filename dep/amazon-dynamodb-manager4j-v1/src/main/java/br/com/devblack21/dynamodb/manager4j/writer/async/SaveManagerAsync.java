package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.SaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;


public class SaveManagerAsync<T> extends AbstractAsyncWriter<T> implements SaveManager<T> {

  private final DynamoDBMapper dynamoDBMapper;

  public SaveManagerAsync(final DynamoDBMapper dynamoDBMapper,
                          final BackoffSingleWriteExecutor backoffExecutor,
                          final ErrorRecoverer<T> errorRecoverer,
                          final ExecutorService executorService,
                          final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
  }

  @Override
  public void save(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDBMapper.save(entity);
  }

}
