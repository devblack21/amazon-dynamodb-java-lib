package br.com.devblack21.dynamodb.manager4j.writer;

import java.util.List;

public interface DynamoDbWriterManager<T> {

  void save(T entity);

  void batchSave(List<T> entity);

  void delete(T entity);

  void batchDelete(List<T>  entity);

}
