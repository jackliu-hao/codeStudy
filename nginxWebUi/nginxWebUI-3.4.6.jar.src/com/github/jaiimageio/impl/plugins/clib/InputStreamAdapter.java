/*    */ package com.github.jaiimageio.impl.plugins.clib;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import javax.imageio.stream.ImageInputStream;
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
/*    */ 
/*    */ 
/*    */ public final class InputStreamAdapter
/*    */   extends InputStream
/*    */ {
/*    */   ImageInputStream stream;
/*    */   
/*    */   public InputStreamAdapter(ImageInputStream stream) {
/* 61 */     this.stream = stream;
/*    */   }
/*    */   
/*    */   public void close() throws IOException {
/* 65 */     this.stream.close();
/*    */   }
/*    */   
/*    */   public void mark(int readlimit) {
/* 69 */     this.stream.mark();
/*    */   }
/*    */   
/*    */   public boolean markSupported() {
/* 73 */     return true;
/*    */   }
/*    */   
/*    */   public int read() throws IOException {
/* 77 */     return this.stream.read();
/*    */   }
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 81 */     return this.stream.read(b, off, len);
/*    */   }
/*    */   
/*    */   public void reset() throws IOException {
/* 85 */     this.stream.reset();
/*    */   }
/*    */   
/*    */   public long skip(long n) throws IOException {
/* 89 */     return this.stream.skipBytes(n);
/*    */   }
/*    */   
/*    */   public ImageInputStream getWrappedStream() {
/* 93 */     return this.stream;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\clib\InputStreamAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */