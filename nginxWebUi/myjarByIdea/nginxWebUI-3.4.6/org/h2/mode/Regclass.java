package org.h2.mode;

import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Operation1;
import org.h2.expression.ValueExpression;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

public final class Regclass extends Operation1 {
   public Regclass(Expression var1) {
      super(var1);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         int var3 = var2.getValueType();
         if (var3 >= 9 && var3 <= 11) {
            return var2.convertToInt((Object)null);
         } else if (var3 == 12) {
            return ValueInteger.get((int)var2.getLong());
         } else {
            String var4 = var2.getString();
            Iterator var5 = var1.getDatabase().getAllSchemas().iterator();

            Index var8;
            do {
               if (!var5.hasNext()) {
                  throw DbException.get(42102, (String)var4);
               }

               Schema var6 = (Schema)var5.next();
               Table var7 = var6.findTableOrView(var1, var4);
               if (var7 != null && !var7.isHidden()) {
                  return ValueInteger.get(var7.getId());
               }

               var8 = var6.findIndex(var1, var4);
            } while(var8 == null || var8.getCreateSQL() == null);

            return ValueInteger.get(var8.getId());
         }
      }
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_INTEGER;
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      return (Expression)(this.arg.isConstant() ? ValueExpression.get(this.getValue(var1)) : this);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.arg.getSQL(var1, var2, 0).append("::REGCLASS");
   }

   public int getCost() {
      return this.arg.getCost() + 100;
   }
}
