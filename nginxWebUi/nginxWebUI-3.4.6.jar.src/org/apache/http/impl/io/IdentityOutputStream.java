/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.http.io.SessionOutputBuffer;
/*    */ import org.apache.http.util.Args;
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
/*    */ public class IdentityOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private final SessionOutputBuffer out;
/*    */   private boolean closed = false;
/*    */   
/*    */   public IdentityOutputStream(SessionOutputBuffer out) {
/* 60 */     this.out = (SessionOutputBuffer)Args.notNull(out, "Session output buffer");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 70 */     if (!this.closed) {
/* 71 */       this.closed = true;
/* 72 */       this.out.flush();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/* 78 */     this.out.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 83 */     if (this.closed) {
/* 84 */       throw new IOException("Attempted write to closed stream.");
/*    */     }
/* 86 */     this.out.write(b, off, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b) throws IOException {
/* 91 */     write(b, 0, b.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 96 */     if (this.closed) {
/* 97 */       throw new IOException("Attempted write to closed stream.");
/*    */     }
/* 99 */     this.out.write(b);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\IdentityOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */