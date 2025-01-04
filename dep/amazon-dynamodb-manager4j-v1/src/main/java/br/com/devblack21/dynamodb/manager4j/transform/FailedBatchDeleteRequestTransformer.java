package br.com.devblack21.dynamodb.manager4j.transform;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FailedBatchDeleteRequestTransformer<T extends TableEntity> {

  private final DynamoDBMapper dynamoDBMapper;
  private final Class<T> ormClass;

  public List<T> transform(final List<FailedBatch> failedBatches) {
    final List<T> ormObjects = new ArrayList<>();

    for (final FailedBatch failedBatch : failedBatches) {
      final Map<String, List<WriteRequest>> unprocessedItems = failedBatch.getUnprocessedItems();

      for (final List<WriteRequest> writeRequests : unprocessedItems.values()) {
        for (final WriteRequest writeRequest : writeRequests) {
          if (writeRequest.getDeleteRequest() != null) {
            final Map<String, AttributeValue> itemAttributes = writeRequest.getDeleteRequest().getKey();
            final T ormObject = dynamoDBMapper.marshallIntoObject(ormClass, itemAttributes);
            ormObjects.add(ormObject);
          }
        }
      }
    }

    return ormObjects;
  }

}
