/*    */ package org.apache.http.client.entity;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class DeflateInputStreamFactory
/*    */   implements InputStreamFactory
/*    */ {
/* 47 */   private static final DeflateInputStreamFactory INSTANCE = new DeflateInputStreamFactory();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DeflateInputStreamFactory getInstance() {
/* 55 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream create(InputStream inputStream) throws IOException {
/* 60 */     return new DeflateInputStream(inputStream);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\entity\DeflateInputStreamFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */