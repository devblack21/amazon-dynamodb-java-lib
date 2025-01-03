package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.ArrayList;
import java.util.List;

public class BatchDeleteManagerSync<T> extends AbstractSyncBatchWriter<T> implements BatchDeleteManager<T> {

  private final DynamoDBMapper dynamoDBMapper;

  public BatchDeleteManagerSync(final DynamoDBMapper dynamoDBMapper,
                                final BackoffBatchWriteExecutor<T> backoffExecutor,
                                final ErrorRecoverer<T> errorRecoverer,
                                final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
  }


  @Override
  public void batchDelete(final List<T> entity) {
    this.execute(entity);
  }

  @Override
  public List<T> executor(final List<T> entity) {
    try {
      return getUnprocessedItens(dynamoDBMapper.batchDelete(entity));
    } catch (final Exception e) {
      return entity;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> List<T> getUnprocessedItens(final List<DynamoDBMapper.FailedBatch> failedBatches) {
    final List<Object> failedItems = new ArrayList<>();
    for (final DynamoDBMapper.FailedBatch failedBatch : failedBatches) {
      failedBatch.getUnprocessedItems().values()
        .forEach(writeRequests -> writeRequests.forEach(writeRequest -> {
          if (writeRequest.getPutRequest() != null) {
            failedItems.add(writeRequest.getPutRequest().getItem());
          }
        }));
    }
    return (List<T>) failedItems;
  }


}
