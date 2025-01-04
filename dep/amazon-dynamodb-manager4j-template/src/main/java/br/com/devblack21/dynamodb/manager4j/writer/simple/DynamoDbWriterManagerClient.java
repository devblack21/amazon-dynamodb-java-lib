package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class DynamoDbWriterManagerClient implements DynamoDbWriterManager {

  private final SaveManager dbResilienceSave;
  private final BatchSaveManager batchSaveManager;
  private final DeleteManager deleteManager;
  private final BatchDeleteManager dynamoDbResilienceBatchDelete;

  @Override
  public void save(final TableEntity entity) {
    this.dbResilienceSave.save(entity);
  }

  @Override
  public void batchSave(final List<? extends TableEntity> entity) {
    this.batchSaveManager.batchSave(entity);
  }

  @Override
  public void delete(final TableEntity entity) {
    this.deleteManager.delete(entity);
  }

  @Override
  public void batchDelete(final List<? extends TableEntity> entity) {
    this.dynamoDbResilienceBatchDelete.batchDelete(entity);
  }

}
