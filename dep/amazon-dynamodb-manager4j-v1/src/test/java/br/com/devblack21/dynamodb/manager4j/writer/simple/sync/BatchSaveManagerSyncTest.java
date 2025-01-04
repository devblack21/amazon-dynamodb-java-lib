package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.BatchSaveClientSyncFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.templates.AbstractBatchSaveManagerTemplate;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchSaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static br.com.devblack21.dynamodb.manager4j.model.MyItem.getMyItem;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BatchSaveManagerSyncTest extends AbstractBatchSaveManagerTemplate {

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchPutRequestTransformer<MyItem> transformer;
  private BackoffBatchWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private BatchSaveManager testWriter;
  private BatchSaveManager testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = mock(FailedBatchPutRequestTransformer.class);
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchSaveClientSyncFactory.createClient(
      dynamoDBMapper,
      transformer,
      BatchWriteRetryPolicyConfiguration.builder()
        .backoffBatchWriteExecutor(mockBackoffExecutor)
        .errorRecoverer(mockErrorRecoverer)
        .build(),
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = BatchSaveClientSyncFactory.createClient(
      dynamoDBMapper,
      transformer,
      mockRequestInterceptor
    );
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final MyItem entity = getMyItem();

    testWriter.batchSave(List.of(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    captureFunctionForRetry(mockBackoffExecutor);

    testWriter.batchSave(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<MyItem> itemsToSave = Arrays.asList(getMyItem(), getMyItem());

    simulateFailedBatch(dynamoDBMapper, transformer);

    testWriter.batchSave(itemsToSave);

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);

    testWriter.batchSave(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateFailedBatch(dynamoDBMapper, transformer);
    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);
    simulateRecoveryFailure(mockErrorRecoverer);

    Assertions.assertThrows(RuntimeException.class, () -> testWriter.batchSave(List.of(entity)));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), any());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);

    assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.batchSave(List.of(entity)));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
  }

}
