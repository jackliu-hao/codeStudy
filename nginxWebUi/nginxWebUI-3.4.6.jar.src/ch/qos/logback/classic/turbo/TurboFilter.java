/*    */ package ch.qos.logback.classic.turbo;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
/*    */ import ch.qos.logback.core.spi.FilterReply;
/*    */ import ch.qos.logback.core.spi.LifeCycle;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class TurboFilter
/*    */   extends ContextAwareBase
/*    */   implements LifeCycle
/*    */ {
/*    */   private String name;
/*    */   boolean start = false;
/*    */   
/*    */   public abstract FilterReply decide(Marker paramMarker, Logger paramLogger, Level paramLevel, String paramString, Object[] paramArrayOfObject, Throwable paramThrowable);
/*    */   
/*    */   public void start() {
/* 55 */     this.start = true;
/*    */   }
/*    */   
/*    */   public boolean isStarted() {
/* 59 */     return this.start;
/*    */   }
/*    */   
/*    */   public void stop() {
/* 63 */     this.start = false;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 67 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 71 */     this.name = name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\turbo\TurboFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */