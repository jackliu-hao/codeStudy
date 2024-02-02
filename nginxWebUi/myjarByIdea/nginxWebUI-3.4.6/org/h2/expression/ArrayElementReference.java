package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueNull;

public final class ArrayElementReference extends Operation2 {
   public ArrayElementReference(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getSQL(var1, var2, 0).append('[');
      return this.right.getUnenclosedSQL(var1, var2).append(']');
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      Value var3 = this.right.getValue(var1);
      if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
         Value[] var4 = ((ValueArray)var2).getList();
         int var5 = var3.getInt();
         int var6 = var4.length;
         if (var5 >= 1 && var5 <= var6) {
            return var4[var5 - 1];
         } else {
            throw DbException.get(22034, (String[])(Integer.toString(var5), "1.." + var6));
         }
      } else {
         return ValueNull.INSTANCE;
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      TypeInfo var2 = this.left.getType();
      switch (var2.getValueType()) {
         case 0:
            return ValueExpression.NULL;
         case 40:
            this.type = (TypeInfo)var2.getExtTypeInfo();
            if (this.left.isConstant() && this.right.isConstant()) {
               return TypedValueExpression.get(this.getValue(var1), this.type);
            }

            return this;
         default:
            throw DbException.getInvalidExpressionTypeException("Array", this.left);
      }
   }
}
