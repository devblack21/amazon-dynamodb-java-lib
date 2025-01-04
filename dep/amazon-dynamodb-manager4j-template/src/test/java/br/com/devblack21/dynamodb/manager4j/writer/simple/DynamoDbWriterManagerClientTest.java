package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class DynamoDbWriterManagerClientTest {

  private SaveManager dbResilienceSave;
  private BatchSaveManager dynamoDbResilienceBatchSave;
  private DeleteManager dynamoDbResilienceDelete;
  private BatchDeleteManager dynamoDbResilienceBatchDelete;
  private DynamoDbWriterManager client;

  @BeforeEach
  void setUp() {
    dbResilienceSave = mock(SaveManager.class);
    dynamoDbResilienceBatchSave = mock(BatchSaveManager.class);
    dynamoDbResilienceDelete = mock(DeleteManager.class);
    dynamoDbResilienceBatchDelete = mock(BatchDeleteManager.class);

    client = new DynamoDbWriterManagerClient(
      dbResilienceSave,
      dynamoDbResilienceBatchSave,
      dynamoDbResilienceDelete,
      dynamoDbResilienceBatchDelete
    );
  }

  @Test
  void testSave() {
    final MyItem entity = new MyItem("87", "nome");

    client.save(entity);

    verify(dbResilienceSave).save(entity);
    verifyNoMoreInteractions(dbResilienceSave);
  }

  @Test
  void testBatchSave() {
    final MyItem entity = new MyItem("87", "nome");

    client.batchSave(List.of(entity));

    verify(dynamoDbResilienceBatchSave).batchSave(List.of(entity));
    verifyNoMoreInteractions(dynamoDbResilienceBatchSave);
  }

  @Test
  void testDelete() {
    final MyItem entity = new MyItem("87", "nome");

    client.delete(entity);

    verify(dynamoDbResilienceDelete).delete(entity);
    verifyNoMoreInteractions(dynamoDbResilienceDelete);
  }

  @Test
  void testBatchDelete() {
    final MyItem entity = new MyItem("87", "nome");

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
