/*    */ package ch.qos.logback.core;
/*    */ 
/*    */ import ch.qos.logback.core.spi.LifeCycle;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
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
/*    */ public class LifeCycleManager
/*    */ {
/* 30 */   private final Set<LifeCycle> components = new HashSet<LifeCycle>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void register(LifeCycle component) {
/* 38 */     this.components.add(component);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void reset() {
/* 47 */     for (LifeCycle component : this.components) {
/* 48 */       if (component.isStarted()) {
/* 49 */         component.stop();
/*    */       }
/*    */     } 
/* 52 */     this.components.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\LifeCycleManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */