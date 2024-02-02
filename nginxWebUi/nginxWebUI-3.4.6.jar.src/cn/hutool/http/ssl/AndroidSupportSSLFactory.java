/*    */ package cn.hutool.http.ssl;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
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
/*    */ public class AndroidSupportSSLFactory
/*    */   extends CustomProtocolsSSLFactory
/*    */ {
/* 18 */   private static final String[] protocols = new String[] { "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" };
/*    */ 
/*    */   
/*    */   public AndroidSupportSSLFactory() throws IORuntimeException {
/* 22 */     super(protocols);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\ssl\AndroidSupportSSLFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */