package br.com.devblack21.dynamodb.resilience.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DynamoDbWriterResilienceClientTest {

  private DynamoDbResilienceSave<Object> dbResilienceSave;
  private DynamoDbResilienceBatchSave<Object> dynamoDbResilienceBatchSave;
  private DynamoDbResilienceDelete<Object> dynamoDbResilienceDelete;
  private DynamoDbResilienceBatchDelete<Object> dynamoDbResilienceBatchDelete;
  private DynamoDbWriterResilienceClient<Object> client;

  @BeforeEach
  void setUp() {
    dbResilienceSave = mock(DynamoDbResilienceSave.class);
    dynamoDbResilienceBatchSave = mock(DynamoDbResilienceBatchSave.class);
    dynamoDbResilienceDelete = mock(DynamoDbResilienceDelete.class);
    dynamoDbResilienceBatchDelete = mock(DynamoDbResilienceBatchDelete.class);

    client = new DynamoDbWriterResilienceClient<>(
      dbResilienceSave,
      dynamoDbResilienceBatchSave,
      dynamoDbResilienceDelete,
      dynamoDbResilienceBatchDelete
    );
  }

  @Test
  void testSave() {
    Object entity = new Object();

    client.save(entity);

    verify(dbResilienceSave).save(entity);
    verifyNoMoreInteractions(dbResilienceSave);
  }

  @Test
  void testBatchSave() {
    Object entity = new Object();

    client.batchSave(entity);

    verify(dynamoDbResilienceBatchSave).batchSave(entity);
    verifyNoMoreInteractions(dynamoDbResilienceBatchSave);
  }

  @Test
  void testDelete() {
    Object entity = new Object();

    client.delete(entity);

    verify(dynamoDbResilienceDelete).delete(entity);
    verifyNoMoreInteractions(dynamoDbResilienceDelete);
  }

  @Test
  void testBatchDelete() {
    Object entity = new Object();

    client.batchDelete(entity);

    verify(dynamoDbResilienceBatchDelete).batchDelete(entity);
    verifyNoMoreInteractions(dynamoDbResilienceBatchDelete);
  }

  @Test
  void testSave_withNullEntity() {
    client.save(null);

    verify(dbResilienceSave).save(null);
  }

  @Test
  void testBatchSave_withNullEntity() {
    client.batchSave(null);

    verify(dynamoDbResilienceBatchSave).batchSave(null);
  }

  @Test
  void testDelete_withNullEntity() {
    client.delete(null);

    verify(dynamoDbResilienceDelete).delete(null);
  }

  @Test
  void testBatchDelete_withNullEntity() {
    client.batchDelete(null);

    verify(dynamoDbResilienceBatchDelete).batchDelete(null);
  }
}
