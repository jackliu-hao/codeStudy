/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.http.io.BufferInfo;
/*    */ import org.apache.http.io.SessionInputBuffer;
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
/*    */ 
/*    */ public class IdentityInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final SessionInputBuffer in;
/*    */   private boolean closed = false;
/*    */   
/*    */   public IdentityInputStream(SessionInputBuffer in) {
/* 62 */     this.in = (SessionInputBuffer)Args.notNull(in, "Session input buffer");
/*    */   }
/*    */ 
/*    */   
/*    */   public int available() throws IOException {
/* 67 */     if (this.in instanceof BufferInfo) {
/* 68 */       return ((BufferInfo)this.in).length();
/*    */     }
/* 70 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 75 */     this.closed = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 80 */     return this.closed ? -1 : this.in.read();
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 85 */     return this.closed ? -1 : this.in.read(b, off, len);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\IdentityInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */