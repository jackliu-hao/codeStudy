/*    */ package org.h2.mode;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FunctionsLegacy
/*    */   extends ModeFunction
/*    */ {
/* 21 */   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
/*    */   
/*    */   private static final int IDENTITY = 6001;
/*    */   
/*    */   private static final int SCOPE_IDENTITY = 6002;
/*    */   
/*    */   static {
/* 28 */     FUNCTIONS.put("IDENTITY", new FunctionInfo("IDENTITY", 6001, 0, 12, true, false));
/* 29 */     FUNCTIONS.put("SCOPE_IDENTITY", new FunctionInfo("SCOPE_IDENTITY", 6002, 0, 12, true, false));
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
/*    */   public static FunctionsLegacy getFunction(String paramString) {
/* 41 */     FunctionInfo functionInfo = FUNCTIONS.get(paramString);
/* 42 */     if (functionInfo != null) {
/* 43 */       return new FunctionsLegacy(functionInfo);
/*    */     }
/* 45 */     return null;
/*    */   }
/*    */   
/*    */   private FunctionsLegacy(FunctionInfo paramFunctionInfo) {
/* 49 */     super(paramFunctionInfo);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 54 */     switch (this.info.type) {
/*    */       case 6001:
/*    */       case 6002:
/* 57 */         return paramSessionLocal.getLastIdentity().convertTo(this.type);
/*    */     } 
/* 59 */     throw DbException.getInternalError("type=" + this.info.type);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 65 */     this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
/* 66 */     return (Expression)this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionsLegacy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */