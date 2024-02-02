/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnaryOperation
/*    */   extends Operation1
/*    */ {
/*    */   public UnaryOperation(Expression paramExpression) {
/* 19 */     super(paramExpression);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean needParentheses() {
/* 24 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 31 */     return this.arg.getSQL(paramStringBuilder.append("- "), paramInt, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 36 */     Value value = this.arg.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/* 37 */     return (value == ValueNull.INSTANCE) ? value : value.negate();
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 42 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 43 */     this.type = this.arg.getType();
/* 44 */     if (this.type.getValueType() == -1) {
/* 45 */       this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
/* 46 */     } else if (this.type.getValueType() == 36) {
/* 47 */       this.type = TypeInfo.TYPE_INTEGER;
/*    */     } 
/* 49 */     if (this.arg.isConstant()) {
/* 50 */       return ValueExpression.get(getValue(paramSessionLocal));
/*    */     }
/* 52 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\UnaryOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */