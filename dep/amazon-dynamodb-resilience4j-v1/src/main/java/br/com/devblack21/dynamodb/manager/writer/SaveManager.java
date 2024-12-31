package br.com.devblack21.dynamodb.manager.writer;

public interface SaveManager<T> {

  void save(T entity);

}
