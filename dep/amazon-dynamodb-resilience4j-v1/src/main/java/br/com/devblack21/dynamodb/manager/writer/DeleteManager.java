package br.com.devblack21.dynamodb.manager.writer;

public interface DeleteManager<T> {

  void delete(T entity);

}
