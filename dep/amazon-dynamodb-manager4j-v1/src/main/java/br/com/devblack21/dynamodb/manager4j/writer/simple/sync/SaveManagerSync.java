//package br.com.devblack21.dynamodb.manager4j.writer.simple.sync;
//
//import br.com.devblack21.dynamodb.manager4j.interceptor.RequestInterceptor;
//import br.com.devblack21.dynamodb.manager4j.resilience.backoff.single.BackoffSingleWriteExecutor;
//import br.com.devblack21.dynamodb.manager4j.resilience.recover.ErrorRecoverer;
//import br.com.devblack21.dynamodb.manager4j.writer.simple.SaveManager;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//
//public class SaveManagerSync<T> extends AbstractSyncWriter<T> implements SaveManager<T> {
//
//  private final DynamoDBMapper dynamoDBMapper;
//
//  public SaveManagerSync(final DynamoDBMapper dynamoDBMapper,
//                         final BackoffSingleWriteExecutor backoffExecutor,
//                         final ErrorRecoverer<T> errorRecoverer,
//                         final RequestInterceptor<T> requestInterceptor) {
//    super(backoffExecutor, errorRecoverer, requestInterceptor);
//    this.dynamoDBMapper = dynamoDBMapper;
//  }
//
//
//  @Override
//  public void save(final T entity) {
//    this.execute(entity);
//  }
//
//  @Override
//  public void executor(final T entity) {
//    this.dynamoDBMapper.save(entity);
//  }
//
//}
