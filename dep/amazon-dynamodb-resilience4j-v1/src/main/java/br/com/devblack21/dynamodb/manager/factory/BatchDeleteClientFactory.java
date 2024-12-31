package br.com.devblack21.dynamodb.manager.factory;

import br.com.devblack21.dynamodb.manager.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager.writer.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager.writer.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager.writer.async.BatchDeleteManagerAsync;
import br.com.devblack21.dynamodb.manager.writer.sync.BatchDeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchDeleteClientFactory {

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return new BatchDeleteManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final BackoffExecutor backoffExecutor,
                                                            final ErrorRecoverer<T> errorRecoverer,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<T> requestInterceptor) {
    return new BatchDeleteManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}