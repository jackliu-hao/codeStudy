/*    */ package org.apache.commons.codec;
/*    */ 
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
/*    */ public class Resources
/*    */ {
/*    */   public static InputStream getInputStream(String name) {
/* 36 */     InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(name);
/* 37 */     if (inputStream == null) {
/* 38 */       throw new IllegalArgumentException("Unable to resolve required resource: " + name);
/*    */     }
/* 40 */     return inputStream;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\Resources.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */