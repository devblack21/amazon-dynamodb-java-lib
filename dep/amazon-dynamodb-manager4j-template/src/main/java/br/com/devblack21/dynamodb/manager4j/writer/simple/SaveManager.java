package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

public interface SaveManager {

  void save(TableEntity entity);

}
