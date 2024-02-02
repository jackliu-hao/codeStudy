/*    */ package ch.qos.logback.classic.turbo;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.core.spi.FilterReply;
/*    */ import org.slf4j.Marker;
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
/*    */ public class DuplicateMessageFilter
/*    */   extends TurboFilter
/*    */ {
/*    */   public static final int DEFAULT_CACHE_SIZE = 100;
/*    */   public static final int DEFAULT_ALLOWED_REPETITIONS = 5;
/* 41 */   public int allowedRepetitions = 5;
/* 42 */   public int cacheSize = 100;
/*    */   
/*    */   private LRUMessageCache msgCache;
/*    */ 
/*    */   
/*    */   public void start() {
/* 48 */     this.msgCache = new LRUMessageCache(this.cacheSize);
/* 49 */     super.start();
/*    */   }
/*    */ 
/*    */   
/*    */   public void stop() {
/* 54 */     this.msgCache.clear();
/* 55 */     this.msgCache = null;
/* 56 */     super.stop();
/*    */   }
/*    */ 
/*    */   
/*    */   public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
/* 61 */     int count = this.msgCache.getMessageCountAndThenIncrement(format);
/* 62 */     if (count <= this.allowedRepetitions) {
/* 63 */       return FilterReply.NEUTRAL;
/*    */     }
/* 65 */     return FilterReply.DENY;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getAllowedRepetitions() {
/* 70 */     return this.allowedRepetitions;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAllowedRepetitions(int allowedRepetitions) {
/* 79 */     this.allowedRepetitions = allowedRepetitions;
/*    */   }
/*    */   
/*    */   public int getCacheSize() {
/* 83 */     return this.cacheSize;
/*    */   }
/*    */   
/*    */   public void setCacheSize(int cacheSize) {
/* 87 */     this.cacheSize = cacheSize;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\turbo\DuplicateMessageFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */