package br.com.devblack21.dynamodb.manager.writer;

public interface BatchSaveManager<T> {

  void batchSave(T entity);

}
