package br.com.devblack21.dynamodb.manager4j.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class UnprocessedItem {

  private TableEntity entity;

  private UnprocessedItem(final TableEntity entity) {
    this.entity = entity;
  }

  public static UnprocessedItem create(final TableEntity entity) {
    return new UnprocessedItem(entity);
  }

  public static List<UnprocessedItem> unprocessedItems(final List<? extends TableEntity> entities) {
    final List<UnprocessedItem> unprocesseds = new ArrayList<>();

    entities.forEach(entity -> unprocesseds.add(create(entity)));

    return unprocesseds;
  }

  public static List<TableEntity> getEntities(final List<UnprocessedItem> unprocessedItems) {
    return unprocessedItems.stream().map(UnprocessedItem::getEntity).collect(Collectors.toList());
  }

}
