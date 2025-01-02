package br.com.devblack21.dynamodb.manager4j.writer.sync;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.SaveManager;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public class SaveManagerSync<T> extends AbstractSyncWriter<T> implements SaveManager<T> {

  private final DynamoDbTable<T> dynamoDbTable;

  public SaveManagerSync(
    final DynamoDbTable<T> dynamoDbTable,
    final BackoffExecutor backoffExecutor,
    final ErrorRecoverer<T> errorRecoverer,
    final RequestInterceptor<T> requestInterceptor) {
    super(backoffExecutor, errorRecoverer, requestInterceptor);
    this.dynamoDbTable = dynamoDbTable;
  }

  @Override
  public void save(final T entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final T entity) {
    this.dynamoDbTable.updateItem(entity);
  }


}
