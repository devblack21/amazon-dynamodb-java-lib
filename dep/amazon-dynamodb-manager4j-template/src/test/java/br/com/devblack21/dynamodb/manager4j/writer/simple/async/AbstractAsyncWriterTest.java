package br.com.devblack21.dynamodb.manager4j.writer.simple.async;


import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
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

  private BackoffSingleWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private AbstractAsyncWriter testWriter;
  private AbstractAsyncWriter testWriterWithoutBackoffAndRecoverer;
  private AbstractAsyncWriter testFailureWriter;
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
    mockBackoffExecutor = mock(BackoffSingleWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration = SingleWriteRetryPolicyConfiguration.builder()
      .backoffSingleWriteExecutor(mockBackoffExecutor)
      .errorRecoverer(mockErrorRecoverer)
      .build();

    testWriter = new TestAsyncWriterSuccess(
      retryPolicyConfiguration,
      executorService,
      mockRequestInterceptor
    );

    testFailureWriter = new TestAsyncWriterFailure(
      retryPolicyConfiguration,
      executorService,
      mockRequestInterceptor);

    testWriterWithoutBackoffAndRecoverer = new TestAsyncWriterFailure(
      SingleWriteRetryPolicyConfiguration.builder()
        .build(),
      executorService,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {

    final MyItem entity = new MyItem("1", "nome");

    testWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
      verify(mockErrorRecoverer, never()).recover(any(MyItem.class));
      verify(mockRequestInterceptor, times(1)).logSuccess(any(MyItem.class));
      verify(mockRequestInterceptor, never()).logError(any(MyItem.class), any());
    });
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, never()).recover(any(MyItem.class));
      verify(mockRequestInterceptor, never()).logError(any(MyItem.class), any());
      verify(mockRequestInterceptor, times(1)).logSuccess(any(MyItem.class));
    });
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testFailureWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, times(1)).recover(any(MyItem.class));
      verify(mockRequestInterceptor, times(1)).logError(any(MyItem.class), any());
      verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
    });
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture());
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(MyItem.class));

    testFailureWriter.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
      verify(mockErrorRecoverer, times(1)).recover(any(MyItem.class));
      verify(mockRequestInterceptor, never()).logError(any(MyItem.class), any());
      verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
    });
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture());

    testWriterWithoutBackoffAndRecoverer.execute(entity);

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Runnable.class));
      verify(mockErrorRecoverer, never()).recover(any(MyItem.class));
      verify(mockRequestInterceptor, times(1)).logError(eq(entity), any());
      verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
    });
  }


  private static class TestAsyncWriterSuccess extends AbstractAsyncWriter {

    public TestAsyncWriterSuccess(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                  final ExecutorService executorService,
                                  final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, executorService, requestInterceptor);
    }

    @Override
    public void executor(final TableEntity entity) {
    }
  }

  private static class TestAsyncWriterFailure extends AbstractAsyncWriter {

    public TestAsyncWriterFailure(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                  final ExecutorService executorService,
                                  final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, executorService, requestInterceptor);
    }

    @Override
    public void executor(final TableEntity entity) {
      throw new RuntimeException();
    }
  }
}


