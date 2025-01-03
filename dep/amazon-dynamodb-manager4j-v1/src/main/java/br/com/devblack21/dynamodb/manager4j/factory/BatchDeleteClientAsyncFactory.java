//package br.com.devblack21.dynamodb.manager4j.factory;
//
//import br.com.devblack21.dynamodb.manager4j.configuration.WriteRetryPolicyConfiguration;
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchDeleteManagerAsync;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchSaveManagerAsync;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//
//import java.util.concurrent.ExecutorService;
//
//public class BatchDeleteClientAsyncFactory {
//
//  private static <T> BatchDeleteManager<T> createClient(final DynamoDBMapper dynamoDBMapper,
//                                                        final FailedBatchDeleteRequestTransformer<T> f,
//                                                        final WriteRetryPolicyConfiguration retryPolicyConfiguration,
//                                                        final ExecutorService executorService,
//                                                        final RequestInterceptor requestInterceptor) {
//    return new BatchSaveManagerAsync.Builder(dynamoDBMapper, transformer)
//      .retryPolicyConfiguration(WriteRetryPolicyConfiguration.builder()
//        .backoffBatchWriteExecutor(mockBackoffExecutor)
//        .errorRecoverer(mockErrorRecoverer)
//        .build())
//      .executorService(executorService)
//      .requestInterceptor(mockRequestInterceptor)
//      .build();
//
//  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                            final BackoffBatchWriteExecutor<T> backoffExecutor,
//                                                            final ErrorRecoverer<T> errorRecoverer,
//                                                            final ExecutorService executorService,
//                                                            final RequestInterceptor<T> requestInterceptor) {
//    return createClient(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                            final BackoffBatchWriteExecutor<T> backoffExecutor,
//                                                            final ExecutorService executorService,
//                                                            final RequestInterceptor<T> requestInterceptor) {
//    return createAsyncClient(dynamoDBMapper, transformer, backoffExecutor, null, executorService, requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                            final ErrorRecoverer<T> errorRecoverer,
//                                                            final ExecutorService executorService,
//                                                            final RequestInterceptor<T> requestInterceptor) {
//    return createAsyncClient(dynamoDBMapper, transformer, null, errorRecoverer, executorService, requestInterceptor);
//  }
//
//  public static <T> BatchDeleteManager<T> createAsyncClient(final DynamoDBMapper dynamoDBMapper,
//                                                            final FailedBatchDeleteRequestTransformer<T> transformer,
//                                                            final ExecutorService executorService) {
//    return createAsyncClient(dynamoDBMapper, transformer, null, null, executorService, null);
//  }
//
//}
