/*     */ package org.h2.mode;
/*     */ 
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.function.FunctionN;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.Value;
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
/*     */ 
/*     */ 
/*     */ public abstract class ModeFunction
/*     */   extends FunctionN
/*     */ {
/*     */   protected static final int VAR_ARGS = -1;
/*     */   protected final FunctionInfo info;
/*     */   
/*     */   public static ModeFunction getFunction(Database paramDatabase, String paramString) {
/*  43 */     Mode.ModeEnum modeEnum = paramDatabase.getMode().getEnum();
/*  44 */     if (modeEnum != Mode.ModeEnum.REGULAR) {
/*  45 */       return getCompatibilityModeFunction(paramString, modeEnum);
/*     */     }
/*  47 */     return null;
/*     */   }
/*     */   
/*     */   private static ModeFunction getCompatibilityModeFunction(String paramString, Mode.ModeEnum paramModeEnum) {
/*  51 */     switch (paramModeEnum) {
/*     */       case LEGACY:
/*  53 */         return FunctionsLegacy.getFunction(paramString);
/*     */       case DB2:
/*     */       case Derby:
/*  56 */         return FunctionsDB2Derby.getFunction(paramString);
/*     */       case MSSQLServer:
/*  58 */         return FunctionsMSSQLServer.getFunction(paramString);
/*     */       case MySQL:
/*  60 */         return FunctionsMySQL.getFunction(paramString);
/*     */       case Oracle:
/*  62 */         return FunctionsOracle.getFunction(paramString);
/*     */       case PostgreSQL:
/*  64 */         return FunctionsPostgreSQL.getFunction(paramString);
/*     */     } 
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ModeFunction(FunctionInfo paramFunctionInfo) {
/*  77 */     super(new Expression[(paramFunctionInfo.parameterCount != -1) ? paramFunctionInfo.parameterCount : 4]);
/*  78 */     this.info = paramFunctionInfo;
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
/*     */ 
/*     */ 
/*     */   
/*     */   static Value getNullOrValue(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression, Value[] paramArrayOfValue, int paramInt) {
/*  93 */     if (paramInt >= paramArrayOfExpression.length) {
/*  94 */       return null;
/*     */     }
/*  96 */     Value value = paramArrayOfValue[paramInt];
/*  97 */     if (value == null) {
/*  98 */       Expression expression = paramArrayOfExpression[paramInt];
/*  99 */       if (expression == null) {
/* 100 */         return null;
/*     */       }
/* 102 */       value = paramArrayOfValue[paramInt] = expression.getValue(paramSessionLocal);
/*     */     } 
/* 104 */     return value;
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
/*     */ 
/*     */ 
/*     */   
/*     */   final Value[] getArgumentsValues(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression) {
/* 119 */     Value[] arrayOfValue = new Value[paramArrayOfExpression.length];
/* 120 */     if (this.info.nullIfParameterIsNull) {
/* 121 */       byte b; int i; for (b = 0, i = paramArrayOfExpression.length; b < i; b++) {
/* 122 */         Value value = paramArrayOfExpression[b].getValue(paramSessionLocal);
/* 123 */         if (value == ValueNull.INSTANCE) {
/* 124 */           return null;
/*     */         }
/* 126 */         arrayOfValue[b] = value;
/*     */       } 
/*     */     } 
/* 129 */     return arrayOfValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void checkParameterCount(int paramInt) {
/* 139 */     throw DbException.getInternalError("type=" + this.info.type);
/*     */   }
/*     */ 
/*     */   
/*     */   public void doneWithParameters() {
/* 144 */     int i = this.info.parameterCount;
/* 145 */     if (i == -1) {
/* 146 */       checkParameterCount(this.argsCount);
/* 147 */       super.doneWithParameters();
/* 148 */     } else if (i != this.argsCount) {
/* 149 */       throw DbException.get(7001, new String[] { this.info.name, Integer.toString(this.argsCount) });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final boolean optimizeArguments(SessionLocal paramSessionLocal) {
/* 161 */     return optimizeArguments(paramSessionLocal, this.info.deterministic);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 166 */     return this.info.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 171 */     if (!super.isEverything(paramExpressionVisitor)) {
/* 172 */       return false;
/*     */     }
/* 174 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/*     */       case 5:
/*     */       case 8:
/* 178 */         return this.info.deterministic;
/*     */     } 
/* 180 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\ModeFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */