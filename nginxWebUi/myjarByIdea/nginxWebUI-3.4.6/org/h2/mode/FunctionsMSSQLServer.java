package org.h2.mode;

import java.util.HashMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.function.CoalesceFunction;
import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.h2.expression.function.RandFunction;
import org.h2.expression.function.StringFunction;
import org.h2.message.DbException;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

public final class FunctionsMSSQLServer extends ModeFunction {
   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap();
   private static final int CHARINDEX = 4001;
   private static final int GETDATE = 4002;
   private static final int ISNULL = 4003;
   private static final int LEN = 4004;
   private static final int NEWID = 4005;
   private static final int SCOPE_IDENTITY = 4006;
   private static final TypeInfo SCOPE_IDENTITY_TYPE = TypeInfo.getTypeInfo(13, 38L, 0, (ExtTypeInfo)null);

   public static FunctionsMSSQLServer getFunction(String var0) {
      FunctionInfo var1 = (FunctionInfo)FUNCTIONS.get(var0);
      return var1 != null ? new FunctionsMSSQLServer(var1) : null;
   }

   private FunctionsMSSQLServer(FunctionInfo var1) {
      super(var1);
   }

   protected void checkParameterCount(int var1) {
      switch (this.info.type) {
         case 4001:
            byte var2 = 2;
            byte var3 = 3;
            if (var1 >= var2 && var1 <= var3) {
               return;
            }

            throw DbException.get(7001, (String[])(this.info.name, var2 + ".." + var3));
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }
   }

   public Value getValue(SessionLocal var1) {
      Value[] var2 = this.getArgumentsValues(var1, this.args);
      if (var2 == null) {
         return ValueNull.INSTANCE;
      } else {
         Value var3 = getNullOrValue(var1, this.args, var2, 0);
         switch (this.info.type) {
            case 4004:
               long var4;
               if (var3.getValueType() == 1) {
                  String var6 = var3.getString();

                  int var7;
                  for(var7 = var6.length(); var7 > 0 && var6.charAt(var7 - 1) == ' '; --var7) {
                  }

                  var4 = (long)var7;
               } else {
                  var4 = var3.charLength();
               }

               return ValueBigint.get(var4);
            case 4006:
               return var1.getLastIdentity().convertTo(this.type);
            default:
               throw DbException.getInternalError("type=" + this.info.type);
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      switch (this.info.type) {
         case 4001:
            return (new StringFunction(this.args, 0)).optimize(var1);
         case 4002:
            return (new CurrentDateTimeValueFunction(4, 3)).optimize(var1);
         case 4003:
            return (new CoalesceFunction(0, this.args)).optimize(var1);
         case 4004:
         default:
            this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
            if (this.optimizeArguments(var1)) {
               return TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type);
            }
            break;
         case 4005:
            return (new RandFunction((Expression)null, 2)).optimize(var1);
         case 4006:
            this.type = SCOPE_IDENTITY_TYPE;
      }

      return this;
   }

   static {
      FUNCTIONS.put("CHARINDEX", new FunctionInfo("CHARINDEX", 4001, -1, 11, true, true));
      FUNCTIONS.put("GETDATE", new FunctionInfo("GETDATE", 4002, 0, 20, false, true));
      FUNCTIONS.put("LEN", new FunctionInfo("LEN", 4004, 1, 11, true, true));
      FUNCTIONS.put("NEWID", new FunctionInfo("NEWID", 4005, 0, 39, true, false));
      FUNCTIONS.put("ISNULL", new FunctionInfo("ISNULL", 4003, 2, 0, false, true));
      FUNCTIONS.put("SCOPE_IDENTITY", new FunctionInfo("SCOPE_IDENTITY", 4006, 0, 13, true, false));
   }
}
