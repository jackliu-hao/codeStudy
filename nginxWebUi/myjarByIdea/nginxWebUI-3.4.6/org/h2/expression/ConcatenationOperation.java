package org.h2.expression;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.function.CastSpecification;
import org.h2.expression.function.ConcatFunction;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public final class ConcatenationOperation extends OperationN {
   public ConcatenationOperation() {
      super(new Expression[4]);
   }

   public ConcatenationOperation(Expression var1, Expression var2) {
      super(new Expression[]{var1, var2});
      this.argsCount = 2;
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      int var3 = 0;

      for(int var4 = this.args.length; var3 < var4; ++var3) {
         if (var3 > 0) {
            var1.append(" || ");
         }

         this.args[var3].getSQL(var1, var2, 0);
      }

      return var1;
   }

   public Value getValue(SessionLocal var1) {
      int var2 = this.args.length;
      if (var2 == 2) {
         Value var3 = this.args[0].getValue(var1);
         var3 = var3.convertTo(this.type, var1);
         if (var3 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
         } else {
            Value var4 = this.args[1].getValue(var1);
            var4 = var4.convertTo(this.type, var1);
            return (Value)(var4 == ValueNull.INSTANCE ? ValueNull.INSTANCE : this.getValue(var1, var3, var4));
         }
      } else {
         return this.getValue(var1, var2);
      }
   }

   private Value getValue(SessionLocal var1, Value var2, Value var3) {
      int var4 = this.type.getValueType();
      if (var4 == 2) {
         String var11 = var2.getString();
         String var13 = var3.getString();
         return ValueVarchar.get((new StringBuilder(var11.length() + var13.length())).append(var11).append(var13).toString());
      } else {
         int var7;
         int var8;
         if (var4 == 6) {
            byte[] var10 = var2.getBytesNoCopy();
            byte[] var12 = var3.getBytesNoCopy();
            var7 = var10.length;
            var8 = var12.length;
            byte[] var14 = Arrays.copyOf(var10, var7 + var8);
            System.arraycopy(var12, 0, var14, var7, var8);
            return ValueVarbinary.getNoCopy(var14);
         } else {
            Value[] var5 = ((ValueArray)var2).getList();
            Value[] var6 = ((ValueArray)var3).getList();
            var7 = var5.length;
            var8 = var6.length;
            Value[] var9 = (Value[])Arrays.copyOf(var5, var7 + var8);
            System.arraycopy(var6, 0, var9, var7, var8);
            return ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), var9, var1);
         }
      }
   }

   private Value getValue(SessionLocal var1, int var2) {
      Value[] var3 = new Value[var2];

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         Value var5 = this.args[var4].getValue(var1).convertTo(this.type, var1);
         if (var5 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
         }

         var3[var4] = var5;
      }

      var4 = this.type.getValueType();
      int var6;
      if (var4 == 2) {
         StringBuilder var12 = new StringBuilder();

         for(var6 = 0; var6 < var2; ++var6) {
            var12.append(var3[var6].getString());
         }

         return ValueVarchar.get(var12.toString(), var1);
      } else {
         int var7;
         int var8;
         int var10;
         int var11;
         if (var4 == 6) {
            var11 = 0;

            for(var6 = 0; var6 < var2; ++var6) {
               var11 += var3[var6].getBytesNoCopy().length;
            }

            byte[] var14 = new byte[var11];
            var7 = 0;

            for(var8 = 0; var8 < var2; ++var8) {
               byte[] var15 = var3[var8].getBytesNoCopy();
               var10 = var15.length;
               System.arraycopy(var15, 0, var14, var7, var10);
               var7 += var10;
            }

            return ValueVarbinary.getNoCopy(var14);
         } else {
            var11 = 0;

            for(var6 = 0; var6 < var2; ++var6) {
               var11 += ((ValueArray)var3[var6]).getList().length;
            }

            Value[] var13 = new Value[var11];
            var7 = 0;

            for(var8 = 0; var8 < var2; ++var8) {
               Value[] var9 = ((ValueArray)var3[var8]).getList();
               var10 = var9.length;
               System.arraycopy(var9, 0, var13, var7, var10);
               var7 += var10;
            }

            return ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), var13, var1);
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.determineType(var1);
      this.inlineArguments();
      if (this.type.getValueType() == 2 && var1.getMode().treatEmptyStringsAsNull) {
         return (new ConcatFunction(0, this.args)).optimize(var1);
      } else {
         int var2 = this.args.length;
         boolean var3 = true;
         boolean var4 = false;

         int var5;
         for(var5 = 0; var5 < var2; ++var5) {
            if (this.args[var5].isConstant()) {
               var4 = true;
            } else {
               var3 = false;
            }
         }

         if (var3) {
            return TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type);
         } else {
            if (var4) {
               var5 = 0;

               for(int var6 = 0; var6 < var2; ++var6) {
                  Object var7 = this.args[var6];
                  if (((Expression)var7).isConstant()) {
                     Value var8 = ((Expression)var7).getValue(var1).convertTo(this.type, var1);
                     if (var8 == ValueNull.INSTANCE) {
                        return TypedValueExpression.get(ValueNull.INSTANCE, this.type);
                     }

                     if (isEmpty(var8)) {
                        continue;
                     }

                     Expression var9;
                     for(; var6 + 1 < var2 && (var9 = this.args[var6 + 1]).isConstant(); ++var6) {
                        Value var10 = var9.getValue(var1).convertTo(this.type, var1);
                        if (var10 == ValueNull.INSTANCE) {
                           return TypedValueExpression.get(ValueNull.INSTANCE, this.type);
                        }

                        if (!isEmpty(var10)) {
                           var8 = this.getValue(var1, var8, var10);
                        }
                     }

                     var7 = ValueExpression.get(var8);
                  }

                  this.args[var5++] = (Expression)var7;
               }

               if (var5 == 1) {
                  Expression var11 = this.args[0];
                  TypeInfo var12 = var11.getType();
                  if (TypeInfo.areSameTypes(this.type, var12)) {
                     return var11;
                  }

                  return new CastSpecification(var11, this.type);
               }

               this.argsCount = var5;
               this.doneWithParameters();
            }

            return this;
         }
      }
   }

   private void determineType(SessionLocal var1) {
      int var2 = this.args.length;
      boolean var3 = false;
      boolean var4 = true;
      boolean var5 = true;

      int var8;
      for(int var6 = 0; var6 < var2; ++var6) {
         Expression var7 = this.args[var6].optimize(var1);
         this.args[var6] = var7;
         var8 = var7.getType().getValueType();
         if (var8 == 40) {
            var3 = true;
            var5 = false;
            var4 = false;
         } else if (var8 != 0) {
            if (DataType.isBinaryStringType(var8)) {
               var5 = false;
            } else if (DataType.isCharacterStringType(var8)) {
               var4 = false;
            } else {
               var5 = false;
               var4 = false;
            }
         }
      }

      if (var3) {
         this.type = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getHigherType(this.args).getExtTypeInfo());
      } else {
         long var9;
         if (var4) {
            var9 = this.getPrecision(0);

            for(var8 = 1; var8 < var2; ++var8) {
               var9 = DataType.addPrecision(var9, this.getPrecision(var8));
            }

            this.type = TypeInfo.getTypeInfo(6, var9, 0, (ExtTypeInfo)null);
         } else if (var5) {
            var9 = this.getPrecision(0);

            for(var8 = 1; var8 < var2; ++var8) {
               var9 = DataType.addPrecision(var9, this.getPrecision(var8));
            }

            this.type = TypeInfo.getTypeInfo(2, var9, 0, (ExtTypeInfo)null);
         } else {
            this.type = TypeInfo.TYPE_VARCHAR;
         }
      }

   }

   private long getPrecision(int var1) {
      TypeInfo var2 = this.args[var1].getType();
      return var2.getValueType() != 0 ? var2.getPrecision() : 0L;
   }

   private void inlineArguments() {
      int var1 = this.type.getValueType();
      int var2 = this.args.length;
      int var3 = var2;

      for(int var4 = 0; var4 < var2; ++var4) {
         Expression var5 = this.args[var4];
         if (var5 instanceof ConcatenationOperation && var5.getType().getValueType() == var1) {
            var3 += var5.getSubexpressionCount() - 1;
         }
      }

      if (var3 > var2) {
         Expression[] var11 = new Expression[var3];
         int var12 = 0;

         for(int var6 = 0; var12 < var2; ++var12) {
            Expression var7 = this.args[var12];
            if (var7 instanceof ConcatenationOperation && var7.getType().getValueType() == var1) {
               ConcatenationOperation var8 = (ConcatenationOperation)var7;
               Expression[] var9 = var8.args;
               int var10 = var9.length;
               System.arraycopy(var9, 0, var11, var6, var10);
               var6 += var10;
            } else {
               var11[var6++] = var7;
            }
         }

         this.args = var11;
         this.argsCount = var3;
      }

   }

   private static boolean isEmpty(Value var0) {
      int var1 = var0.getValueType();
      if (var1 == 2) {
         return var0.getString().isEmpty();
      } else if (var1 == 6) {
         return var0.getBytesNoCopy().length == 0;
      } else {
         return ((ValueArray)var0).getList().length == 0;
      }
   }
}
