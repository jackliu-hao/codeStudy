/*    */ package org.h2.result;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.h2.util.Utils;
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
/*    */ public final class MergedResult
/*    */ {
/* 25 */   private final ArrayList<Map<SimpleResult.Column, Value>> data = Utils.newSmallArrayList();
/*    */   
/* 27 */   private final ArrayList<SimpleResult.Column> columns = Utils.newSmallArrayList();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(ResultInterface paramResultInterface) {
/* 36 */     int i = paramResultInterface.getVisibleColumnCount();
/* 37 */     if (i == 0) {
/*    */       return;
/*    */     }
/* 40 */     SimpleResult.Column[] arrayOfColumn = new SimpleResult.Column[i];
/* 41 */     for (byte b = 0; b < i; b++) {
/*    */       
/* 43 */       SimpleResult.Column column = new SimpleResult.Column(paramResultInterface.getAlias(b), paramResultInterface.getColumnName(b), paramResultInterface.getColumnType(b));
/* 44 */       arrayOfColumn[b] = column;
/* 45 */       if (!this.columns.contains(column)) {
/* 46 */         this.columns.add(column);
/*    */       }
/*    */     } 
/* 49 */     while (paramResultInterface.next()) {
/* 50 */       if (i == 1) {
/* 51 */         this.data.add(Collections.singletonMap(arrayOfColumn[0], paramResultInterface.currentRow()[0])); continue;
/*    */       } 
/* 53 */       HashMap<Object, Object> hashMap = new HashMap<>();
/* 54 */       for (byte b1 = 0; b1 < i; b1++) {
/* 55 */         SimpleResult.Column column = arrayOfColumn[b1];
/* 56 */         hashMap.put(column, paramResultInterface.currentRow()[b1]);
/*    */       } 
/* 58 */       this.data.add(hashMap);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleResult getResult() {
/* 69 */     SimpleResult simpleResult = new SimpleResult();
/* 70 */     for (SimpleResult.Column column : this.columns) {
/* 71 */       simpleResult.addColumn(column);
/*    */     }
/* 73 */     for (Map<SimpleResult.Column, Value> map : this.data) {
/* 74 */       Value[] arrayOfValue = new Value[this.columns.size()];
/* 75 */       for (Map.Entry entry : map.entrySet()) {
/* 76 */         arrayOfValue[this.columns.indexOf(entry.getKey())] = (Value)entry.getValue();
/*    */       }
/* 78 */       simpleResult.addRow(arrayOfValue);
/*    */     } 
/* 80 */     return simpleResult;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return this.columns + ": " + this.data.size();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\MergedResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */