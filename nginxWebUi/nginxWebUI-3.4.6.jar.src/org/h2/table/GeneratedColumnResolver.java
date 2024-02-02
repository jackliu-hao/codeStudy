/*    */ package org.h2.table;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import org.h2.result.Row;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class GeneratedColumnResolver
/*    */   implements ColumnResolver
/*    */ {
/*    */   private final Table table;
/*    */   private Column[] columns;
/*    */   private HashMap<String, Column> columnMap;
/*    */   private Row current;
/*    */   
/*    */   GeneratedColumnResolver(Table paramTable) {
/* 34 */     this.table = paramTable;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void set(Row paramRow) {
/* 44 */     this.current = paramRow;
/*    */   }
/*    */ 
/*    */   
/*    */   public Column[] getColumns() {
/* 49 */     Column[] arrayOfColumn = this.columns;
/* 50 */     if (arrayOfColumn == null) {
/* 51 */       this.columns = arrayOfColumn = createColumns();
/*    */     }
/* 53 */     return arrayOfColumn;
/*    */   }
/*    */   
/*    */   private Column[] createColumns() {
/* 57 */     Column[] arrayOfColumn1 = this.table.getColumns();
/* 58 */     int i = arrayOfColumn1.length, j = i;
/* 59 */     for (byte b1 = 0; b1 < i; b1++) {
/* 60 */       if (arrayOfColumn1[b1].isGenerated()) {
/* 61 */         j--;
/*    */       }
/*    */     } 
/* 64 */     Column[] arrayOfColumn2 = new Column[j];
/* 65 */     for (byte b2 = 0, b3 = 0; b2 < i; b2++) {
/* 66 */       Column column = arrayOfColumn1[b2];
/* 67 */       if (!column.isGenerated()) {
/* 68 */         arrayOfColumn2[b3++] = column;
/*    */       }
/*    */     } 
/* 71 */     return arrayOfColumn2;
/*    */   }
/*    */ 
/*    */   
/*    */   public Column findColumn(String paramString) {
/* 76 */     HashMap<String, Column> hashMap = this.columnMap;
/* 77 */     if (hashMap == null) {
/* 78 */       hashMap = this.table.getDatabase().newStringMap();
/* 79 */       for (Column column : getColumns()) {
/* 80 */         hashMap.put(column.getName(), column);
/*    */       }
/* 82 */       this.columnMap = hashMap;
/*    */     } 
/* 84 */     return hashMap.get(paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(Column paramColumn) {
/* 89 */     int i = paramColumn.getColumnId();
/* 90 */     if (i == -1) {
/* 91 */       return (Value)ValueBigint.get(this.current.getKey());
/*    */     }
/* 93 */     return this.current.getValue(i);
/*    */   }
/*    */ 
/*    */   
/*    */   public Column getRowIdColumn() {
/* 98 */     return this.table.getRowIdColumn();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\GeneratedColumnResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */