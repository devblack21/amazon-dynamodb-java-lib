package br.com.devblack21.dynamodb.manager4j.writer.simple;

import br.com.devblack21.dynamodb.manager4j.configuration.SingleWriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class DeleteManagerAsync extends AbstractAsyncWriter implements DeleteManager {

  private final DynamoDBMapper dynamoDBMapper;

  private DeleteManagerAsync(final Builder builder) {
    super(builder.retryPolicyConfiguration, builder.executorService, builder.requestInterceptor);
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
    private ExecutorService executorService;
    private RequestInterceptor requestInterceptor;

    public Builder(final DynamoDBMapper dynamoDBMapper) {
      this.dynamoDBMapper = dynamoDBMapper;
      validate();
    }

    public Builder retryPolicyConfiguration(final SingleWriteRetryPolicyConfiguration retryPolicyConfiguration) {
      this.retryPolicyConfiguration = retryPolicyConfiguration;
      return this;
    }

    public Builder executorService(final ExecutorService executorService) {
      this.executorService = executorService;
      return this;
    }

    public Builder requestInterceptor(final RequestInterceptor requestInterceptor) {
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    public DeleteManagerAsync build() {
      validate();
      if (executorService == null) {
        executorService = Executors.newCachedThreadPool();
      }
      return new DeleteManagerAsync(this);
    }

    private void validate() {
      if (dynamoDBMapper == null) {
        throw new IllegalArgumentException("DynamoDBMapper é obrigatórios.");
      }
    }
  }

}
