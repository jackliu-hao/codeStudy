package org.h2.util.json;

import java.math.BigDecimal;
import org.h2.util.ByteStack;

public final class JSONValidationTargetWithoutUniqueKeys extends JSONValidationTarget {
   private static final byte OBJECT = 1;
   private static final byte ARRAY = 2;
   private JSONItemType type;
   private final ByteStack stack = new ByteStack();
   private boolean needSeparator;
   private boolean afterName;

   public void startObject() {
      this.beforeValue();
      this.afterName = false;
      this.stack.push((byte)1);
   }

   public void endObject() {
      if (!this.afterName && this.stack.poll(-1) == 1) {
         this.afterValue(JSONItemType.OBJECT);
      } else {
         throw new IllegalStateException();
      }
   }

   public void startArray() {
      this.beforeValue();
      this.afterName = false;
      this.stack.push((byte)2);
   }

   public void endArray() {
      if (this.stack.poll(-1) != 2) {
         throw new IllegalStateException();
      } else {
         this.afterValue(JSONItemType.ARRAY);
      }
   }

   public void member(String var1) {
      if (!this.afterName && this.stack.peek(-1) == 1) {
         this.afterName = true;
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
      if (!this.afterName && this.stack.peek(-1) == 1) {
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
      this.needSeparator = true;
      this.afterName = false;
      if (this.stack.isEmpty()) {
         this.type = var1;
      }

   }

   public boolean isPropertyExpected() {
      return !this.afterName && this.stack.peek(-1) == 1;
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
