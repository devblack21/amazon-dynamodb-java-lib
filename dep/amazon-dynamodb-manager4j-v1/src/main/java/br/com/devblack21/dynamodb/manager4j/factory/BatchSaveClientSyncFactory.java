package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.WriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.BatchSaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class BatchSaveClientSyncFactory {

  public static BatchSaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                  final FailedBatchPutRequestTransformer<? extends TableEntity> failedTransformer,
                                                  final WriteRetryPolicyConfiguration retryPolicyConfiguration,
                                                  final RequestInterceptor requestInterceptor) {
    return new BatchSaveManagerSync.Builder(dynamoDBMapper, failedTransformer)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .requestInterceptor(requestInterceptor)
      .build();
  }


  public static BatchSaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                  final FailedBatchPutRequestTransformer<? extends TableEntity> transformer,
                                                  final WriteRetryPolicyConfiguration retryPolicyConfiguration) {
    return createClient(dynamoDBMapper, transformer, retryPolicyConfiguration, null);
  }

  public static BatchSaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                                  final FailedBatchPutRequestTransformer<? extends TableEntity> transformer) {
    return createClient(dynamoDBMapper, transformer, null);
  }


}