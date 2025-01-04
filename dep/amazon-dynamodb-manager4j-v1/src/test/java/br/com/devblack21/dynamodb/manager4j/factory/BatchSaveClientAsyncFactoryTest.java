package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchSaveManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchSaveClientAsyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final FailedBatchPutRequestTransformer<MyItem> transformer = Mockito.mock(FailedBatchPutRequestTransformer.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchSaveManager client = BatchSaveClientAsyncFactory.createClient(dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder().build(),
      executorService,
      requestInterceptor);
    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithoutInterceptor() {
    final BatchSaveManager client = BatchSaveClientAsyncFactory.createClient(dynamoDBMapper, transformer, executorService);
    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchSaveManager client = BatchSaveClientAsyncFactory.createClient(dynamoDBMapper, transformer, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }



}
