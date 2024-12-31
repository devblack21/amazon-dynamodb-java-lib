package br.com.devblack21.dynamodb.manager.writer;

public interface BatchDeleteManager<T> {

  void batchDelete(T entity);

}
