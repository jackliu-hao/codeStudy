/*    */ package io.undertow.protocols.ssl;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import javax.net.ssl.SSLEngine;
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
/*    */ class ALPNHackClientByteArrayOutputStream
/*    */   extends ByteArrayOutputStream
/*    */ {
/*    */   private final SSLEngine sslEngine;
/*    */   private boolean ready = true;
/*    */   private byte[] receivedServerHello;
/*    */   private byte[] sentClientHello;
/*    */   
/*    */   ALPNHackClientByteArrayOutputStream(SSLEngine sslEngine) {
/* 42 */     this.sslEngine = sslEngine;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) {
/* 47 */     if (this.ready && 
/* 48 */       b[off] == 2) {
/* 49 */       byte[] newData; this.ready = false;
/*    */       
/* 51 */       if (this.receivedServerHello != null) {
/* 52 */         int b1 = b[off + 1];
/* 53 */         int b2 = b[off + 2];
/* 54 */         int b3 = b[off + 3];
/* 55 */         int length = (b1 & 0xFF) << 16 | (b2 & 0xFF) << 8 | b3 & 0xFF;
/* 56 */         if (length + 4 == len) {
/* 57 */           newData = this.receivedServerHello;
/*    */         } else {
/* 59 */           newData = new byte[this.receivedServerHello.length + len - 4 - length];
/* 60 */           System.arraycopy(this.receivedServerHello, 0, newData, 0, this.receivedServerHello.length);
/* 61 */           System.arraycopy(b, length + 4, newData, this.receivedServerHello.length, len - 4 - length);
/*    */         } 
/*    */       } else {
/* 64 */         newData = new byte[len];
/* 65 */         System.arraycopy(b, off, newData, 0, len);
/*    */       } 
/* 67 */       ALPNHackSSLEngine.regenerateHashes(this.sslEngine, this, new byte[][] { this.sentClientHello, newData });
/*    */       
/*    */       return;
/*    */     } 
/* 71 */     super.write(b, off, len);
/*    */   }
/*    */   
/*    */   byte[] getSentClientHello() {
/* 75 */     return this.sentClientHello;
/*    */   }
/*    */   
/*    */   void setSentClientHello(byte[] sentClientHello) {
/* 79 */     this.sentClientHello = sentClientHello;
/*    */   }
/*    */   
/*    */   byte[] getReceivedServerHello() {
/* 83 */     return this.receivedServerHello;
/*    */   }
/*    */   
/*    */   void setReceivedServerHello(byte[] receivedServerHello) {
/* 87 */     this.receivedServerHello = receivedServerHello;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\ALPNHackClientByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */