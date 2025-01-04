package br.com.devblack21.dynamodb.manager4j.resilience.algorithm;

import java.util.Random;

public final class ExponencialFullJitter implements BackoffDelayAlgorithm {

  private final int baseDelay;
  private final int maxBackoff;
  private final Random random;

  public ExponencialFullJitter(final int baseDelay, final int maxBackoff) {
    this.baseDelay = baseDelay;
    this.maxBackoff = maxBackoff;
    this.random = new Random();
  }

  @Override
  public int delay(final int retries) {
    final int maxDelay = Math.min(this.baseDelay * (1 << retries), this.maxBackoff);
    return this.random.nextInt(this.getBound(maxDelay + 1));
  }

  private int getBound(final int maxDelay) {
    return Math.max(maxDelay, 1);
  }

}
