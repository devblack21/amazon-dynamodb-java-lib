package br.com.devblack21.dynamodb.manager4j.resilience;

public interface ErrorRecoverer<T> {

  void recover(T object);

}
