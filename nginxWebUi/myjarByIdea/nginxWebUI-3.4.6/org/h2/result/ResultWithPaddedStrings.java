package org.h2.result;

import java.util.Arrays;
import org.h2.engine.Session;
import org.h2.util.MathUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

public class ResultWithPaddedStrings implements ResultInterface {
   private final ResultInterface source;

   public static ResultInterface get(ResultInterface var0) {
      int var1 = var0.getVisibleColumnCount();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (var0.getColumnType(var2).getValueType() == 1) {
            return new ResultWithPaddedStrings(var0);
         }
      }

      return var0;
   }

   private ResultWithPaddedStrings(ResultInterface var1) {
      this.source = var1;
   }

   public void reset() {
      this.source.reset();
   }

   public Value[] currentRow() {
      int var1 = this.source.getVisibleColumnCount();
      Value[] var2 = (Value[])Arrays.copyOf(this.source.currentRow(), var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         TypeInfo var4 = this.source.getColumnType(var3);
         if (var4.getValueType() == 1) {
            long var5 = var4.getPrecision();
            if (var5 == 2147483647L) {
               var5 = 1L;
            }

            String var7 = var2[var3].getString();
            if (var7 != null && (long)var7.length() < var5) {
               var2[var3] = ValueVarchar.get(rightPadWithSpaces(var7, MathUtils.convertLongToInt(var5)));
            }
         }
      }

      return var2;
   }

   private static String rightPadWithSpaces(String var0, int var1) {
      int var2 = var0.length();
      if (var1 <= var2) {
         return var0;
      } else {
         char[] var3 = new char[var1];
         var0.getChars(0, var2, var3, 0);
         Arrays.fill(var3, var2, var1, ' ');
         return new String(var3);
      }
   }

   public boolean next() {
      return this.source.next();
   }

   public long getRowId() {
      return this.source.getRowId();
   }

   public boolean isAfterLast() {
      return this.source.isAfterLast();
   }

   public int getVisibleColumnCount() {
      return this.source.getVisibleColumnCount();
   }

   public long getRowCount() {
      return this.source.getRowCount();
   }

   public boolean hasNext() {
      return this.source.hasNext();
   }

   public boolean needToClose() {
      return this.source.needToClose();
   }

   public void close() {
      this.source.close();
   }

   public String getAlias(int var1) {
      return this.source.getAlias(var1);
   }

   public String getSchemaName(int var1) {
      return this.source.getSchemaName(var1);
   }

   public String getTableName(int var1) {
      return this.source.getTableName(var1);
   }

   public String getColumnName(int var1) {
      return this.source.getColumnName(var1);
   }

   public TypeInfo getColumnType(int var1) {
      return this.source.getColumnType(var1);
   }

   public boolean isIdentity(int var1) {
      return this.source.isIdentity(var1);
   }

   public int getNullable(int var1) {
      return this.source.getNullable(var1);
   }

   public void setFetchSize(int var1) {
      this.source.setFetchSize(var1);
   }

   public int getFetchSize() {
      return this.source.getFetchSize();
   }

   public boolean isLazy() {
      return this.source.isLazy();
   }

   public boolean isClosed() {
      return this.source.isClosed();
   }

   public ResultInterface createShallowCopy(Session var1) {
      ResultInterface var2 = this.source.createShallowCopy(var1);
      return var2 != null ? new ResultWithPaddedStrings(var2) : null;
   }
}
