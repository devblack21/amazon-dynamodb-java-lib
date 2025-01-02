package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.sync.DeleteManagerSync;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public class DeleteClientFactory {

  public static <T> DeleteManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper,
                                                    final BackoffExecutor backoffExecutor,
                                                    final ErrorRecoverer<T> errorRecoverer,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return new DeleteManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
  }


  public static <T> DeleteManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper,
                                                    final BackoffExecutor backoffExecutor,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
  }

  public static <T> DeleteManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper,
                                                    final ErrorRecoverer<T> errorRecoverer,
                                                    final RequestInterceptor<T> requestInterceptor) {
    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
  }

  public static <T> DeleteManager<T> createSyncClient(final DynamoDbTable<T> dynamoDBMapper) {
    return createSyncClient(dynamoDBMapper, null, null, null);
  }


//  public static <T> DeleteManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper,
//                                                     final BackoffExecutor backoffExecutor,
//                                                     final ErrorRecoverer<T> errorRecoverer,
//                                                     final ExecutorService executorService,
//                                                     final RequestInterceptor<T> requestInterceptor) {
//    return new DeleteManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
//  }
//
//
//  public static <T> DeleteManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper,
//                                                     final BackoffExecutor backoffExecutor,
//                                                     final ExecutorService executorService,
//                                                     final RequestInterceptor<T> requestInterceptor) {
//    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
//  }
//
//  public static <T> DeleteManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper,
//                                                     final ErrorRecoverer<T> errorRecoverer,
//                                                     final ExecutorService executorService,
//                                                     final RequestInterceptor<T> requestInterceptor) {
//    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
//  }
//
//  public static <T> DeleteManager<T> createAsyncClient(final DynamoDbTable<T> dynamoDBMapper, final ExecutorService executorService) {
//    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
//  }
}