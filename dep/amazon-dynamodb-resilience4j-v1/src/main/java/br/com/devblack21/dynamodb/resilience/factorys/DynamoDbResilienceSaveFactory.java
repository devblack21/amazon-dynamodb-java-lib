package br.com.devblack21.dynamodb.resilience.factorys;

import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceSave;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceSaveAsync;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceSaveSync;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.backoff.RetryableExecutor;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class DynamoDbResilienceSaveFactory {

  static <T> DynamoDbResilienceSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final RetryableExecutor retryableExecutor,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceSaveSync<>(dynamoDBMapper, retryableExecutor, errorRecoverer, requestInterceptor);
  }


  static <T> DynamoDbResilienceSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final RetryableExecutor retryableExecutor,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, retryableExecutor, null, requestInterceptor);
  }

  static <T> DynamoDbResilienceSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  static <T> DynamoDbResilienceSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  static <T> DynamoDbResilienceSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final RetryableExecutor retryableExecutor,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceSaveAsync<>(dynamoDBMapper, retryableExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  static <T> DynamoDbResilienceSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final RetryableExecutor retryableExecutor,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, retryableExecutor, null, executorService, requestInterceptor);
  }

  static <T> DynamoDbResilienceSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  static <T> DynamoDbResilienceSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}