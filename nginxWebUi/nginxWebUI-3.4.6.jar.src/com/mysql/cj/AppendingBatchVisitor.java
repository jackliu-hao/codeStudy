/*    */ package com.mysql.cj;
/*    */ 
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ import java.util.LinkedList;
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
/*    */ 
/*    */ public class AppendingBatchVisitor
/*    */   implements BatchVisitor
/*    */ {
/* 37 */   LinkedList<byte[]> statementComponents = (LinkedList)new LinkedList<>();
/*    */   
/*    */   public BatchVisitor append(byte[] values) {
/* 40 */     this.statementComponents.addLast(values);
/*    */     
/* 42 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public BatchVisitor increment() {
/* 47 */     return this;
/*    */   }
/*    */   
/*    */   public BatchVisitor decrement() {
/* 51 */     this.statementComponents.removeLast();
/*    */     
/* 53 */     return this;
/*    */   }
/*    */   
/*    */   public BatchVisitor merge(byte[] front, byte[] back) {
/* 57 */     int mergedLength = front.length + back.length;
/* 58 */     byte[] merged = new byte[mergedLength];
/* 59 */     System.arraycopy(front, 0, merged, 0, front.length);
/* 60 */     System.arraycopy(back, 0, merged, front.length, back.length);
/* 61 */     this.statementComponents.addLast(merged);
/* 62 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public BatchVisitor mergeWithLast(byte[] values) {
/* 67 */     if (this.statementComponents.isEmpty()) {
/* 68 */       return append(values);
/*    */     }
/* 70 */     return merge(this.statementComponents.removeLast(), values);
/*    */   }
/*    */   
/*    */   public byte[][] getStaticSqlStrings() {
/* 74 */     byte[][] asBytes = new byte[this.statementComponents.size()][];
/* 75 */     this.statementComponents.toArray(asBytes);
/*    */     
/* 77 */     return asBytes;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     StringBuilder sb = new StringBuilder();
/* 83 */     for (byte[] comp : this.statementComponents) {
/* 84 */       sb.append(StringUtils.toString(comp));
/*    */     }
/* 86 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\AppendingBatchVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */