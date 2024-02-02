package org.h2.expression.function;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class CurrentGeneralValueSpecification extends Operation0 implements NamedExpression {
   public static final int CURRENT_CATALOG = 0;
   public static final int CURRENT_PATH = 1;
   public static final int CURRENT_ROLE = 2;
   public static final int CURRENT_SCHEMA = 3;
   public static final int CURRENT_USER = 4;
   public static final int SESSION_USER = 5;
   public static final int SYSTEM_USER = 6;
   private static final String[] NAMES = new String[]{"CURRENT_CATALOG", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_USER", "SESSION_USER", "SYSTEM_USER"};
   private final int specification;

   public CurrentGeneralValueSpecification(int var1) {
      this.specification = var1;
   }

   public Value getValue(SessionLocal var1) {
      String var2;
      switch (this.specification) {
         case 0:
            var2 = var1.getDatabase().getShortName();
            break;
         case 1:
            String[] var6 = var1.getSchemaSearchPath();
            if (var6 != null) {
               StringBuilder var4 = new StringBuilder();

               for(int var5 = 0; var5 < var6.length; ++var5) {
                  if (var5 > 0) {
                     var4.append(',');
                  }

                  ParserUtil.quoteIdentifier(var4, var6[var5], 0);
               }

               var2 = var4.toString();
            } else {
               var2 = "";
            }
            break;
         case 2:
            Database var3 = var1.getDatabase();
            var2 = var3.getPublicRole().getName();
            if (var3.getSettings().databaseToLower) {
               var2 = StringUtils.toLowerEnglish(var2);
            }
            break;
         case 3:
            var2 = var1.getCurrentSchemaName();
            break;
         case 4:
         case 5:
         case 6:
            var2 = var1.getUser().getName();
            if (var1.getDatabase().getSettings().databaseToLower) {
               var2 = StringUtils.toLowerEnglish(var2);
            }
            break;
         default:
            throw DbException.getInternalError("specification=" + this.specification);
      }

      return (Value)(var2 != null ? ValueVarchar.get(var2, var1) : ValueNull.INSTANCE);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return var1.append(this.getName());
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return true;
      }
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_VARCHAR;
   }

   public int getCost() {
      return 1;
   }

   public String getName() {
      return NAMES[this.specification];
   }
}
