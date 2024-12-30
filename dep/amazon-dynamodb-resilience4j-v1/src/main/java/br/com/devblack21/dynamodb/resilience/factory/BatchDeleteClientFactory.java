package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceBatchDelete;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceBatchDeleteAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceBatchDeleteSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchDeleteClientFactory {

  static <T> DynamoDbResilienceBatchDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceBatchDeleteSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  static <T> DynamoDbResilienceBatchDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  static <T> DynamoDbResilienceBatchDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  static <T> DynamoDbResilienceBatchDelete<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  static <T> DynamoDbResilienceBatchDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceBatchDeleteAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  static <T> DynamoDbResilienceBatchDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  static <T> DynamoDbResilienceBatchDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  static <T> DynamoDbResilienceBatchDelete<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}