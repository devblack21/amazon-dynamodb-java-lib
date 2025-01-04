package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.factory.DeleteClientSyncFactory;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.templates.AbstractSingleDeleteManagerTemplate;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static br.com.devblack21.dynamodb.manager4j.model.MyItem.getMyItem;
import static org.mockito.Mockito.*;

class DeleteManagerSyncTest extends AbstractSingleDeleteManagerTemplate {

  private DynamoDBMapper dynamoDBMapper;
  private BackoffSingleWriteExecutor mockBackoffExecutor;
  private ErrorRecoverer mockErrorRecoverer;
  private RequestInterceptor mockRequestInterceptor;
  private DeleteManager testWriter;
  private DeleteManager testWriterWithoutBackoffAndRecoverer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    mockBackoffExecutor = mock(BackoffSingleWriteExecutor.class);
    mockErrorRecoverer = mock(ErrorRecoverer.class);
    mockRequestInterceptor = mock(RequestInterceptor.class);

    testWriter = DeleteClientSyncFactory.createClient(
      dynamoDBMapper,
      SingleWriteRetryPolicyConfiguration.builder()
        .errorRecoverer(mockErrorRecoverer)
        .backoffSingleWriteExecutor(mockBackoffExecutor)
        .build(),
      mockRequestInterceptor
    );

    testWriterWithoutBackoffAndRecoverer = DeleteClientSyncFactory.createClient(
      dynamoDBMapper,
      mockRequestInterceptor
    );
  }

  @Test
  void shouldExecuteSuccessfullyWithoutErrors() {
    final MyItem entity = getMyItem();

    testWriter.delete(entity);

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logSuccess(entity);
    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
  }

  @Test
  void shouldRetryOnFailure() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    captureRunnableForRetry(mockBackoffExecutor);

    testWriter.delete(entity);


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verifyNoInteractions(mockErrorRecoverer);
    verify(mockRequestInterceptor, never()).logError(eq(entity), any());
  }

  @Test
  void shouldRecoverOnFailureWhenBackoffExecutorFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);

    testWriter.delete(entity);


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(entity);
    verify(mockRequestInterceptor, times(1)).logError(eq(entity), any(RuntimeException.class));
  }

  @Test
  void shouldLogErrorWhenRecoveryFails() throws ExecutionException, InterruptedException {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);
    simulateBackoffFailure(mockBackoffExecutor);
    simulateRecoveryFailure(mockErrorRecoverer);

    Assertions.assertThrows(RuntimeException.class, () -> testWriter.delete(entity));


    verify(mockBackoffExecutor, times(1)).execute(any(Runnable.class));
    verify(mockErrorRecoverer, times(1)).recover(entity);
    verify(mockRequestInterceptor, times(1)).logError(eq(entity), any(RuntimeException.class));
    verify(mockRequestInterceptor, never()).logSuccess(eq(entity));
  }

  @Test
  void shouldLogErrorWhenNoRecoveryAndNoBackoff() {
    final MyItem entity = getMyItem();

    simulateDynamoDbFailure(dynamoDBMapper);

    Assertions.assertThrows(RuntimeException.class, () -> testWriterWithoutBackoffAndRecoverer.delete(entity));

    verifyNoInteractions(mockBackoffExecutor, mockErrorRecoverer);
    verify(mockRequestInterceptor, times(1)).logError(eq(entity), any());
  }

}
