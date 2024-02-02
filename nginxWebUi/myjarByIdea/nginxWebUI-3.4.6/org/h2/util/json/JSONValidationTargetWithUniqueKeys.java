package org.h2.util.json;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class JSONValidationTargetWithUniqueKeys extends JSONValidationTarget {
   private final ArrayDeque<Object> stack = new ArrayDeque();
   private final ArrayDeque<String> names = new ArrayDeque();
   private boolean needSeparator;
   private String memberName;
   private JSONItemType type;

   public void startObject() {
      this.beforeValue();
      this.names.push(this.memberName != null ? this.memberName : "");
      this.memberName = null;
      this.stack.push(new HashSet());
   }

   public void endObject() {
      if (this.memberName != null) {
         throw new IllegalStateException();
      } else if (!(this.stack.poll() instanceof HashSet)) {
         throw new IllegalStateException();
      } else {
         this.memberName = (String)this.names.pop();
         this.afterValue(JSONItemType.OBJECT);
      }
   }

   public void startArray() {
      this.beforeValue();
      this.names.push(this.memberName != null ? this.memberName : "");
      this.memberName = null;
      this.stack.push(Collections.emptyList());
   }

   public void endArray() {
      if (!(this.stack.poll() instanceof List)) {
         throw new IllegalStateException();
      } else {
         this.memberName = (String)this.names.pop();
         this.afterValue(JSONItemType.ARRAY);
      }
   }

   public void member(String var1) {
      if (this.memberName == null && this.stack.peek() instanceof HashSet) {
         this.memberName = var1;
         this.beforeValue();
      } else {
         throw new IllegalStateException();
      }
   }

   public void valueNull() {
      this.beforeValue();
      this.afterValue(JSONItemType.SCALAR);
   }

   public void valueFalse() {
      this.beforeValue();
      this.afterValue(JSONItemType.SCALAR);
   }

   public void valueTrue() {
      this.beforeValue();
      this.afterValue(JSONItemType.SCALAR);
   }

   public void valueNumber(BigDecimal var1) {
      this.beforeValue();
      this.afterValue(JSONItemType.SCALAR);
   }

   public void valueString(String var1) {
      this.beforeValue();
      this.afterValue(JSONItemType.SCALAR);
   }

   private void beforeValue() {
      if (this.memberName == null && this.stack.peek() instanceof HashSet) {
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

   private void afterValue(JSONItemType var1) {
      Object var2 = this.stack.peek();
      if (var2 == null) {
         this.type = var1;
      } else if (var2 instanceof HashSet && !((HashSet)var2).add(this.memberName)) {
         throw new IllegalStateException();
      }

      this.needSeparator = true;
      this.memberName = null;
   }

   public boolean isPropertyExpected() {
      return this.memberName == null && this.stack.peek() instanceof HashSet;
   }

   public boolean isValueSeparatorExpected() {
      return this.needSeparator;
   }

   public JSONItemType getResult() {
      if (this.stack.isEmpty() && this.type != null) {
         return this.type;
      } else {
         throw new IllegalStateException();
      }
   }
}
