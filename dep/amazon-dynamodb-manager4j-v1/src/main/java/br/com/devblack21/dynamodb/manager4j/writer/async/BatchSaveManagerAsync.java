package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchSaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;


public class BatchSaveManagerAsync<T> extends AbstractAsyncWriter<List<T>> implements BatchSaveManager<T> {

  private final DynamoDBMapper dynamoDBMapper;

  public BatchSaveManagerAsync(final DynamoDBMapper dynamoDBMapper,
                               final BackoffExecutor backoffExecutor,
                               final ErrorRecoverer<List<T>> errorRecoverer,
                               final ExecutorService executorService,
                               final RequestInterceptor<List<T>> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
  }

  @Override
  public void batchSave(final List<T> entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final List<T> entity) {
    this.dynamoDBMapper.batchSave(entity);
  }

}
