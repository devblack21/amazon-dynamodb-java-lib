package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.DeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DeleteClientSyncFactory {

  public static DeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                         final RequestInterceptor requestInterceptor) {

    return new DeleteManagerSync.Builder(dynamoDBMapper)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .requestInterceptor(requestInterceptor)
      .build();
  }

  public static DeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, null, requestInterceptor);
  }

  public static DeleteManager createClient(final DynamoDBMapper dynamoDBMapper) {
    return createClient(dynamoDBMapper, null);
  }

}
