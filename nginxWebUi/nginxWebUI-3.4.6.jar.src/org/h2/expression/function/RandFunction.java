/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.util.Random;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDouble;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueUuid;
/*     */ import org.h2.value.ValueVarbinary;
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
/*     */ public final class RandFunction
/*     */   extends Function0_1
/*     */ {
/*     */   public static final int RAND = 0;
/*     */   public static final int SECURE_RAND = 1;
/*     */   public static final int RANDOM_UUID = 2;
/*  42 */   private static final String[] NAMES = new String[] { "RAND", "SECURE_RAND", "RANDOM_UUID" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public RandFunction(Expression paramExpression, int paramInt) {
/*  49 */     super(paramExpression);
/*  50 */     this.function = paramInt;
/*     */   }
/*     */   public Value getValue(SessionLocal paramSessionLocal) { Value value;
/*     */     ValueDouble valueDouble;
/*     */     ValueVarbinary valueVarbinary;
/*     */     Random random;
/*  56 */     if (this.arg != null) {
/*  57 */       value = this.arg.getValue(paramSessionLocal);
/*  58 */       if (value == ValueNull.INSTANCE) {
/*  59 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*     */     } else {
/*  62 */       value = null;
/*     */     } 
/*  64 */     switch (this.function) {
/*     */       case 0:
/*  66 */         random = paramSessionLocal.getRandom();
/*  67 */         if (value != null) {
/*  68 */           random.setSeed(value.getInt());
/*     */         }
/*  70 */         valueDouble = ValueDouble.get(random.nextDouble());
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
/*  82 */         return (Value)valueDouble;
/*     */       case 1:
/*     */         return (Value)ValueVarbinary.getNoCopy(MathUtils.secureRandomBytes(valueDouble.getInt()));
/*     */       case 2:
/*     */         return (Value)ValueUuid.getNewRandom();
/*  87 */     }  throw DbException.getInternalError("function=" + this.function); } public Expression optimize(SessionLocal paramSessionLocal) { Value value; if (this.arg != null) {
/*  88 */       this.arg = this.arg.optimize(paramSessionLocal);
/*     */     }
/*  90 */     switch (this.function) {
/*     */       case 0:
/*  92 */         this.type = TypeInfo.TYPE_DOUBLE;
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
/* 107 */         return this;case 1: this.type = (this.arg.isConstant() && (value = this.arg.getValue(paramSessionLocal)) != ValueNull.INSTANCE) ? TypeInfo.getTypeInfo(6, Math.max(value.getInt(), 1), 0, null) : TypeInfo.TYPE_VARBINARY; return this;case 2: this.type = TypeInfo.TYPE_UUID; return this;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); }
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 112 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/* 114 */         return false;
/*     */     } 
/* 116 */     return super.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 121 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\RandFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */