package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

import java.util.List;

public interface BatchSaveManager {

  void batchSave(List<? extends TableEntity> entities);

}
