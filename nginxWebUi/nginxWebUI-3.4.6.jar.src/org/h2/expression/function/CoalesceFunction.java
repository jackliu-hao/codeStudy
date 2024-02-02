/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
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
/*     */ public final class CoalesceFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int COALESCE = 0;
/*     */   public static final int GREATEST = 1;
/*     */   public static final int LEAST = 2;
/*  36 */   private static final String[] NAMES = new String[] { "COALESCE", "GREATEST", "LEAST" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public CoalesceFunction(int paramInt) {
/*  43 */     this(paramInt, new Expression[4]);
/*     */   }
/*     */   
/*     */   public CoalesceFunction(int paramInt, Expression... paramVarArgs) {
/*  47 */     super(paramVarArgs);
/*  48 */     this.function = paramInt;
/*     */   } public Value getValue(SessionLocal paramSessionLocal) {
/*     */     Value value;
/*     */     byte b;
/*     */     int i;
/*  53 */     ValueNull valueNull = ValueNull.INSTANCE;
/*  54 */     switch (this.function) {
/*     */       case 0:
/*  56 */         for (b = 0, i = this.args.length; b < i; b++) {
/*  57 */           Value value1 = this.args[b].getValue(paramSessionLocal);
/*  58 */           if (value1 != ValueNull.INSTANCE) {
/*  59 */             value = value1.convertTo(this.type, (CastDataProvider)paramSessionLocal);
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
/*     */             break;
/*     */           } 
/*     */         } 
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
/*  90 */         return value;case 1: case 2: for (b = 0, i = this.args.length; b < i; b++) { Value value1 = this.args[b].getValue(paramSessionLocal); if (value1 != ValueNull.INSTANCE) { value1 = value1.convertTo(this.type, (CastDataProvider)paramSessionLocal); if (value == ValueNull.INSTANCE) { value = value1; } else { int j = paramSessionLocal.compareTypeSafe(value, value1); if (this.function == 1) { if (j < 0) value = value1;  } else if (j > 0) { value = value1; }  }  }  }  return value;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  95 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/*  96 */     this.type = TypeInfo.getHigherType((Typed[])this.args);
/*  97 */     if (this.type.getValueType() <= 0) {
/*  98 */       this.type = TypeInfo.TYPE_VARCHAR;
/*     */     }
/* 100 */     if (bool) {
/* 101 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 103 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 108 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CoalesceFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */