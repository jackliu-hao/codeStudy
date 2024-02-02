/*    */ package org.apache.http.client.entity;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ class LazyDecompressingInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final InputStream wrappedStream;
/*    */   private final InputStreamFactory inputStreamFactory;
/*    */   private InputStream wrapperStream;
/*    */   
/*    */   public LazyDecompressingInputStream(InputStream wrappedStream, InputStreamFactory inputStreamFactory) {
/* 45 */     this.wrappedStream = wrappedStream;
/* 46 */     this.inputStreamFactory = inputStreamFactory;
/*    */   }
/*    */   
/*    */   private void initWrapper() throws IOException {
/* 50 */     if (this.wrapperStream == null) {
/* 51 */       this.wrapperStream = this.inputStreamFactory.create(this.wrappedStream);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 57 */     initWrapper();
/* 58 */     return this.wrapperStream.read();
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b) throws IOException {
/* 63 */     initWrapper();
/* 64 */     return this.wrapperStream.read(b);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 69 */     initWrapper();
/* 70 */     return this.wrapperStream.read(b, off, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public long skip(long n) throws IOException {
/* 75 */     initWrapper();
/* 76 */     return this.wrapperStream.skip(n);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean markSupported() {
/* 81 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int available() throws IOException {
/* 86 */     initWrapper();
/* 87 */     return this.wrapperStream.available();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/*    */     try {
/* 93 */       if (this.wrapperStream != null) {
/* 94 */         this.wrapperStream.close();
/*    */       }
/*    */     } finally {
/* 97 */       this.wrappedStream.close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\entity\LazyDecompressingInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */