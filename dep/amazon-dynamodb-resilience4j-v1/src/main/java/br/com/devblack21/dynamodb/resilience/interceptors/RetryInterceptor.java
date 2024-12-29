package br.com.devblack21.dynamodb.resilience.interceptors;

public interface RetryInterceptor {

  void logRetryStart();

  void logRetryEnd();


}
