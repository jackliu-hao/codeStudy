/*    */ package org.apache.http.impl.auth;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.security.MessageDigest;
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
/*    */ class HttpEntityDigester
/*    */   extends OutputStream
/*    */ {
/*    */   private final MessageDigest digester;
/*    */   private boolean closed;
/*    */   private byte[] digest;
/*    */   
/*    */   HttpEntityDigester(MessageDigest digester) {
/* 41 */     this.digester = digester;
/* 42 */     this.digester.reset();
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 47 */     if (this.closed) {
/* 48 */       throw new IOException("Stream has been already closed");
/*    */     }
/* 50 */     this.digester.update((byte)b);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 55 */     if (this.closed) {
/* 56 */       throw new IOException("Stream has been already closed");
/*    */     }
/* 58 */     this.digester.update(b, off, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 63 */     if (this.closed) {
/*    */       return;
/*    */     }
/* 66 */     this.closed = true;
/* 67 */     this.digest = this.digester.digest();
/* 68 */     super.close();
/*    */   }
/*    */   
/*    */   public byte[] getDigest() {
/* 72 */     return this.digest;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\HttpEntityDigester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */