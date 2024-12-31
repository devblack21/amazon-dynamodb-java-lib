package br.com.devblack21.dynamodb.manager.helper.mock;

public interface CustomMock {

  boolean times(int value);

  boolean never();

  void verify(int times);

  void verifyNoMoreInterations();

  void verifyNeverInterations();


}
