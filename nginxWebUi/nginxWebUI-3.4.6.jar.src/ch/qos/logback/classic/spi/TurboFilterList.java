/*    */ package ch.qos.logback.classic.spi;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.classic.turbo.TurboFilter;
/*    */ import ch.qos.logback.core.spi.FilterReply;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*    */ public final class TurboFilterList
/*    */   extends CopyOnWriteArrayList<TurboFilter>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public FilterReply getTurboFilterChainDecision(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
/* 42 */     int size = size();
/*    */ 
/*    */ 
/*    */     
/* 46 */     if (size == 1) {
/*    */       try {
/* 48 */         TurboFilter tf = get(0);
/* 49 */         return tf.decide(marker, logger, level, format, params, t);
/* 50 */       } catch (IndexOutOfBoundsException iobe) {
/* 51 */         return FilterReply.NEUTRAL;
/*    */       } 
/*    */     }
/*    */     
/* 55 */     Object[] tfa = toArray();
/* 56 */     int len = tfa.length;
/* 57 */     for (int i = 0; i < len; i++) {
/*    */       
/* 59 */       TurboFilter tf = (TurboFilter)tfa[i];
/* 60 */       FilterReply r = tf.decide(marker, logger, level, format, params, t);
/* 61 */       if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
/* 62 */         return r;
/*    */       }
/*    */     } 
/* 65 */     return FilterReply.NEUTRAL;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\TurboFilterList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */