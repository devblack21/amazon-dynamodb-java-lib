package br.com.devblack21.dynamodb.manager4j.writer;

public interface BatchDeleteManager<T> {

  void batchDelete(T entity);

}
