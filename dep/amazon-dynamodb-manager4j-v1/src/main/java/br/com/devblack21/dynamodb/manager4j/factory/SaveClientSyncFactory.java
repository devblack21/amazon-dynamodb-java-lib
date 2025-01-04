package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.SaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class SaveClientSyncFactory {

  public static SaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                         final RequestInterceptor requestInterceptor) {

    return new SaveManagerSync.Builder(dynamoDBMapper)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .requestInterceptor(requestInterceptor)
      .build();
  }

  public static SaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, null, requestInterceptor);
  }

  public static SaveManager createClient(final DynamoDBMapper dynamoDBMapper) {
    return createClient(dynamoDBMapper, null);
  }

}
