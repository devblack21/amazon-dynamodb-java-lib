package br.com.devblack21.dynamodb.manager4j.writer.simple.templates;

import br.com.devblack21.dynamodb.manager4j.model.MyItem;
import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;

public abstract class AbstractManagerTemplate {

  protected void simulateRecoveryFailure(final ErrorRecoverer errorRecoverer) {
    doThrow(RuntimeException.class).when(errorRecoverer).recover(anyList());
    doThrow(RuntimeException.class).when(errorRecoverer).recover(any(MyItem.class));
  }


}
