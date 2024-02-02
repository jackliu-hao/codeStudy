/*    */ package org.h2.command.query;
/*    */ 
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.result.SortOrder;
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
/*    */ public class QueryOrderBy
/*    */ {
/*    */   public Expression expression;
/*    */   public Expression columnIndexExpr;
/*    */   public int sortType;
/*    */   
/*    */   public void getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 40 */     ((this.expression != null) ? this.expression : this.columnIndexExpr).getUnenclosedSQL(paramStringBuilder, paramInt);
/* 41 */     SortOrder.typeToString(paramStringBuilder, this.sortType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\QueryOrderBy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */