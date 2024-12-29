package br.com.devblack21.dynamodb.resilience.writer;

public interface DynamoDbResilienceDelete<T> {

  void delete(T entity);

}
