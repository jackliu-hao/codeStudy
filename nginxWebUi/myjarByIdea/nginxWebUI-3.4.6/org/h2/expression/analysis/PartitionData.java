package org.h2.expression.analysis;

import java.util.HashMap;
import org.h2.value.Value;

public final class PartitionData {
   private Object data;
   private Value result;
   private HashMap<Integer, Value> orderedResult;

   PartitionData(Object var1) {
      this.data = var1;
   }

   Object getData() {
      return this.data;
   }

   Value getResult() {
      return this.result;
   }

   void setResult(Value var1) {
      this.result = var1;
      this.data = null;
   }

   HashMap<Integer, Value> getOrderedResult() {
      return this.orderedResult;
   }

   void setOrderedResult(HashMap<Integer, Value> var1) {
      this.orderedResult = var1;
      this.data = null;
   }
}
