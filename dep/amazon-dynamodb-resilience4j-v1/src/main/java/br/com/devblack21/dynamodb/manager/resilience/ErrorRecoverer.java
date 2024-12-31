package br.com.devblack21.dynamodb.manager.resilience;

public interface ErrorRecoverer<T> {

  void recover(T object);

}
