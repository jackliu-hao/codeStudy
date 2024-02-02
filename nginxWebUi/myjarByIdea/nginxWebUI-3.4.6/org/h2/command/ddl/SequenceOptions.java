package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.schema.Sequence;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

public class SequenceOptions {
   private TypeInfo dataType;
   private Expression start;
   private Expression restart;
   private Expression increment;
   private Expression maxValue;
   private Expression minValue;
   private Sequence.Cycle cycle;
   private Expression cacheSize;
   private long[] bounds;
   private final Sequence oldSequence;

   private static Long getLong(SessionLocal var0, Expression var1) {
      if (var1 != null) {
         Value var2 = var1.optimize(var0).getValue(var0);
         if (var2 != ValueNull.INSTANCE) {
            return var2.getLong();
         }
      }

      return null;
   }

   public SequenceOptions() {
      this.oldSequence = null;
   }

   public SequenceOptions(Sequence var1, TypeInfo var2) {
      this.oldSequence = var1;
      this.dataType = var2;
      this.getBounds();
   }

   public TypeInfo getDataType() {
      if (this.oldSequence != null) {
         synchronized(this.oldSequence) {
            this.copyFromOldSequence();
         }
      }

      return this.dataType;
   }

   private void copyFromOldSequence() {
      long[] var1 = this.getBounds();
      long var2 = Math.max(this.oldSequence.getMinValue(), var1[0]);
      long var4 = Math.min(this.oldSequence.getMaxValue(), var1[1]);
      if (var4 < var2) {
         var2 = var1[0];
         var4 = var1[1];
      }

      this.minValue = ValueExpression.get(ValueBigint.get(var2));
      this.maxValue = ValueExpression.get(ValueBigint.get(var4));
      long var6 = this.oldSequence.getStartValue();
      if (var6 >= var2 && var6 <= var4) {
         this.start = ValueExpression.get(ValueBigint.get(var6));
      }

      var6 = this.oldSequence.getBaseValue();
      if (var6 >= var2 && var6 <= var4) {
         this.restart = ValueExpression.get(ValueBigint.get(var6));
      }

      this.increment = ValueExpression.get(ValueBigint.get(this.oldSequence.getIncrement()));
      this.cycle = this.oldSequence.getCycle();
      this.cacheSize = ValueExpression.get(ValueBigint.get(this.oldSequence.getCacheSize()));
   }

   public void setDataType(TypeInfo var1) {
      this.dataType = var1;
   }

   public Long getStartValue(SessionLocal var1) {
      return this.check(getLong(var1, this.start));
   }

   public void setStartValue(Expression var1) {
      this.start = var1;
   }

   public Long getRestartValue(SessionLocal var1, long var2) {
      return this.check(this.restart == ValueExpression.DEFAULT ? var2 : getLong(var1, this.restart));
   }

   public void setRestartValue(Expression var1) {
      this.restart = var1;
   }

   public Long getIncrement(SessionLocal var1) {
      return this.check(getLong(var1, this.increment));
   }

   public void setIncrement(Expression var1) {
      this.increment = var1;
   }

   public Long getMaxValue(Sequence var1, SessionLocal var2) {
      Long var3;
      if (this.maxValue == ValueExpression.NULL && var1 != null) {
         var3 = Sequence.getDefaultMaxValue(this.getCurrentStart(var1, var2), this.increment != null ? this.getIncrement(var2) : var1.getIncrement(), this.getBounds());
      } else {
         var3 = getLong(var2, this.maxValue);
      }

      return this.check(var3);
   }

   public void setMaxValue(Expression var1) {
      this.maxValue = var1;
   }

   public Long getMinValue(Sequence var1, SessionLocal var2) {
      Long var3;
      if (this.minValue == ValueExpression.NULL && var1 != null) {
         var3 = Sequence.getDefaultMinValue(this.getCurrentStart(var1, var2), this.increment != null ? this.getIncrement(var2) : var1.getIncrement(), this.getBounds());
      } else {
         var3 = getLong(var2, this.minValue);
      }

      return this.check(var3);
   }

   public void setMinValue(Expression var1) {
      this.minValue = var1;
   }

   private Long check(Long var1) {
      if (var1 == null) {
         return null;
      } else {
         long[] var2 = this.getBounds();
         long var3 = var1;
         if (var3 >= var2[0] && var3 <= var2[1]) {
            return var1;
         } else {
            throw DbException.get(22003, (String)Long.toString(var3));
         }
      }
   }

   public long[] getBounds() {
      long[] var1 = this.bounds;
      if (var1 == null) {
         this.bounds = var1 = getBounds(this.dataType);
      }

      return var1;
   }

   public static long[] getBounds(TypeInfo var0) {
      long var1;
      long var3;
      long var5;
      int var7;
      switch (var0.getValueType()) {
         case 9:
            var1 = -128L;
            var3 = 127L;
            break;
         case 10:
            var1 = -32768L;
            var3 = 32767L;
            break;
         case 11:
            var1 = -2147483648L;
            var3 = 2147483647L;
            break;
         case 12:
            var1 = Long.MIN_VALUE;
            var3 = Long.MAX_VALUE;
            break;
         case 13:
            if (var0.getScale() != 0) {
               throw DbException.getUnsupportedException(var0.getTraceSQL());
            }

            var5 = var0.getPrecision() - (long)var0.getScale();
            if (var5 <= 0L) {
               throw DbException.getUnsupportedException(var0.getTraceSQL());
            }

            if (var5 > 18L) {
               var1 = Long.MIN_VALUE;
               var3 = Long.MAX_VALUE;
            } else {
               var3 = 10L;

               for(var7 = 1; (long)var7 < var5; ++var7) {
                  var3 *= 10L;
               }

               var1 = -(--var3);
            }
            break;
         case 14:
            var1 = -16777216L;
            var3 = 16777216L;
            break;
         case 15:
            var1 = -9007199254740992L;
            var3 = 9007199254740992L;
            break;
         case 16:
            var5 = var0.getPrecision();
            if (var5 > 18L) {
               var1 = Long.MIN_VALUE;
               var3 = Long.MAX_VALUE;
            } else {
               var3 = 10L;

               for(var7 = 1; (long)var7 < var5; ++var7) {
                  var3 *= 10L;
               }

               var1 = -var3;
            }
            break;
         default:
            throw DbException.getUnsupportedException(var0.getTraceSQL());
      }

      long[] var8 = new long[]{var1, var3};
      return var8;
   }

   public Sequence.Cycle getCycle() {
      return this.cycle;
   }

   public void setCycle(Sequence.Cycle var1) {
      this.cycle = var1;
   }

   public Long getCacheSize(SessionLocal var1) {
      return getLong(var1, this.cacheSize);
   }

   public void setCacheSize(Expression var1) {
      this.cacheSize = var1;
   }

   private long getCurrentStart(Sequence var1, SessionLocal var2) {
      return this.start != null ? this.getStartValue(var2) : var1.getBaseValue();
   }
}
