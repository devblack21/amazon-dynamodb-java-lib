//package br.com.devblack21.dynamodb.manager4j.factory;
//
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.BatchDeleteManagerSync;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//
//public class BatchDeleteClientSyncFactory {
//
//  private static <T> BatchDeleteManager<T> createClient(final DynamoDBMapper dynamoDBMapper,
//                                                        final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                        final BackoffBatchWriteExecutor<T> backoffExecutor,
//                                                        final ErrorRecoverer<T> errorRecoverer,
//                                                        final RequestInterceptor<T> requestInterceptor) {
//    return new BatchDeleteManagerSync<>(dynamoDBMapper,
//      transformer,
//      backoffExecutor,
//      errorRecoverer,
//      requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                           final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                           final BackoffBatchWriteExecutor<T> backoffExecutor,
//                                                           final ErrorRecoverer<T> errorRecoverer,
//                                                           final RequestInterceptor<T> requestInterceptor) {
//    return createClient(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                           final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                           final BackoffBatchWriteExecutor<T> backoffExecutor,
//                                                           final RequestInterceptor<T> requestInterceptor) {
//    return createSyncClient(dynamoDBMapper, transformer, backoffExecutor, null, requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                           final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                           final ErrorRecoverer<T> errorRecoverer,
//                                                           final RequestInterceptor<T> requestInterceptor) {
//    return createSyncClient(dynamoDBMapper, transformer, null, errorRecoverer, requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createSyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                           final FailedBatchDeleteRequestTransformer<T> transformer) {
//    return createSyncClient(dynamoDBMapper, transformer, null, null, null);
//  }
//
//}
