/*    */ package ch.qos.logback.core.net.server;
/*    */ 
/*    */ import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
/*    */ import ch.qos.logback.core.net.ssl.SSLComponent;
/*    */ import ch.qos.logback.core.net.ssl.SSLConfiguration;
/*    */ import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
/*    */ import ch.qos.logback.core.spi.ContextAware;
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
/*    */ public abstract class SSLServerSocketAppenderBase<E>
/*    */   extends AbstractServerSocketAppender<E>
/*    */   implements SSLComponent
/*    */ {
/*    */   private SSLConfiguration ssl;
/*    */   private ServerSocketFactory socketFactory;
/*    */   
/*    */   protected ServerSocketFactory getServerSocketFactory() {
/* 38 */     return this.socketFactory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void start() {
/*    */     try {
/* 47 */       SSLContext sslContext = getSsl().createContext((ContextAware)this);
/* 48 */       SSLParametersConfiguration parameters = getSsl().getParameters();
/* 49 */       parameters.setContext(getContext());
/* 50 */       this.socketFactory = (ServerSocketFactory)new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
/* 51 */       super.start();
/* 52 */     } catch (Exception ex) {
/* 53 */       addError(ex.getMessage(), ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLConfiguration getSsl() {
/* 63 */     if (this.ssl == null) {
/* 64 */       this.ssl = new SSLConfiguration();
/*    */     }
/* 66 */     return this.ssl;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSsl(SSLConfiguration ssl) {
/* 74 */     this.ssl = ssl;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\SSLServerSocketAppenderBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */