package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.DeleteManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class DeleteClientAsyncFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final RequestInterceptor requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DeleteManager client = DeleteClientAsyncFactory.createClient(dynamoDBMapper,
      SingleWriteRetryPolicyConfiguration.builder().build(),
      executorService,
      requestInterceptor);
    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DeleteManager client = DeleteClientAsyncFactory.createClient(dynamoDBMapper, executorService);
    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DeleteManager client = DeleteClientAsyncFactory.createClient(dynamoDBMapper, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }


}
