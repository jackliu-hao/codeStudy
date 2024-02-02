package com.google.protobuf;

import java.util.Map;

public interface StructOrBuilder extends MessageOrBuilder {
   int getFieldsCount();

   boolean containsFields(String var1);

   /** @deprecated */
   @Deprecated
   Map<String, Value> getFields();

   Map<String, Value> getFieldsMap();

   Value getFieldsOrDefault(String var1, Value var2);

   Value getFieldsOrThrow(String var1);
}
