/*    */ package io.undertow.protocols.ssl;
/*    */ 
/*    */ import javax.net.ssl.SSLContext;
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
/*    */ public class SNISSLContext
/*    */   extends SSLContext
/*    */ {
/*    */   public SNISSLContext(SNIContextMatcher matcher) {
/* 23 */     super(new SNISSLContextSpi(matcher), matcher.getDefaultContext().getProvider(), matcher.getDefaultContext().getProtocol());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SNISSLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */