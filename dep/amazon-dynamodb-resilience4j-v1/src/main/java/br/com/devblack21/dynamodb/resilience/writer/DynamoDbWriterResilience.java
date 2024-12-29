package br.com.devblack21.dynamodb.resilience.writer;

public interface DynamoDbWriterResilience<T> {

  void save(T entity);

  void batchSave(T entity);

  void delete(T entity);

  void batchDelete(T entity);

}
