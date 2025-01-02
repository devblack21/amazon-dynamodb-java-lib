package br.com.devblack21.dynamodb.manager4j.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class DynamoDbWriterManagerClientTest {

  private SaveManager<Object> dbResilienceSave;
  private BatchSaveManager<Object> dynamoDbResilienceBatchSave;
  private DeleteManager<Object> dynamoDbResilienceDelete;
  private BatchDeleteManager<Object> dynamoDbResilienceBatchDelete;
  private DynamoDbWriterManager<Object> client;

  @BeforeEach
  void setUp() {
    dbResilienceSave = mock(SaveManager.class);
    dynamoDbResilienceBatchSave = mock(BatchSaveManager.class);
    dynamoDbResilienceDelete = mock(DeleteManager.class);
    dynamoDbResilienceBatchDelete = mock(BatchDeleteManager.class);

    client = new DynamoDbWriterManagerClient<>(
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

    client.batchSave(List.of(entity));

    verify(dynamoDbResilienceBatchSave).batchSave(List.of(entity));
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

    client.batchDelete(List.of(entity));

    verify(dynamoDbResilienceBatchDelete).batchDelete(List.of(entity));
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
