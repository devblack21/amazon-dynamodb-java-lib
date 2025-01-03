package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DynamoDbWriterManagerClient<T extends TableEntity> implements DynamoDbWriterManager<T> {

  private final SaveManager dbResilienceSave;
  private final BatchSaveManager batchSaveManager;
  private final DeleteManager<T> deleteManager;
  private final BatchDeleteManager<T> dynamoDbResilienceBatchDelete;

  @Override
  public void save(final T entity) {
    this.dbResilienceSave.save(entity);
  }

  @Override
  public void batchSave(final List<T> entity) {
    this.batchSaveManager.batchSave(entity);
  }

  @Override
  public void delete(final T entity) {
    this.deleteManager.delete(entity);
  }

  @Override
  public void batchDelete(final List<T> entity) {
    this.dynamoDbResilienceBatchDelete.batchDelete(entity);
  }

}
