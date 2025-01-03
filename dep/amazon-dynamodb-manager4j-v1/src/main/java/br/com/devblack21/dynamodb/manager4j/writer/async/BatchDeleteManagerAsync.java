package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;


public class BatchDeleteManagerAsync<T> extends AbstractAsyncBatchWriter<T> implements BatchDeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final FailedBatchDeleteRequestTransformer<T> transformer;

  public BatchDeleteManagerAsync(final DynamoDBMapper dynamoDBMapper,
                                 final FailedBatchDeleteRequestTransformer<T> transformer,
                                 final BackoffBatchWriteExecutor<T> backoffExecutor,
                                 final ErrorRecoverer<T> errorRecoverer,
                                 final ExecutorService executorService,
                                 final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
    this.transformer = transformer;
  }

  @Override
  public void batchDelete(final List<T> entities) {
    this.execute(entities);
  }

  @Override
  public List<UnprocessedItem<T>> executor(final List<T> entities) {
    try {
      return UnprocessedItem.unprocessedItems(transformer.transform(dynamoDBMapper.batchDelete(entities)));
    } catch (final Exception e) {
      return UnprocessedItem.unprocessedItems(entities);
    }
  }

}
