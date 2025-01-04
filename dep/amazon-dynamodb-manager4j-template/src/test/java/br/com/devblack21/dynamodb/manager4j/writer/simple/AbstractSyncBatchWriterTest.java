package br.com.devblack21.dynamodb.manager4j.writer.simple;


import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
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

  private BackoffBatchWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private AbstractSyncBatchWriter testWriter;
  private AbstractSyncBatchWriter testWriterWithoutBackoffAndRecoverer;
  private AbstractSyncBatchWriter testFailureWriter;

  @BeforeEach
  void setUp() {
    mockBackoffExecutor = mock(BackoffBatchWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration = BatchWriteRetryPolicyConfiguration.builder()
      .backoffBatchWriteExecutor(mockBackoffExecutor)
      .errorRecoverer(mockErrorRecoverer)
      .build();

    testWriter = new TestSyncWriterSuccess(
      retryPolicyConfiguration,
      mockRequestInterceptor
    );

    testFailureWriter = new TestSyncWriterFailure(
      retryPolicyConfiguration,
      mockRequestInterceptor);

    testWriterWithoutBackoffAndRecoverer = new TestSyncWriterFailure(
      null,
      mockRequestInterceptor);

  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    testWriter.execute(List.of(entity));

    verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, never()).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, never()).recover(anyList());
    verify(mockRequestInterceptor, never()).logError(anyList(), any());
    verify(mockRequestInterceptor, times(1)).logSuccess(anyList());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {

    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    testFailureWriter.execute(List.of(entity));

    verify(mockBackoffExecutor, times(1)).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, times(1)).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }

  @Test
  void shouldThrowExceptionWhenNoRecoveryAndNoBackoff() throws ExecutionException, InterruptedException {
    final MyItem entity = new MyItem("87", "nome");

    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> runnableCaptor = ArgumentCaptor.forClass(Function.class);

    doNothing().when(mockBackoffExecutor).execute(runnableCaptor.capture(), anyList());

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.execute(List.of(entity)));

    verify(mockBackoffExecutor, never()).execute(any(Function.class), anyList());
    verify(mockErrorRecoverer, never()).recover(anyList());
    verify(mockRequestInterceptor, times(1)).logError(anyList(), any());
    verify(mockRequestInterceptor, never()).logSuccess(anyList());
  }


  private static class TestSyncWriterSuccess extends AbstractSyncBatchWriter {

    public TestSyncWriterSuccess(final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                 final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, requestInterceptor);
    }


    @Override
    protected List<UnprocessedItem> executor(final List<? extends TableEntity> entity) {
      return List.of();
    }

  }

  private static class TestSyncWriterFailure extends AbstractSyncBatchWriter {

    public TestSyncWriterFailure(final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration,
                                 final RequestInterceptor requestInterceptor) {
      super(retryPolicyConfiguration, requestInterceptor);
    }

    @Override
    protected List<UnprocessedItem> executor(final List<? extends TableEntity> entity) {
      return UnprocessedItem.unprocessedItems(List.of(new MyItem("1", "0"), new MyItem("2", "")));
    }

  }
}


