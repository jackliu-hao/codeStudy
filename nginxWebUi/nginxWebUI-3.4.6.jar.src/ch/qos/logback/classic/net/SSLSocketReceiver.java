/*    */ package ch.qos.logback.classic.net;
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
/*    */ public class SSLSocketReceiver
/*    */   extends SocketReceiver
/*    */   implements SSLComponent
/*    */ {
/*    */   private SSLConfiguration ssl;
/*    */   private SocketFactory socketFactory;
/*    */   
/*    */   protected SocketFactory getSocketFactory() {
/* 41 */     return this.socketFactory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean shouldStart() {
/*    */     try {
/* 50 */       SSLContext sslContext = getSsl().createContext((ContextAware)this);
/* 51 */       SSLParametersConfiguration parameters = getSsl().getParameters();
/* 52 */       parameters.setContext(getContext());
/* 53 */       this.socketFactory = (SocketFactory)new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
/* 54 */       return super.shouldStart();
/* 55 */     } catch (Exception ex) {
/* 56 */       addError(ex.getMessage(), ex);
/* 57 */       return false;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLConfiguration getSsl() {
/* 67 */     if (this.ssl == null) {
/* 68 */       this.ssl = new SSLConfiguration();
/*    */     }
/* 70 */     return this.ssl;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSsl(SSLConfiguration ssl) {
/* 78 */     this.ssl = ssl;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SSLSocketReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */