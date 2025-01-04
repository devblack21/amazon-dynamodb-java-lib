package br.com.devblack21.dynamodb.manager4j.writer.simple.templates;

import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class AbstractSingleManagerTemplate extends AbstractManagerTemplate {

  protected void simulateBackoffFailure(final BackoffSingleWriteExecutor executor) throws ExecutionException, InterruptedException {
    final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doThrow(RuntimeException.class).when(executor).execute(runnableCaptor.capture());
  }

  protected void captureRunnableForRetry(final BackoffSingleWriteExecutor executor) throws ExecutionException, InterruptedException {
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doNothing().when(executor).execute(runnableCaptor.capture());
  }

}
