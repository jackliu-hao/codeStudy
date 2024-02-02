package com.mysql.cj;

import java.util.function.Consumer;

public interface QueryAttributesBindings {
   void setAttribute(String var1, Object var2);

   int getCount();

   QueryAttributesBindValue getAttributeValue(int var1);

   void runThroughAll(Consumer<QueryAttributesBindValue> var1);

   void clearAttributes();
}
