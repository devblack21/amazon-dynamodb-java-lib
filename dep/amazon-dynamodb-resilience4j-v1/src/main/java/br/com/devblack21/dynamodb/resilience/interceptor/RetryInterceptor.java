package br.com.devblack21.dynamodb.resilience.interceptor;

public interface RetryInterceptor {

  void logRetryStart();

  void logRetryEnd();


}
