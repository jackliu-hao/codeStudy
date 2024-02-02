package org.h2.util.json;

import java.math.BigDecimal;
import java.util.ArrayDeque;

public final class JSONValueTarget extends JSONTarget<JSONValue> {
   private final ArrayDeque<JSONValue> stack = new ArrayDeque();
   private final ArrayDeque<String> names = new ArrayDeque();
   private boolean needSeparator;
   private String memberName;
   private JSONValue result;

   public void startObject() {
      this.beforeValue();
      this.names.push(this.memberName != null ? this.memberName : "");
      this.memberName = null;
      this.stack.push(new JSONObject());
   }

   public void endObject() {
      if (this.memberName != null) {
         throw new IllegalStateException();
      } else {
         JSONValue var1 = (JSONValue)this.stack.poll();
         if (!(var1 instanceof JSONObject)) {
            throw new IllegalStateException();
         } else {
            this.memberName = (String)this.names.pop();
            this.afterValue(var1);
         }
      }
   }

   public void startArray() {
      this.beforeValue();
      this.names.push(this.memberName != null ? this.memberName : "");
      this.memberName = null;
      this.stack.push(new JSONArray());
   }

   public void endArray() {
      JSONValue var1 = (JSONValue)this.stack.poll();
      if (!(var1 instanceof JSONArray)) {
         throw new IllegalStateException();
      } else {
         this.memberName = (String)this.names.pop();
         this.afterValue(var1);
      }
   }

   public void member(String var1) {
      if (this.memberName == null && this.stack.peek() instanceof JSONObject) {
         this.memberName = var1;
         this.beforeValue();
      } else {
         throw new IllegalStateException();
      }
   }

   public void valueNull() {
      this.beforeValue();
      this.afterValue(JSONNull.NULL);
   }

   public void valueFalse() {
      this.beforeValue();
      this.afterValue(JSONBoolean.FALSE);
   }

   public void valueTrue() {
      this.beforeValue();
      this.afterValue(JSONBoolean.TRUE);
   }

   public void valueNumber(BigDecimal var1) {
      this.beforeValue();
      this.afterValue(new JSONNumber(var1));
   }

   public void valueString(String var1) {
      this.beforeValue();
      this.afterValue(new JSONString(var1));
   }

   private void beforeValue() {
      if (this.memberName == null && this.stack.peek() instanceof JSONObject) {
         throw new IllegalStateException();
      } else {
         if (this.needSeparator) {
            if (this.stack.isEmpty()) {
               throw new IllegalStateException();
            }

            this.needSeparator = false;
         }

      }
   }

   private void afterValue(JSONValue var1) {
      JSONValue var2 = (JSONValue)this.stack.peek();
      if (var2 == null) {
         this.result = var1;
      } else if (var2 instanceof JSONObject) {
         ((JSONObject)var2).addMember(this.memberName, var1);
      } else {
         ((JSONArray)var2).addElement(var1);
      }

      this.needSeparator = true;
      this.memberName = null;
   }

   public boolean isPropertyExpected() {
      return this.memberName == null && this.stack.peek() instanceof JSONObject;
   }

   public boolean isValueSeparatorExpected() {
      return this.needSeparator;
   }

   public JSONValue getResult() {
      if (this.stack.isEmpty() && this.result != null) {
         return this.result;
      } else {
         throw new IllegalStateException();
      }
   }
}
