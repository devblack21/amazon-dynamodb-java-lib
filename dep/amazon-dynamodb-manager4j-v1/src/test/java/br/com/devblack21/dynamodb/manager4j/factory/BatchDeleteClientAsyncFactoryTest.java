package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchDeleteClientAsyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final FailedBatchDeleteRequestTransformer<MyItem> transformer = Mockito.mock(FailedBatchDeleteRequestTransformer.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchDeleteManager client = BatchDeleteClientAsyncFactory.createClient(dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder().build(),
      executorService,
      requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithoutInterceptor() {
    final BatchDeleteManager client = BatchDeleteClientAsyncFactory.createClient(dynamoDBMapper, transformer, executorService);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchDeleteManager client = BatchDeleteClientAsyncFactory.createClient(dynamoDBMapper, transformer, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

}
