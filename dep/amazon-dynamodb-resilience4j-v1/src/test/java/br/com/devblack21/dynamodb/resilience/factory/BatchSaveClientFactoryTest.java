package br.com.devblack21.dynamodb.resilience.factory;

import br.com.devblack21.dynamodb.resilience.backoff.BackoffExecutor;
import br.com.devblack21.dynamodb.resilience.backoff.ErrorRecoverer;
import br.com.devblack21.dynamodb.resilience.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.resilience.writer.DynamoDbResilienceBatchSave;
import br.com.devblack21.dynamodb.resilience.writer.async.DynamoDbResilienceBatchSaveAsync;
import br.com.devblack21.dynamodb.resilience.writer.sync.DynamoDbResilienceBatchSaveSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchSaveClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffExecutor backoffExecutor = Mockito.mock(BackoffExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final DynamoDbResilienceBatchSave<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(DynamoDbResilienceBatchSaveAsync.class)));
  }

}
