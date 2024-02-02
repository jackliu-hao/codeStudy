package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class CurrentDateTimeValueFunction extends Operation0 implements NamedExpression {
   public static final int CURRENT_DATE = 0;
   public static final int CURRENT_TIME = 1;
   public static final int LOCALTIME = 2;
   public static final int CURRENT_TIMESTAMP = 3;
   public static final int LOCALTIMESTAMP = 4;
   private static final int[] TYPES = new int[]{17, 19, 18, 21, 20};
   private static final String[] NAMES = new String[]{"CURRENT_DATE", "CURRENT_TIME", "LOCALTIME", "CURRENT_TIMESTAMP", "LOCALTIMESTAMP"};
   private final int function;
   private final int scale;
   private final TypeInfo type;

   public static String getName(int var0) {
      return NAMES[var0];
   }

   public CurrentDateTimeValueFunction(int var1, int var2) {
      this.function = var1;
      this.scale = var2;
      if (var2 < 0) {
         var2 = var1 >= 3 ? 6 : 0;
      }

      this.type = TypeInfo.getTypeInfo(TYPES[var1], 0L, var2, (ExtTypeInfo)null);
   }

   public Value getValue(SessionLocal var1) {
      return var1.currentTimestamp().castTo(this.type, var1);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.getName());
      if (this.scale >= 0) {
         var1.append('(').append(this.scale).append(')');
      }

      return var1;
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
