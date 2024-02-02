package com.mysql.cj;

import java.util.function.Consumer;

public interface QueryAttributesBindings {
  void setAttribute(String paramString, Object paramObject);
  
  int getCount();
  
  QueryAttributesBindValue getAttributeValue(int paramInt);
  
  void runThroughAll(Consumer<QueryAttributesBindValue> paramConsumer);
  
  void clearAttributes();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\QueryAttributesBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */