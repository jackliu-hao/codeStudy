/*    */ package org.h2.command.query;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionColumn;
/*    */ import org.h2.table.Column;
/*    */ import org.h2.table.ColumnResolver;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SelectListColumnResolver
/*    */   implements ColumnResolver
/*    */ {
/*    */   private final Select select;
/*    */   private final Expression[] expressions;
/*    */   private final Column[] columns;
/*    */   
/*    */   SelectListColumnResolver(Select paramSelect) {
/* 36 */     this.select = paramSelect;
/* 37 */     int i = paramSelect.getColumnCount();
/* 38 */     this.columns = new Column[i];
/* 39 */     this.expressions = new Expression[i];
/* 40 */     ArrayList<Expression> arrayList = paramSelect.getExpressions();
/* 41 */     SessionLocal sessionLocal = paramSelect.getSession();
/* 42 */     for (byte b = 0; b < i; b++) {
/* 43 */       Expression expression = arrayList.get(b);
/* 44 */       this.columns[b] = new Column(expression.getAlias(sessionLocal, b), TypeInfo.TYPE_NULL, null, b);
/* 45 */       this.expressions[b] = expression.getNonAliasExpression();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Column[] getColumns() {
/* 51 */     return this.columns;
/*    */   }
/*    */ 
/*    */   
/*    */   public Column findColumn(String paramString) {
/* 56 */     Database database = this.select.getSession().getDatabase();
/* 57 */     for (Column column : this.columns) {
/* 58 */       if (database.equalsIdentifiers(column.getName(), paramString)) {
/* 59 */         return column;
/*    */       }
/*    */     } 
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Select getSelect() {
/* 67 */     return this.select;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(Column paramColumn) {
/* 72 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(ExpressionColumn paramExpressionColumn, Column paramColumn) {
/* 77 */     return this.expressions[paramColumn.getColumnId()];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\SelectListColumnResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */