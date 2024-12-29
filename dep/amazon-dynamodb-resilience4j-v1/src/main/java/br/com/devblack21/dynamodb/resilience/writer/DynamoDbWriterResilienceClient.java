package br.com.devblack21.dynamodb.resilience.writer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DynamoDbWriterResilienceClient<T> implements DynamoDbWriterResilience<T> {

  private final DynamoDbResilienceSave<T> dbResilienceSave;
  private final DynamoDbResilienceBatchSave<T> dynamoDbResilienceBatchSave;
  private final DynamoDbResilienceDelete<T> dynamoDbResilienceDelete;
  private final DynamoDbResilienceBatchDelete<T> dynamoDbResilienceBatchDelete;

  @Override
  public void save(final T entity) {
    this.dbResilienceSave.save(entity);
  }

  @Override
  public void batchSave(final T entity) {
    this.dynamoDbResilienceBatchSave.batchSave(entity);
  }

  @Override
  public void delete(final T entity) {
    this.dynamoDbResilienceDelete.delete(entity);
  }

  @Override
  public void batchDelete(final T entity) {
    this.dynamoDbResilienceBatchDelete.batchDelete(entity);
  }

}
