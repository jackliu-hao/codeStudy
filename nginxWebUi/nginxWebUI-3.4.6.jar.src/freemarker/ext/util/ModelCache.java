/*     */ package freemarker.ext.util;
/*     */ 
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelAdapter;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
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
/*     */ public abstract class ModelCache
/*     */ {
/*     */   private boolean useCache = false;
/*  36 */   private Map<Object, ModelReference> modelCache = null;
/*  37 */   private ReferenceQueue<TemplateModel> refQueue = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setUseCache(boolean useCache) {
/*  48 */     this.useCache = useCache;
/*  49 */     if (useCache) {
/*  50 */       this.modelCache = new IdentityHashMap<>();
/*  51 */       this.refQueue = new ReferenceQueue<>();
/*     */     } else {
/*  53 */       this.modelCache = null;
/*  54 */       this.refQueue = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean getUseCache() {
/*  62 */     return this.useCache;
/*     */   }
/*     */   
/*     */   public TemplateModel getInstance(Object object) {
/*  66 */     if (object instanceof TemplateModel) {
/*  67 */       return (TemplateModel)object;
/*     */     }
/*  69 */     if (object instanceof TemplateModelAdapter) {
/*  70 */       return ((TemplateModelAdapter)object).getTemplateModel();
/*     */     }
/*  72 */     if (this.useCache && isCacheable(object)) {
/*  73 */       TemplateModel model = lookup(object);
/*  74 */       if (model == null) {
/*  75 */         model = create(object);
/*  76 */         register(model, object);
/*     */       } 
/*  78 */       return model;
/*     */     } 
/*  80 */     return create(object);
/*     */   }
/*     */   
/*     */   protected abstract TemplateModel create(Object paramObject);
/*     */   
/*     */   protected abstract boolean isCacheable(Object paramObject);
/*     */   
/*     */   public void clearCache() {
/*  88 */     if (this.modelCache != null) {
/*  89 */       synchronized (this.modelCache) {
/*  90 */         this.modelCache.clear();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private final TemplateModel lookup(Object object) {
/*  96 */     ModelReference ref = null;
/*     */ 
/*     */ 
/*     */     
/* 100 */     synchronized (this.modelCache) {
/* 101 */       ref = this.modelCache.get(object);
/*     */     } 
/*     */     
/* 104 */     if (ref != null) {
/* 105 */       return ref.getModel();
/*     */     }
/* 107 */     return null;
/*     */   }
/*     */   
/*     */   private final void register(TemplateModel model, Object object) {
/* 111 */     synchronized (this.modelCache) {
/*     */       
/*     */       while (true) {
/* 114 */         ModelReference queuedRef = (ModelReference)this.refQueue.poll();
/* 115 */         if (queuedRef == null) {
/*     */           break;
/*     */         }
/* 118 */         this.modelCache.remove(queuedRef.object);
/*     */       } 
/*     */       
/* 121 */       this.modelCache.put(object, new ModelReference(model, object, this.refQueue));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ModelReference
/*     */     extends SoftReference<TemplateModel>
/*     */   {
/*     */     Object object;
/*     */ 
/*     */     
/*     */     ModelReference(TemplateModel ref, Object object, ReferenceQueue<TemplateModel> refQueue) {
/* 134 */       super(ref, refQueue);
/* 135 */       this.object = object;
/*     */     }
/*     */     
/*     */     TemplateModel getModel() {
/* 139 */       return get();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ex\\util\ModelCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */