package br.com.devblack21.dynamodb.manager4j.resilience.algorithm;

public interface BackoffDelayAlgorithm {

  int delay(int retries);

}
