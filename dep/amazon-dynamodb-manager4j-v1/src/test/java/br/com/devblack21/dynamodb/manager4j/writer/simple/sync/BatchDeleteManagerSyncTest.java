package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.BatchDeleteClientSyncFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.mockito.Mockito.*;

class BatchDeleteManagerSyncTest {

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
    final MyItem entity = new MyItem("12", "");

    testWriter.batchDelete(List.of(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("12", "");

    simulateDynamoDbFailure();
    captureRunnableForRetry();

    testWriter.batchDelete(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void testBatchSaveFailureWithUnprocessedItems() throws ExecutionException, InterruptedException {
    final List<MyItem> items = Arrays.asList(new MyItem("12", ""), new MyItem("12", ""));

    simulateFailedBatch();

    testWriter.batchDelete(items);


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("12", "");

    simulateFailedBatch();
    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.batchDelete(List.of(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any(RuntimeException.class));

  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("12", "");

    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    testWriter.batchDelete(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());

  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final MyItem entity = new MyItem("12", "");

    simulateDynamoDbFailure();


    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.batchDelete(List.of(entity)));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
  }

  private void simulateDynamoDbFailure() {
    doThrow(RuntimeException.class).when(dynamoDBMapper).batchDelete(anyList());
  }

  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateRecoveryFailure() {
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(TableEntity.class));
  }

  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doNothing().when(mockBackoffExecutor).execute(captor.capture(), anyList());
  }

  private void simulateFailedBatch() {

    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
    final WriteRequest writeRequest = new WriteRequest();
    writeRequest.setDeleteRequest(new DeleteRequest().withKey(Map.of("a", new AttributeValue("b"))));
    unprocessedItems.put("TableName", List.of(writeRequest));

    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
    failedBatches.add(failedBatch);

    when(transformer.transform(anyList())).thenReturn(List.of(new MyItem("11", "")));

    when(dynamoDBMapper.batchDelete(anyList()))
      .thenReturn(failedBatches)
      .thenReturn(List.of());
  }

}
