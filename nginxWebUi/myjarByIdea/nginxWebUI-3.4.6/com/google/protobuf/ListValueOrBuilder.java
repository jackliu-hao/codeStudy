package com.google.protobuf;

import java.util.List;

public interface ListValueOrBuilder extends MessageOrBuilder {
   List<Value> getValuesList();

   Value getValues(int var1);

   int getValuesCount();

   List<? extends ValueOrBuilder> getValuesOrBuilderList();

   ValueOrBuilder getValuesOrBuilder(int var1);
}
