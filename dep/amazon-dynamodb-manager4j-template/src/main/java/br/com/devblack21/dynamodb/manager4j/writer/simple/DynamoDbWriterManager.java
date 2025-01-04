package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

import java.util.List;

public interface DynamoDbWriterManager {

  void save(TableEntity entity);

  void batchSave(List<? extends TableEntity> entity);

  void delete(TableEntity entity);

  void batchDelete(List<? extends TableEntity> entity);

}
