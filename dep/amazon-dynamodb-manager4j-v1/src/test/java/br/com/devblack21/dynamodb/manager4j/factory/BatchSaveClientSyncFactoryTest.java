package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchSaveClientSyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final FailedBatchPutRequestTransformer<MyItem> transformer = Mockito.mock(FailedBatchPutRequestTransformer.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchSaveManager client = BatchSaveClientSyncFactory.createClient(dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder().build(),
      requestInterceptor);
    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithoutInterceptor() {
    final BatchSaveManager client = BatchSaveClientSyncFactory.createClient(dynamoDBMapper, transformer, BatchWriteRetryPolicyConfiguration.builder().build());
    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchSaveManager client = BatchSaveClientSyncFactory.createClient(dynamoDBMapper, transformer, requestInterceptor);
    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final BatchSaveManager client = BatchSaveClientSyncFactory.createClient(dynamoDBMapper, transformer);
    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

}
