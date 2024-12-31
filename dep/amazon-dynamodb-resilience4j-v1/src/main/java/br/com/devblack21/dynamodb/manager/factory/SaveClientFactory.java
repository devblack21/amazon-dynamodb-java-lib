package br.com.devblack21.dynamodb.manager.factory;

import br.com.devblack21.dynamodb.manager.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager.writer.SaveManager;
import br.com.devblack21.dynamodb.manager.writer.async.SaveManagerAsync;
import br.com.devblack21.dynamodb.manager.writer.sync.SaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class SaveClientFactory {

  public static <T> SaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                    final BackoffExecutor backoffExecutor,
                                                    final ErrorRecoverer<T> errorRecoverer,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return new SaveManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> SaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                    final BackoffExecutor backoffExecutor,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> SaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                    final ErrorRecoverer<T> errorRecoverer,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> SaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  public static <T> SaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                     final BackoffExecutor backoffExecutor,
                                                     final ErrorRecoverer<T> errorRecoverer,
                                                     final ExecutorService executorService,
                                                     final RequestInterceptor<T> requestInterceptor) {
    return new SaveManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> SaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                     final BackoffExecutor backoffExecutor,
                                                     final ExecutorService executorService,
                                                     final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> SaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                     final ErrorRecoverer<T> errorRecoverer,
                                                     final ExecutorService executorService,
                                                     final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> SaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}