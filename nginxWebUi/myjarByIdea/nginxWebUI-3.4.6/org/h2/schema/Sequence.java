package org.h2.schema;

import org.h2.command.ddl.SequenceOptions;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

public final class Sequence extends SchemaObject {
   public static final int DEFAULT_CACHE_SIZE = 32;
   private long baseValue;
   private long margin;
   private TypeInfo dataType;
   private long increment;
   private long cacheSize;
   private long startValue;
   private long minValue;
   private long maxValue;
   private Cycle cycle;
   private boolean belongsToTable;
   private boolean writeWithMargin;

   public Sequence(SessionLocal var1, Schema var2, int var3, String var4, SequenceOptions var5, boolean var6) {
      super(var2, var3, var4, 9);
      this.dataType = var5.getDataType();
      if (this.dataType == null) {
         var5.setDataType(this.dataType = var1.getMode().decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT);
      }

      long[] var7 = var5.getBounds();
      Long var8 = var5.getIncrement(var1);
      long var9 = var8 != null ? var8 : 1L;
      Long var11 = var5.getStartValue(var1);
      Long var12 = var5.getMinValue((Sequence)null, var1);
      Long var13 = var5.getMaxValue((Sequence)null, var1);
      long var14 = var12 != null ? var12 : getDefaultMinValue(var11, var9, var7);
      long var16 = var13 != null ? var13 : getDefaultMaxValue(var11, var9, var7);
      long var18 = var11 != null ? var11 : (var9 >= 0L ? var14 : var16);
      Long var20 = var5.getRestartValue(var1, var18);
      long var21 = var20 != null ? var20 : var18;
      var8 = var5.getCacheSize(var1);
      long var23;
      boolean var25;
      if (var8 != null) {
         var23 = var8;
         var25 = false;
      } else {
         var23 = 32L;
         var25 = true;
      }

      var23 = this.checkOptions(var21, var18, var14, var16, var9, var23, var25);
      Cycle var26 = var5.getCycle();
      if (var26 == null) {
         var26 = Sequence.Cycle.NO_CYCLE;
      } else if (var26 == Sequence.Cycle.EXHAUSTED) {
         var21 = var18;
      }

      this.margin = this.baseValue = var21;
      this.increment = var9;
      this.cacheSize = var23;
      this.startValue = var18;
      this.minValue = var14;
      this.maxValue = var16;
      this.cycle = var26;
      this.belongsToTable = var6;
   }

   public synchronized void modify(Long var1, Long var2, Long var3, Long var4, Long var5, Cycle var6, Long var7) {
      long var8 = var1 != null ? var1 : this.baseValue;
      long var10 = var2 != null ? var2 : this.startValue;
      long var12 = var3 != null ? var3 : this.minValue;
      long var14 = var4 != null ? var4 : this.maxValue;
      long var16 = var5 != null ? var5 : this.increment;
      long var18;
      boolean var20;
      if (var7 != null) {
         var18 = var7;
         var20 = false;
      } else {
         var18 = this.cacheSize;
         var20 = true;
      }

      var18 = this.checkOptions(var8, var10, var12, var14, var16, var18, var20);
      if (var6 == null) {
         var6 = this.cycle;
         if (var6 == Sequence.Cycle.EXHAUSTED && var1 != null) {
            var6 = Sequence.Cycle.NO_CYCLE;
         }
      } else if (var6 == Sequence.Cycle.EXHAUSTED) {
         var8 = var10;
      }

      this.margin = this.baseValue = var8;
      this.startValue = var10;
      this.minValue = var12;
      this.maxValue = var14;
      this.increment = var16;
      this.cacheSize = var18;
      this.cycle = var6;
   }

   private long checkOptions(long var1, long var3, long var5, long var7, long var9, long var11, boolean var13) {
      if (var5 <= var1 && var1 <= var7 && var5 <= var3 && var3 <= var7 && var5 < var7 && var9 != 0L) {
         long var14 = var7 - var5;
         if (Long.compareUnsigned(Math.abs(var9), var14) <= 0 && var11 >= 0L) {
            if (var11 <= 1L) {
               return 1L;
            }

            long var16 = getMaxCacheSize(var14, var9);
            if (var11 <= var16) {
               return var11;
            }

            if (var13) {
               return var16;
            }
         }
      }

      throw DbException.get(90009, this.getName(), Long.toString(var1), Long.toString(var3), Long.toString(var5), Long.toString(var7), Long.toString(var9), Long.toString(var11));
   }

   private static long getMaxCacheSize(long var0, long var2) {
      if (var2 > 0L) {
         if (var0 < 0L) {
            var0 = Long.MAX_VALUE;
         } else {
            var0 += var2;
            if (var0 < 0L) {
               var0 = Long.MAX_VALUE;
            }
         }
      } else {
         var0 = -var0;
         if (var0 > 0L) {
            var0 = Long.MIN_VALUE;
         } else {
            var0 += var2;
            if (var0 >= 0L) {
               var0 = Long.MIN_VALUE;
            }
         }
      }

      return var0 / var2;
   }

   public static long getDefaultMinValue(Long var0, long var1, long[] var3) {
      long var4 = var1 >= 0L ? 1L : var3[0];
      if (var0 != null && var1 >= 0L && var0 < var4) {
         var4 = var0;
      }

      return var4;
   }

   public static long getDefaultMaxValue(Long var0, long var1, long[] var3) {
      long var4 = var1 >= 0L ? var3[1] : -1L;
      if (var0 != null && var1 < 0L && var0 > var4) {
         var4 = var0;
      }

      return var4;
   }

   public boolean getBelongsToTable() {
      return this.belongsToTable;
   }

   public TypeInfo getDataType() {
      return this.dataType;
   }

   public int getEffectivePrecision() {
      TypeInfo var1 = this.dataType;
      switch (var1.getValueType()) {
         case 13:
            int var2 = (int)var1.getPrecision();
            int var3 = var1.getScale();
            if (var2 - var3 > 19) {
               return 19 + var3;
            }

            return var2;
         case 16:
            return Math.min((int)var1.getPrecision(), 19);
         default:
            return (int)var1.getPrecision();
      }
   }

   public long getIncrement() {
      return this.increment;
   }

   public long getStartValue() {
      return this.startValue;
   }

   public long getMinValue() {
      return this.minValue;
   }

   public long getMaxValue() {
      return this.maxValue;
   }

   public Cycle getCycle() {
      return this.cycle;
   }

   public String getDropSQL() {
      if (this.getBelongsToTable()) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder("DROP SEQUENCE IF EXISTS ");
         return this.getSQL(var1, 0).toString();
      }
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL() {
      StringBuilder var1 = this.getSQL(new StringBuilder("CREATE SEQUENCE "), 0);
      if (this.dataType.getValueType() != 12) {
         this.dataType.getSQL(var1.append(" AS "), 0);
      }

      var1.append(' ');
      synchronized(this) {
         this.getSequenceOptionsSQL(var1, this.writeWithMargin ? this.margin : this.baseValue);
      }

      if (this.belongsToTable) {
         var1.append(" BELONGS_TO_TABLE");
      }

      return var1.toString();
   }

   public synchronized StringBuilder getSequenceOptionsSQL(StringBuilder var1) {
      return this.getSequenceOptionsSQL(var1, this.baseValue);
   }

   private StringBuilder getSequenceOptionsSQL(StringBuilder var1, long var2) {
      var1.append("START WITH ").append(this.startValue);
      if (var2 != this.startValue && this.cycle != Sequence.Cycle.EXHAUSTED) {
         var1.append(" RESTART WITH ").append(var2);
      }

      if (this.increment != 1L) {
         var1.append(" INCREMENT BY ").append(this.increment);
      }

      long[] var4 = SequenceOptions.getBounds(this.dataType);
      if (this.minValue != getDefaultMinValue(var2, this.increment, var4)) {
         var1.append(" MINVALUE ").append(this.minValue);
      }

      if (this.maxValue != getDefaultMaxValue(var2, this.increment, var4)) {
         var1.append(" MAXVALUE ").append(this.maxValue);
      }

      if (this.cycle == Sequence.Cycle.CYCLE) {
         var1.append(" CYCLE");
      } else if (this.cycle == Sequence.Cycle.EXHAUSTED) {
         var1.append(" EXHAUSTED");
      }

      if (this.cacheSize != 32L) {
         if (this.cacheSize == 1L) {
            var1.append(" NO CACHE");
         } else if (this.cacheSize > 32L || this.cacheSize != getMaxCacheSize(this.maxValue - this.minValue, this.increment)) {
            var1.append(" CACHE ").append(this.cacheSize);
         }
      }

      return var1;
   }

   public Value getNext(SessionLocal var1) {
      long var2;
      boolean var4;
      synchronized(this) {
         if (this.cycle == Sequence.Cycle.EXHAUSTED) {
            throw DbException.get(90006, this.getName());
         }

         var2 = this.baseValue;
         long var6 = var2 + this.increment;
         var4 = this.increment > 0L ? this.increment(var2, var6) : this.decrement(var2, var6);
      }

      if (var4) {
         this.flush(var1);
      }

      return ValueBigint.get(var2).castTo(this.dataType, var1);
   }

   private boolean increment(long var1, long var3) {
      boolean var5 = false;
      if (var3 <= this.maxValue && (~var1 & var3) >= 0L) {
         if (var3 > this.margin) {
            long var6 = var3 + this.increment * (this.cacheSize - 1L);
            if (var6 > this.maxValue || (~var3 & var6) < 0L) {
               var6 = var3;
            }

            this.margin = var6;
            var5 = true;
         }
      } else {
         var3 = this.minValue;
         var5 = true;
         if (this.cycle == Sequence.Cycle.CYCLE) {
            this.margin = var3 + this.increment * (this.cacheSize - 1L);
         } else {
            this.margin = var3;
            this.cycle = Sequence.Cycle.EXHAUSTED;
         }
      }

      this.baseValue = var3;
      return var5;
   }

   private boolean decrement(long var1, long var3) {
      boolean var5 = false;
      if (var3 >= this.minValue && (var1 & ~var3) >= 0L) {
         if (var3 < this.margin) {
            long var6 = var3 + this.increment * (this.cacheSize - 1L);
            if (var6 < this.minValue || (var3 & ~var6) < 0L) {
               var6 = var3;
            }

            this.margin = var6;
            var5 = true;
         }
      } else {
         var3 = this.maxValue;
         var5 = true;
         if (this.cycle == Sequence.Cycle.CYCLE) {
            this.margin = var3 + this.increment * (this.cacheSize - 1L);
         } else {
            this.margin = var3;
            this.cycle = Sequence.Cycle.EXHAUSTED;
         }
      }

      this.baseValue = var3;
      return var5;
   }

   public void flushWithoutMargin() {
      if (this.margin != this.baseValue) {
         this.margin = this.baseValue;
         this.flush((SessionLocal)null);
      }

   }

   public void flush(SessionLocal var1) {
      if (!this.isTemporary()) {
         if (var1 != null && this.database.isSysTableLockedBy(var1)) {
            synchronized(var1) {
               this.flushInternal(var1);
            }
         } else {
            SessionLocal var2 = this.database.getSystemSession();
            synchronized(var2) {
               this.flushInternal(var2);
               var2.commit(false);
            }
         }

      }
   }

   private void flushInternal(SessionLocal var1) {
      boolean var2 = this.database.lockMeta(var1);

      try {
         this.writeWithMargin = true;
         this.database.updateMeta(var1, this);
      } finally {
         this.writeWithMargin = false;
         if (!var2) {
            this.database.unlockMeta(var1);
         }

      }

   }

   public void close() {
      this.flushWithoutMargin();
   }

   public int getType() {
      return 3;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.database.removeMeta(var1, this.getId());
      this.invalidate();
   }

   public synchronized long getBaseValue() {
      return this.baseValue;
   }

   public synchronized long getCurrentValue() {
      return this.baseValue - this.increment;
   }

   public void setBelongsToTable(boolean var1) {
      this.belongsToTable = var1;
   }

   public long getCacheSize() {
      return this.cacheSize;
   }

   public static enum Cycle {
      CYCLE,
      NO_CYCLE,
      EXHAUSTED;

      public boolean isCycle() {
         return this == CYCLE;
      }
   }
}
