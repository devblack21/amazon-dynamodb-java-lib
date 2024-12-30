package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceDelete;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceDeleteAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceDeleteSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class DeleteClientFactory {

  static <T> DynamoDbResilienceDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceDeleteSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  static <T> DynamoDbResilienceDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  static <T> DynamoDbResilienceDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  static <T> DynamoDbResilienceDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  static <T> DynamoDbResilienceDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceDeleteAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  static <T> DynamoDbResilienceDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  static <T> DynamoDbResilienceDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  static <T> DynamoDbResilienceDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}