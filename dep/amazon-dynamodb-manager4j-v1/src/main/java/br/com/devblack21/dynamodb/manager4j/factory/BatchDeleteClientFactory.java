package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.BatchDeleteManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.sync.BatchDeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BatchDeleteClientFactory {

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                           final BackoffExecutor backoffExecutor,
                                                           final ErrorRecoverer<List<T>> errorRecoverer,
                                                           final RequestInterceptor<List<T>> requestInterceptor) {
    return new BatchDeleteManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                           final BackoffExecutor backoffExecutor,
                                                           final RequestInterceptor<List<T>> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                           final ErrorRecoverer<List<T>> errorRecoverer,
                                                           final RequestInterceptor<List<T>> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final BackoffExecutor backoffExecutor,
                                                            final ErrorRecoverer<List<T>> errorRecoverer,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<List<T>> requestInterceptor) {
    return new BatchDeleteManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final BackoffExecutor backoffExecutor,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<List<T>> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final ErrorRecoverer<List<T>> errorRecoverer,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<List<T>> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}