package br.com.devblack21.dynamodb.manager4j.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class UnprocessedItem<T> {

  private T entity;

  private UnprocessedItem(final T entity) {
    this.entity = entity;
  }

  private static <T> UnprocessedItem<T> create(final T entity) {
    return new UnprocessedItem<>(entity);
  }

  public static <T> List<UnprocessedItem<T>> unprocessedItems(final List<T> entities) {
    final List<UnprocessedItem<T>> unprocesseds = new ArrayList<>();

    entities.forEach(entity -> unprocesseds.add(create(entity)));

    return unprocesseds;
  }


  public static <T> List<T> getEntities(final List<UnprocessedItem<T>> unprocessedItems) {
    return unprocessedItems.stream().map(UnprocessedItem::getEntity).collect(Collectors.toList());
  }

}
