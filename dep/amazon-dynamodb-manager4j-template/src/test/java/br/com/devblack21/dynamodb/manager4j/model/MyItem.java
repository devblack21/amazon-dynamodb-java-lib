package br.com.devblack21.dynamodb.manager4j.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MyItem implements TableEntity {
  private final String id;
  private final String name;

  public MyItem(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

}