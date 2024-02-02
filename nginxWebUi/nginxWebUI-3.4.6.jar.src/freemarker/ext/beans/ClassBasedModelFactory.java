/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ abstract class ClassBasedModelFactory
/*     */   implements TemplateHashModel
/*     */ {
/*     */   private final BeansWrapper wrapper;
/*  40 */   private final Map<String, TemplateModel> cache = new ConcurrentHashMap<>();
/*  41 */   private final Set<String> classIntrospectionsInProgress = new HashSet<>();
/*     */   
/*     */   protected ClassBasedModelFactory(BeansWrapper wrapper) {
/*  44 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*     */     try {
/*  50 */       return getInternal(key);
/*  51 */     } catch (Exception e) {
/*  52 */       if (e instanceof TemplateModelException) {
/*  53 */         throw (TemplateModelException)e;
/*     */       }
/*  55 */       throw new _TemplateModelException(e, new Object[] { "Failed to get value for key ", new _DelayedJQuote(key), "; see cause exception." });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private TemplateModel getInternal(String key) throws TemplateModelException, ClassNotFoundException {
/*     */     ClassIntrospector classIntrospector;
/*     */     int classIntrospectorClearingCounter;
/*  63 */     TemplateModel model = this.cache.get(key);
/*  64 */     if (model != null) return model;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     Object sharedLock = this.wrapper.getSharedIntrospectionLock();
/*  70 */     synchronized (sharedLock) {
/*  71 */       TemplateModel templateModel = this.cache.get(key);
/*  72 */       if (templateModel != null) return templateModel;
/*     */       
/*  74 */       while (templateModel == null && this.classIntrospectionsInProgress.contains(key)) {
/*     */ 
/*     */         
/*     */         try {
/*  78 */           sharedLock.wait();
/*  79 */           templateModel = this.cache.get(key);
/*  80 */         } catch (InterruptedException e) {
/*  81 */           throw new RuntimeException("Class inrospection data lookup aborted: " + e);
/*     */         } 
/*     */       } 
/*  84 */       if (templateModel != null) return templateModel;
/*     */ 
/*     */       
/*  87 */       this.classIntrospectionsInProgress.add(key);
/*     */ 
/*     */ 
/*     */       
/*  91 */       classIntrospector = this.wrapper.getClassIntrospector();
/*  92 */       classIntrospectorClearingCounter = classIntrospector.getClearingCounter();
/*     */     } 
/*     */     try {
/*  95 */       Class<?> clazz = ClassUtil.forName(key);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 101 */       classIntrospector.get(clazz);
/*     */       
/* 103 */       TemplateModel templateModel = createModel(clazz);
/*     */ 
/*     */ 
/*     */       
/* 107 */       if (templateModel != null) {
/* 108 */         synchronized (sharedLock) {
/*     */           
/* 110 */           if (classIntrospector == this.wrapper.getClassIntrospector() && classIntrospectorClearingCounter == classIntrospector
/* 111 */             .getClearingCounter()) {
/* 112 */             this.cache.put(key, templateModel);
/*     */           }
/*     */         } 
/*     */       }
/* 116 */       return templateModel;
/*     */     } finally {
/* 118 */       synchronized (sharedLock) {
/* 119 */         this.classIntrospectionsInProgress.remove(key);
/* 120 */         sharedLock.notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   void clearCache() {
/* 126 */     synchronized (this.wrapper.getSharedIntrospectionLock()) {
/* 127 */       this.cache.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   void removeFromCache(Class<?> clazz) {
/* 132 */     synchronized (this.wrapper.getSharedIntrospectionLock()) {
/* 133 */       this.cache.remove(clazz.getName());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 139 */     return false;
/*     */   }
/*     */   
/*     */   protected abstract TemplateModel createModel(Class<?> paramClass) throws TemplateModelException;
/*     */   
/*     */   protected BeansWrapper getWrapper() {
/* 145 */     return this.wrapper;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ClassBasedModelFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */