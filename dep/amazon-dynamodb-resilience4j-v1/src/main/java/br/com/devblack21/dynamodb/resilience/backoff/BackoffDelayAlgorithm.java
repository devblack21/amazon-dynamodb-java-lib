package br.com.devblack21.dynamodb.resilience.backoff;

public interface BackoffDelayAlgorithm {

  int delay(int retries);

}
