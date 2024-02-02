/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ class LoggingOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private final OutputStream out;
/*    */   private final Wire wire;
/*    */   
/*    */   public LoggingOutputStream(OutputStream out, Wire wire) {
/* 45 */     this.out = out;
/* 46 */     this.wire = wire;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/*    */     try {
/* 52 */       this.wire.output(b);
/* 53 */     } catch (IOException ex) {
/* 54 */       this.wire.output("[write] I/O error: " + ex.getMessage());
/* 55 */       throw ex;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b) throws IOException {
/*    */     try {
/* 62 */       this.wire.output(b);
/* 63 */       this.out.write(b);
/* 64 */     } catch (IOException ex) {
/* 65 */       this.wire.output("[write] I/O error: " + ex.getMessage());
/* 66 */       throw ex;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/*    */     try {
/* 73 */       this.wire.output(b, off, len);
/* 74 */       this.out.write(b, off, len);
/* 75 */     } catch (IOException ex) {
/* 76 */       this.wire.output("[write] I/O error: " + ex.getMessage());
/* 77 */       throw ex;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/*    */     try {
/* 84 */       this.out.flush();
/* 85 */     } catch (IOException ex) {
/* 86 */       this.wire.output("[flush] I/O error: " + ex.getMessage());
/* 87 */       throw ex;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/*    */     try {
/* 94 */       this.out.close();
/* 95 */     } catch (IOException ex) {
/* 96 */       this.wire.output("[close] I/O error: " + ex.getMessage());
/* 97 */       throw ex;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\LoggingOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */