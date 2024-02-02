package org.h2.expression.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;

public final class MathFunction extends Function1_2 {
   public static final int ABS = 0;
   public static final int MOD = 1;
   public static final int FLOOR = 2;
   public static final int CEIL = 3;
   public static final int ROUND = 4;
   public static final int ROUNDMAGIC = 5;
   public static final int SIGN = 6;
   public static final int TRUNC = 7;
   private static final String[] NAMES = new String[]{"ABS", "MOD", "FLOOR", "CEIL", "ROUND", "ROUNDMAGIC", "SIGN", "TRUNC"};
   private final int function;
   private TypeInfo commonType;

   public MathFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      switch (this.function) {
         case 0:
            if (((Value)var2).getSignum() < 0) {
               var2 = ((Value)var2).negate();
            }
            break;
         case 1:
            var2 = ((Value)var2).convertTo(this.commonType, var1).modulus(var3.convertTo(this.commonType, var1)).convertTo(this.type, var1);
            break;
         case 2:
            var2 = this.round((Value)var2, var3, RoundingMode.FLOOR);
            break;
         case 3:
            var2 = this.round((Value)var2, var3, RoundingMode.CEILING);
            break;
         case 4:
            var2 = this.round((Value)var2, var3, RoundingMode.HALF_UP);
            break;
         case 5:
            var2 = ValueDouble.get(roundMagic(((Value)var2).getDouble()));
            break;
         case 6:
            var2 = ValueInteger.get(((Value)var2).getSignum());
            break;
         case 7:
            var2 = this.round((Value)var2, var3, RoundingMode.DOWN);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var2;
   }

   private Value round(Value var1, Value var2, RoundingMode var3) {
      int var4 = var2 != null ? var2.getInt() : 0;
      int var5 = this.type.getValueType();
      switch (var5) {
         case 9:
         case 10:
         case 11:
         case 12:
            if (var4 < 0) {
               long var12 = ((Value)var1).getLong();
               long var8 = BigDecimal.valueOf(var12).setScale(var4, var3).longValue();
               if (var12 != var8) {
                  var1 = ValueBigint.get(var8).convertTo(this.type);
               }
            }
            break;
         case 13:
            int var11 = this.type.getScale();
            BigDecimal var7 = ((Value)var1).getBigDecimal();
            if (var4 < var11) {
               var7 = var7.setScale(var4, var3);
            }

            var1 = ValueNumeric.get(var7.setScale(var11, var3));
            break;
         case 14:
         case 15:
            if (var4 == 0) {
               label62: {
                  double var6;
                  switch (var3) {
                     case DOWN:
                        var6 = ((Value)var1).getDouble();
                        var6 = var6 < 0.0 ? Math.ceil(var6) : Math.floor(var6);
                        break;
                     case CEILING:
                        var6 = Math.ceil(((Value)var1).getDouble());
                        break;
                     case FLOOR:
                        var6 = Math.floor(((Value)var1).getDouble());
                        break;
                     default:
                        break label62;
                  }

                  var1 = var5 == 14 ? ValueReal.get((float)var6) : ValueDouble.get(var6);
                  break;
               }
            }

            BigDecimal var10 = ((Value)var1).getBigDecimal().setScale(var4, var3);
            var1 = var5 == 14 ? ValueReal.get(var10.floatValue()) : ValueDouble.get(var10.doubleValue());
            break;
         case 16:
            var1 = ValueDecfloat.get(((Value)var1).getBigDecimal().setScale(var4, var3));
      }

      return (Value)var1;
   }

   private static double roundMagic(double var0) {
      if (var0 < 1.0E-13 && var0 > -1.0E-13) {
         return 0.0;
      } else if (!(var0 > 1.0E12) && !(var0 < -1.0E12)) {
         StringBuilder var2 = new StringBuilder();
         var2.append(var0);
         if (var2.toString().indexOf(69) >= 0) {
            return var0;
         } else {
            int var3 = var2.length();
            if (var3 < 16) {
               return var0;
            } else if (var2.toString().indexOf(46) > var3 - 3) {
               return var0;
            } else {
               var2.delete(var3 - 2, var3);
               var3 -= 2;
               char var4 = var2.charAt(var3 - 2);
               char var5 = var2.charAt(var3 - 3);
               char var6 = var2.charAt(var3 - 4);
               if (var4 == '0' && var5 == '0' && var6 == '0') {
                  var2.setCharAt(var3 - 1, '0');
               } else if (var4 == '9' && var5 == '9' && var6 == '9') {
                  var2.setCharAt(var3 - 1, '9');
                  var2.append('9');
                  var2.append('9');
                  var2.append('9');
               }

               return Double.parseDouble(var2.toString());
            }
         }
      } else {
         return var0;
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      Expression var4;
      switch (this.function) {
         case 0:
            this.type = this.left.getType();
            if (this.type.getValueType() == 0) {
               this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
            }
            break;
         case 1:
            TypeInfo var5 = this.right.getType();
            this.commonType = TypeInfo.getHigherType(this.left.getType(), var5);
            int var3 = this.commonType.getValueType();
            if (var3 == 0) {
               this.commonType = TypeInfo.TYPE_BIGINT;
            } else if (!DataType.isNumericType(var3)) {
               throw DbException.getInvalidExpressionTypeException("MOD argument", DataType.isNumericType(this.left.getType().getValueType()) ? this.right : this.left);
            }

            this.type = DataType.isNumericType(var5.getValueType()) ? var5 : this.commonType;
            break;
         case 2:
         case 3:
            Expression var2 = this.optimizeRound(0, true, false, true);
            if (var2 != null) {
               return var2;
            }
            break;
         case 4:
            var4 = this.optimizeRoundWithScale(var1, true);
            if (var4 != null) {
               return var4;
            }
            break;
         case 5:
            this.type = TypeInfo.TYPE_DOUBLE;
            break;
         case 6:
            this.type = TypeInfo.TYPE_INTEGER;
            break;
         case 7:
            switch (this.left.getType().getValueType()) {
               case 2:
                  this.left = (new CastSpecification(this.left, TypeInfo.getTypeInfo(20, -1L, 0, (ExtTypeInfo)null))).optimize(var1);
               case 20:
               case 21:
                  if (this.right != null) {
                     throw DbException.get(7001, (String[])("TRUNC", "1"));
                  }

                  return (new DateTimeFunction(1, 2, this.left, (Expression)null)).optimize(var1);
               case 17:
                  if (this.right != null) {
                     throw DbException.get(7001, (String[])("TRUNC", "1"));
                  }

                  return (new CastSpecification(this.left, TypeInfo.getTypeInfo(20, -1L, 0, (ExtTypeInfo)null))).optimize(var1);
               default:
                  var4 = this.optimizeRoundWithScale(var1, false);
                  if (var4 != null) {
                     return var4;
                  }

                  return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
            }
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
   }

   private Expression optimizeRoundWithScale(SessionLocal var1, boolean var2) {
      boolean var4 = false;
      boolean var5 = false;
      int var3;
      if (this.right != null) {
         if (this.right.isConstant()) {
            Value var6 = this.right.getValue(var1);
            var4 = true;
            if (var6 != ValueNull.INSTANCE) {
               var3 = var6.getInt();
            } else {
               var3 = -1;
               var5 = true;
            }
         } else {
            var3 = -1;
         }
      } else {
         var3 = 0;
         var4 = true;
      }

      return this.optimizeRound(var3, var4, var5, var2);
   }

   private Expression optimizeRound(int var1, boolean var2, boolean var3, boolean var4) {
      TypeInfo var5 = this.left.getType();
      switch (var5.getValueType()) {
         case 0:
            this.type = TypeInfo.TYPE_NUMERIC_SCALE_0;
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         default:
            throw DbException.getInvalidExpressionTypeException(this.getName() + " argument", this.left);
         case 9:
         case 10:
         case 11:
         case 12:
            if (var2 && var1 >= 0) {
               return this.left;
            }

            this.type = var5;
            break;
         case 13:
            int var8 = var5.getScale();
            long var6;
            if (var2) {
               if (var8 <= var1) {
                  return this.left;
               }

               if (var1 < 0) {
                  var1 = 0;
               } else if (var1 > 100000) {
                  var1 = 100000;
               }

               var6 = var5.getPrecision() - (long)var8 + (long)var1;
               if (var4) {
                  ++var6;
               }
            } else {
               var6 = var5.getPrecision();
               if (var4) {
                  ++var6;
               }

               var1 = var8;
            }

            this.type = TypeInfo.getTypeInfo(13, var6, var1, (ExtTypeInfo)null);
            break;
         case 14:
         case 15:
         case 16:
            this.type = var5;
      }

      return var3 ? TypedValueExpression.get(ValueNull.INSTANCE, this.type) : null;
   }

   public String getName() {
      return NAMES[this.function];
   }
}
