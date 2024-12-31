package br.com.devblack21.dynamodb.manager4j.resilience;

public interface BackoffDelayAlgorithm {

  int delay(int retries);

}
