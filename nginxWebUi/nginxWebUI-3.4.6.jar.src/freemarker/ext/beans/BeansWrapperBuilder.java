/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.Version;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeansWrapperBuilder
/*     */   extends BeansWrapperConfiguration
/*     */ {
/* 117 */   private static final Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> INSTANCE_CACHE = new WeakHashMap<>();
/* 118 */   private static final ReferenceQueue<BeansWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue<>();
/*     */   
/*     */   private static class BeansWrapperFactory
/*     */     implements _BeansAPI._BeansWrapperSubclassFactory<BeansWrapper, BeansWrapperConfiguration>
/*     */   {
/* 123 */     private static final BeansWrapperFactory INSTANCE = new BeansWrapperFactory();
/*     */ 
/*     */     
/*     */     public BeansWrapper create(BeansWrapperConfiguration bwConf) {
/* 127 */       return new BeansWrapper(bwConf, true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeansWrapperBuilder(Version incompatibleImprovements) {
/* 136 */     super(incompatibleImprovements);
/*     */   }
/*     */ 
/*     */   
/*     */   static void clearInstanceCache() {
/* 141 */     synchronized (INSTANCE_CACHE) {
/* 142 */       INSTANCE_CACHE.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> getInstanceCache() {
/* 150 */     return INSTANCE_CACHE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeansWrapper build() {
/* 158 */     return _BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, BeansWrapperFactory
/* 159 */         .INSTANCE);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BeansWrapperBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */