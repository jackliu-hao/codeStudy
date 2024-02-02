/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.result.Row;
/*    */ import com.mysql.cj.result.RowList;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.function.Consumer;
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
/*    */ public class XProtocolRowInputStream
/*    */   implements RowList
/*    */ {
/*    */   private ColumnDefinition metadata;
/*    */   private XProtocol protocol;
/*    */   private boolean isDone = false;
/* 43 */   private int position = -1;
/*    */   
/*    */   private Row next;
/*    */   private Consumer<Notice> noticeConsumer;
/*    */   
/*    */   public XProtocolRowInputStream(ColumnDefinition metadata, XProtocol protocol, Consumer<Notice> noticeConsumer) {
/* 49 */     this.metadata = metadata;
/* 50 */     this.protocol = protocol;
/* 51 */     this.noticeConsumer = noticeConsumer;
/*    */   }
/*    */   
/*    */   public XProtocolRowInputStream(ColumnDefinition metadata, Row row, XProtocol protocol, Consumer<Notice> noticeConsumer) {
/* 55 */     this.metadata = metadata;
/* 56 */     this.protocol = protocol;
/* 57 */     this.next = row;
/* 58 */     this.next.setMetadata(metadata);
/* 59 */     this.noticeConsumer = noticeConsumer;
/*    */   }
/*    */   
/*    */   public Row readRow() {
/* 63 */     if (!hasNext()) {
/* 64 */       this.isDone = true;
/* 65 */       return null;
/*    */     } 
/* 67 */     this.position++;
/* 68 */     Row r = this.next;
/* 69 */     this.next = null;
/* 70 */     return r;
/*    */   }
/*    */   
/*    */   public Row next() {
/* 74 */     if (!hasNext()) {
/* 75 */       throw new NoSuchElementException();
/*    */     }
/* 77 */     return readRow();
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 81 */     if (this.isDone)
/* 82 */       return false; 
/* 83 */     if (this.next == null) {
/* 84 */       this.next = this.protocol.readRowOrNull(this.metadata, this.noticeConsumer);
/*    */     }
/* 86 */     return (this.next != null);
/*    */   }
/*    */   
/*    */   public int getPosition() {
/* 90 */     return this.position;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XProtocolRowInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */