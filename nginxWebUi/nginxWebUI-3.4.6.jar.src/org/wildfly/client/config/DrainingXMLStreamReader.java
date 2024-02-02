/*    */ package org.wildfly.client.config;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class DrainingXMLStreamReader
/*    */   extends AbstractDelegatingXMLStreamReader
/*    */ {
/*    */   DrainingXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate) {
/* 27 */     super(closeDelegate, delegate);
/*    */   }
/*    */   
/*    */   public void close() throws ConfigXMLParseException {
/*    */     try {
/* 32 */       while (hasNext()) {
/* 33 */         next();
/*    */       }
/* 35 */       super.close();
/* 36 */     } catch (Throwable t) {
/*    */       try {
/* 38 */         super.close();
/* 39 */       } catch (Throwable t2) {
/* 40 */         t.addSuppressed(t2);
/*    */       } 
/* 42 */       throw t;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\DrainingXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */