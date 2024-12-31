package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceSave;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceSaveAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceSaveSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class SaveClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffExecutor backoffExecutor = Mockito.mock(BackoffExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
    final DynamoDbResilienceSaveSync<Object> syncClient = (DynamoDbResilienceSaveSync<Object>) client;
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
    final DynamoDbResilienceSaveAsync<Object> asyncClient = (DynamoDbResilienceSaveAsync<Object>) client;
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final DynamoDbResilienceSave<Object> client = SaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceSaveAsync.class)));
  }


}
