package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class SaveManagerSync extends AbstractSyncWriter implements SaveManager {

  private final DynamoDBMapper dynamoDBMapper;

  private SaveManagerSync(final Builder builder) {
    super(builder.retryPolicyConfiguration, builder.requestInterceptor);
    this.dynamoDBMapper = builder.dynamoDBMapper;
  }

  @Override
  public void save(final TableEntity entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final TableEntity entity) {
    this.dynamoDBMapper.save(entity);
  }

  public static class Builder {
    private final DynamoDBMapper dynamoDBMapper;
    private SingleWriteRetryPolicyConfiguration retryPolicyConfiguration;
    private RequestInterceptor requestInterceptor;

    public Builder(final DynamoDBMapper dynamoDBMapper) {
      this.dynamoDBMapper = dynamoDBMapper;
      validate();
    }

    public SaveManagerSync.Builder retryPolicyConfiguration(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration) {
      this.retryPolicyConfiguration = retryPolicyConfiguration;
      return this;
    }

    public SaveManagerSync.Builder requestInterceptor(final RequestInterceptor requestInterceptor) {
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    public SaveManagerSync build() {
      validate();
      return new SaveManagerSync(this);
    }

    private void validate() {
      if (dynamoDBMapper == null) {
        throw new IllegalArgumentException("DynamoDBMapper é obrigatórios.");
      }
    }
  }

}
