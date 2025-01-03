package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.simple.async.BatchDeleteManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchDeleteClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final FailedBatchDeleteRequestTransformer<Object> transformer = Mockito.mock(FailedBatchDeleteRequestTransformer.class);
  private final BackoffBatchWriteExecutor<Object> backoffExecutor = Mockito.mock(BackoffBatchWriteExecutor.class);
  private final ErrorRecoverer<Object> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<Object> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer, backoffExecutor, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer);
    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, backoffExecutor, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, executorService);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer, errorRecoverer, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, errorRecoverer, executorService, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer, backoffExecutor, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, null, requestInterceptor);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, backoffExecutor, errorRecoverer, executorService, null);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer, null, null, null);
    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, null, null, executorService, null);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, transformer);
    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, transformer, executorService);
    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

}
