package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.writer.simple.DeleteManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DeleteManagerSync extends AbstractSyncWriter implements DeleteManager {

  private final DynamoDBMapper dynamoDBMapper;

  private DeleteManagerSync(final Builder builder) {
    super(builder.retryPolicyConfiguration, builder.requestInterceptor);
    this.dynamoDBMapper = builder.dynamoDBMapper;
  }

  @Override
  public void delete(final TableEntity entity) {
    this.execute(entity);
  }

  @Override
  public void executor(final TableEntity entity) {
    this.dynamoDBMapper.delete(entity);
  }

  public static class Builder {
    private final DynamoDBMapper dynamoDBMapper;
    private SingleWriteRetryPolicyConfiguration retryPolicyConfiguration;
    private RequestInterceptor requestInterceptor;

    public Builder(final DynamoDBMapper dynamoDBMapper) {
      this.dynamoDBMapper = dynamoDBMapper;
      validate();
    }

    public Builder retryPolicyConfiguration(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration) {
      this.retryPolicyConfiguration = retryPolicyConfiguration;
      return this;
    }

    public Builder requestInterceptor(final RequestInterceptor requestInterceptor) {
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    public DeleteManagerSync build() {
      validate();
      return new DeleteManagerSync(this);
    }

    private void validate() {
      if (dynamoDBMapper == null) {
        throw new IllegalArgumentException("DynamoDBMapper é obrigatórios.");
      }
    }
  }

}
