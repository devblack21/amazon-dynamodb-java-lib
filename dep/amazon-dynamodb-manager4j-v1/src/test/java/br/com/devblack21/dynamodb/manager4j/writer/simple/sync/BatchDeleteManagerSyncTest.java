package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.BatchDeleteClientSyncFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.templates.AbstractBatchDeleteManagerTemplate;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static br.com.devblack21.dynamodb.manager4j.model.MyItem.getMyItem;
import static org.mockito.Mockito.*;

class BatchDeleteManagerSyncTest extends AbstractBatchDeleteManagerTemplate {

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchDeleteRequestTransformer<MyItem> transformer;
  private BackoffBatchWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private BatchDeleteManager testWriter;
  private BatchDeleteManager testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = mock(FailedBatchDeleteRequestTransformer.class);
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchDeleteClientSyncFactory.createClient(
      dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder()
        .errorRecoverer(mockErrorRecoverer)
        .backoffBatchWriteExecutor(mockBackoffExecutor)
        .build(),
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = BatchDeleteClientSyncFactory.createClient(
      dynamoDBMapper,
      transformer,
      mockRequestInterceptor
    );
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final MyItem entity = getMyItem();

    testWriter.batchDelete(List.of(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    captureFunctionForRetry(mockBackoffExecutor);

    testWriter.batchDelete(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<MyItem> items = Arrays.asList(getMyItem(), getMyItem());

    simulateFailedBatch(dynamoDBMapper, transformer);

    testWriter.batchDelete(items);


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateFailedBatch(dynamoDBMapper, transformer);
    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);

    testWriter.batchDelete(List.of(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);
    simulateRecoveryFailure(mockErrorRecoverer);

    Assertions.assertThrows(RuntimeException.class, () -> testWriter.batchDelete(List.of(entity)));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.batchDelete(List.of(entity)));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
  }

}
