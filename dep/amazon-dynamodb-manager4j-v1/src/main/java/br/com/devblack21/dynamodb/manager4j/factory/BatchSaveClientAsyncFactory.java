package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.WriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchSaveManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;

public class BatchSaveClientAsyncFactory {


  public static BatchSaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                              final FailedBatchPutRequestTransformer<? extends TableEntity> failedTransformer,
                                              final WriteRetryPolicyConfiguration retryPolicyConfiguration,
                                              final ExecutorService executorService,
                                              final RequestInterceptor requestInterceptor) {
    return new BatchSaveManagerAsync.Builder(dynamoDBMapper, failedTransformer)
      .retryPolicyConfiguration(retryPolicyConfiguration)
      .executorService(executorService)
      .requestInterceptor(requestInterceptor)
      .build();
  }

  public static BatchSaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                              final FailedBatchPutRequestTransformer<? extends TableEntity> failedTransformer,
                                              final ExecutorService executorService,
                                              final RequestInterceptor requestInterceptor) {
    return createClient(dynamoDBMapper, failedTransformer, null, executorService, requestInterceptor);
  }

  public static BatchSaveManager createClient(final DynamoDBMapper dynamoDBMapper,
                                              final FailedBatchPutRequestTransformer<? extends TableEntity> failedTransformer,
                                              final ExecutorService executorService) {
    return createClient(dynamoDBMapper, failedTransformer, executorService, null);
  }

}
