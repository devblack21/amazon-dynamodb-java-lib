package br.com.devblack21.dynamodb.manager4j.writer.async;

import br.com.devblack21.dynamodb.manager4j.factory.BatchDeleteClientFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class BatchDeleteManagerAsyncTest {

  private final Integer TIMEOUT = 3;

  private DynamoDBMapper dynamoDBMapper;
  private BackoffExecutor mockBackoffExecutor;
  private ErrorRecoverer<List<Object>> mockErrorRecoverer;
  private RequestInterceptor<List<Object>> mockRequestInterceptor;
  private BatchDeleteManager<Object> testWriter;
  private BatchDeleteManager<Object> testWriterWithoutBackoffAndRecoverer;
  private static ExecutorService executorService;

  @BeforeAll
  static void setUpExecutorService() {
    executorService = Executors.newCachedThreadPool();
  }

  @AfterAll
  static void tearDownExecutorService() {
    executorService.shutdown();
  }

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    mockBackoffExecutor = mock(BackoffExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = BatchDeleteClientFactory.createAsyncClient(
      dynamoDBMapper,
      mockBackoffExecutor,
      mockErrorRecoverer,
      executorService,
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = BatchDeleteClientFactory.createAsyncClient(
      dynamoDBMapper,
      null,
      null,
      executorService,
      mockRequestInterceptor
    );

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final Object entity = new Object();

    testWriter.batchDelete(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
      verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
      verify(mockRequestInterceptor, never()).logError(any(), any());
    });
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    captureRunnableForRetry();

    testWriter.batchDelete(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verifyNoInteractions(mockErrorRecoverer);
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
    });

  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();

    testWriter.batchDelete(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
    });
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    simulateDynamoDbFailure();
    simulateBackoffFailure();
    simulateRecoveryFailure();

    testWriter.batchDelete(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
      verify(mockRequestInterceptor, never()).logSuccess(any());
    });
  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final Object entity = new Object();

    simulateDynamoDbFailure();

    testWriterWithoutBackoffAndRecoverer.batchDelete(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    });
  }

  private void simulateDynamoDbFailure() {
    doThrow(RuntimeException.class).when(dynamoDBMapper).batchDelete(anyList());
  }

  private void simulateBackoffFailure() throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
  }

  private void simulateRecoveryFailure() {
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any());
  }

  private void captureRunnableForRetry() throws ExecutionException, InterruptedException {
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());
  }

}
