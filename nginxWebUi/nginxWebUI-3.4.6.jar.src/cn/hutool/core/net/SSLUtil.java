/*    */ package cn.hutool.core.net;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.SSLContext;
/*    */ import javax.net.ssl.TrustManager;
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
/*    */ public class SSLUtil
/*    */ {
/*    */   public static SSLContext createSSLContext(String protocol) throws IORuntimeException {
/* 26 */     return SSLContextBuilder.create().setProtocol(protocol).build();
/*    */   }
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
/*    */   public static SSLContext createSSLContext(String protocol, KeyManager keyManager, TrustManager trustManager) throws IORuntimeException {
/* 40 */     (new KeyManager[1])[0] = keyManager; (new TrustManager[1])[0] = trustManager; return createSSLContext(protocol, (keyManager == null) ? null : new KeyManager[1], (trustManager == null) ? null : new TrustManager[1]);
/*    */   }
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
/*    */   public static SSLContext createSSLContext(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers) throws IORuntimeException {
/* 55 */     return SSLContextBuilder.create()
/* 56 */       .setProtocol(protocol)
/* 57 */       .setKeyManagers(keyManagers)
/* 58 */       .setTrustManagers(trustManagers).build();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\SSLUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */