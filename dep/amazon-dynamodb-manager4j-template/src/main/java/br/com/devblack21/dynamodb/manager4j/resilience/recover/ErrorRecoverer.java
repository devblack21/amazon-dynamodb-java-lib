package br.com.devblack21.dynamodb.manager4j.resilience.recover;

import java.util.List;

public interface ErrorRecoverer<T> {

  void recover(T object);

  void recover(List<T> object);


}
