//package br.com.devblack21.dynamodb.manager4j.factory;
//
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.SaveManagerSync;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
//
//public class SaveClientFactory {
//
//  public static SaveManager createSyncClient(final DynamoDbTable<? extends TableEntity> dynamoDBMapper,
//                                             final BackoffSingleWriteExecutor backoffExecutor,
//                                             final ErrorRecoverer errorRecoverer,
//                                             final RequestInterceptor requestInterceptor) {
//    return new SaveManagerSync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);
//  }
//
//
//  public static SaveManager createSyncClient(final DynamoDbTable<? extends TableEntity> dynamoDBMapper,
//                                             final BackoffSingleWriteExecutor backoffExecutor,
//                                             final RequestInterceptor requestInterceptor) {
//    return createSyncClient(dynamoDBMapper, backoffExecutor, null, requestInterceptor);
//  }
//
//  public static SaveManager createSyncClient(final DynamoDbTable<? extends TableEntity> dynamoDBMapper,
//                                             final ErrorRecoverer errorRecoverer,
//                                             final RequestInterceptor requestInterceptor) {
//    return createSyncClient(dynamoDBMapper, null, errorRecoverer, requestInterceptor);
//  }
//
//  public static SaveManager createSyncClient(final DynamoDbTable<? extends TableEntity> dynamoDBMapper) {
//    return createSyncClient(dynamoDBMapper, null, null, null);
//  }
//
//
////  public static  SaveManager createAsyncClient(final DynamoDbTable dynamoDBMapper,
////                                                     final BackoffSingleWriteExecutor backoffExecutor,
////                                                     final ErrorRecoverer errorRecoverer,
////                                                     final ExecutorService executorService,
////                                                     final RequestInterceptor requestInterceptor) {
////    return new SaveManagerAsync<>(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
////  }
////
////
////  public static  SaveManager createAsyncClient(final DynamoDbTable dynamoDBMapper,
////                                                     final BackoffSingleWriteExecutor backoffExecutor,
////                                                     final ExecutorService executorService,
////                                                     final RequestInterceptor requestInterceptor) {
////    return createAsyncClient(dynamoDBMapper, backoffExecutor, null, executorService, requestInterceptor);
////  }
////
////  public static  SaveManager createAsyncClient(final DynamoDbTable dynamoDBMapper,
////                                                     final ErrorRecoverer errorRecoverer,
////                                                     final ExecutorService executorService,
////                                                     final RequestInterceptor requestInterceptor) {
////    return createAsyncClient(dynamoDBMapper, null, errorRecoverer, executorService, requestInterceptor);
////  }
////
////  public static  SaveManager createAsyncClient(final DynamoDbTable dynamoDBMapper, final ExecutorService executorService) {
////    return createAsyncClient(dynamoDBMapper, null, null, executorService, null);
////  }
//}