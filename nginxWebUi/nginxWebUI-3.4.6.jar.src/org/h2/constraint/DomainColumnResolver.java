/*    */ package org.h2.constraint;
/*    */ 
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
/*    */ public class DomainColumnResolver
/*    */   implements ColumnResolver
/*    */ {
/*    */   private final Column column;
/*    */   private Value value;
/*    */   private String name;
/*    */   
/*    */   public DomainColumnResolver(TypeInfo paramTypeInfo) {
/* 24 */     this.column = new Column("VALUE", paramTypeInfo);
/*    */   }
/*    */   
/*    */   public void setValue(Value paramValue) {
/* 28 */     this.value = paramValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(Column paramColumn) {
/* 33 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Column[] getColumns() {
/* 38 */     return new Column[] { this.column };
/*    */   }
/*    */ 
/*    */   
/*    */   public Column findColumn(String paramString) {
/* 43 */     return null;
/*    */   }
/*    */   
/*    */   void setColumnName(String paramString) {
/* 47 */     this.name = paramString;
/*    */   }
/*    */   
/*    */   void resetColumnName() {
/* 51 */     this.name = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getColumnName() {
/* 60 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TypeInfo getValueType() {
/* 69 */     return this.column.getType();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\DomainColumnResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */