/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBigint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Rownum
/*    */   extends Operation0
/*    */ {
/*    */   private final Prepared prepared;
/*    */   private boolean singleRow;
/*    */   
/*    */   public Rownum(Prepared paramPrepared) {
/* 25 */     if (paramPrepared == null) {
/* 26 */       throw DbException.getInternalError();
/*    */     }
/* 28 */     this.prepared = paramPrepared;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 33 */     return (Value)ValueBigint.get(this.prepared.getCurrentRowNumber());
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 38 */     return TypeInfo.TYPE_BIGINT;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 43 */     return paramStringBuilder.append("ROWNUM()");
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 48 */     return this.singleRow ? ValueExpression.get((Value)ValueBigint.get(1L)) : this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 53 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 0:
/*    */       case 1:
/*    */       case 2:
/*    */       case 3:
/*    */       case 8:
/* 59 */         return false;
/*    */       case 11:
/* 61 */         if (paramExpressionVisitor.getQueryLevel() > 0) {
/* 62 */           this.singleRow = true;
/*    */         }
/*    */         break;
/*    */     } 
/* 66 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 72 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Rownum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */