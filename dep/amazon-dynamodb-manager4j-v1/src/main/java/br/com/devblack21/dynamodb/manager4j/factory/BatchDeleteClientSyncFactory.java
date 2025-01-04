package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class BatchDeleteClientSyncFactory {

  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedTransformer,
                                                final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                                final RequestInterceptor requestInterceptor) {
    return new BatchDeleteManagerSync.Builder(dynamoDBMapper, failedTransformer)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .requestInterceptor(requestInterceptor)
      .build();
  }


  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> transformer,
                                                final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration) {
    return createClient(dynamoDBMapper, transformer, retryPolicyConfiguration, null);
  }


  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> transformer) {
    return createClient(dynamoDBMapper, transformer, null, null);
  }

  public static BatchDeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                final FailedBatchDeleteRequestTransformer<? extends TableEntity> transformer,
                                                final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, transformer, null, requestInterceptor);

  }
}
