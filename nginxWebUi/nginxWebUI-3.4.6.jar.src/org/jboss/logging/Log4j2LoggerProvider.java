/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.apache.logging.log4j.ThreadContext;
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
/*    */ final class Log4j2LoggerProvider
/*    */   implements LoggerProvider
/*    */ {
/*    */   public Log4j2Logger getLogger(String name) {
/* 30 */     return new Log4j2Logger(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public void clearMdc() {
/* 35 */     ThreadContext.clearMap();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object putMdc(String key, Object value) {
/*    */     try {
/* 41 */       return ThreadContext.get(key);
/*    */     } finally {
/* 43 */       ThreadContext.put(key, String.valueOf(value));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getMdc(String key) {
/* 49 */     return ThreadContext.get(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeMdc(String key) {
/* 54 */     ThreadContext.remove(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public Map<String, Object> getMdcMap() {
/* 59 */     return new HashMap<>(ThreadContext.getImmutableContext());
/*    */   }
/*    */ 
/*    */   
/*    */   public void clearNdc() {
/* 64 */     ThreadContext.clearStack();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNdc() {
/* 69 */     return ThreadContext.peek();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNdcDepth() {
/* 74 */     return ThreadContext.getDepth();
/*    */   }
/*    */ 
/*    */   
/*    */   public String popNdc() {
/* 79 */     return ThreadContext.pop();
/*    */   }
/*    */ 
/*    */   
/*    */   public String peekNdc() {
/* 84 */     return ThreadContext.peek();
/*    */   }
/*    */ 
/*    */   
/*    */   public void pushNdc(String message) {
/* 89 */     ThreadContext.push(message);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setNdcMaxDepth(int maxDepth) {
/* 94 */     ThreadContext.trim(maxDepth);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Log4j2LoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */