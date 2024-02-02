/*    */ package io.undertow.websockets.core;
/*    */ 
/*    */ import io.undertow.websockets.WebSocketExtension;
/*    */ import java.io.Serializable;
/*    */ import java.util.Locale;
/*    */ import org.jboss.logging.BasicLogger;
/*    */ import org.jboss.logging.DelegatingBasicLogger;
/*    */ import org.jboss.logging.Logger;
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
/*    */ public class WebSocketLogger_$logger
/*    */   extends DelegatingBasicLogger
/*    */   implements WebSocketLogger, BasicLogger, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 24 */   private static final String FQCN = WebSocketLogger_$logger.class.getName();
/*    */   public WebSocketLogger_$logger(Logger log) {
/* 26 */     super(log);
/*    */   }
/* 28 */   private static final Locale LOCALE = Locale.ROOT;
/*    */   protected Locale getLoggingLocale() {
/* 30 */     return LOCALE;
/*    */   }
/*    */   
/*    */   public final void decodingFrameWithOpCode(int opCode) {
/* 34 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, decodingFrameWithOpCode$str(), Integer.valueOf(opCode));
/*    */   }
/*    */   protected String decodingFrameWithOpCode$str() {
/* 37 */     return "UT025003: Decoding WebSocket Frame with opCode %s";
/*    */   }
/*    */   
/*    */   public final void unhandledErrorInAnnotatedEndpoint(Object instance, Throwable thr) {
/* 41 */     this.log.logf(FQCN, Logger.Level.ERROR, thr, unhandledErrorInAnnotatedEndpoint$str(), instance);
/*    */   }
/*    */   protected String unhandledErrorInAnnotatedEndpoint$str() {
/* 44 */     return "UT025007: Unhandled exception for annotated endpoint %s";
/*    */   }
/*    */   
/*    */   public final void incorrectExtensionParameter(WebSocketExtension.Parameter param) {
/* 48 */     this.log.logf(FQCN, Logger.Level.WARN, null, incorrectExtensionParameter$str(), param);
/*    */   }
/*    */   protected String incorrectExtensionParameter$str() {
/* 51 */     return "UT025008: Incorrect parameter %s for extension";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketLogger_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */