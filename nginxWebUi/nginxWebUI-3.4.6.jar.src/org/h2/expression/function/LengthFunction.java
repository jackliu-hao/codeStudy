/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBigint;
/*    */ import org.h2.value.ValueNull;
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
/*    */ public final class LengthFunction
/*    */   extends Function1
/*    */ {
/*    */   public static final int CHAR_LENGTH = 0;
/*    */   public static final int OCTET_LENGTH = 1;
/*    */   public static final int BIT_LENGTH = 2;
/* 37 */   private static final String[] NAMES = new String[] { "CHAR_LENGTH", "OCTET_LENGTH", "BIT_LENGTH" };
/*    */ 
/*    */   
/*    */   private final int function;
/*    */ 
/*    */   
/*    */   public LengthFunction(Expression paramExpression, int paramInt) {
/* 44 */     super(paramExpression);
/* 45 */     this.function = paramInt;
/*    */   }
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/*    */     long l;
/* 50 */     Value value = this.arg.getValue(paramSessionLocal);
/* 51 */     if (value == ValueNull.INSTANCE) {
/* 52 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/*    */     
/* 55 */     switch (this.function) {
/*    */       case 0:
/* 57 */         l = value.charLength();
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
/* 68 */         return (Value)ValueBigint.get(l);case 1: l = value.octetLength(); return (Value)ValueBigint.get(l);case 2: l = value.octetLength() * 8L; return (Value)ValueBigint.get(l);
/*    */     } 
/*    */     throw DbException.getInternalError("function=" + this.function);
/*    */   }
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 73 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 74 */     this.type = TypeInfo.TYPE_BIGINT;
/* 75 */     if (this.arg.isConstant()) {
/* 76 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 78 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 83 */     return NAMES[this.function];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\LengthFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */