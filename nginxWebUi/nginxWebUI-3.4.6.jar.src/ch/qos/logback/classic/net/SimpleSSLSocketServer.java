/*    */ package ch.qos.logback.classic.net;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
/*    */ import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import javax.net.ServerSocketFactory;
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
/*    */ public class SimpleSSLSocketServer
/*    */   extends SimpleSocketServer
/*    */ {
/*    */   private final ServerSocketFactory socketFactory;
/*    */   
/*    */   public static void main(String[] argv) throws Exception {
/* 62 */     doMain((Class)SimpleSSLSocketServer.class, argv);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleSSLSocketServer(LoggerContext lc, int port) throws NoSuchAlgorithmException {
/* 73 */     this(lc, port, SSLContext.getDefault());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleSSLSocketServer(LoggerContext lc, int port, SSLContext sslContext) {
/* 83 */     super(lc, port);
/* 84 */     if (sslContext == null) {
/* 85 */       throw new NullPointerException("SSL context required");
/*    */     }
/* 87 */     SSLParametersConfiguration parameters = new SSLParametersConfiguration();
/*    */     
/* 89 */     parameters.setContext((Context)lc);
/* 90 */     this.socketFactory = (ServerSocketFactory)new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
/*    */   }
/*    */ 
/*    */   
/*    */   protected ServerSocketFactory getServerSocketFactory() {
/* 95 */     return this.socketFactory;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SimpleSSLSocketServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */