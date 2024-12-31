package br.com.devblack21.dynamodb.manager4j.writer;

public interface BatchSaveManager<T> {

  void batchSave(T entity);

}
