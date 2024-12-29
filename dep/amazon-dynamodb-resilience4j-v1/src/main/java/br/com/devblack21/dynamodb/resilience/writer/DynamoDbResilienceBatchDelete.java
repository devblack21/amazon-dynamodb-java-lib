package br.com.devblack21.dynamodb.resilience.writer;

public interface DynamoDbResilienceBatchDelete<T> {

  void batchDelete(T entity);

}
