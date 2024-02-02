package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import java.util.Properties;
import javax.naming.Reference;

public interface RuntimeProperty<T> {
  PropertyDefinition<T> getPropertyDefinition();
  
  void initializeFrom(Properties paramProperties, ExceptionInterceptor paramExceptionInterceptor);
  
  void initializeFrom(Reference paramReference, ExceptionInterceptor paramExceptionInterceptor);
  
  void resetValue();
  
  boolean isExplicitlySet();
  
  void addListener(RuntimePropertyListener paramRuntimePropertyListener);
  
  void removeListener(RuntimePropertyListener paramRuntimePropertyListener);
  
  T getValue();
  
  T getInitialValue();
  
  String getStringValue();
  
  void setValue(T paramT);
  
  void setValue(T paramT, ExceptionInterceptor paramExceptionInterceptor);
  
  @FunctionalInterface
  public static interface RuntimePropertyListener {
    void handlePropertyChange(RuntimeProperty<?> param1RuntimeProperty);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\RuntimeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */