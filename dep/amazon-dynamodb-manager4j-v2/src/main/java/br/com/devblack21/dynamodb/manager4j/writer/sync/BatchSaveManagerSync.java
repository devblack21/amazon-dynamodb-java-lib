package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.SaveManager;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;

import java.util.List;

public class BatchSaveManagerSync<T> extends AbstractSyncWriter<List<T>> implements BatchSaveManager<T> {

  private final DynamoDbEnhancedClient enhancedClient;
  private final DynamoDbTable<T> dynamoDbTable;

  public BatchSaveManagerSync(
    final DynamoDbEnhancedClient enhancedClient,
    final DynamoDbTable<T> dynamoDbTable,
    final BackoffExecutor backoffExecutor,
    final ErrorRecoverer<List<T>> errorRecoverer,
    final RequestInterceptor<List<T>> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.enhancedClient = enhancedClient;
    this.dynamoDbTable = dynamoDbTable;
  }

  @Override
  public void batchSave(final List<T> entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final List<T> entities) {
    WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(dynamoDbTable);
    for (T item : entities) {
      writeBatchBuilder.addPutItem(item);
    }
    WriteBatch writeBatch = writeBatchBuilder.build();

    var response = enhancedClient.batchWriteItem(r -> r.addWriteBatch(writeBatch));
    List<T> unprocesseded = response.unprocessedPutItemsForTable(dynamoDbTable);
  }


}
