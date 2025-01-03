package br.com.devblack21.dynamodb.manager4j.resilience.recover;

import br.com.devblack21.dynamodb.manager4j.model.TableEntity;

import java.util.List;

public interface ErrorRecoverer {

  void recover(TableEntity object);

  void recover(List<? extends TableEntity> object);


}
