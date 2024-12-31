package br.com.devblack21.dynamodb.manager.interceptor;

public interface RetryInterceptor {

  void logRetryStart();

  void logRetryEnd();


}
