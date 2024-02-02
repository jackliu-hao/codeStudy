/*    */ package com.sun.mail.util;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.InputStream;
/*    */ import javax.mail.util.SharedByteArrayInputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SharedByteArrayOutputStream
/*    */   extends ByteArrayOutputStream
/*    */ {
/*    */   public SharedByteArrayOutputStream(int size) {
/* 58 */     super(size);
/*    */   }
/*    */   
/*    */   public InputStream toStream() {
/* 62 */     return (InputStream)new SharedByteArrayInputStream(this.buf, 0, this.count);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\SharedByteArrayOutputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */