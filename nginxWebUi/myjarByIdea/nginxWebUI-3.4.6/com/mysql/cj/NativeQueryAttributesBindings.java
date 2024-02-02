package com.mysql.cj;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NativeQueryAttributesBindings implements QueryAttributesBindings {
   private List<NativeQueryAttributesBindValue> bindAttributes = new ArrayList();

   public void setAttribute(String name, Object value) {
      this.bindAttributes.add(new NativeQueryAttributesBindValue(name, value));
   }

   public int getCount() {
      return this.bindAttributes.size();
   }

   public QueryAttributesBindValue getAttributeValue(int index) {
      return (QueryAttributesBindValue)this.bindAttributes.get(index);
   }

   public void runThroughAll(Consumer<QueryAttributesBindValue> bindAttribute) {
      this.bindAttributes.forEach(bindAttribute::accept);
   }

   public void clearAttributes() {
      this.bindAttributes.clear();
   }
}
