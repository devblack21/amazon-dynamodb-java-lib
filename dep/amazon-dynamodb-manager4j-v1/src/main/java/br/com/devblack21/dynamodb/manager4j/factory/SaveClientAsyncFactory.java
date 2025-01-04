package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.SaveManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class SaveClientAsyncFactory {

  public static SaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                         final ExecutorService executorService,
                                         final RequestInterceptor requestInterceptor) {

    return new SaveManagerAsync.Builder(dynamoDBMapper)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .executorService(executorService)
      .requestInterceptor(requestInterceptor)
      .build();
  }

  public static SaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final ExecutorService executorService,
                                         final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, null, executorService, requestInterceptor);
  }

  public static SaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final ExecutorService executorService) {
    return createClient(dynamoDBMapper, executorService, null);
  }

}
