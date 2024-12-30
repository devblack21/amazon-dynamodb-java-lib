package br.com.devblack21.dynamodb.resilience.factorys;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptors.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceSave;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceSaveAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceSaveSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class SaveClientFactory {

  static <T> DynamoDbResilienceSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final ErrorRecoverer<T> errorRecoverer,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceSaveSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  static <T> DynamoDbResilienceSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                        final BackoffExecutor backoffExecutor,
                                                        final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
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
                                                         final BackoffExecutor backoffExecutor,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceSaveAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  static <T> DynamoDbResilienceSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final BackoffExecutor backoffExecutor,
                                                         final ExecutorService executorService,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
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