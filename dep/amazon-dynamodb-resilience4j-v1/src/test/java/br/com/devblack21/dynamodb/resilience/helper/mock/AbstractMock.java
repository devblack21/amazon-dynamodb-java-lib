package br.com.devblack21.dynamodb.resilience.helper.mock;

import org.junit.jupiter.api.Assertions;

abstract class AbstractMock implements CustomMock, Runnable {

  private int times = 0;

  protected int getTimes() {
    return times;
  }

  public boolean times(int value) {
    return this.times == value;
  }

  public boolean never() {
    return this.times == 0;
  }

  public void verify(int times) {
    Assertions.assertTrue(times(times));
    this.times = 0;
  }

  public void verifyNoMoreInterations() {
    Assertions.assertTrue(times(0));
    this.times = 0;
  }

  public void verifyNeverInterations() {
    Assertions.assertTrue(never());
  }

  @Override
  public void run() {
    this.times++;
  }

}
