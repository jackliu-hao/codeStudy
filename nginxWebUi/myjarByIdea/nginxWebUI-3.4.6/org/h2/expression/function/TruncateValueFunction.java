package org.h2.expression.function;

import java.math.BigDecimal;
import java.math.MathContext;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueNumeric;

public final class TruncateValueFunction extends FunctionN {
   public TruncateValueFunction(Expression var1, Expression var2, Expression var3) {
      super(new Expression[]{var1, var2, var3});
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      long var5 = var3.getLong();
      boolean var7 = var4.getBoolean();
      if (var5 <= 0L) {
         throw DbException.get(90150, Long.toString(var5), "1", "2147483647");
      } else {
         TypeInfo var8 = var2.getType();
         int var9 = var8.getValueType();
         BigDecimal var10;
         if (DataType.getDataType(var9).supportsPrecision) {
            if (var5 < var8.getPrecision()) {
               switch (var9) {
                  case 13:
                     var10 = var2.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(var5)));
                     if (var10.scale() < 0) {
                        var10 = var10.setScale(0);
                     }

                     return ValueNumeric.get(var10);
                  case 16:
                     return ValueDecfloat.get(var2.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(var5))));
                  default:
                     return var2.castTo(TypeInfo.getTypeInfo(var9, var5, var8.getScale(), var8.getExtTypeInfo()), var1);
               }
            }
         } else if (var7) {
            switch (var9) {
               case 9:
               case 10:
               case 11:
                  var10 = BigDecimal.valueOf((long)var2.getInt());
                  break;
               case 12:
                  var10 = BigDecimal.valueOf(var2.getLong());
                  break;
               case 13:
               default:
                  return var2;
               case 14:
               case 15:
                  var10 = var2.getBigDecimal();
            }

            var10 = var10.round(new MathContext(MathUtils.convertLongToInt(var5)));
            if (var9 == 16) {
               return ValueDecfloat.get(var10);
            }

            if (var10.scale() < 0) {
               var10 = var10.setScale(0);
            }

            return ValueNumeric.get(var10).convertTo(var9);
         }

         return var2;
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      this.type = this.args[0].getType();
      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return "TRUNCATE_VALUE";
   }
}
