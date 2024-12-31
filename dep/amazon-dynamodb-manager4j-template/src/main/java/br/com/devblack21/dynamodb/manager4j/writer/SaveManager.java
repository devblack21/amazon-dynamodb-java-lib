package br.com.devblack21.dynamodb.manager4j.writer;

public interface SaveManager<T> {

  void save(T entity);

}
