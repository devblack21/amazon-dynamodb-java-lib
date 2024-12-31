package br.com.devblack21.dynamodb.manager4j.interceptor;

public interface RetryInterceptor {

  void logRetryStart();

  void logRetryEnd();


}
