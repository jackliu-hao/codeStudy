/*    */ package org.codehaus.plexus.util;
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
/*    */ public class StringOutputStream
/*    */   extends OutputStream
/*    */ {
/* 33 */   private StringBuffer buf = new StringBuffer();
/*    */ 
/*    */   
/*    */   public void write(byte[] b) throws IOException {
/* 37 */     this.buf.append(new String(b));
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 42 */     this.buf.append(new String(b, off, len));
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 47 */     byte[] bytes = new byte[1];
/* 48 */     bytes[0] = (byte)b;
/* 49 */     this.buf.append(new String(bytes));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return this.buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\StringOutputStream.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */