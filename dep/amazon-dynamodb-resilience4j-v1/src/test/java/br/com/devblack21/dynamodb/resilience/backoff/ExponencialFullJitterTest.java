package br.com.devblack21.dynamodb.resilience.backoff;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExponencialFullJitterTest {

  @Test
  void testDelayWithinBounds() {
    int baseDelay = 100;
    int maxBackoff = 1000;
    final ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    for (int retries = 0; retries <= 10; retries++) {
      int delay = backoffAlgorithm.delay(retries);

      int maxDelay = Math.min(baseDelay * (1 << retries), maxBackoff);

      assertThat(delay, allOf(
        greaterThanOrEqualTo(0),
        lessThanOrEqualTo(maxDelay)
      ));
    }
  }

  @Test
  void testDelayWithZeroRetries() {
    int baseDelay = 100;
    int maxBackoff = 500;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int delay = backoffAlgorithm.delay(0);

    assertThat(delay, allOf(
      greaterThanOrEqualTo(0),
      lessThanOrEqualTo(baseDelay)
    ));
  }

  @Test
  void testDelayRespectsMaxBackoff() {
    int baseDelay = 100;
    int maxBackoff = 300;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 10;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, allOf(
      greaterThanOrEqualTo(0),
      lessThanOrEqualTo(maxBackoff)
    ));
  }

  @Test
  void testDelayUsesRandomness() {
    int baseDelay = 100;
    int maxBackoff = 1000;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 3;
    int delay1 = backoffAlgorithm.delay(retries);
    int delay2 = backoffAlgorithm.delay(retries);

    assertThat(delay1, not(equalTo(delay2)));
  }

  @Test
  void testDelayWithBaseDelayZero() {
    int baseDelay = 0;
    int maxBackoff = 1000;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 5;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, is(0));
  }

  @Test
  void testDelayWithMaxBackoffZero() {
    int baseDelay = 100;
    int maxBackoff = 0;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 5;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, is(0));
  }

  @Test
  void testDelayWithNegativeRetries() {
    int baseDelay = 100;
    int maxBackoff = 1000;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = -1;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, allOf(
      greaterThanOrEqualTo(0),
      lessThanOrEqualTo(maxBackoff)
    ));
  }

  @Test
  void testDelayForHighRetryCount() {
    int baseDelay = 100;
    int maxBackoff = 1000;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 30;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, allOf(
      greaterThanOrEqualTo(0),
      lessThanOrEqualTo(maxBackoff)
    ));
  }

  @Test
  void testDelayForRandomnessBoundary() {
    int baseDelay = 100;
    int maxBackoff = 1000;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 10;
    int delay = backoffAlgorithm.delay(retries);

    int expectedMaxDelay = Math.min(baseDelay * (1 << retries), maxBackoff);
    assertThat(delay, allOf(
      greaterThanOrEqualTo(0),
      lessThanOrEqualTo(expectedMaxDelay)
    ));
  }

  @Test
  void testDelayHandlesIntegerOverflow() {
    int baseDelay = Integer.MAX_VALUE / 2;
    int maxBackoff = Integer.MAX_VALUE;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 2;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, allOf(
      greaterThanOrEqualTo(0),
      lessThanOrEqualTo(maxBackoff)
    ));
  }

  @Test
  void testDelayConsistentBaseDelay() {
    int baseDelay = 500;
    int maxBackoff = 1000;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    for (int retries = 0; retries <= 10; retries++) {
      int delay = backoffAlgorithm.delay(retries);
      int maxDelay = Math.min(baseDelay * (1 << retries), maxBackoff);

      assertThat(delay, allOf(
        greaterThanOrEqualTo(0),
        lessThanOrEqualTo(maxDelay)
      ));
    }
  }

  @Test
  void testDelayZeroForBothDelaysZero() {
    int baseDelay = 0;
    int maxBackoff = 0;
    ExponencialFullJitter backoffAlgorithm = new ExponencialFullJitter(baseDelay, maxBackoff);

    int retries = 5;
    int delay = backoffAlgorithm.delay(retries);

    assertThat(delay, is(0));
  }
}
