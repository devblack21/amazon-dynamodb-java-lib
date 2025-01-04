package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.configuration.BatchWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.model.UnprocessedItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchDeleteRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import br.com.devblack21.dynamodb.manager4j.writer.simple.BatchDeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDeleteManagerAsync extends AbstractAsyncBatchWriter implements BatchDeleteManager {

  private final DynamoDBMapper dynamoDBMapper;
  private final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedBatchtransformer;


  private BatchDeleteManagerAsync(final Builder builder) {
    super(builder.retryPolicyConfiguration, builder.executorService, builder.requestInterceptor);
    this.dynamoDBMapper = builder.dynamoDBMapper;
    this.failedBatchtransformer = builder.failedBatchTransformer;
  }

  @Override
  public void batchDelete(final List<? extends TableEntity> entities) {
    this.execute(entities);
  }

  @Override
  public List<UnprocessedItem> executor(final List<? extends TableEntity> entities) {
    try {
      return UnprocessedItem.unprocessedItems(this.failedBatchtransformer.transform(dynamoDBMapper.batchDelete(entities)));
    } catch (final Exception e) {
      return UnprocessedItem.unprocessedItems(entities);
    }
  }

  public static class Builder {
    private final DynamoDBMapper dynamoDBMapper;
    private final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedBatchTransformer;
    private BatchWriteRetryPolicyConfiguration retryPolicyConfiguration;
    private ExecutorService executorService;
    private RequestInterceptor requestInterceptor;

    public Builder(final DynamoDBMapper dynamoDBMapper, final FailedBatchDeleteRequestTransformer<? extends TableEntity> failedBatchTransformer) {
      this.dynamoDBMapper = dynamoDBMapper;
      this.failedBatchTransformer = failedBatchTransformer;
      validate();
    }

    public BatchDeleteManagerAsync.Builder retryPolicyConfiguration(final BatchWriteRetryPolicyConfiguration retryPolicyConfiguration) {
      this.retryPolicyConfiguration = retryPolicyConfiguration;
      return this;
    }

    public BatchDeleteManagerAsync.Builder executorService(final ExecutorService executorService) {
      this.executorService = executorService;
      return this;
    }

    public BatchDeleteManagerAsync.Builder requestInterceptor(final RequestInterceptor requestInterceptor) {
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    public BatchDeleteManagerAsync build() {
      validate();
      if (this.executorService == null) {
        this.executorService = Executors.newCachedThreadPool();
      }
      return new BatchDeleteManagerAsync(this);
    }

    private void validate() {
      if (dynamoDBMapper == null || failedBatchTransformer == null) {
        throw new IllegalArgumentException("DynamoDBMapper e FailedBatchTransformer são obrigatórios.");
      }
    }
  }

}
