package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchDeleteManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.BatchDeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchDeleteClientFactory {

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                           final FailedBatchDeleteRequestTransformer<T> transformer,
                                                           final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                           final ErrorRecoverer<T> errorRecoverer,
                                                           final RequestInterceptor<T> requestInterceptor) {
    return new BatchDeleteManagerSync<>(dynamoDBMapper,
      transformer,
      backoffExecutor,
      errorRecoverer,
      requestInterceptor);
  }


  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                           final FailedBatchDeleteRequestTransformer<T> transformer,
                                                           final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                           final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper,
      transformer,
      backoffExecutor,
      null,
      requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
                                                           final FailedBatchDeleteRequestTransformer<T> transformer,
                                                           final ErrorRecoverer<T> errorRecoverer,
                                                           final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper,
      transformer,
      null,
      errorRecoverer,
      requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper, final FailedBatchDeleteRequestTransformer<T> transformer) {
    return createSyncClient(dynamoDBMapper,
      transformer,
      null,
      null,
      null);
  }


  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
                                                            final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                            final ErrorRecoverer<T> errorRecoverer,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<T> requestInterceptor) {
    return new BatchDeleteManagerAsync<>(dynamoDBMapper,
      transformer,
      backoffExecutor,
      errorRecoverer,
      executorService,
      requestInterceptor);
  }


  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
                                                            final BackoffBatchWriteExecutor<T> backoffExecutor,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper,
      transformer,
      backoffExecutor,
      null,
      executorService,
      requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
                                                            final ErrorRecoverer<T> errorRecoverer,
                                                            final ExecutorService executorService,
                                                            final RequestInterceptor<T> requestInterceptor) {
    return createAsyncClient(dynamoDBMapper,
      transformer,
      null,
      errorRecoverer,
      executorService,
      requestInterceptor);
  }

  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
                                                            final ExecutorService executorService) {
    return createAsyncClient(dynamoDBMapper,
      transformer,
      null,
      null,
      executorService,
      null);
  }
}