package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;

public class BatchDeleteManagerSync<T> extends AbstractSyncBatchWriter<T> implements BatchDeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;
  private final FailedBatchDeleteRequestTransformer<T> transformer;

  public BatchDeleteManagerSync(final DynamoDBMapper dynamoDBMapper,
                                final FailedBatchDeleteRequestTransformer<T> transformer,
                                final BackoffBatchWriteExecutor<T> backoffExecutor,
                                final ErrorRecoverer<T> errorRecoverer,
                                final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
    this.transformer = transformer;
  }


  @Override
  public void batchDelete(final List<T> entity) {
    this.execute(entity);
  }

  @Override
  public List<UnprocessedItem<T>> executor(final List<T> entity) {
    try {
      return UnprocessedItem.unprocessedItems(transformer.transform(dynamoDBMapper.batchDelete(entity)));
    } catch (final Exception e) {
      return UnprocessedItem.unprocessedItems(entity);
    }
  }

}
