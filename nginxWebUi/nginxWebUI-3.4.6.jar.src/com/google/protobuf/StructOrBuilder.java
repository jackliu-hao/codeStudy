package com.google.protobuf;

import java.util.Map;

public interface StructOrBuilder extends MessageOrBuilder {
  int getFieldsCount();
  
  boolean containsFields(String paramString);
  
  @Deprecated
  Map<String, Value> getFields();
  
  Map<String, Value> getFieldsMap();
  
  Value getFieldsOrDefault(String paramString, Value paramValue);
  
  Value getFieldsOrThrow(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\StructOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */