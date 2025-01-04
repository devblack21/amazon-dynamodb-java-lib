package br.com.devblack21.dynamodb.manager4j.interceptor;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

import java.util.List;

public interface RequestInterceptor {

  void logSuccess(TableEntity entity);

  void logSuccess(List<? extends TableEntity> entity);

  void logError(TableEntity entity, final Throwable throwable);

  void logError(List<? extends TableEntity> entity, final Throwable throwable);

}
