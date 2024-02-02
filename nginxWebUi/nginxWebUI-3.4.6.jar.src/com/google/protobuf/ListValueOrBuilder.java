package com.google.protobuf;

import java.util.List;

public interface ListValueOrBuilder extends MessageOrBuilder {
  List<Value> getValuesList();
  
  Value getValues(int paramInt);
  
  int getValuesCount();
  
  List<? extends ValueOrBuilder> getValuesOrBuilderList();
  
  ValueOrBuilder getValuesOrBuilder(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ListValueOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */