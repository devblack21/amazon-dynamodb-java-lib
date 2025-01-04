package br.com.devblack21.dynamodb.manager4j.writer.simple.templates;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.batch.BackoffBatchWriteExecutor;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class AbstractBatchManagerTemplate extends AbstractManagerTemplate {

  protected void simulateBackoffFailure(final BackoffBatchWriteExecutor executor) throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doThrow(RuntimeException.class).when(executor).execute(captor.capture(), anyList());
  }

  protected void captureFunctionForRetry(final BackoffBatchWriteExecutor executor) throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Function<List<? extends TableEntity>, List<UnprocessedItem>>> captor = ArgumentCaptor.forClass(Function.class);
    doNothing().when(executor).execute(captor.capture(), anyList());
  }

}
