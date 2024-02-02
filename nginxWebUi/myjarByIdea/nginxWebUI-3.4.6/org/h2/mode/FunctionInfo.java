package org.h2.mode;

public final class FunctionInfo {
   public final String name;
   public final int type;
   final int parameterCount;
   public final int returnDataType;
   public final boolean nullIfParameterIsNull;
   public final boolean deterministic;

   public FunctionInfo(String var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      this.name = var1;
      this.type = var2;
      this.parameterCount = var3;
      this.returnDataType = var4;
      this.nullIfParameterIsNull = var5;
      this.deterministic = var6;
   }

   public FunctionInfo(FunctionInfo var1, String var2) {
      this.name = var2;
      this.type = var1.type;
      this.returnDataType = var1.returnDataType;
      this.parameterCount = var1.parameterCount;
      this.nullIfParameterIsNull = var1.nullIfParameterIsNull;
      this.deterministic = var1.deterministic;
   }
}
