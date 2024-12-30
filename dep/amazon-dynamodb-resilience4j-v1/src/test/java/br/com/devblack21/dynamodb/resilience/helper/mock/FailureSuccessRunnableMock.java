package br.com.devblack21.dynamodb.resilience.helper.mock;

public class FailureSuccessRunnableMock extends AbstractMock {

  private final int timeSuccess;

  public FailureSuccessRunnableMock(final int timeSuccess) {
    this.timeSuccess = timeSuccess;
  }

  @Override
  public void run() {
    super.run();
    if (getTimes() != timeSuccess) {
      throw new RuntimeException("");
    }

  }

}
