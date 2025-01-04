package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class DeleteClientAsyncFactory {

  public static DeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                           final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                           final ExecutorService executorService,
                                           final RequestInterceptor requestInterceptor) {

    return new DeleteManagerAsync.Builder(dynamoDBMapper)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .executorService(executorService)
      .requestInterceptor(requestInterceptor)
      .build();
  }

  public static DeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final ExecutorService executorService,
                                         final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, null, executorService, requestInterceptor);
  }

  public static DeleteManager createClient(final DynamoDBMapper dynamoDBMapper,
                                         final ExecutorService executorService) {
    return createClient(dynamoDBMapper, executorService, null);
  }

}
