package br.com.devblack21.dynamodb.manager4j.writer.simple.templates;

import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.transform.FailedBatchPutRequestTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.devblack21.dynamodb.manager4j.model.MyItem.getMyItem;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class AbstractBatchSaveManagerTemplate extends AbstractBatchManagerTemplate {

  protected void simulateDynamoDbFailure(final DynamoDBMapper dynamoDBMapper) {
    doThrow(RuntimeException.class).when(dynamoDBMapper).batchSave(anyList());
  }

  protected void simulateFailedBatch(final DynamoDBMapper dynamoDBMapper, final FailedBatchPutRequestTransformer<MyItem> transformer) {

    final List<DynamoDBMapper.FailedBatch> failedBatches = new ArrayList<>();
    final DynamoDBMapper.FailedBatch failedBatch = mock(DynamoDBMapper.FailedBatch.class);
    final Map<String, List<WriteRequest>> unprocessedItems = new HashMap<>();
    final WriteRequest writeRequest = new WriteRequest();
    writeRequest.setPutRequest(new PutRequest().withItem(Map.of("a", new AttributeValue("b"))));
    unprocessedItems.put("TableName", List.of(writeRequest));

    when(failedBatch.getUnprocessedItems()).thenReturn(unprocessedItems);
    failedBatches.add(failedBatch);

    when(transformer.transform(anyList())).thenReturn(List.of(getMyItem()));

    when(dynamoDBMapper.batchSave(anyList()))
      .thenReturn(failedBatches)
      .thenReturn(List.of());
  }

}
