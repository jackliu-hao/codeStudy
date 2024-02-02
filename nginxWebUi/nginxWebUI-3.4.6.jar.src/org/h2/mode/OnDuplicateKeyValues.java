/*    */ package org.h2.mode;
/*    */ 
/*    */ import org.h2.command.dml.Update;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.expression.Operation0;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.table.Column;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class OnDuplicateKeyValues
/*    */   extends Operation0
/*    */ {
/*    */   private final Column column;
/*    */   private final Update update;
/*    */   
/*    */   public OnDuplicateKeyValues(Column paramColumn, Update paramUpdate) {
/* 27 */     this.column = paramColumn;
/* 28 */     this.update = paramUpdate;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 33 */     Value value = this.update.getOnDuplicateKeyInsert().getOnDuplicateKeyValue(this.column.getColumnId());
/* 34 */     if (value == null) {
/* 35 */       throw DbException.getUnsupportedException(getTraceSQL());
/*    */     }
/* 37 */     return value;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 42 */     return this.column.getSQL(paramStringBuilder.append("VALUES("), paramInt).append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 47 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 2:
/* 49 */         return false;
/*    */     } 
/* 51 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 56 */     return this.column.getType();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 61 */     return 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\OnDuplicateKeyValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */