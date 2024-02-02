package org.h2.mode;

import java.util.HashMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.ExtTypeInfoNumeric;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class FunctionsDB2Derby extends ModeFunction {
   private static final int IDENTITY_VAL_LOCAL = 5001;
   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap();
   private static final TypeInfo IDENTITY_VAL_LOCAL_TYPE;

   public static FunctionsDB2Derby getFunction(String var0) {
      FunctionInfo var1 = (FunctionInfo)FUNCTIONS.get(var0);
      return var1 != null ? new FunctionsDB2Derby(var1) : null;
   }

   private FunctionsDB2Derby(FunctionInfo var1) {
      super(var1);
   }

   public Value getValue(SessionLocal var1) {
      switch (this.info.type) {
         case 5001:
            return var1.getLastIdentity().convertTo(this.type);
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }
   }

   public Expression optimize(SessionLocal var1) {
      switch (this.info.type) {
         case 5001:
            this.type = IDENTITY_VAL_LOCAL_TYPE;
            return this;
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }
   }

   static {
      IDENTITY_VAL_LOCAL_TYPE = TypeInfo.getTypeInfo(13, 31L, 0, ExtTypeInfoNumeric.DECIMAL);
      FUNCTIONS.put("IDENTITY_VAL_LOCAL", new FunctionInfo("IDENTITY_VAL_LOCAL", 5001, 0, 12, true, false));
   }
}
