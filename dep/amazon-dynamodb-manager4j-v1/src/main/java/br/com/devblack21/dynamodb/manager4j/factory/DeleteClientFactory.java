package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.DeleteManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.sync.DeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class DeleteClientFactory {

  public static <T> DeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                      final BackoffSingleWriteExecutor backoffExecutor,
                                                      final ErrorRecoverer<T> errorRecoverer,
                                                      final RequestInterceptor<T> requestInterceptor) {
    return new DeleteManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> DeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                      final BackoffSingleWriteExecutor backoffExecutor,
                                                      final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> DeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                      final ErrorRecoverer<T> errorRecoverer,
                                                      final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> DeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  public static <T> DeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                       final BackoffSingleWriteExecutor backoffExecutor,
                                                       final ErrorRecoverer<T> errorRecoverer,
                                                       final ExecutorService executorService,
                                                       final RequestInterceptor<T> requestInterceptor) {
    return new DeleteManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> DeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                       final BackoffSingleWriteExecutor backoffExecutor,
                                                       final ExecutorService executorService,
                                                       final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> DeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                       final ErrorRecoverer<T> errorRecoverer,
                                                       final ExecutorService executorService,
                                                       final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> DeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}