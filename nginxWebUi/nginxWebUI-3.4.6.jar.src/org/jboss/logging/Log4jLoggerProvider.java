/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import org.apache.log4j.MDC;
/*    */ import org.apache.log4j.NDC;
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
/*    */ final class Log4jLoggerProvider
/*    */   implements LoggerProvider
/*    */ {
/*    */   public Logger getLogger(String name) {
/* 30 */     return new Log4jLogger("".equals(name) ? "ROOT" : name);
/*    */   }
/*    */   
/*    */   public void clearMdc() {
/* 34 */     MDC.clear();
/*    */   }
/*    */   
/*    */   public Object getMdc(String key) {
/* 38 */     return MDC.get(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public Map<String, Object> getMdcMap() {
/* 43 */     Map<String, Object> map = MDC.getContext();
/* 44 */     return (map == null) ? Collections.<String, Object>emptyMap() : map;
/*    */   }
/*    */   
/*    */   public Object putMdc(String key, Object val) {
/*    */     try {
/* 49 */       return MDC.get(key);
/*    */     } finally {
/* 51 */       MDC.put(key, val);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void removeMdc(String key) {
/* 56 */     MDC.remove(key);
/*    */   }
/*    */   
/*    */   public void clearNdc() {
/* 60 */     NDC.remove();
/*    */   }
/*    */   
/*    */   public String getNdc() {
/* 64 */     return NDC.get();
/*    */   }
/*    */   
/*    */   public int getNdcDepth() {
/* 68 */     return NDC.getDepth();
/*    */   }
/*    */   
/*    */   public String peekNdc() {
/* 72 */     return NDC.peek();
/*    */   }
/*    */   
/*    */   public String popNdc() {
/* 76 */     return NDC.pop();
/*    */   }
/*    */   
/*    */   public void pushNdc(String message) {
/* 80 */     NDC.push(message);
/*    */   }
/*    */   
/*    */   public void setNdcMaxDepth(int maxDepth) {
/* 84 */     NDC.setMaxDepth(maxDepth);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Log4jLoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */