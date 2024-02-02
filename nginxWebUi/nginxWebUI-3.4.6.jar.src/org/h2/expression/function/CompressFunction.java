/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.tools.CompressTool;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueVarbinary;
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
/*    */ public final class CompressFunction
/*    */   extends Function1_2
/*    */ {
/*    */   public static final int COMPRESS = 0;
/*    */   public static final int EXPAND = 1;
/* 32 */   private static final String[] NAMES = new String[] { "COMPRESS", "EXPAND" };
/*    */ 
/*    */   
/*    */   private final int function;
/*    */ 
/*    */   
/*    */   public CompressFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/* 39 */     super(paramExpression1, paramExpression2);
/* 40 */     this.function = paramInt;
/*    */   }
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*    */     ValueVarbinary valueVarbinary;
/* 45 */     switch (this.function) {
/*    */       case 0:
/* 47 */         valueVarbinary = ValueVarbinary.getNoCopy(
/* 48 */             CompressTool.getInstance().compress(paramValue1.getBytesNoCopy(), (paramValue2 != null) ? paramValue2.getString() : null));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 56 */         return (Value)valueVarbinary;case 1: valueVarbinary = ValueVarbinary.getNoCopy(CompressTool.getInstance().expand(valueVarbinary.getBytesNoCopy())); return (Value)valueVarbinary;
/*    */     } 
/*    */     throw DbException.getInternalError("function=" + this.function);
/*    */   }
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 61 */     this.left = this.left.optimize(paramSessionLocal);
/* 62 */     if (this.right != null) {
/* 63 */       this.right = this.right.optimize(paramSessionLocal);
/*    */     }
/* 65 */     this.type = TypeInfo.TYPE_VARBINARY;
/* 66 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 67 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 69 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 74 */     return NAMES[this.function];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CompressFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */