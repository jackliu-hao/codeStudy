/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.FilterInputStream;
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
/*    */ public class CloseShieldFilterInputStream
/*    */   extends FilterInputStream
/*    */ {
/*    */   public CloseShieldFilterInputStream(InputStream in) {
/* 33 */     super(in);
/*    */   }
/*    */   
/*    */   public void close() throws IOException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\CloseShieldFilterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */