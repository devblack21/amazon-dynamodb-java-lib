package br.com.devblack21.dynamodb.manager4j.writer.simple;

import java.util.List;

public interface BatchDeleteManager<T> {

  void batchDelete(List<T> entities);

}
