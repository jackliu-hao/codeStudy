/*    */ package org.codehaus.plexus.util.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
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
/*    */ public class URLInputStreamFacade
/*    */   implements InputStreamFacade
/*    */ {
/*    */   private final URL url;
/*    */   
/*    */   public URLInputStreamFacade(URL url) {
/* 34 */     this.url = url;
/*    */   }
/*    */   
/*    */   public InputStream getInputStream() throws IOException {
/* 38 */     return this.url.openStream();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\io\URLInputStreamFacade.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */