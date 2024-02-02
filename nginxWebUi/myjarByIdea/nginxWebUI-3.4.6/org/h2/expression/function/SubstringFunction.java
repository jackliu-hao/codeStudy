package org.h2.expression.function;

import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public final class SubstringFunction extends FunctionN {
   public SubstringFunction() {
      super(new Expression[3]);
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      int var6;
      int var7;
      int var8;
      if (this.type.getValueType() == 6) {
         byte[] var9 = var2.getBytesNoCopy();
         var6 = var9.length;
         var7 = var3.getInt();
         if (var7 == 0) {
            var7 = 1;
         } else if (var7 < 0) {
            var7 = var6 + var7 + 1;
         }

         var8 = var4 == null ? Math.max(var6 + 1, var7) : var7 + var4.getInt();
         var7 = Math.max(var7, 1);
         var8 = Math.min(var8, var6 + 1);
         if (var7 <= var6 && var8 > var7) {
            --var7;
            --var8;
            return (Value)(var7 == 0 && var8 == var9.length ? var2.convertTo(TypeInfo.TYPE_VARBINARY) : ValueVarbinary.getNoCopy(Arrays.copyOfRange(var9, var7, var8)));
         } else {
            return ValueVarbinary.EMPTY;
         }
      } else {
         String var5 = var2.getString();
         var6 = var5.length();
         var7 = var3.getInt();
         if (var7 == 0) {
            var7 = 1;
         } else if (var7 < 0) {
            var7 = var6 + var7 + 1;
         }

         var8 = var4 == null ? Math.max(var6 + 1, var7) : var7 + var4.getInt();
         var7 = Math.max(var7, 1);
         var8 = Math.min(var8, var6 + 1);
         if (var7 <= var6 && var8 > var7) {
            return ValueVarchar.get(var5.substring(var7 - 1, var8 - 1), (CastDataProvider)null);
         } else {
            return (Value)(var1.getMode().treatEmptyStringsAsNull ? ValueNull.INSTANCE : ValueVarchar.EMPTY);
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      int var3 = this.args.length;
      if (var3 >= 2 && var3 <= 3) {
         TypeInfo var4 = this.args[0].getType();
         long var5 = var4.getPrecision();
         Expression var7 = this.args[1];
         Value var8;
         if (var7.isConstant() && (var8 = var7.getValue(var1)) != ValueNull.INSTANCE) {
            var5 -= var8.getLong() - 1L;
         }

         if (this.args.length == 3) {
            var7 = this.args[2];
            if (var7.isConstant() && (var8 = var7.getValue(var1)) != ValueNull.INSTANCE) {
               var5 = Math.min(var5, var8.getLong());
            }
         }

         var5 = Math.max(0L, var5);
         this.type = TypeInfo.getTypeInfo(DataType.isBinaryStringType(var4.getValueType()) ? 6 : 2, var5, 0, (ExtTypeInfo)null);
         return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
      } else {
         throw DbException.get(7001, (String[])(this.getName(), "2..3"));
      }
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.args[0].getUnenclosedSQL(var1.append(this.getName()).append('('), var2);
      this.args[1].getUnenclosedSQL(var1.append(" FROM "), var2);
      if (this.args.length > 2) {
         this.args[2].getUnenclosedSQL(var1.append(" FOR "), var2);
      }

      return var1.append(')');
   }

   public String getName() {
      return "SUBSTRING";
   }
}
