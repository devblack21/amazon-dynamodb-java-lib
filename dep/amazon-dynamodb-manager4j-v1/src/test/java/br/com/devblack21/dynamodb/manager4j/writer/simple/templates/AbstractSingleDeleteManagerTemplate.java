package br.com.devblack21.dynamodb.manager4j.writer.simple.templates;

import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class AbstractSingleDeleteManagerTemplate extends AbstractSingleManagerTemplate {

  protected void simulateDynamoDbFailure(final DynamoDBMapper dynamoDBMapper) {
    doThrow(RuntimeException.class).when(dynamoDBMapper).delete(any(MyItem.class));
  }


}
