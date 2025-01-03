package br.com.devblack21.dynamodb.manager4j.exception;

import lombok.Getter;

@Getter
public class UnprocessedItemsExceptions extends RuntimeException {

  private final Object unprocessedItems;

  public UnprocessedItemsExceptions(final Object unprocessedItems) {
    this.unprocessedItems = unprocessedItems;
  }


}
