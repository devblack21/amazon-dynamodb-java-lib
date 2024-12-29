package br.com.devblack21.dynamodb.resilience.backoff;

public interface BackoffDelayCalculator {

  int delay(int retries);

}
