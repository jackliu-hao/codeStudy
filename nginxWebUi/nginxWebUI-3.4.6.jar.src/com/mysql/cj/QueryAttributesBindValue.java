package com.mysql.cj;

public interface QueryAttributesBindValue {
  boolean isNull();
  
  String getName();
  
  int getType();
  
  Object getValue();
  
  long getBoundLength();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\QueryAttributesBindValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */