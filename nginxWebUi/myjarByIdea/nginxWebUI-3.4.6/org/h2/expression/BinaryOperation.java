package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.expression.function.DateTimeFunction;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

public class BinaryOperation extends Operation2 {
   private OpType opType;
   private TypeInfo forcedType;
   private boolean convertRight = true;

   public BinaryOperation(OpType var1, Expression var2, Expression var3) {
      super(var2, var3);
      this.opType = var1;
   }

   public void setForcedType(TypeInfo var1) {
      if (this.opType != BinaryOperation.OpType.MINUS) {
         throw this.getUnexpectedForcedTypeException();
      } else {
         this.forcedType = var1;
      }
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getSQL(var1, var2, 0).append(' ').append(this.getOperationToken()).append(' ');
      return this.right.getSQL(var1, var2, 0);
   }

   private String getOperationToken() {
      switch (this.opType) {
         case PLUS:
            return "+";
         case MINUS:
            return "-";
         case MULTIPLY:
            return "*";
         case DIVIDE:
            return "/";
         default:
            throw DbException.getInternalError("opType=" + this.opType);
      }
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1).convertTo(this.type, var1);
      Value var3 = this.right.getValue(var1);
      if (this.convertRight) {
         var3 = var3.convertTo(this.type, var1);
      }

      switch (this.opType) {
         case PLUS:
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
               return var2.add(var3);
            }

            return ValueNull.INSTANCE;
         case MINUS:
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
               return var2.subtract(var3);
            }

            return ValueNull.INSTANCE;
         case MULTIPLY:
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
               return var2.multiply(var3);
            }

            return ValueNull.INSTANCE;
         case DIVIDE:
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
               return var2.divide(var3, this.type);
            }

            return ValueNull.INSTANCE;
         default:
            throw DbException.getInternalError("type=" + this.opType);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      TypeInfo var2 = this.left.getType();
      TypeInfo var3 = this.right.getType();
      int var4 = var2.getValueType();
      int var5 = var3.getValueType();
      if ((var4 != 0 || var5 != 0) && (var4 != -1 || var5 != -1)) {
         if (DataType.isIntervalType(var4) || DataType.isIntervalType(var5)) {
            if (this.forcedType != null) {
               throw this.getUnexpectedForcedTypeException();
            }

            return this.optimizeInterval(var4, var5);
         }

         if (DataType.isDateTimeType(var4) || DataType.isDateTimeType(var5)) {
            return this.optimizeDateTime(var1, var4, var5);
         }

         if (this.forcedType != null) {
            throw this.getUnexpectedForcedTypeException();
         }

         int var6 = Value.getHigherOrder(var4, var5);
         if (var6 == 13) {
            this.optimizeNumeric(var2, var3);
         } else if (var6 == 16) {
            this.optimizeDecfloat(var2, var3);
         } else if (var6 == 36) {
            this.type = TypeInfo.TYPE_INTEGER;
         } else {
            if (DataType.isCharacterStringType(var6) && this.opType == BinaryOperation.OpType.PLUS && var1.getDatabase().getMode().allowPlusForStringConcat) {
               return (new ConcatenationOperation(this.left, this.right)).optimize(var1);
            }

            this.type = TypeInfo.getTypeInfo(var6);
         }
      } else {
         if (this.opType == BinaryOperation.OpType.PLUS && var1.getDatabase().getMode().allowPlusForStringConcat) {
            return (new ConcatenationOperation(this.left, this.right)).optimize(var1);
         }

         this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
      }

      if (this.left.isConstant() && this.right.isConstant()) {
         return ValueExpression.get(this.getValue(var1));
      } else {
         return this;
      }
   }

   private void optimizeNumeric(TypeInfo var1, TypeInfo var2) {
      var1 = var1.toNumericType();
      var2 = var2.toNumericType();
      long var3 = var1.getPrecision();
      long var5 = var2.getPrecision();
      int var7 = var1.getScale();
      int var8 = var2.getScale();
      long var9;
      int var11;
      switch (this.opType) {
         case PLUS:
         case MINUS:
            if (var7 < var8) {
               var3 += (long)(var8 - var7);
               var11 = var8;
            } else {
               var5 += (long)(var7 - var8);
               var11 = var7;
            }

            var9 = Math.max(var3, var5) + 1L;
            break;
         case MULTIPLY:
            var9 = var3 + var5;
            var11 = var7 + var8;
            break;
         case DIVIDE:
            long var12 = (long)(var7 - var8) + var5 * 2L;
            if (var12 >= 100000L) {
               var11 = 100000;
            } else if (var12 <= 0L) {
               var11 = 0;
            } else {
               var11 = (int)var12;
            }

            var9 = var3 + (long)var8 - (long)var7 + (long)var11;
            break;
         default:
            throw DbException.getInternalError("type=" + this.opType);
      }

      this.type = TypeInfo.getTypeInfo(13, var9, var11, (ExtTypeInfo)null);
   }

   private void optimizeDecfloat(TypeInfo var1, TypeInfo var2) {
      var1 = var1.toDecfloatType();
      var2 = var2.toDecfloatType();
      long var3 = var1.getPrecision();
      long var5 = var2.getPrecision();
      long var7;
      switch (this.opType) {
         case PLUS:
         case MINUS:
         case DIVIDE:
            var7 = Math.max(var3, var5) + 1L;
            break;
         case MULTIPLY:
            var7 = var3 + var5;
            break;
         default:
            throw DbException.getInternalError("type=" + this.opType);
      }

      this.type = TypeInfo.getTypeInfo(16, var7, 0, (ExtTypeInfo)null);
   }

   private Expression optimizeInterval(int var1, int var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      if (DataType.isIntervalType(var1)) {
         var3 = true;
      } else if (DataType.isNumericType(var1)) {
         var4 = true;
      } else {
         if (!DataType.isDateTimeType(var1)) {
            throw this.getUnsupported(var1, var2);
         }

         var5 = true;
      }

      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      if (DataType.isIntervalType(var2)) {
         var6 = true;
      } else if (DataType.isNumericType(var2)) {
         var7 = true;
      } else {
         if (!DataType.isDateTimeType(var2)) {
            throw this.getUnsupported(var1, var2);
         }

         var8 = true;
      }

      switch (this.opType) {
         case PLUS:
            if (var3 && var6) {
               if (DataType.isYearMonthIntervalType(var1) == DataType.isYearMonthIntervalType(var2)) {
                  return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_PLUS_INTERVAL, this.left, this.right);
               }
               break;
            } else if (var3 && var8) {
               if (var2 == 18 && DataType.isYearMonthIntervalType(var1)) {
                  break;
               }

               return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL, this.right, this.left);
            } else {
               if (!var5 || !var6 || var1 == 18 && DataType.isYearMonthIntervalType(var2)) {
                  break;
               }

               return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL, this.left, this.right);
            }
         case MINUS:
            if (var3 && var6) {
               if (DataType.isYearMonthIntervalType(var1) == DataType.isYearMonthIntervalType(var2)) {
                  return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MINUS_INTERVAL, this.left, this.right);
               }
               break;
            } else {
               if (!var5 || !var6 || var1 == 18 && DataType.isYearMonthIntervalType(var2)) {
                  break;
               }

               return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_INTERVAL, this.left, this.right);
            }
         case MULTIPLY:
            if (var3 && var7) {
               return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC, this.left, this.right);
            }

            if (var4 && var6) {
               return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC, this.right, this.left);
            }
            break;
         case DIVIDE:
            if (var3) {
               if (var7) {
                  return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_DIVIDE_NUMERIC, this.left, this.right);
               }

               if (var6 && DataType.isYearMonthIntervalType(var1) == DataType.isYearMonthIntervalType(var2)) {
                  return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_DIVIDE_INTERVAL, this.left, this.right);
               }
            }
      }

      throw this.getUnsupported(var1, var2);
   }

   private Expression optimizeDateTime(SessionLocal var1, int var2, int var3) {
      switch (this.opType) {
         case PLUS:
            if (DataType.isDateTimeType(var2)) {
               if (DataType.isDateTimeType(var3)) {
                  if (var2 > var3) {
                     this.swap();
                  }

                  return (new CompatibilityDatePlusTimeOperation(this.right, this.left)).optimize(var1);
               }

               this.swap();
               int var4 = var2;
               var2 = var3;
               var3 = var4;
            }

            switch (var2) {
               case 11:
                  return (new DateTimeFunction(2, 2, this.left, this.right)).optimize(var1);
               case 12:
               default:
                  throw this.getUnsupported(var2, var3);
               case 13:
               case 14:
               case 15:
               case 16:
                  return (new DateTimeFunction(2, 5, new BinaryOperation(BinaryOperation.OpType.MULTIPLY, ValueExpression.get(ValueInteger.get(86400)), this.left), this.right)).optimize(var1);
            }
         case MINUS:
            switch (var2) {
               case 17:
               case 20:
               case 21:
                  switch (var3) {
                     case 11:
                        if (this.forcedType != null) {
                           throw this.getUnexpectedForcedTypeException();
                        }

                        return (new DateTimeFunction(2, 2, new UnaryOperation(this.right), this.left)).optimize(var1);
                     case 12:
                     default:
                        throw this.getUnsupported(var2, var3);
                     case 13:
                     case 14:
                     case 15:
                     case 16:
                        if (this.forcedType != null) {
                           throw this.getUnexpectedForcedTypeException();
                        }

                        return (new DateTimeFunction(2, 5, new BinaryOperation(BinaryOperation.OpType.MULTIPLY, ValueExpression.get(ValueInteger.get(-86400)), this.right), this.left)).optimize(var1);
                     case 17:
                     case 18:
                     case 19:
                     case 20:
                     case 21:
                        return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_DATETIME, this.left, this.right, this.forcedType);
                  }
               case 18:
               case 19:
                  if (DataType.isDateTimeType(var3)) {
                     return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_DATETIME, this.left, this.right, this.forcedType);
                  }

                  throw this.getUnsupported(var2, var3);
               default:
                  throw this.getUnsupported(var2, var3);
            }
         case MULTIPLY:
            if (var2 == 18) {
               this.type = TypeInfo.TYPE_TIME;
               this.convertRight = false;
               return this;
            }

            if (var3 == 18) {
               this.swap();
               this.type = TypeInfo.TYPE_TIME;
               this.convertRight = false;
               return this;
            }
            break;
         case DIVIDE:
            if (var2 == 18) {
               this.type = TypeInfo.TYPE_TIME;
               this.convertRight = false;
               return this;
            }
      }

      throw this.getUnsupported(var2, var3);
   }

   private DbException getUnsupported(int var1, int var2) {
      return DbException.getUnsupportedException(Value.getTypeName(var1) + ' ' + this.getOperationToken() + ' ' + Value.getTypeName(var2));
   }

   private DbException getUnexpectedForcedTypeException() {
      StringBuilder var1 = this.getUnenclosedSQL(new StringBuilder(), 3);
      int var2 = var1.length();
      return DbException.getSyntaxError(IntervalOperation.getForcedTypeSQL(var1.append(' '), this.forcedType).toString(), var2, "");
   }

   private void swap() {
      Expression var1 = this.left;
      this.left = this.right;
      this.right = var1;
   }

   public OpType getOperationType() {
      return this.opType;
   }

   public static enum OpType {
      PLUS,
      MINUS,
      MULTIPLY,
      DIVIDE;
   }
}
