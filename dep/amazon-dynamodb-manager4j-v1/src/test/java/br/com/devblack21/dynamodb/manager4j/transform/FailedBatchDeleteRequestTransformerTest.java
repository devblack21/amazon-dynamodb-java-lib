package br.com.devblack21.dynamodb.manager4j.transform;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FailedBatchDeleteRequestTransformerTest {

  private DynamoDBMapper dynamoDBMapper;
  private FailedBatchDeleteRequestTransformer<MyItem> transformer;

  @BeforeEach
  void setUp() {
    dynamoDBMapper = mock(DynamoDBMapper.class);
    transformer = new FailedBatchDeleteRequestTransformer<>(dynamoDBMapper, MyItem.class);
  }

  @Test
  void testTransformWithSingleDeleteRequest() {
    final Map<String, AttributeValue> keyAttributes = new HashMap<>();
    keyAttributes.put("id", new AttributeValue("Item1"));

    final DeleteRequest deleteRequest = new DeleteRequest().withKey(keyAttributes);
    final WriteRequest writeRequest = new WriteRequest().withDeleteRequest(deleteRequest);

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
  void testTransformWithMultipleDeleteRequests() {
    final Map<String, AttributeValue> keyAttributes1 = new HashMap<>();
    keyAttributes1.put("id", new AttributeValue("Item1"));

    final Map<String, AttributeValue> keyAttributes2 = new HashMap<>();
    keyAttributes2.put("id", new AttributeValue("Item2"));

    final DeleteRequest deleteRequest1 = new DeleteRequest().withKey(keyAttributes1);
    final WriteRequest writeRequest1 = new WriteRequest().withDeleteRequest(deleteRequest1);

    final DeleteRequest deleteRequest2 = new DeleteRequest().withKey(keyAttributes2);
    final WriteRequest writeRequest2 = new WriteRequest().withDeleteRequest(deleteRequest2);

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
    final DeleteRequest deleteRequest = new DeleteRequest().withKey(null);
    final WriteRequest writeRequest = new WriteRequest().withDeleteRequest(deleteRequest);

    final FailedBatch failedBatch = new FailedBatch();
    failedBatch.setUnprocessedItems(Map.of("TableName", List.of(writeRequest)));

    when(dynamoDBMapper.marshallIntoObject(MyItem.class, null)).thenReturn(null);

    final List<MyItem> result = transformer.transform(List.of(failedBatch));

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(nullValue()));
  }

  @EqualsAndHashCode
  class MyItem {
    private final String id;
    private final String name;

    public MyItem(final String id, final String name) {
      this.id = id;
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }
}
