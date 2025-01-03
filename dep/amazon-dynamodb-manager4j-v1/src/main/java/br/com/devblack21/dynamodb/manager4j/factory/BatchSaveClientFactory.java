package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchSaveManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.BatchSaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchSaveClientFactory {

  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final FailedBatchPutRequestTransformer<T> transformer,
                                                         final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return new BatchSaveManagerSync<>(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final FailedBatchPutRequestTransformer<T> transformer,
                                                         final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, transformer, backoffExecutor, null, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final FailedBatchPutRequestTransformer<T> transformer,
                                                         final ErrorRecoverer<T> errorRecoverer,
                                                         final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, transformer, null, errorRecoverer, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                         final FailedBatchPutRequestTransformer<T> transformer) {
    return createSyncClient(dynamoDBMapper, transformer, null, null, null);
  }


  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final FailedBatchPutRequestTransformer<T> transformer,
                                                          final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                          final ErrorRecoverer<T> errorRecoverer,
                                                          final ExecutorService executorService,
                                                          final RequestInterceptor<T> requestInterceptor) {
    return new BatchSaveManagerAsync<>(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
  }


  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final FailedBatchPutRequestTransformer<T> transformer,
                                                          final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                          final ExecutorService executorService,
                                                          final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, transformer, backoffExecutor, null, executorService, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final FailedBatchPutRequestTransformer<T> transformer,
                                                          final ErrorRecoverer<T> errorRecoverer,
                                                          final ExecutorService executorService,
                                                          final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper, transformer, null, errorRecoverer, executorService, requestInterceptor);
  }

  public static <T> BatchSaveManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                          final FailedBatchPutRequestTransformer<T> transformer,
                                                          final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper, transformer, null, null, executorService, null);
  }
}