package br.com.devblack21.dynamodb.manager4j.writer.simple.async;


import br.com.devblack21.dynamodb.manager4j.configuration.WriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
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

  private BackoffBatchWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private AbstractAsyncBatchWriter testWriter;
  private AbstractAsyncBatchWriter testWriterWithoutBackoffAndRecoverer;
  private AbstractAsyncBatchWriter testFailureWriter;
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

    final WriteRetryPolicyConfiguration retryPolicyConfiguration = WriteRetryPolicyConfiguration.builder()
      .backoffBatchWriteExecutor(mockBackoffExecutor)
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
      WriteRetryPolicyConfiguration.builder()
        .build(),
      executorService,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {

    final MyItem entity = new MyItem("1", "nome");

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

    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

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

    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);
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

    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());
    doThrow(RuntimeException.class).when(mockErrorRecoverer).recover(any(MyItem.class));

    testFailureWriter.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, times(1)).recover(anyList());
      verify(mockRequestInterceptor, never()).logError(any(MyItem.class), any());
      verify(mockRequestInterceptor, never()).logSuccess(any(MyItem.class));
    });
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("1", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testWriterWithoutBackoffAndRecoverer.execute(List.of(entity));

    Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
      verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
      verify(mockErrorRecoverer, never()).recover(anyList());
      verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
      verify(mockRequestInterceptor, never()).logSuccess(anyList());
    });
  }


  private static class TestAsyncWriterSuccess extends AbstractAsyncBatchWriter {

    public TestAsyncWriterSuccess(final WriteRetryPolicyConfiguration retryPolicyConfiguration,
                                  final ExecutorService executorService,
                                  final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, executorService, requestInterceptor);
    }


    @Override
    protected List<UnprocessedItem> executor(List<? extends TableEntity> entity) {
      return List.of();
    }

  }

  private static class TestAsyncWriterFailure extends AbstractAsyncBatchWriter {

    public TestAsyncWriterFailure(final WriteRetryPolicyConfiguration retryPolicyConfiguration,
                                  final ExecutorService executorService,
                                  final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, executorService, requestInterceptor);
    }

    @Override
    protected List<UnprocessedItem> executor(List<? extends TableEntity> entity) {
      return UnprocessedItem.unprocessedItems(List.of(new MyItem("1", ""), new MyItem("1", "")));
    }
  }


}


