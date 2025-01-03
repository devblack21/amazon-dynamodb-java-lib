package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchSaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.ArrayList;
import java.util.List;

public class BatchSaveManagerSync<T> extends AbstractSyncBatchWriter<T> implements BatchSaveManager<T> {

  private final DynamoDBMapper dynamoDBMapper;

  public BatchSaveManagerSync(final DynamoDBMapper dynamoDBMapper,
                              final BackoffBatchWriteExecutor<T> backoffExecutor,
                              final ErrorRecoverer<T> errorRecoverer,
                              final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDBMapper = dynamoDBMapper;
  }


  @Override
  public void batchSave(final List<T> entity) {
    this.execute(entity);
  }

  @Override
  public List<T> executor(final List<T> entity) {
    try {
      return getUnprocessedItens(dynamoDBMapper.batchSave(entity));
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