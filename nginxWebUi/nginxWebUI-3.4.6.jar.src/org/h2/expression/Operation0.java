/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
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
/*    */ public abstract class Operation0
/*    */   extends Expression
/*    */ {
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {}
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 27 */     return this;
/*    */   }
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {}
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Operation0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */