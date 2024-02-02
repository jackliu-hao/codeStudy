/*    */ package ch.qos.logback.classic.net;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.LoggingEventVO;
/*    */ import ch.qos.logback.core.spi.PreSerializationTransformer;
/*    */ import java.io.Serializable;
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
/*    */ public class LoggingEventPreSerializationTransformer
/*    */   implements PreSerializationTransformer<ILoggingEvent>
/*    */ {
/*    */   public Serializable transform(ILoggingEvent event) {
/* 26 */     if (event == null) {
/* 27 */       return null;
/*    */     }
/* 29 */     if (event instanceof ch.qos.logback.classic.spi.LoggingEvent)
/* 30 */       return (Serializable)LoggingEventVO.build(event); 
/* 31 */     if (event instanceof LoggingEventVO) {
/* 32 */       return (Serializable)event;
/*    */     }
/* 34 */     throw new IllegalArgumentException("Unsupported type " + event.getClass().getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\LoggingEventPreSerializationTransformer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */