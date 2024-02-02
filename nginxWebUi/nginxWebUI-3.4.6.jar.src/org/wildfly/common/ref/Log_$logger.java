/*    */ package org.wildfly.common.ref;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Locale;
/*    */ import org.jboss.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Log_$logger
/*    */   implements Log, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 18 */   private static final String FQCN = Log_$logger.class.getName();
/*    */   public Log_$logger(Logger log) {
/* 20 */     this.log = log;
/*    */   }
/*    */   protected final Logger log;
/* 23 */   private static final Locale LOCALE = Locale.ROOT;
/*    */   protected Locale getLoggingLocale() {
/* 25 */     return LOCALE;
/*    */   }
/*    */   
/*    */   public final void reapFailed(Throwable cause) {
/* 29 */     this.log.logf(FQCN, Logger.Level.DEBUG, cause, reapFailed$str(), new Object[0]);
/*    */   }
/*    */   protected String reapFailed$str() {
/* 32 */     return "COM03000: Reaping a reference failed";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\Log_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */