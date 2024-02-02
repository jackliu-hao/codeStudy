/*    */ package org.h2.mode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class FunctionInfo
/*    */ {
/*    */   public final String name;
/*    */   public final int type;
/*    */   final int parameterCount;
/*    */   public final int returnDataType;
/*    */   public final boolean nullIfParameterIsNull;
/*    */   public final boolean deterministic;
/*    */   
/*    */   public FunctionInfo(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2) {
/* 63 */     this.name = paramString;
/* 64 */     this.type = paramInt1;
/* 65 */     this.parameterCount = paramInt2;
/* 66 */     this.returnDataType = paramInt3;
/* 67 */     this.nullIfParameterIsNull = paramBoolean1;
/* 68 */     this.deterministic = paramBoolean2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FunctionInfo(FunctionInfo paramFunctionInfo, String paramString) {
/* 81 */     this.name = paramString;
/* 82 */     this.type = paramFunctionInfo.type;
/* 83 */     this.returnDataType = paramFunctionInfo.returnDataType;
/* 84 */     this.parameterCount = paramFunctionInfo.parameterCount;
/* 85 */     this.nullIfParameterIsNull = paramFunctionInfo.nullIfParameterIsNull;
/* 86 */     this.deterministic = paramFunctionInfo.deterministic;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */