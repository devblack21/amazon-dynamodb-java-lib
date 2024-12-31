package br.com.devblack21.dynamodb.manager.resilience;

public interface BackoffDelayAlgorithm {

  int delay(int retries);

}
