package br.com.devblack21.dynamodb.manager4j.writer.async;


import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.resilience.BackoffExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.ErrorRecoverer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class AbstractAsyncWriterTest {

  private final Integer TIMEOUT = 3;

  private BackoffExecutor mockBackoffExecutor;
  private ErrorRecoverer<Object> mockErrorRecoverer;
  private RequestInterceptor<Object> mockRequestInterceptor;
  private AbstractAsyncWriter<Object> testWriter;
  private AbstractAsyncWriter<Object> testWriterWithoutBackoffAndRecoverer;
  private AbstractAsyncWriter<Object> testFailureWriter;
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
    mockBackoffExecutor = mock(BackoffExecutor.class);
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
  void shouldExecuteSuccessfullyWithoutErrors() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    testWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
      verify(mockErrorRecoverer, never()).recover(any());
      verify(mockRequestInterceptor, never()).logSuccess(any());
      verify(mockRequestInterceptor, never()).logError(any(), any());
    });
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, never()).recover(any());
      verify(mockRequestInterceptor, never()).logError(eq(entity), any());
      verify(mockRequestInterceptor, never()).logSuccess(any());
    });
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, times(1)).recover(any());
      verify(mockRequestInterceptor, never()).logError(eq(entity), any());
      verify(mockRequestInterceptor, never()).logSuccess(any());
    });
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any());

    testFailureWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, times(1)).recover(any());
      verify(mockRequestInterceptor, never()).logError(eq(entity), any());
      verify(mockRequestInterceptor, never()).logSuccess(any());
    });
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testWriterWithoutBackoffAndRecoverer.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
      verify(mockErrorRecoverer, never()).recover(any());
      verify(mockRequestInterceptor, times(1)).logError(eq(entity), any());
      verify(mockRequestInterceptor, never()).logSuccess(any());
    });
  }


  private static class TestAsyncWriterSuccess extends AbstractAsyncWriter<Object> {

    public TestAsyncWriterSuccess(final BackoffExecutor backoffExecutor,
                                  final ErrorRecoverer<Object> errorRecoverer,
                                  final ExecutorService executorService,
                                  final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    }

    @Override
    public void executor(final Object entity) {
    }
  }

  private static class TestAsyncWriterFailure extends AbstractAsyncWriter<Object> {

    public TestAsyncWriterFailure(final BackoffExecutor backoffExecutor,
                                  final ErrorRecoverer<Object> errorRecoverer,
                                  final ExecutorService executorService,
                                  final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, executorService, requestInterceptor);
    }

    @Override
    public void executor(final Object entity) {
      throw new RuntimeException();
    }
  }
}


