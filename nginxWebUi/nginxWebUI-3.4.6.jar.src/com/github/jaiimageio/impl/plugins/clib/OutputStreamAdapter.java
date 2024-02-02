/*    */ package com.github.jaiimageio.impl.plugins.clib;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import javax.imageio.stream.ImageOutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class OutputStreamAdapter
/*    */   extends OutputStream
/*    */ {
/*    */   ImageOutputStream stream;
/*    */   
/*    */   public OutputStreamAdapter(ImageOutputStream stream) {
/* 61 */     this.stream = stream;
/*    */   }
/*    */   
/*    */   public void close() throws IOException {
/* 65 */     this.stream.close();
/*    */   }
/*    */   
/*    */   public void write(byte[] b) throws IOException {
/* 69 */     this.stream.write(b);
/*    */   }
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 73 */     this.stream.write(b, off, len);
/*    */   }
/*    */   
/*    */   public void write(int b) throws IOException {
/* 77 */     this.stream.write(b);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\clib\OutputStreamAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */