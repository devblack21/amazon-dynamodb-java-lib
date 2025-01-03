package br.com.devblack21.dynamodb.manager4j.writer;

import java.util.List;

public interface BatchWriterExecution<T> {

  List<T> write(List<T> entities);

}
