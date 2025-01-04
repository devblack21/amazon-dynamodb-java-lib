package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchSaveManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.simple.sync.BatchDeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchDeleteClientSyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final FailedBatchDeleteRequestTransformer<MyItem> transformer = Mockito.mock(FailedBatchDeleteRequestTransformer.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchDeleteManager client = BatchDeleteClientSyncFactory.createClient(dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder().build(),
      requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithoutInterceptor() {
    final BatchDeleteManager client = BatchDeleteClientSyncFactory.createClient(dynamoDBMapper, transformer, BatchWriteRetryPolicyConfiguration.builder().build());
    assertThat(client, is(instanceOf(BatchDeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchDeleteManager client = BatchDeleteClientSyncFactory.createClient(dynamoDBMapper, transformer, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final BatchDeleteManager client = BatchDeleteClientSyncFactory.createClient(dynamoDBMapper, transformer);
    assertThat(client, is(instanceOf(BatchDeleteManagerSync.class)));
  }

}
