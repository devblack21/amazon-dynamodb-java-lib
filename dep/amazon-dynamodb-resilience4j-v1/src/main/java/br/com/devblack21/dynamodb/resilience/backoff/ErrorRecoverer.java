package br.com.devblack21.dynamodb.resilience.backoff;

public interface ErrorRecoverer<T> {

  void recover(T object);

}
