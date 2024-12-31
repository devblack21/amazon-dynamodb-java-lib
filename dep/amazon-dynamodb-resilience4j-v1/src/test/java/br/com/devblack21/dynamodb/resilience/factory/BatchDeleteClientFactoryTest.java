package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceBatchDelete;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceBatchDeleteAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceBatchDeleteSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchBatchDeleteClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffExecutor backoffExecutor = Mockito.mock(BackoffExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
    final DynamoDbResilienceBatchDeleteAsync<Object> asyncClient = (DynamoDbResilienceBatchDeleteAsync<Object>) client;
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final DynamoDbResilienceBatchDelete<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchDeleteAsync.class)));
  }


}
