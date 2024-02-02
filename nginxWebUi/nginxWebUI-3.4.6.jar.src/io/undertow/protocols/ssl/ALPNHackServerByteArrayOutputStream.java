/*    */ package io.undertow.protocols.ssl;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import javax.net.ssl.SSLException;
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
/*    */ class ALPNHackServerByteArrayOutputStream
/*    */   extends ByteArrayOutputStream
/*    */ {
/*    */   private final SSLEngine sslEngine;
/*    */   private byte[] serverHello;
/*    */   private final String alpnProtocol;
/*    */   private boolean ready = false;
/*    */   
/*    */   ALPNHackServerByteArrayOutputStream(SSLEngine sslEngine, byte[] bytes, String alpnProtocol) {
/* 42 */     this.sslEngine = sslEngine;
/* 43 */     this.alpnProtocol = alpnProtocol;
/*    */     try {
/* 45 */       write(bytes);
/* 46 */     } catch (IOException e) {
/* 47 */       throw new RuntimeException(e);
/*    */     } 
/* 49 */     this.ready = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) {
/* 54 */     if (this.ready && 
/* 55 */       b[off] == 2) {
/* 56 */       this.ready = false;
/*    */       
/* 58 */       this.serverHello = new byte[len];
/* 59 */       System.arraycopy(b, off, this.serverHello, 0, len);
/*    */       try {
/* 61 */         this.serverHello = ALPNHackServerHelloExplorer.addAlpnExtensionsToServerHello(this.serverHello, this.alpnProtocol);
/* 62 */       } catch (SSLException e) {
/* 63 */         throw new RuntimeException(e);
/*    */       } 
/* 65 */       ALPNHackSSLEngine.regenerateHashes(this.sslEngine, this, new byte[][] { toByteArray(), this.serverHello });
/*    */       
/*    */       return;
/*    */     } 
/* 69 */     super.write(b, off, len);
/*    */   }
/*    */   
/*    */   byte[] getServerHello() {
/* 73 */     return this.serverHello;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\ALPNHackServerByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */