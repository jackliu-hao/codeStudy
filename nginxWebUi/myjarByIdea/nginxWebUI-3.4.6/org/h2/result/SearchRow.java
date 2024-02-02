package org.h2.result;

import org.h2.engine.CastDataProvider;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class SearchRow extends Value {
   public static final int ROWID_INDEX = -1;
   public static long MATCH_ALL_ROW_KEY = -9223372036854775807L;
   public static final int MEMORY_CALCULATE = -1;
   protected long key;

   public abstract int getColumnCount();

   public boolean isNull(int var1) {
      return this.getValue(var1) == ValueNull.INSTANCE;
   }

   public abstract Value getValue(int var1);

   public abstract void setValue(int var1, Value var2);

   public void setKey(long var1) {
      this.key = var1;
   }

   public long getKey() {
      return this.key;
   }

   public abstract int getMemory();

   public abstract void copyFrom(SearchRow var1);

   public TypeInfo getType() {
      return TypeInfo.TYPE_ROW_EMPTY;
   }

   public int getValueType() {
      return 41;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append("ROW (");
      int var3 = 0;

      for(int var4 = this.getColumnCount(); var3 < var4; ++var3) {
         if (var3 != 0) {
            var1.append(", ");
         }

         this.getValue(var3).getSQL(var1, var2);
      }

      return var1.append(')');
   }

   public String getString() {
      return this.getTraceSQL();
   }

   public int hashCode() {
      throw new UnsupportedOperationException();
   }

   public boolean equals(Object var1) {
      throw new UnsupportedOperationException();
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      throw new UnsupportedOperationException();
   }
}
