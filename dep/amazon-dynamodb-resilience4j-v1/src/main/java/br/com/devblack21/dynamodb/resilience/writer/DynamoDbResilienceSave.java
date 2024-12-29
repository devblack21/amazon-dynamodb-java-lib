package br.com.devblack21.dynamodb.resilience.writer;

public interface DynamoDbResilienceSave<T> {

  void save(T entity);

}
