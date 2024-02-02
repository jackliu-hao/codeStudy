/*    */ package org.codehaus.plexus.util.io;
/*    */ 
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
/*    */ public class RawInputStreamFacade
/*    */   implements InputStreamFacade
/*    */ {
/*    */   final InputStream stream;
/*    */   
/*    */   public RawInputStreamFacade(InputStream stream) {
/* 33 */     this.stream = stream;
/*    */   }
/*    */   
/*    */   public InputStream getInputStream() throws IOException {
/* 37 */     return this.stream;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\io\RawInputStreamFacade.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */