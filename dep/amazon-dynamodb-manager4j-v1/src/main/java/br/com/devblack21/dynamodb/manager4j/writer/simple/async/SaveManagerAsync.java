package br.com.devblack21.dynamodb.manager4j.writer.simple.async;

import br.com.devblack21.dynamodb.manager4j.configuration.WriteRetryPolicyConfiguration;
import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaveManagerAsync extends AbstractAsyncWriter implements SaveManager {

  private final DynamoDBMapper dynamoDBMapper;

  private SaveManagerAsync(final Builder builder) {
    super(builder.retryPolicyConfiguration, builder.executorService, builder.requestInterceptor);
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
    private WriteRetryPolicyConfiguration retryPolicyConfiguration;
    private ExecutorService executorService;
    private RequestInterceptor requestInterceptor;

    public Builder(final DynamoDBMapper dynamoDBMapper) {
      this.dynamoDBMapper = dynamoDBMapper;
      validate();
    }

    public Builder retryPolicyConfiguration(final WriteRetryPolicyConfiguration retryPolicyConfiguration) {
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

    public SaveManagerAsync build() {
      validate();
      if (executorService == null) {
        executorService = Executors.newCachedThreadPool();
      }
      return new SaveManagerAsync(this);
    }

    private void validate() {
      if (dynamoDBMapper == null) {
        throw new IllegalArgumentException("DynamoDBMapper é obrigatórios.");
      }
    }
  }
}
