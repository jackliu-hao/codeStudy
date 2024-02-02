package org.h2.mode;

import java.util.HashMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class FunctionsLegacy extends ModeFunction {
   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap();
   private static final int IDENTITY = 6001;
   private static final int SCOPE_IDENTITY = 6002;

   public static FunctionsLegacy getFunction(String var0) {
      FunctionInfo var1 = (FunctionInfo)FUNCTIONS.get(var0);
      return var1 != null ? new FunctionsLegacy(var1) : null;
   }

   private FunctionsLegacy(FunctionInfo var1) {
      super(var1);
   }

   public Value getValue(SessionLocal var1) {
      switch (this.info.type) {
         case 6001:
         case 6002:
            return var1.getLastIdentity().convertTo(this.type);
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
      return this;
   }

   static {
      FUNCTIONS.put("IDENTITY", new FunctionInfo("IDENTITY", 6001, 0, 12, true, false));
      FUNCTIONS.put("SCOPE_IDENTITY", new FunctionInfo("SCOPE_IDENTITY", 6002, 0, 12, true, false));
   }
}
