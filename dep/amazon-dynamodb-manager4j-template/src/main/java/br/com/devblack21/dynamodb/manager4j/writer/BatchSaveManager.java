package br.com.devblack21.dynamodb.manager4j.writer;

import java.util.List;

public interface BatchSaveManager<T> {

  void batchSave(List<T> entities);

}
