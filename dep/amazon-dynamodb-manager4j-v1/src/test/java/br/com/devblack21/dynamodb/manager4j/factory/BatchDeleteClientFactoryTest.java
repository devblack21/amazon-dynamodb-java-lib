package br.com.devblack21.dynamodb.manager4j.factory;

import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import br.com.devblack21.dynamodb.manager4j.writer.async.BatchDeleteManagerAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class BatchDeleteClientFactoryTest {

  private final DynamoDBMapper dynamoDBMapper = Mockito.mock(DynamoDBMapper.class);
  private final BackoffExecutor backoffExecutor = Mockito.mock(BackoffExecutor.class);
  private final ErrorRecoverer<List<Object>> errorRecoverer = Mockito.mock(ErrorRecoverer.class);
  private final RequestInterceptor<List<Object>> requestInterceptor = Mockito.mock(RequestInterceptor.class);
  private final ExecutorService executorService = Mockito.mock(ExecutorService.class);

  @Test
  void testCreateSyncClientWithAllParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateSyncClientWithPartialParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateSyncClientWithMinimalParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithAllParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithPartialParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithMinimalParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullBackoffExecutor() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, errorRecoverer, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithNullBackoffExecutor() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, errorRecoverer, executorService, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullErrorRecoverer() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, backoffExecutor, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithNullExecutorService() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, null, requestInterceptor);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateAsyncClientWithNullRequestInterceptor() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, backoffExecutor, errorRecoverer, executorService, null);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithNullParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper, null, null, null);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithNullParameters() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, null, null, executorService, null);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }

  @Test
  void testCreateSyncClientWithOnlyDynamoDBMapper() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createSyncClient(dynamoDBMapper);

    assertThat(client, is(instanceOf(BatchDeleteManager.class)));
  }

  @Test
  void testCreateAsyncClientWithOnlyExecutorService() {
    final BatchDeleteManager<Object> client = BatchDeleteClientFactory.createAsyncClient(dynamoDBMapper, executorService);

    assertThat(client, is(instanceOf(BatchDeleteManagerAsync.class)));
  }


}
