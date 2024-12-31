package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceDelete;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceDeleteAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceDeleteSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class DeleteClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffExecutor backoffExecutor = Mockito.mock(BackoffExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
    final DynamoDbResilienceDeleteAsync<Object> asyncClient = (DynamoDbResilienceDeleteAsync<Object>) client;
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final DynamoDbResilienceDelete<Object> client = DeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceDeleteAsync.class)));
  }


}
