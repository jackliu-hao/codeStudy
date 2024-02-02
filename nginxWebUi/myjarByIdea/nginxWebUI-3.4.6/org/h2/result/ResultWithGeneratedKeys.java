package org.h2.result;

public class ResultWithGeneratedKeys {
   private final long updateCount;

   public static ResultWithGeneratedKeys of(long var0) {
      return new ResultWithGeneratedKeys(var0);
   }

   ResultWithGeneratedKeys(long var1) {
      this.updateCount = var1;
   }

   public ResultInterface getGeneratedKeys() {
      return null;
   }

   public long getUpdateCount() {
      return this.updateCount;
   }

   public static final class WithKeys extends ResultWithGeneratedKeys {
      private final ResultInterface generatedKeys;

      public WithKeys(long var1, ResultInterface var3) {
         super(var1);
         this.generatedKeys = var3;
      }

      public ResultInterface getGeneratedKeys() {
         return this.generatedKeys;
      }
   }
}
