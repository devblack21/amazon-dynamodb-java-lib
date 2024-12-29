package br.com.devblack21.dynamodb.resilience.writer;

public interface DynamoDbResilienceBatchSave<T> {

  void batchSave(T entity);

}
