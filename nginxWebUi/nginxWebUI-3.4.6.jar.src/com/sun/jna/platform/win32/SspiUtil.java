/*    */ package com.sun.jna.platform.win32;
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
/*    */ public class SspiUtil
/*    */ {
/*    */   public static class ManagedSecBufferDesc
/*    */     extends Sspi.SecBufferDesc
/*    */   {
/*    */     private final Sspi.SecBuffer[] secBuffers;
/*    */     
/*    */     public ManagedSecBufferDesc(int type, byte[] token) {
/* 56 */       this.secBuffers = new Sspi.SecBuffer[] { new Sspi.SecBuffer(type, token) };
/* 57 */       this.pBuffers = this.secBuffers[0].getPointer();
/* 58 */       this.cBuffers = this.secBuffers.length;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public ManagedSecBufferDesc(int type, int tokenSize) {
/* 67 */       this.secBuffers = new Sspi.SecBuffer[] { new Sspi.SecBuffer(type, tokenSize) };
/* 68 */       this.pBuffers = this.secBuffers[0].getPointer();
/* 69 */       this.cBuffers = this.secBuffers.length;
/*    */     }
/*    */     
/*    */     public ManagedSecBufferDesc(int bufferCount) {
/* 73 */       this.cBuffers = bufferCount;
/* 74 */       this.secBuffers = (Sspi.SecBuffer[])(new Sspi.SecBuffer()).toArray(bufferCount);
/* 75 */       this.pBuffers = this.secBuffers[0].getPointer();
/* 76 */       this.cBuffers = this.secBuffers.length;
/*    */     }
/*    */     
/*    */     public Sspi.SecBuffer getBuffer(int idx) {
/* 80 */       return this.secBuffers[idx];
/*    */     }
/*    */ 
/*    */     
/*    */     public void write() {
/* 85 */       for (Sspi.SecBuffer sb : this.secBuffers) {
/* 86 */         sb.write();
/*    */       }
/* 88 */       writeField("ulVersion");
/* 89 */       writeField("pBuffers");
/* 90 */       writeField("cBuffers");
/*    */     }
/*    */ 
/*    */     
/*    */     public void read() {
/* 95 */       for (Sspi.SecBuffer sb : this.secBuffers)
/* 96 */         sb.read(); 
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\SspiUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */