/*    */ package ch.qos.logback.core.joran.util.beans;
/*    */ 
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public class BeanDescriptionCache
/*    */   extends ContextAwareBase
/*    */ {
/* 22 */   private Map<Class<?>, BeanDescription> classToBeanDescription = new HashMap<Class<?>, BeanDescription>();
/*    */   private BeanDescriptionFactory beanDescriptionFactory;
/*    */   
/*    */   public BeanDescriptionCache(Context context) {
/* 26 */     setContext(context);
/*    */   }
/*    */   
/*    */   private BeanDescriptionFactory getBeanDescriptionFactory() {
/* 30 */     if (this.beanDescriptionFactory == null) {
/* 31 */       this.beanDescriptionFactory = new BeanDescriptionFactory(getContext());
/*    */     }
/* 33 */     return this.beanDescriptionFactory;
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
/*    */   public BeanDescription getBeanDescription(Class<?> clazz) {
/* 46 */     if (!this.classToBeanDescription.containsKey(clazz)) {
/* 47 */       BeanDescription beanDescription = getBeanDescriptionFactory().create(clazz);
/* 48 */       this.classToBeanDescription.put(clazz, beanDescription);
/*    */     } 
/* 50 */     return this.classToBeanDescription.get(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\jora\\util\beans\BeanDescriptionCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */