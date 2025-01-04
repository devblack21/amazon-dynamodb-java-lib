package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class DeleteClientSyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DeleteManager client = DeleteClientSyncFactory.createClient(dynamoDBMapper,
      SingleWriteRetryPolicyConfiguration.builder().build(),
      requestInterceptor);
    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DeleteManager client = DeleteClientSyncFactory.createClient(dynamoDBMapper);
    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DeleteManager client = DeleteClientSyncFactory.createClient(dynamoDBMapper, requestInterceptor);
    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

}
