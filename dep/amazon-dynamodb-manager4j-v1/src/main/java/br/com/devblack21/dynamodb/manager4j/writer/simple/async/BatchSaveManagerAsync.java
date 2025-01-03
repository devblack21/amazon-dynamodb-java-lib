package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;


public class BatchSaveManagerAsync<T> extends AbstractAsyncBatchWriter<T> implements BatchSaveManager<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final FailedBatchPutRequestTransformer<T> transformer;

  public BatchSaveManagerAsync(final DynamoDBMapper dynamoDBMapper,
                               final FailedBatchPutRequestTransformer<T> transformer,
                               final BackoffBatchWriteExecutor<T> backoffExecutor,
                               final ErrorRecoverer<T> errorRecoverer,
                               final ExecutorService executorService,
                               final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
    this.transformer = transformer;
  }

  @Override
  public void batchSave(final List<T> entities) {
    this.execute(entities);
  }

  @Override
  protected List<UnprocessedItem<T>> executor(final List<T> entities) {
    try {
      return UnprocessedItem.unprocessedItems(transformer.transform(dynamoDBMapper.batchSave(entities)));
    } catch (final Exception e) {
      return UnprocessedItem.unprocessedItems(entities);
    }
  }

}
