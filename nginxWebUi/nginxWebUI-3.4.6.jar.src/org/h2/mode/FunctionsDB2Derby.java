/*    */ package org.h2.mode;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.ExtTypeInfo;
/*    */ import org.h2.value.ExtTypeInfoNumeric;
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
/*    */ 
/*    */ public final class FunctionsDB2Derby
/*    */   extends ModeFunction
/*    */ {
/*    */   private static final int IDENTITY_VAL_LOCAL = 5001;
/* 25 */   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
/*    */   
/* 27 */   private static final TypeInfo IDENTITY_VAL_LOCAL_TYPE = TypeInfo.getTypeInfo(13, 31L, 0, (ExtTypeInfo)ExtTypeInfoNumeric.DECIMAL);
/*    */ 
/*    */   
/*    */   static {
/* 31 */     FUNCTIONS.put("IDENTITY_VAL_LOCAL", new FunctionInfo("IDENTITY_VAL_LOCAL", 5001, 0, 12, true, false));
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
/*    */   public static FunctionsDB2Derby getFunction(String paramString) {
/* 43 */     FunctionInfo functionInfo = FUNCTIONS.get(paramString);
/* 44 */     return (functionInfo != null) ? new FunctionsDB2Derby(functionInfo) : null;
/*    */   }
/*    */   
/*    */   private FunctionsDB2Derby(FunctionInfo paramFunctionInfo) {
/* 48 */     super(paramFunctionInfo);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 53 */     switch (this.info.type) {
/*    */       case 5001:
/* 55 */         return paramSessionLocal.getLastIdentity().convertTo(this.type);
/*    */     } 
/* 57 */     throw DbException.getInternalError("type=" + this.info.type);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 63 */     switch (this.info.type) {
/*    */       case 5001:
/* 65 */         this.type = IDENTITY_VAL_LOCAL_TYPE;
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 70 */         return (Expression)this;
/*    */     } 
/*    */     throw DbException.getInternalError("type=" + this.info.type);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionsDB2Derby.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */