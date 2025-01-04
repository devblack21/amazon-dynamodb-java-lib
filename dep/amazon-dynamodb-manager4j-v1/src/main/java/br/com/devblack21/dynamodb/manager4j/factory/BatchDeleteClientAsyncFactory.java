package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchDeleteClientAsyncFactory {

  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedTransformer,
                                                final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                                final ExecutorService executorService,
                                                final RequestInterceptor requestInterceptor) {
    return new BatchDeleteManagerAsync.Builder(dynamoDBMapper, failedTransformer)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .executorService(executorService)
      .requestInterceptor(requestInterceptor)
      .build();
  }

  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedTransformer,
                                                final ExecutorService executorService,
                                                final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, failedTransformer, null, executorService, requestInterceptor);
  }

  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedTransformer,
                                                final ExecutorService executorService) {
    return createClient(dynamoDBMapper, failedTransformer, executorService, null);
  }

}
