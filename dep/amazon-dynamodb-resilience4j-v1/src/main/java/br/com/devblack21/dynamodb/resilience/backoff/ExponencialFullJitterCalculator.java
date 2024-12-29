package br.com.devblack21.dynamodb.resilience.backoff;

import java.util.Random;

public class ExponencialFullJitterCalculator implements BackoffDelayCalculator {

  private final int baseDelay;
  private final int maxBackoff;
  private final Random random;

  public ExponencialFullJitterCalculator(final int baseDelay, final int maxBackoff) {
    this.baseDelay = baseDelay;
    this.maxBackoff = maxBackoff;
    this.random = new Random();
  }

  @Override
  public int delay(final int retries) {
    final int maxDelay = Math.min(this.baseDelay * (1 << retries), this.maxBackoff);
    return this.random.nextInt(maxDelay + 1);
  }

}
