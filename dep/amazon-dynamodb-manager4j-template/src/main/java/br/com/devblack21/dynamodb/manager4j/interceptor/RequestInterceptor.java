package br.com.devblack21.dynamodb.manager4j.interceptor;

import java.util.List;

public interface RequestInterceptor<T> {

  void logSuccess(T entity);

  void logSuccess(List<T> entity);

  void logError(T entity, final Throwable throwable);

  void logError(List<T> entity, final Throwable throwable);


}
