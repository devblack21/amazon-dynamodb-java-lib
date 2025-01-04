package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class SaveClientAsyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final SaveManager client = SaveClientAsyncFactory.createClient(dynamoDBMapper,
      SingleWriteRetryPolicyConfiguration.builder().build(),
      executorService,
      requestInterceptor);
    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final SaveManager client = SaveClientAsyncFactory.createClient(dynamoDBMapper, executorService);
    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final SaveManager client = SaveClientAsyncFactory.createClient(dynamoDBMapper, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

}
