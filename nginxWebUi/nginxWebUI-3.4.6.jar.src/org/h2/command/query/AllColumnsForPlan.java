/*    */ package org.h2.command.query;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.table.Column;
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
/*    */ public class AllColumnsForPlan
/*    */ {
/*    */   private final TableFilter[] filters;
/*    */   private HashMap<Table, ArrayList<Column>> map;
/*    */   
/*    */   public AllColumnsForPlan(TableFilter[] paramArrayOfTableFilter) {
/* 26 */     this.filters = paramArrayOfTableFilter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(Column paramColumn) {
/* 35 */     ArrayList<Column> arrayList = this.map.get(paramColumn.getTable());
/* 36 */     if (arrayList == null) {
/* 37 */       arrayList = new ArrayList();
/* 38 */       this.map.put(paramColumn.getTable(), arrayList);
/*    */     } 
/* 40 */     if (!arrayList.contains(paramColumn)) {
/* 41 */       arrayList.add(paramColumn);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ArrayList<Column> get(Table paramTable) {
/* 51 */     if (this.map == null) {
/* 52 */       this.map = new HashMap<>();
/* 53 */       ExpressionVisitor.allColumnsForTableFilters(this.filters, this);
/*    */     } 
/* 55 */     return this.map.get(paramTable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\AllColumnsForPlan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */