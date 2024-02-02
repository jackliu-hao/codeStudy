package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionInterceptor;

public interface PropertyDefinition<T> {
  boolean hasValueConstraints();
  
  boolean isRangeBased();
  
  PropertyKey getPropertyKey();
  
  String getName();
  
  String getCcAlias();
  
  boolean hasCcAlias();
  
  T getDefaultValue();
  
  boolean isRuntimeModifiable();
  
  String getDescription();
  
  String getSinceVersion();
  
  String getCategory();
  
  int getOrder();
  
  String[] getAllowableValues();
  
  int getLowerBound();
  
  int getUpperBound();
  
  T parseObject(String paramString, ExceptionInterceptor paramExceptionInterceptor);
  
  RuntimeProperty<T> createRuntimeProperty();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\PropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */