package br.com.devblack21.dynamodb.manager4j.transform;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FailedBatchPutRequestTransformerTest {

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchPutRequestTransformer<MyItem> transformer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = new FailedBatchPutRequestTransformer<>(dynamoDBMapper, MyItem.class);
  }

  @Test
  void testTransformWithSinglePutRequest() {
    final Map<String, AttributeValue> keyAttributes = new HashMap<>();
    keyAttributes.put("id", new AttributeValue("Item1"));

    final PutRequest putRequest = new PutRequest().withItem(keyAttributes);
    final WriteRequest writeRequest = new WriteRequest().withPutRequest(putRequest);

    final FailedBatch failedBatch = new FailedBatch();
    failedBatch.setUnprocessedItems(Map.of("TableName", List.of(writeRequest)));

    final MyItem expectedItem = new MyItem("Item1", null);
    when(dynamoDBMapper.marshallIntoObject(MyItem.class, keyAttributes)).thenReturn(expectedItem);

    final List<MyItem> result = transformer.transform(List.of(failedBatch));

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getId(), is("Item1"));
    assertThat(result.get(0).getName(), is(nullValue()));
  }

  @Test
  void testTransformWithMultiplePutRequests() {
    final Map<String, AttributeValue> keyAttributes1 = new HashMap<>();
    keyAttributes1.put("id", new AttributeValue("Item1"));

    final Map<String, AttributeValue> keyAttributes2 = new HashMap<>();
    keyAttributes2.put("id", new AttributeValue("Item2"));

    final PutRequest putRequest1 = new PutRequest().withItem(keyAttributes1);
    final WriteRequest writeRequest1 = new WriteRequest().withPutRequest(putRequest1);

    final PutRequest putRequest2 = new PutRequest().withItem(keyAttributes2);
    final WriteRequest writeRequest2 = new WriteRequest().withPutRequest(putRequest2);

    final FailedBatch failedBatch = new FailedBatch();
    failedBatch.setUnprocessedItems(Map.of("TableName", List.of(writeRequest1, writeRequest2)));

    final MyItem expectedItem1 = new MyItem("Item1", "nome1");
    final MyItem expectedItem2 = new MyItem("Item2", "nome2");

    when(dynamoDBMapper.marshallIntoObject(MyItem.class, keyAttributes1)).thenReturn(expectedItem1);
    when(dynamoDBMapper.marshallIntoObject(MyItem.class, keyAttributes2)).thenReturn(expectedItem2);

    final List<MyItem> result = transformer.transform(List.of(failedBatch));

    assertThat(result, containsInAnyOrder(
      new MyItem("Item1", "nome1"),
      new MyItem("Item2", "nome2")
    ));
  }

  @Test
  void testTransformWithNoUnprocessedItems() {
    final FailedBatch failedBatch = new FailedBatch();
    failedBatch.setUnprocessedItems(Map.of());

    final List<MyItem> result = transformer.transform(List.of(failedBatch));

    assertThat(result, is(empty()));
  }

  @Test
  void testTransformWithEmptyFailedBatches() {
    final List<MyItem> result = transformer.transform(List.of());

    assertThat(result, is(empty()));
  }

  @Test
  void testTransformWithNullKeyAttributes() {
    final PutRequest putRequest = new PutRequest().withItem(null);
    final WriteRequest writeRequest = new WriteRequest().withPutRequest(putRequest);

    final FailedBatch failedBatch = new FailedBatch();
    failedBatch.setUnprocessedItems(Map.of("TableName", List.of(writeRequest)));

    when(dynamoDBMapper.marshallIntoObject(MyItem.class, null)).thenReturn(null);

    final List<MyItem> result = transformer.transform(List.of(failedBatch));

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(nullValue()));
  }

  @Getter
  @EqualsAndHashCode
  static class MyItem {
    private final String id;
    private final String name;

    public MyItem(final String id, final String name) {
      this.id = id;
      this.name = name;
    }

  }
}
