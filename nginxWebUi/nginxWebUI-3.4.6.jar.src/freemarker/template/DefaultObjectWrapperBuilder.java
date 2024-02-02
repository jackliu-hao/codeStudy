/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.ext.beans.BeansWrapperConfiguration;
/*    */ import freemarker.ext.beans._BeansAPI;
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.Map;
/*    */ import java.util.WeakHashMap;
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
/*    */ public class DefaultObjectWrapperBuilder
/*    */   extends DefaultObjectWrapperConfiguration
/*    */ {
/* 42 */   private static final Map<ClassLoader, Map<DefaultObjectWrapperConfiguration, WeakReference<DefaultObjectWrapper>>> INSTANCE_CACHE = new WeakHashMap<>();
/* 43 */   private static final ReferenceQueue<DefaultObjectWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultObjectWrapperBuilder(Version incompatibleImprovements) {
/* 52 */     super(incompatibleImprovements);
/*    */   }
/*    */ 
/*    */   
/*    */   static void clearInstanceCache() {
/* 57 */     synchronized (INSTANCE_CACHE) {
/* 58 */       INSTANCE_CACHE.clear();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultObjectWrapper build() {
/* 67 */     return (DefaultObjectWrapper)_BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, DefaultObjectWrapperFactory
/* 68 */         .INSTANCE);
/*    */   }
/*    */   
/*    */   private static class DefaultObjectWrapperFactory
/*    */     implements _BeansAPI._BeansWrapperSubclassFactory<DefaultObjectWrapper, DefaultObjectWrapperConfiguration>
/*    */   {
/* 74 */     private static final DefaultObjectWrapperFactory INSTANCE = new DefaultObjectWrapperFactory();
/*    */ 
/*    */     
/*    */     public DefaultObjectWrapper create(DefaultObjectWrapperConfiguration bwConf) {
/* 78 */       return new DefaultObjectWrapper(bwConf, true);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultObjectWrapperBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */