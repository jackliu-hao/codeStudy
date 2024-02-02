/*    */ package cn.hutool.http.ssl;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import javax.net.ssl.SSLSocketFactory;
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
/*    */ public class DefaultSSLInfo
/*    */ {
/* 24 */   public static final TrustAnyHostnameVerifier TRUST_ANY_HOSTNAME_VERIFIER = new TrustAnyHostnameVerifier(); static {
/* 25 */     if (StrUtil.equalsIgnoreCase("dalvik", System.getProperty("java.vm.name"))) {
/*    */       
/* 27 */       DEFAULT_SSF = new AndroidSupportSSLFactory();
/*    */     } else {
/* 29 */       DEFAULT_SSF = new DefaultSSLFactory();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static final SSLSocketFactory DEFAULT_SSF;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\ssl\DefaultSSLInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */