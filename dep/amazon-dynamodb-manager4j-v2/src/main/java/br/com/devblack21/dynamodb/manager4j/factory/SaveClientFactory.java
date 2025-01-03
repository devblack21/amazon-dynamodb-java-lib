package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.SaveManagerSync;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public class SaveClientFactory {

  public static <T> SaveManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper,
                                                    final BackoffSingleWriteExecutor backoffExecutor,
                                                    final ErrorRecoverer<T> errorRecoverer,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return new SaveManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> SaveManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper,
                                                    final BackoffSingleWriteExecutor backoffExecutor,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> SaveManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper,
                                                    final ErrorRecoverer<T> errorRecoverer,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> SaveManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


//  public static <T> SaveManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper,
//                                                     final BackoffSingleWriteExecutor backoffExecutor,
//                                                     final ErrorRecoverer<T> errorRecoverer,
//                                                     final ExecutorService executorService,
//                                                     final RequestInterceptor<T> requestInterceptor) {
//    return new SaveManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
//  }
//
//
//  public static <T> SaveManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper,
//                                                     final BackoffSingleWriteExecutor backoffExecutor,
//                                                     final ExecutorService executorService,
//                                                     final RequestInterceptor<T> requestInterceptor) {
//    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
//  }
//
//  public static <T> SaveManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper,
//                                                     final ErrorRecoverer<T> errorRecoverer,
//                                                     final ExecutorService executorService,
//                                                     final RequestInterceptor<T> requestInterceptor) {
//    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
//  }
//
//  public static <T> SaveManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper, final ExecutorService executorService) {
//    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
//  }
}