/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.table.Table;
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
/*    */ abstract class FilteredDataChangeStatement
/*    */   extends DataChangeStatement
/*    */ {
/*    */   Expression condition;
/*    */   TableFilter targetTableFilter;
/*    */   Expression fetchExpr;
/*    */   
/*    */   FilteredDataChangeStatement(SessionLocal paramSessionLocal) {
/* 41 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public final Table getTable() {
/* 46 */     return this.targetTableFilter.getTable();
/*    */   }
/*    */   
/*    */   public final void setTableFilter(TableFilter paramTableFilter) {
/* 50 */     this.targetTableFilter = paramTableFilter;
/*    */   }
/*    */   
/*    */   public final TableFilter getTableFilter() {
/* 54 */     return this.targetTableFilter;
/*    */   }
/*    */   
/*    */   public final void setCondition(Expression paramExpression) {
/* 58 */     this.condition = paramExpression;
/*    */   }
/*    */   
/*    */   public final Expression getCondition() {
/* 62 */     return this.condition;
/*    */   }
/*    */   
/*    */   public void setFetch(Expression paramExpression) {
/* 66 */     this.fetchExpr = paramExpression;
/*    */   }
/*    */   
/*    */   final boolean nextRow(long paramLong1, long paramLong2) {
/* 70 */     if (paramLong1 < 0L || paramLong2 < paramLong1) {
/* 71 */       while (this.targetTableFilter.next()) {
/* 72 */         setCurrentRowNumber(paramLong2 + 1L);
/* 73 */         if (this.condition == null || this.condition.getBooleanValue(this.session)) {
/* 74 */           return true;
/*    */         }
/*    */       } 
/*    */     }
/* 78 */     return false;
/*    */   }
/*    */   
/*    */   final void appendFilterCondition(StringBuilder paramStringBuilder, int paramInt) {
/* 82 */     if (this.condition != null) {
/* 83 */       paramStringBuilder.append("\nWHERE ");
/* 84 */       this.condition.getUnenclosedSQL(paramStringBuilder, paramInt);
/*    */     } 
/* 86 */     if (this.fetchExpr != null) {
/* 87 */       paramStringBuilder.append("\nFETCH FIRST ");
/* 88 */       String str = this.fetchExpr.getSQL(paramInt, 2);
/* 89 */       if ("1".equals(str)) {
/* 90 */         paramStringBuilder.append("ROW ONLY");
/*    */       } else {
/* 92 */         paramStringBuilder.append(str).append(" ROWS ONLY");
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\FilteredDataChangeStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */