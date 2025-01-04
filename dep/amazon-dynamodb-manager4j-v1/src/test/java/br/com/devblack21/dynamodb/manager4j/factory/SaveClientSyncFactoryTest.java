package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class SaveClientSyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final SaveManager client = SaveClientSyncFactory.createClient(dynamoDBMapper,
      SingleWriteRetryPolicyConfiguration.builder().build(),
      requestInterceptor);
    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final SaveManager client = SaveClientSyncFactory.createClient(dynamoDBMapper);
    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final SaveManager client = SaveClientSyncFactory.createClient(dynamoDBMapper, requestInterceptor);
    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

}
