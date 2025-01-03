package br.com.devblack21.dynamodb.manager4j.writer.async;


import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
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
import java.util.function.Function;

import static org.mockito.Mockito.*;

class AbstractAsyncBatchWriterTest {

  private final Integer TIMEOUT = 3;

  private BackoffBatchWriteExecutor<Object> mockBackoffExecutor;
  private ErrorRecoverer<Object> mockErrorRecoverer;
  private RequestInterceptor<Object> mockRequestInterceptor;
  private AbstractAsyncBatchWriter<Object> testWriter;
  private AbstractAsyncBatchWriter<Object> testWriterWithoutBackoffAndRecoverer;
  private AbstractAsyncBatchWriter<Object> testFailureWriter;
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
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = new TestAsyncWriterSuccess(
      mockBackoffExecutor,
      mockErrorRecoverer,
      executorService,
      mockRequestInterceptor
    );

    testFailureWriter = new TestAsyncWriterFailure(
      mockBackoffExecutor,
      mockErrorRecoverer,
      executorService,
      mockRequestInterceptor);

    testWriterWithoutBackoffAndRecoverer = new TestAsyncWriterFailure(
      null,
      null,
      executorService,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {

    final Object entity = new Object();

    testWriter.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, never()).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
    });
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, never()).recover(anyList());
      verify(mockRequestInterceptor, never()).logError(anyList(), any());
      verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    });
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
      verify(mockRequestInterceptor, never()).logSuccess(anyList());
    });
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(Object.class));

    testFailureWriter.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, never()).logError(any(Object.class), any());
      verify(mockRequestInterceptor, never()).logSuccess(any(Object.class));
    });
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testWriterWithoutBackoffAndRecoverer.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, never()).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
      verify(mockRequestInterceptor, never()).logSuccess(anyList());
    });
  }


  private static class TestAsyncWriterSuccess extends AbstractAsyncBatchWriter<Object> {

    public TestAsyncWriterSuccess(final BackoffBatchWriteExecutor<Object> backoffExecutor,
                                  final ErrorRecoverer<Object> errorRecoverer,
                                  final ExecutorService executorService,
                                  final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    }


    @Override
    protected List<UnprocessedItem<Object>> executor(List<Object> entity) {
      return List.of();
    }
  }

  private static class TestAsyncWriterFailure extends AbstractAsyncBatchWriter<Object> {

    public TestAsyncWriterFailure(final BackoffBatchWriteExecutor<Object> backoffExecutor,
                                  final ErrorRecoverer<Object> errorRecoverer,
                                  final ExecutorService executorService,
                                  final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    }

    @Override
    protected List<UnprocessedItem<Object>> executor(List<Object> entity) {
      return UnprocessedItem.unprocessedItems(List.of(new Object(), new Object()));
    }
  }
}


