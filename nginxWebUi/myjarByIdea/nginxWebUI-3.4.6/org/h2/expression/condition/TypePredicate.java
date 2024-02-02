package org.h2.expression.condition;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class TypePredicate extends SimplePredicate {
   private final TypeInfo[] typeList;
   private int[] valueTypes;

   public TypePredicate(Expression var1, boolean var2, boolean var3, TypeInfo[] var4) {
      super(var1, var2, var3);
      this.typeList = var4;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      var1.append(" IS");
      if (this.not) {
         var1.append(" NOT");
      }

      var1.append(" OF (");

      for(int var3 = 0; var3 < this.typeList.length; ++var3) {
         if (var3 > 0) {
            var1.append(", ");
         }

         this.typeList[var3].getSQL(var1, var2);
      }

      return var1.append(')');
   }

   public Expression optimize(SessionLocal var1) {
      int var2 = this.typeList.length;
      this.valueTypes = new int[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.valueTypes[var3] = this.typeList[var3].getValueType();
      }

      Arrays.sort(this.valueTypes);
      return super.optimize(var1);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      return (Value)(var2 == ValueNull.INSTANCE ? ValueNull.INSTANCE : ValueBoolean.get(Arrays.binarySearch(this.valueTypes, var2.getValueType()) >= 0 ^ this.not));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      if (!this.whenOperand) {
         return super.getWhenValue(var1, var2);
      } else {
         return var2 == ValueNull.INSTANCE ? false : Arrays.binarySearch(this.valueTypes, var2.getValueType()) >= 0 ^ this.not;
      }
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new TypePredicate(this.left, !this.not, false, this.typeList);
   }
}
