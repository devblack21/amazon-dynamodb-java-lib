//package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;
//
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.AbstractSyncWriter;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
//
//public class DeleteManagerSync<T> extends AbstractSyncWriter<T> implements DeleteManager<T> {
//
//  private final DynamoDbTable<T> dynamoDbTable;
//
//  public DeleteManagerSync(
//    final DynamoDbTable<T> dynamoDbTable,
//    final BackoffSingleWriteExecutor backoffExecutor,
//    final ErrorRecoverer<T> errorRecoverer,
//    final RequestInterceptor<T> requestInterceptor) {
//    super(backoffExecutor, errorRecoverer, requestInterceptor);
//    this.dynamoDbTable = dynamoDbTable;
//  }
//
//  @Override
//  public void delete(final T entity) {
//    this.execute(entity);
//  }
//
//  @Override
//  public void executor(final T entity) {
//    this.dynamoDbTable.deleteItem(entity);
//  }
//
//
//}