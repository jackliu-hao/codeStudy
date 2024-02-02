/*    */ package ch.qos.logback.core.net;
/*    */ 
/*    */ import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
/*    */ import ch.qos.logback.core.net.ssl.SSLComponent;
/*    */ import ch.qos.logback.core.net.ssl.SSLConfiguration;
/*    */ import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
/*    */ import ch.qos.logback.core.spi.ContextAware;
/*    */ import javax.net.SocketFactory;
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
/*    */ public abstract class AbstractSSLSocketAppender<E>
/*    */   extends AbstractSocketAppender<E>
/*    */   implements SSLComponent
/*    */ {
/*    */   private SSLConfiguration ssl;
/*    */   private SocketFactory socketFactory;
/*    */   
/*    */   protected SocketFactory getSocketFactory() {
/* 48 */     return this.socketFactory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void start() {
/*    */     try {
/* 57 */       SSLContext sslContext = getSsl().createContext((ContextAware)this);
/* 58 */       SSLParametersConfiguration parameters = getSsl().getParameters();
/* 59 */       parameters.setContext(getContext());
/* 60 */       this.socketFactory = (SocketFactory)new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
/* 61 */       super.start();
/* 62 */     } catch (Exception ex) {
/* 63 */       addError(ex.getMessage(), ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLConfiguration getSsl() {
/* 73 */     if (this.ssl == null) {
/* 74 */       this.ssl = new SSLConfiguration();
/*    */     }
/* 76 */     return this.ssl;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSsl(SSLConfiguration ssl) {
/* 84 */     this.ssl = ssl;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\AbstractSSLSocketAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */