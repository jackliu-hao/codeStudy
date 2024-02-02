/*    */ package com.mysql.cj.protocol.a;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PacketSplitter
/*    */ {
/*    */   private int totalSize;
/* 37 */   private int currentPacketLen = 0;
/* 38 */   private int offset = 0;
/*    */   
/*    */   public PacketSplitter(int totalSize) {
/* 41 */     this.totalSize = totalSize;
/*    */   }
/*    */   
/*    */   public int getPacketLen() {
/* 45 */     return this.currentPacketLen;
/*    */   }
/*    */   
/*    */   public int getOffset() {
/* 49 */     return this.offset;
/*    */   }
/*    */   
/*    */   public boolean nextPacket() {
/* 53 */     this.offset += this.currentPacketLen;
/*    */     
/* 55 */     if (this.currentPacketLen == 16777215 && this.offset == this.totalSize) {
/* 56 */       this.currentPacketLen = 0;
/* 57 */       return true;
/*    */     } 
/*    */ 
/*    */     
/* 61 */     if (this.totalSize == 0) {
/* 62 */       this.totalSize = -1;
/* 63 */       return true;
/*    */     } 
/*    */     
/* 66 */     this.currentPacketLen = this.totalSize - this.offset;
/* 67 */     if (this.currentPacketLen > 16777215) {
/* 68 */       this.currentPacketLen = 16777215;
/*    */     }
/* 70 */     return (this.offset < this.totalSize);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\PacketSplitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */