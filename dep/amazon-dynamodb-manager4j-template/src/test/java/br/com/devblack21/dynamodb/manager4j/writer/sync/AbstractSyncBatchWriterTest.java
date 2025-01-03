package br.com.devblack21.dynamodb.manager4j.writer.sync;


import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.mockito.Mockito.*;

class AbstractSyncBatchWriterTest {

  private BackoffBatchWriteExecutor<Object> mockBackoffExecutor;
  private ErrorRecoverer<Object> mockErrorRecoverer;
  private RequestInterceptor<Object> mockRequestInterceptor;
  private AbstractSyncBatchWriter<Object> testWriter;
  private AbstractSyncBatchWriter<Object> testWriterWithoutBackoffAndRecoverer;
  private AbstractSyncBatchWriter<Object> testFailureWriter;

  @BeforeEach
  void setUp() {
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = new TestSyncWriterSuccess(
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor
    );

    testFailureWriter = new TestSyncWriterFailure(
      mockBackoffExecutor,
      mockErrorRecoverer,
      mockRequestInterceptor);

    testWriterWithoutBackoffAndRecoverer = new TestSyncWriterFailure(
      null,
      null,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    testWriter.execute(List.of(entity));

    verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, never()).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, never()).recover(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final Object entity = new Object();

    final ArgumentCaptor<Function<List<Object>, List<UnprocessedItem<Object>>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.execute(List.of(entity)));

    verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, never()).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }


  private static class TestSyncWriterSuccess extends AbstractSyncBatchWriter<Object> {

    public TestSyncWriterSuccess(final BackoffBatchWriteExecutor<Object> backoffExecutor,
                                 final ErrorRecoverer<Object> errorRecoverer,
                                 final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, requestInterceptor);
    }


    @Override
    protected List<UnprocessedItem<Object>> executor(final List<Object> entity) {
      return List.of();
    }
  }

  private static class TestSyncWriterFailure extends AbstractSyncBatchWriter<Object> {

    public TestSyncWriterFailure(final BackoffBatchWriteExecutor<Object> backoffExecutor,
                                 final ErrorRecoverer<Object> errorRecoverer,
                                 final RequestInterceptor<Object> requestInterceptor) {
      super(backoffExecutor, errorRecoverer, requestInterceptor);
    }

    @Override
    protected List<UnprocessedItem<Object>> executor(final List<Object> entity) {
      return UnprocessedItem.unprocessedItems(List.of(new Object(), new Object()));
    }
  }
}


