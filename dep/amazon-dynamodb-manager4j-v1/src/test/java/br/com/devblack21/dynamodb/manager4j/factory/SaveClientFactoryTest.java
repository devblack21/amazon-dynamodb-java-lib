package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.SaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.SaveManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.sync.SaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class SaveClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffSingleWriteExecutor backoffExecutor = Mockito.mock(BackoffSingleWriteExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
    final SaveManagerSync<Object> syncClient = (SaveManagerSync<Object>) client;
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
    final SaveManagerAsync<Object> asyncClient = (SaveManagerAsync<Object>) client;
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final SaveManager<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(SaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final SaveManager<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(SaveManagerAsync.class)));
  }


}
