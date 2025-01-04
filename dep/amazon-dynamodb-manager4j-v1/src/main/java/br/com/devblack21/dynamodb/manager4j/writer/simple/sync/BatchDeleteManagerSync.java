package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;

public class BatchDeleteManagerSync extends AbstractSyncBatchWriter implements BatchDeleteManager {

  private final DynamoDBMapper dynamoDBMapper;
  private final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedBatchtransformer;


  private BatchDeleteManagerSync(final Builder builder) {
    super(builder.retryPolicyConfiguration, builder.requestInterceptor);
    this.dynamoDBMapper = builder.dynamoDBMapper;
    this.failedBatchtransformer = builder.failedBatchTransformer;
  }


  @Override
  public void batchDelete(final List<? extends TableEntity> entity) {
    this.execute(entity);
  }

  @Override
  public List<UnprocessedItem> executor(final List<? extends TableEntity> entity) {
    try {
      return UnprocessedItem.unprocessedItems(this.failedBatchtransformer.transform(dynamoDBMapper.batchDelete(entity)));
    } catch (final Exception e) {
      return UnprocessedItem.unprocessedItems(entity);
    }
  }

  public static class Builder {
    private final DynamoDBMapper dynamoDBMapper;
    private final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedBatchTransformer;
    private BatchWriteRetryPolicyConfiguration retryPolicyConfiguration;
    private RequestInterceptor requestInterceptor;

    public Builder(final DynamoDBMapper dynamoDBMapper, final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedBatchTransformer) {
      this.dynamoDBMapper = dynamoDBMapper;
      this.failedBatchTransformer = failedBatchTransformer;
      validate();
    }

    public Builder retryPolicyConfiguration(final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration) {
      this.retryPolicyConfiguration = retryPolicyConfiguration;
      return this;
    }

    public Builder requestInterceptor(final RequestInterceptor requestInterceptor) {
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    public BatchDeleteManagerSync build() {
      validate();
      return new BatchDeleteManagerSync(this);
    }


    private void validate() {
      if (dynamoDBMapper == null || failedBatchTransformer == null) {
        throw new IllegalArgumentException("DynamoDBMapper e FailedBatchTransformer são obrigatórios.");
      }
    }
  }

}
