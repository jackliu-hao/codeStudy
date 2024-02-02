package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueNull;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

public class CompatibilityDatePlusTimeOperation extends Operation2 {
   public CompatibilityDatePlusTimeOperation(Expression var1, Expression var2) {
      super(var1, var2);
      TypeInfo var3 = var1.getType();
      TypeInfo var4 = var2.getType();
      int var5;
      switch (var3.getValueType()) {
         case 19:
            if (var4.getValueType() == 19) {
               throw DbException.getUnsupportedException("TIME WITH TIME ZONE + TIME WITH TIME ZONE");
            }

            var5 = var4.getValueType() == 17 ? 21 : var3.getValueType();
            break;
         case 20:
            var5 = var4.getValueType() == 19 ? 21 : 20;
            break;
         case 21:
            if (var4.getValueType() == 19) {
               throw DbException.getUnsupportedException("TIMESTAMP WITH TIME ZONE + TIME WITH TIME ZONE");
            }
         case 18:
            var5 = var4.getValueType() == 17 ? 20 : var3.getValueType();
            break;
         default:
            throw DbException.getUnsupportedException(Value.getTypeName(var3.getValueType()) + " + " + Value.getTypeName(var4.getValueType()));
      }

      this.type = TypeInfo.getTypeInfo(var5, 0L, Math.max(var3.getScale(), var4.getScale()), (ExtTypeInfo)null);
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getSQL(var1, var2, 0).append(" + ");
      return this.right.getSQL(var1, var2, 0);
   }

   public Value getValue(SessionLocal var1) {
      Object var2 = this.left.getValue(var1);
      Value var3 = this.right.getValue(var1);
      if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
         switch (((Value)var2).getValueType()) {
            case 18:
               if (var3.getValueType() == 17) {
                  return ValueTimestamp.fromDateValueAndNanos(((ValueDate)var3).getDateValue(), ((ValueTime)var2).getNanos());
               }
               break;
            case 19:
               if (var3.getValueType() == 17) {
                  ValueTimeTimeZone var9 = (ValueTimeTimeZone)var2;
                  return ValueTimestampTimeZone.fromDateValueAndNanos(((ValueDate)var3).getDateValue(), var9.getNanos(), var9.getTimeZoneOffsetSeconds());
               }
               break;
            case 20:
               if (var3.getValueType() == 19) {
                  ValueTimestamp var4 = (ValueTimestamp)var2;
                  var2 = ValueTimestampTimeZone.fromDateValueAndNanos(var4.getDateValue(), var4.getTimeNanos(), ((ValueTimeTimeZone)var3).getTimeZoneOffsetSeconds());
               }
         }

         long[] var10 = DateTimeUtils.dateAndTimeFromValue((Value)var2, var1);
         long var5 = var10[0];
         long var7 = var10[1] + (var3 instanceof ValueTime ? ((ValueTime)var3).getNanos() : ((ValueTimeTimeZone)var3).getNanos());
         if (var7 >= 86400000000000L) {
            var7 -= 86400000000000L;
            var5 = DateTimeUtils.incrementDateValue(var5);
         }

         return DateTimeUtils.dateTimeToValue((Value)var2, var5, var7);
      } else {
         return ValueNull.INSTANCE;
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      return (Expression)(this.left.isConstant() && this.right.isConstant() ? ValueExpression.get(this.getValue(var1)) : this);
   }
}
