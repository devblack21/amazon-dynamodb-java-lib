package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.BatchSaveManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.sync.BatchSaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchSaveClientFactory {

  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return new BatchSaveManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final BackoffExecutor backoffExecutor,
                                                          final ErrorRecoverer<T> errorRecoverer,
                                                          final ExecutorService executorService,
                                                          final RequestInterceptor<T> requestInterceptor) {
    return new BatchSaveManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final BackoffExecutor backoffExecutor,
                                                          final ExecutorService executorService,
                                                          final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final ErrorRecoverer<T> errorRecoverer,
                                                          final ExecutorService executorService,
                                                          final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}