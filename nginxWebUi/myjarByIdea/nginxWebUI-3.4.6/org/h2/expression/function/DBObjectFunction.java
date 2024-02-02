package org.h2.expression.function;

import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class DBObjectFunction extends FunctionN {
   public static final int DB_OBJECT_ID = 0;
   public static final int DB_OBJECT_SQL = 1;
   private static final String[] NAMES = new String[]{"DB_OBJECT_ID", "DB_OBJECT_SQL"};
   private final int function;

   public DBObjectFunction(Expression var1, Expression var2, Expression var3, int var4) {
      super(var3 == null ? new Expression[]{var1, var2} : new Expression[]{var1, var2, var3});
      this.function = var4;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      var1.getUser().checkAdmin();
      String var5 = var2.getString();
      Object var6;
      String var11;
      if (var4 != null) {
         Schema var7 = var1.getDatabase().findSchema(var3.getString());
         if (var7 == null) {
            return ValueNull.INSTANCE;
         }

         String var8 = var4.getString();
         switch (var5) {
            case "CONSTANT":
               var6 = var7.findConstant(var8);
               break;
            case "CONSTRAINT":
               var6 = var7.findConstraint(var1, var8);
               break;
            case "DOMAIN":
               var6 = var7.findDomain(var8);
               break;
            case "INDEX":
               var6 = var7.findIndex(var1, var8);
               break;
            case "ROUTINE":
               var6 = var7.findFunctionOrAggregate(var8);
               break;
            case "SEQUENCE":
               var6 = var7.findSequence(var8);
               break;
            case "SYNONYM":
               var6 = var7.getSynonym(var8);
               break;
            case "TABLE":
               var6 = var7.findTableOrView(var1, var8);
               break;
            case "TRIGGER":
               var6 = var7.findTrigger(var8);
               break;
            default:
               return ValueNull.INSTANCE;
         }
      } else {
         var11 = var3.getString();
         Database var12 = var1.getDatabase();
         switch (var5) {
            case "ROLE":
               var6 = var12.findRole(var11);
               break;
            case "SETTING":
               var6 = var12.findSetting(var11);
               break;
            case "SCHEMA":
               var6 = var12.findSchema(var11);
               break;
            case "USER":
               var6 = var12.findUser(var11);
               break;
            default:
               return ValueNull.INSTANCE;
         }
      }

      if (var6 == null) {
         return ValueNull.INSTANCE;
      } else {
         switch (this.function) {
            case 0:
               return ValueInteger.get(((DbObject)var6).getId());
            case 1:
               var11 = ((DbObject)var6).getCreateSQLForMeta();
               return (Value)(var11 != null ? ValueVarchar.get(var11, var1) : ValueNull.INSTANCE);
            default:
               throw DbException.getInternalError("function=" + this.function);
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.optimizeArguments(var1, false);
      this.type = this.function == 0 ? TypeInfo.TYPE_INTEGER : TypeInfo.TYPE_VARCHAR;
      return this;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return super.isEverything(var1);
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
