package org.h2.mode;

import java.util.HashMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.expression.function.DateTimeFunction;
import org.h2.message.DbException;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueUuid;

public final class FunctionsOracle extends ModeFunction {
   private static final int ADD_MONTHS = 2001;
   private static final int SYS_GUID = 2002;
   private static final int TO_DATE = 2003;
   private static final int TO_TIMESTAMP = 2004;
   private static final int TO_TIMESTAMP_TZ = 2005;
   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap();

   public static FunctionsOracle getFunction(String var0) {
      FunctionInfo var1 = (FunctionInfo)FUNCTIONS.get(var0);
      return var1 != null ? new FunctionsOracle(var1) : null;
   }

   private FunctionsOracle(FunctionInfo var1) {
      super(var1);
   }

   protected void checkParameterCount(int var1) {
      boolean var2 = false;
      int var3 = Integer.MAX_VALUE;
      byte var4;
      byte var5;
      switch (this.info.type) {
         case 2003:
            var4 = 1;
            var5 = 3;
            break;
         case 2004:
         case 2005:
            var4 = 1;
            var5 = 2;
            break;
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }

      if (var1 < var4 || var1 > var5) {
         throw DbException.get(7001, (String[])(this.info.name, var4 + ".." + var5));
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1);
      switch (this.info.type) {
         case 2002:
            this.type = TypeInfo.getTypeInfo(6, 16L, 0, (ExtTypeInfo)null);
            break;
         default:
            this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
      }

      return (Expression)(var2 ? ValueExpression.get(this.getValue(var1)) : this);
   }

   public Value getValue(SessionLocal var1) {
      Value[] var2 = this.getArgumentsValues(var1, this.args);
      if (var2 == null) {
         return ValueNull.INSTANCE;
      } else {
         Value var3 = getNullOrValue(var1, this.args, var2, 0);
         Value var4 = getNullOrValue(var1, this.args, var2, 1);
         Object var5;
         switch (this.info.type) {
            case 2001:
               var5 = DateTimeFunction.dateadd(var1, 1, (long)var4.getInt(), var3);
               break;
            case 2002:
               var5 = ValueUuid.getNewRandom().convertTo(TypeInfo.TYPE_VARBINARY);
               break;
            case 2003:
               var5 = ToDateParser.toDate(var1, var3.getString(), var4 == null ? null : var4.getString());
               break;
            case 2004:
               var5 = ToDateParser.toTimestamp(var1, var3.getString(), var4 == null ? null : var4.getString());
               break;
            case 2005:
               var5 = ToDateParser.toTimestampTz(var1, var3.getString(), var4 == null ? null : var4.getString());
               break;
            default:
               throw DbException.getInternalError("type=" + this.info.type);
         }

         return (Value)var5;
      }
   }

   static {
      FUNCTIONS.put("ADD_MONTHS", new FunctionInfo("ADD_MONTHS", 2001, 2, 20, true, true));
      FUNCTIONS.put("SYS_GUID", new FunctionInfo("SYS_GUID", 2002, 0, 6, false, false));
      FUNCTIONS.put("TO_DATE", new FunctionInfo("TO_DATE", 2003, -1, 20, true, true));
      FUNCTIONS.put("TO_TIMESTAMP", new FunctionInfo("TO_TIMESTAMP", 2004, -1, 20, true, true));
      FUNCTIONS.put("TO_TIMESTAMP_TZ", new FunctionInfo("TO_TIMESTAMP_TZ", 2005, -1, 21, true, true));
   }
}
