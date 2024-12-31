package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceBatchSave;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceBatchSaveAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceBatchSaveSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchSaveClientFactory {

  public static <T> DynamoDbResilienceBatchSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                             final BackoffExecutor backoffExecutor,
                                                             final ErrorRecoverer<T> errorRecoverer,
                                                             final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceBatchSaveSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> DynamoDbResilienceBatchSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                             final BackoffExecutor backoffExecutor,
                                                             final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> DynamoDbResilienceBatchSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                             final ErrorRecoverer<T> errorRecoverer,
                                                             final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> DynamoDbResilienceBatchSave<T> createSyncClient(final DynamoDBMapper dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


  public static <T> DynamoDbResilienceBatchSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                                     final BackoffExecutor backoffExecutor,
                                                                     final ErrorRecoverer<T> errorRecoverer,
                                                                     final ExecutorService executorService,
                                                                     final RequestInterceptor<T> requestInterceptor) {
    return new DynamoDbResilienceBatchSaveAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> DynamoDbResilienceBatchSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                              final BackoffExecutor backoffExecutor,
                                                              final ExecutorService executorService,
                                                              final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> DynamoDbResilienceBatchSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                              final ErrorRecoverer<T> errorRecoverer,
                                                              final ExecutorService executorService,
                                                              final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> DynamoDbResilienceBatchSave<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper, final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
  }
}