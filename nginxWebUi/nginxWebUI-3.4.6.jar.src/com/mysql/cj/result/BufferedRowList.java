/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.Spliterators;
/*    */ import java.util.stream.Collectors;
/*    */ import java.util.stream.StreamSupport;
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
/*    */ public class BufferedRowList
/*    */   implements RowList
/*    */ {
/*    */   private List<Row> rowList;
/* 41 */   private int position = -1;
/*    */   
/*    */   public BufferedRowList(List<Row> rowList) {
/* 44 */     this.rowList = rowList;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BufferedRowList(Iterator<Row> ris) {
/* 54 */     this.rowList = (List<Row>)StreamSupport.stream(Spliterators.spliteratorUnknownSize(ris, 0), false).collect(Collectors.toList());
/*    */   }
/*    */   
/*    */   public Row next() {
/* 58 */     if (this.position + 1 == this.rowList.size()) {
/* 59 */       throw new NoSuchElementException("Can't next() when position=" + this.position + " and size=" + this.rowList.size());
/*    */     }
/* 61 */     return this.rowList.get(++this.position);
/*    */   }
/*    */   
/*    */   public Row previous() {
/* 65 */     if (this.position < 1) {
/* 66 */       throw new NoSuchElementException("Can't previous() when position=" + this.position);
/*    */     }
/* 68 */     return this.rowList.get(--this.position);
/*    */   }
/*    */   
/*    */   public Row get(int n) {
/* 72 */     if (n < 0 || n >= this.rowList.size()) {
/* 73 */       throw new NoSuchElementException("Can't get(" + n + ") when size=" + this.rowList.size());
/*    */     }
/* 75 */     return this.rowList.get(n);
/*    */   }
/*    */   
/*    */   public int getPosition() {
/* 79 */     return this.position;
/*    */   }
/*    */   
/*    */   public int size() {
/* 83 */     return this.rowList.size();
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 87 */     return (this.position + 1 < this.rowList.size());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\BufferedRowList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */