package br.com.devblack21.dynamodb.manager4j.helper.mock;

public class FailureRunnableMock extends AbstractMock {

  @Override
  public void run() {
    super.run();
    throw new RuntimeException("");
  }
  
}
