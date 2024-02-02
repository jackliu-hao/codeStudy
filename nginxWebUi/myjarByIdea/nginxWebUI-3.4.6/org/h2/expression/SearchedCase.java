package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class SearchedCase extends OperationN {
   public SearchedCase() {
      super(new Expression[4]);
   }

   public SearchedCase(Expression[] var1) {
      super(var1);
   }

   public Value getValue(SessionLocal var1) {
      int var2 = this.args.length - 1;

      for(int var3 = 0; var3 < var2; var3 += 2) {
         if (this.args[var3].getBooleanValue(var1)) {
            return this.args[var3 + 1].getValue(var1).convertTo(this.type, var1);
         }
      }

      if ((var2 & 1) == 0) {
         return this.args[var2].getValue(var1).convertTo(this.type, var1);
      } else {
         return ValueNull.INSTANCE;
      }
   }

   public Expression optimize(SessionLocal var1) {
      TypeInfo var2 = TypeInfo.TYPE_UNKNOWN;
      int var3 = this.args.length - 1;
      boolean var4 = true;

      for(int var5 = 0; var5 < var3; var5 += 2) {
         Expression var6 = this.args[var5].optimize(var1);
         Expression var7 = this.args[var5 + 1].optimize(var1);
         if (var4) {
            if (var6.isConstant()) {
               if (var6.getBooleanValue(var1)) {
                  return var7;
               }
            } else {
               var4 = false;
            }
         }

         this.args[var5] = var6;
         this.args[var5 + 1] = var7;
         var2 = SimpleCase.combineTypes(var2, var7);
      }

      if ((var3 & 1) == 0) {
         Expression var8 = this.args[var3].optimize(var1);
         if (var4) {
            return var8;
         }

         this.args[var3] = var8;
         var2 = SimpleCase.combineTypes(var2, var8);
      } else if (var4) {
         return ValueExpression.NULL;
      }

      if (var2.getValueType() == -1) {
         var2 = TypeInfo.TYPE_VARCHAR;
      }

      this.type = var2;
      return this;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append("CASE");
      int var3 = this.args.length - 1;

      for(int var4 = 0; var4 < var3; var4 += 2) {
         var1.append(" WHEN ");
         this.args[var4].getUnenclosedSQL(var1, var2);
         var1.append(" THEN ");
         this.args[var4 + 1].getUnenclosedSQL(var1, var2);
      }

      if ((var3 & 1) == 0) {
         var1.append(" ELSE ");
         this.args[var3].getUnenclosedSQL(var1, var2);
      }

      return var1.append(" END");
   }
}
