/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import org.slf4j.MDC;
/*    */ import org.slf4j.spi.LocationAwareLogger;
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
/*    */ final class Slf4jLoggerProvider
/*    */   extends AbstractLoggerProvider
/*    */   implements LoggerProvider
/*    */ {
/*    */   public Logger getLogger(String name) {
/* 32 */     Logger l = LoggerFactory.getLogger(name);
/* 33 */     if (l instanceof LocationAwareLogger) {
/* 34 */       return new Slf4jLocationAwareLogger(name, (LocationAwareLogger)l);
/*    */     }
/* 36 */     return new Slf4jLogger(name, l);
/*    */   }
/*    */   
/*    */   public void clearMdc() {
/* 40 */     MDC.clear();
/*    */   }
/*    */   
/*    */   public Object putMdc(String key, Object value) {
/*    */     try {
/* 45 */       return MDC.get(key);
/*    */     } finally {
/* 47 */       if (value == null) {
/* 48 */         MDC.remove(key);
/*    */       } else {
/* 50 */         MDC.put(key, String.valueOf(value));
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public Object getMdc(String key) {
/* 56 */     return MDC.get(key);
/*    */   }
/*    */   
/*    */   public void removeMdc(String key) {
/* 60 */     MDC.remove(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public Map<String, Object> getMdcMap() {
/* 65 */     Map<? extends String, ?> copy = MDC.getCopyOfContextMap();
/* 66 */     return (copy == null) ? Collections.<String, Object>emptyMap() : new LinkedHashMap<>(copy);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Slf4jLoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */