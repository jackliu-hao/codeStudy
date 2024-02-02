/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.function.CoalesceFunction;
/*     */ import org.h2.expression.function.CurrentDateTimeValueFunction;
/*     */ import org.h2.expression.function.RandFunction;
/*     */ import org.h2.expression.function.StringFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FunctionsMSSQLServer
/*     */   extends ModeFunction
/*     */ {
/*  30 */   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
/*     */   
/*     */   private static final int CHARINDEX = 4001;
/*     */   
/*     */   private static final int GETDATE = 4002;
/*     */   
/*     */   private static final int ISNULL = 4003;
/*     */   
/*     */   private static final int LEN = 4004;
/*     */   
/*     */   private static final int NEWID = 4005;
/*     */   
/*     */   private static final int SCOPE_IDENTITY = 4006;
/*     */   
/*  44 */   private static final TypeInfo SCOPE_IDENTITY_TYPE = TypeInfo.getTypeInfo(13, 38L, 0, null);
/*     */   
/*     */   static {
/*  47 */     FUNCTIONS.put("CHARINDEX", new FunctionInfo("CHARINDEX", 4001, -1, 11, true, true));
/*  48 */     FUNCTIONS.put("GETDATE", new FunctionInfo("GETDATE", 4002, 0, 20, false, true));
/*  49 */     FUNCTIONS.put("LEN", new FunctionInfo("LEN", 4004, 1, 11, true, true));
/*  50 */     FUNCTIONS.put("NEWID", new FunctionInfo("NEWID", 4005, 0, 39, true, false));
/*  51 */     FUNCTIONS.put("ISNULL", new FunctionInfo("ISNULL", 4003, 2, 0, false, true));
/*  52 */     FUNCTIONS.put("SCOPE_IDENTITY", new FunctionInfo("SCOPE_IDENTITY", 4006, 0, 13, true, false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FunctionsMSSQLServer getFunction(String paramString) {
/*  64 */     FunctionInfo functionInfo = FUNCTIONS.get(paramString);
/*  65 */     if (functionInfo != null) {
/*  66 */       return new FunctionsMSSQLServer(functionInfo);
/*     */     }
/*  68 */     return null;
/*     */   }
/*     */   
/*     */   private FunctionsMSSQLServer(FunctionInfo paramFunctionInfo) {
/*  72 */     super(paramFunctionInfo);
/*     */   }
/*     */   
/*     */   protected void checkParameterCount(int paramInt) {
/*     */     byte b1;
/*     */     byte b2;
/*  78 */     switch (this.info.type) {
/*     */       case 4001:
/*  80 */         b1 = 2;
/*  81 */         b2 = 3;
/*     */         break;
/*     */       default:
/*  84 */         throw DbException.getInternalError("type=" + this.info.type);
/*     */     } 
/*  86 */     if (paramInt < b1 || paramInt > b2) {
/*  87 */       throw DbException.get(7001, new String[] { this.info.name, b1 + ".." + b2 });
/*     */     }
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*     */     long l;
/*  93 */     Value[] arrayOfValue = getArgumentsValues(paramSessionLocal, this.args);
/*  94 */     if (arrayOfValue == null) {
/*  95 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  97 */     Value value = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 0);
/*  98 */     switch (this.info.type) {
/*     */       
/*     */       case 4004:
/* 101 */         if (value.getValueType() == 1) {
/* 102 */           String str = value.getString();
/* 103 */           int i = str.length();
/* 104 */           while (i > 0 && str.charAt(i - 1) == ' ') {
/* 105 */             i--;
/*     */           }
/* 107 */           l = i;
/*     */         } else {
/* 109 */           l = value.charLength();
/*     */         } 
/* 111 */         return (Value)ValueBigint.get(l);
/*     */       
/*     */       case 4006:
/* 114 */         return paramSessionLocal.getLastIdentity().convertTo(this.type);
/*     */     } 
/* 116 */     throw DbException.getInternalError("type=" + this.info.type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 122 */     switch (this.info.type)
/*     */     { case 4001:
/* 124 */         return (new StringFunction(this.args, 0)).optimize(paramSessionLocal);
/*     */       case 4002:
/* 126 */         return (new CurrentDateTimeValueFunction(4, 3)).optimize(paramSessionLocal);
/*     */       case 4003:
/* 128 */         return (new CoalesceFunction(0, this.args)).optimize(paramSessionLocal);
/*     */       case 4005:
/* 130 */         return (new RandFunction(null, 2)).optimize(paramSessionLocal);
/*     */       case 4006:
/* 132 */         this.type = SCOPE_IDENTITY_TYPE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 140 */         return (Expression)this; }  this.type = TypeInfo.getTypeInfo(this.info.returnDataType); if (optimizeArguments(paramSessionLocal)) return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);  return (Expression)this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionsMSSQLServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */