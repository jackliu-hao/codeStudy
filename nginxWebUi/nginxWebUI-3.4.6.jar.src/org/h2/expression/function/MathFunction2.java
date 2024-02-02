/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueDouble;
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
/*    */ public final class MathFunction2
/*    */   extends Function2
/*    */ {
/*    */   public static final int ATAN2 = 0;
/*    */   public static final int LOG = 1;
/*    */   public static final int POWER = 2;
/* 36 */   private static final String[] NAMES = new String[] { "ATAN2", "LOG", "POWER" };
/*    */ 
/*    */   
/*    */   private final int function;
/*    */ 
/*    */   
/*    */   public MathFunction2(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/* 43 */     super(paramExpression1, paramExpression2);
/* 44 */     this.function = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/* 49 */     double d1 = paramValue1.getDouble(), d2 = paramValue2.getDouble();
/* 50 */     switch (this.function) {
/*    */       case 0:
/* 52 */         d1 = Math.atan2(d1, d2);
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
/* 81 */         return (Value)ValueDouble.get(d1);case 1: if ((paramSessionLocal.getMode()).swapLogFunctionParameters) { double d = d2; d2 = d1; d1 = d; }  if (d2 <= 0.0D) throw DbException.getInvalidValueException("LOG() argument", Double.valueOf(d2));  if (d1 <= 0.0D || d1 == 1.0D) throw DbException.getInvalidValueException("LOG() base", Double.valueOf(d1));  if (d1 == Math.E) { d1 = Math.log(d2); } else if (d1 == 10.0D) { d1 = Math.log10(d2); } else { d1 = Math.log(d2) / Math.log(d1); }  return (Value)ValueDouble.get(d1);case 2: d1 = Math.pow(d1, d2); return (Value)ValueDouble.get(d1);
/*    */     } 
/*    */     throw DbException.getInternalError("function=" + this.function);
/*    */   }
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 86 */     this.left = this.left.optimize(paramSessionLocal);
/* 87 */     this.right = this.right.optimize(paramSessionLocal);
/* 88 */     this.type = TypeInfo.TYPE_DOUBLE;
/* 89 */     if (this.left.isConstant() && this.right.isConstant()) {
/* 90 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 92 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 97 */     return NAMES[this.function];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\MathFunction2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */