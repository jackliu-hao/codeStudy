/*     */ package freemarker.template;
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
/*     */ public abstract class WrappingTemplateModel
/*     */ {
/*     */   @Deprecated
/*  30 */   private static ObjectWrapper defaultObjectWrapper = (ObjectWrapper)DefaultObjectWrapper.instance;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ObjectWrapper objectWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void setDefaultObjectWrapper(ObjectWrapper objectWrapper) {
/*  48 */     defaultObjectWrapper = objectWrapper;
/*     */   }
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
/*     */   @Deprecated
/*     */   public static ObjectWrapper getDefaultObjectWrapper() {
/*  63 */     return defaultObjectWrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WrappingTemplateModel() {
/*  74 */     this(defaultObjectWrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WrappingTemplateModel(ObjectWrapper objectWrapper) {
/*  85 */     this.objectWrapper = (objectWrapper != null) ? objectWrapper : defaultObjectWrapper;
/*     */     
/*  87 */     if (this.objectWrapper == null) {
/*  88 */       this.objectWrapper = defaultObjectWrapper = (ObjectWrapper)new DefaultObjectWrapper();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectWrapper getObjectWrapper() {
/*  96 */     return this.objectWrapper;
/*     */   }
/*     */   
/*     */   public void setObjectWrapper(ObjectWrapper objectWrapper) {
/* 100 */     this.objectWrapper = objectWrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TemplateModel wrap(Object obj) throws TemplateModelException {
/* 112 */     return this.objectWrapper.wrap(obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\WrappingTemplateModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */