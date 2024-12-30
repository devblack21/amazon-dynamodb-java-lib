package br.com.devblack21.dynamodb.resilience.helper.mock;

public class FailureRunnableMock extends AbstractMock {

  @Override
  public void run() {
    super.run();
    throw new RuntimeException("");
  }
  
}
