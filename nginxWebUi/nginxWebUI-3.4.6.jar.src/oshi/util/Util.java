/*    */ package oshi.util;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
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
/*    */ @ThreadSafe
/*    */ public final class Util
/*    */ {
/* 36 */   private static final Logger LOG = LoggerFactory.getLogger(Util.class);
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
/*    */   public static void sleep(long ms) {
/*    */     try {
/* 49 */       LOG.trace("Sleeping for {} ms", Long.valueOf(ms));
/* 50 */       Thread.sleep(ms);
/* 51 */     } catch (InterruptedException e) {
/* 52 */       LOG.warn("Interrupted while sleeping for {} ms: {}", Long.valueOf(ms), e.getMessage());
/* 53 */       Thread.currentThread().interrupt();
/*    */     } 
/*    */   }
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
/*    */   public static boolean wildcardMatch(String text, String pattern) {
/* 72 */     if (pattern.length() > 0 && pattern.charAt(0) == '^') {
/* 73 */       return !wildcardMatch(text, pattern.substring(1));
/*    */     }
/* 75 */     return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isBlank(String s) {
/* 86 */     return (s == null || s.isEmpty());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */