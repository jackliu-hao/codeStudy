package com.mysql.cj.conf;

import java.util.Properties;

public interface PropertySet {
  void addProperty(RuntimeProperty<?> paramRuntimeProperty);
  
  void removeProperty(String paramString);
  
  void removeProperty(PropertyKey paramPropertyKey);
  
  <T> RuntimeProperty<T> getProperty(String paramString);
  
  <T> RuntimeProperty<T> getProperty(PropertyKey paramPropertyKey);
  
  RuntimeProperty<Boolean> getBooleanProperty(String paramString);
  
  RuntimeProperty<Boolean> getBooleanProperty(PropertyKey paramPropertyKey);
  
  RuntimeProperty<Integer> getIntegerProperty(String paramString);
  
  RuntimeProperty<Integer> getIntegerProperty(PropertyKey paramPropertyKey);
  
  RuntimeProperty<Long> getLongProperty(String paramString);
  
  RuntimeProperty<Long> getLongProperty(PropertyKey paramPropertyKey);
  
  RuntimeProperty<Integer> getMemorySizeProperty(String paramString);
  
  RuntimeProperty<Integer> getMemorySizeProperty(PropertyKey paramPropertyKey);
  
  RuntimeProperty<String> getStringProperty(String paramString);
  
  RuntimeProperty<String> getStringProperty(PropertyKey paramPropertyKey);
  
  <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(String paramString);
  
  <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(PropertyKey paramPropertyKey);
  
  void initializeProperties(Properties paramProperties);
  
  void postInitialization();
  
  Properties exposeAsProperties();
  
  void reset();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\PropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */