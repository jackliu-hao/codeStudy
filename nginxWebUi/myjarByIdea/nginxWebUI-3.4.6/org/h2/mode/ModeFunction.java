package org.h2.mode;

import org.h2.engine.Database;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.function.FunctionN;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class ModeFunction extends FunctionN {
   protected static final int VAR_ARGS = -1;
   protected final FunctionInfo info;

   public static ModeFunction getFunction(Database var0, String var1) {
      Mode.ModeEnum var2 = var0.getMode().getEnum();
      return var2 != Mode.ModeEnum.REGULAR ? getCompatibilityModeFunction(var1, var2) : null;
   }

   private static ModeFunction getCompatibilityModeFunction(String var0, Mode.ModeEnum var1) {
      switch (var1) {
         case LEGACY:
            return FunctionsLegacy.getFunction(var0);
         case DB2:
         case Derby:
            return FunctionsDB2Derby.getFunction(var0);
         case MSSQLServer:
            return FunctionsMSSQLServer.getFunction(var0);
         case MySQL:
            return FunctionsMySQL.getFunction(var0);
         case Oracle:
            return FunctionsOracle.getFunction(var0);
         case PostgreSQL:
            return FunctionsPostgreSQL.getFunction(var0);
         default:
            return null;
      }
   }

   ModeFunction(FunctionInfo var1) {
      super(new Expression[var1.parameterCount != -1 ? var1.parameterCount : 4]);
      this.info = var1;
   }

   static Value getNullOrValue(SessionLocal var0, Expression[] var1, Value[] var2, int var3) {
      if (var3 >= var1.length) {
         return null;
      } else {
         Value var4 = var2[var3];
         if (var4 == null) {
            Expression var5 = var1[var3];
            if (var5 == null) {
               return null;
            }

            var4 = var2[var3] = var5.getValue(var0);
         }

         return var4;
      }
   }

   final Value[] getArgumentsValues(SessionLocal var1, Expression[] var2) {
      Value[] var3 = new Value[var2.length];
      if (this.info.nullIfParameterIsNull) {
         int var4 = 0;

         for(int var5 = var2.length; var4 < var5; ++var4) {
            Value var6 = var2[var4].getValue(var1);
            if (var6 == ValueNull.INSTANCE) {
               return null;
            }

            var3[var4] = var6;
         }
      }

      return var3;
   }

   void checkParameterCount(int var1) {
      throw DbException.getInternalError("type=" + this.info.type);
   }

   public void doneWithParameters() {
      int var1 = this.info.parameterCount;
      if (var1 == -1) {
         this.checkParameterCount(this.argsCount);
         super.doneWithParameters();
      } else if (var1 != this.argsCount) {
         throw DbException.get(7001, (String[])(this.info.name, Integer.toString(this.argsCount)));
      }

   }

   final boolean optimizeArguments(SessionLocal var1) {
      return this.optimizeArguments(var1, this.info.deterministic);
   }

   public String getName() {
      return this.info.name;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (!super.isEverything(var1)) {
         return false;
      } else {
         switch (var1.getType()) {
            case 2:
            case 5:
            case 8:
               return this.info.deterministic;
            default:
               return true;
         }
      }
   }
}
