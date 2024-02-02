/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDouble;
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
/*     */ public final class MathFunction1
/*     */   extends Function1
/*     */ {
/*     */   public static final int SIN = 0;
/*     */   public static final int COS = 1;
/*     */   public static final int TAN = 2;
/*     */   public static final int COT = 3;
/*     */   public static final int SINH = 4;
/*     */   public static final int COSH = 5;
/*     */   public static final int TANH = 6;
/*     */   public static final int ASIN = 7;
/*     */   public static final int ACOS = 8;
/*     */   public static final int ATAN = 9;
/*     */   public static final int LOG10 = 10;
/*     */   public static final int LN = 11;
/*     */   public static final int EXP = 12;
/*     */   public static final int SQRT = 13;
/*     */   public static final int DEGREES = 14;
/*     */   public static final int RADIANS = 15;
/* 113 */   private static final String[] NAMES = new String[] { "SIN", "COS", "TAN", "COT", "SINH", "COSH", "TANH", "ASIN", "ACOS", "ATAN", "LOG10", "LN", "EXP", "SQRT", "DEGREES", "RADIANS" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */ 
/*     */   
/*     */   public MathFunction1(Expression paramExpression, int paramInt) {
/* 121 */     super(paramExpression);
/* 122 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 127 */     Value value = this.arg.getValue(paramSessionLocal);
/* 128 */     if (value == ValueNull.INSTANCE) {
/* 129 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 131 */     double d = value.getDouble();
/* 132 */     switch (this.function) {
/*     */       case 0:
/* 134 */         d = Math.sin(d);
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
/* 194 */         return (Value)ValueDouble.get(d);case 1: d = Math.cos(d); return (Value)ValueDouble.get(d);case 2: d = Math.tan(d); return (Value)ValueDouble.get(d);case 3: d = Math.tan(d); if (d == 0.0D) throw DbException.get(22012, getTraceSQL());  d = 1.0D / d; return (Value)ValueDouble.get(d);case 4: d = Math.sinh(d); return (Value)ValueDouble.get(d);case 5: d = Math.cosh(d); return (Value)ValueDouble.get(d);case 6: d = Math.tanh(d); return (Value)ValueDouble.get(d);case 7: d = Math.asin(d); return (Value)ValueDouble.get(d);case 8: d = Math.acos(d); return (Value)ValueDouble.get(d);case 9: d = Math.atan(d); return (Value)ValueDouble.get(d);case 10: if (d <= 0.0D) throw DbException.getInvalidValueException("LOG10() argument", Double.valueOf(d));  d = Math.log10(d); return (Value)ValueDouble.get(d);case 11: if (d <= 0.0D) throw DbException.getInvalidValueException("LN() argument", Double.valueOf(d));  d = Math.log(d); return (Value)ValueDouble.get(d);case 12: d = Math.exp(d); return (Value)ValueDouble.get(d);case 13: d = Math.sqrt(d); return (Value)ValueDouble.get(d);case 14: d = Math.toDegrees(d); return (Value)ValueDouble.get(d);case 15: d = Math.toRadians(d); return (Value)ValueDouble.get(d);
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 199 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 200 */     this.type = TypeInfo.TYPE_DOUBLE;
/* 201 */     if (this.arg.isConstant()) {
/* 202 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 204 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 209 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\MathFunction1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */