/*    */ package org.apache.commons.compress.compressors.pack200;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
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
/*    */ class InMemoryCachingStreamBridge
/*    */   extends StreamBridge
/*    */ {
/*    */   InMemoryCachingStreamBridge() {
/* 34 */     super(new ByteArrayOutputStream());
/*    */   }
/*    */ 
/*    */   
/*    */   InputStream getInputView() throws IOException {
/* 39 */     return new ByteArrayInputStream(((ByteArrayOutputStream)this.out)
/* 40 */         .toByteArray());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\pack200\InMemoryCachingStreamBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */