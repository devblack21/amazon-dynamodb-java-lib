package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchSaveManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.BatchSaveManagerAsync;
import br.com.devblack21.dynamodb.manager4j.writer.sync.BatchSaveManagerSync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchSaveClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffBatchWriteExecutor<Object> backoffExecutor = Mockito.mock(BackoffBatchWriteExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(BatchSaveManagerSync.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final BatchSaveManager<Object> client = BatchSaveClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(BatchSaveManagerAsync.class)));
  }

}
