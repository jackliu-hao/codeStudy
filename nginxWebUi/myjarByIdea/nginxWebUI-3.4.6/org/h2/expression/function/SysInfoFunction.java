package org.h2.expression.function;

import org.h2.engine.Constants;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.message.DbException;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class SysInfoFunction extends Operation0 implements NamedExpression {
   public static final int AUTOCOMMIT = 0;
   public static final int DATABASE_PATH = 1;
   public static final int H2VERSION = 2;
   public static final int LOCK_MODE = 3;
   public static final int LOCK_TIMEOUT = 4;
   public static final int MEMORY_FREE = 5;
   public static final int MEMORY_USED = 6;
   public static final int READONLY = 7;
   public static final int SESSION_ID = 8;
   public static final int TRANSACTION_ID = 9;
   private static final int[] TYPES = new int[]{8, 2, 2, 11, 11, 12, 12, 8, 11, 2};
   private static final String[] NAMES = new String[]{"AUTOCOMMIT", "DATABASE_PATH", "H2VERSION", "LOCK_MODE", "LOCK_TIMEOUT", "MEMORY_FREE", "MEMORY_USED", "READONLY", "SESSION_ID", "TRANSACTION_ID"};
   private final int function;
   private final TypeInfo type;

   public static String getName(int var0) {
      return NAMES[var0];
   }

   public SysInfoFunction(int var1) {
      this.function = var1;
      this.type = TypeInfo.getTypeInfo(TYPES[var1]);
   }

   public Value getValue(SessionLocal var1) {
      Object var2;
      switch (this.function) {
         case 0:
            var2 = ValueBoolean.get(var1.getAutoCommit());
            break;
         case 1:
            String var3 = var1.getDatabase().getDatabasePath();
            var2 = var3 != null ? ValueVarchar.get(var3, var1) : ValueNull.INSTANCE;
            break;
         case 2:
            var2 = ValueVarchar.get(Constants.VERSION, var1);
            break;
         case 3:
            var2 = ValueInteger.get(var1.getDatabase().getLockMode());
            break;
         case 4:
            var2 = ValueInteger.get(var1.getLockTimeout());
            break;
         case 5:
            var1.getUser().checkAdmin();
            var2 = ValueBigint.get(Utils.getMemoryFree());
            break;
         case 6:
            var1.getUser().checkAdmin();
            var2 = ValueBigint.get(Utils.getMemoryUsed());
            break;
         case 7:
            var2 = ValueBoolean.get(var1.getDatabase().isReadOnly());
            break;
         case 8:
            var2 = ValueInteger.get(var1.getId());
            break;
         case 9:
            var2 = var1.getTransactionId();
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var2;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return var1.append(this.getName()).append("()");
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
      return this.type;
   }

   public int getCost() {
      return 1;
   }

   public String getName() {
      return NAMES[this.function];
   }
}
