package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.DeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.DeleteManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.sync.DeleteManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class DeleteClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffSingleWriteExecutor backoffExecutor = Mockito.mock(BackoffSingleWriteExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
    final DeleteManagerAsync<Object> asyncClient = (DeleteManagerAsync<Object>) client;
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final DeleteManager<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DeleteManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final DeleteManager<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DeleteManagerAsync.class)));
  }


}
