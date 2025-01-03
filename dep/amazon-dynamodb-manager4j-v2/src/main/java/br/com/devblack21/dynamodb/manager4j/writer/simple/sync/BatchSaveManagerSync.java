//package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;
//
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
//
//import java.util.List;
//
//public class BatchSaveManagerSync extends AbstractSyncWriter implements BatchSaveManager {
//
//  private final DynamoDbEnhancedClient enhancedClient;
//  private final DynamoDbTable<T> dynamoDbTable;
//
//  public BatchSaveManagerSync(
//    final DynamoDbEnhancedClient enhancedClient,
//    final DynamoDbTable<T> dynamoDbTable,
//    final BackoffSingleWriteExecutor backoffExecutor,
//    final ErrorRecoverer<List<T>> errorRecoverer,
//    final RequestInterceptor<List<T>> requestInterceptor) {
//    super(backoffExecutor, errorRecoverer, requestInterceptor);
//    this.enhancedClient = enhancedClient;
//    this.dynamoDbTable = dynamoDbTable;
//  }
//
//  @Override
//  public void batchSave(final List<T> entity) {
//    this.execute(entity);
//  }
//
//  @Override
//  public void executor(final List<T> entities) {
////    WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(dynamoDbTable);
////    for (T item : entities) {
////      writeBatchBuilder.addPutItem(item);
////    }
////    WriteBatch writeBatch = writeBatchBuilder.build();
////
////    var response = enhancedClient.batchWriteItem(r -> r.addWriteBatch(writeBatch));
////    List<T> unprocesseded = response.unprocessedPutItemsForTable(dynamoDbTable);
//  }
//
//
//}
