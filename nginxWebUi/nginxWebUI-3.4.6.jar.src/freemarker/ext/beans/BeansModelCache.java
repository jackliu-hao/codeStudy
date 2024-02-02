/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.ext.util.ModelCache;
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateModel;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public class BeansModelCache
/*    */   extends ModelCache
/*    */ {
/* 33 */   private final Map<Class<?>, ModelFactory> classToFactory = new ConcurrentHashMap<>();
/* 34 */   private final Set<String> mappedClassNames = new HashSet<>();
/*    */   
/*    */   private final BeansWrapper wrapper;
/*    */   
/*    */   BeansModelCache(BeansWrapper wrapper) {
/* 39 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isCacheable(Object object) {
/* 44 */     return (object.getClass() != Boolean.class);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected TemplateModel create(Object object) {
/* 50 */     Class<?> clazz = object.getClass();
/*    */     
/* 52 */     ModelFactory factory = this.classToFactory.get(clazz);
/*    */     
/* 54 */     if (factory == null)
/*    */     {
/* 56 */       synchronized (this.classToFactory) {
/* 57 */         factory = this.classToFactory.get(clazz);
/* 58 */         if (factory == null) {
/* 59 */           String className = clazz.getName();
/*    */           
/* 61 */           if (!this.mappedClassNames.add(className)) {
/* 62 */             this.classToFactory.clear();
/* 63 */             this.mappedClassNames.clear();
/* 64 */             this.mappedClassNames.add(className);
/*    */           } 
/* 66 */           factory = this.wrapper.getModelFactory(clazz);
/* 67 */           this.classToFactory.put(clazz, factory);
/*    */         } 
/*    */       } 
/*    */     }
/*    */     
/* 72 */     return factory.create(object, (ObjectWrapper)this.wrapper);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BeansModelCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */