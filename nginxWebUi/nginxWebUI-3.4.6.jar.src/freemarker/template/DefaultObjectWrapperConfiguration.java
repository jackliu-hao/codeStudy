/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.beans.BeansWrapperConfiguration;
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
/*     */ public abstract class DefaultObjectWrapperConfiguration
/*     */   extends BeansWrapperConfiguration
/*     */ {
/*     */   private boolean useAdaptersForContainers;
/*     */   private boolean forceLegacyNonListCollections;
/*     */   private boolean iterableSupport;
/*     */   private boolean domNodeSupport;
/*     */   private boolean jythonSupport;
/*     */   
/*     */   protected DefaultObjectWrapperConfiguration(Version incompatibleImprovements) {
/*  42 */     super(DefaultObjectWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements), true);
/*  43 */     _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.configuration", "DefaultObjectWrapper");
/*     */ 
/*     */     
/*  46 */     this.useAdaptersForContainers = (getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_22);
/*  47 */     this.forceLegacyNonListCollections = true;
/*  48 */     this.domNodeSupport = true;
/*  49 */     this.jythonSupport = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseAdaptersForContainers() {
/*  54 */     return this.useAdaptersForContainers;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
/*  59 */     this.useAdaptersForContainers = useAdaptersForContainers;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getForceLegacyNonListCollections() {
/*  64 */     return this.forceLegacyNonListCollections;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setForceLegacyNonListCollections(boolean legacyNonListCollectionWrapping) {
/*  69 */     this.forceLegacyNonListCollections = legacyNonListCollectionWrapping;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getDOMNodeSupport() {
/*  74 */     return this.domNodeSupport;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDOMNodeSupport(boolean domNodeSupport) {
/*  79 */     this.domNodeSupport = domNodeSupport;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getJythonSupport() {
/*  84 */     return this.jythonSupport;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setJythonSupport(boolean jythonSupport) {
/*  89 */     this.jythonSupport = jythonSupport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getIterableSupport() {
/*  98 */     return this.iterableSupport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIterableSupport(boolean iterableSupport) {
/* 107 */     this.iterableSupport = iterableSupport;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 112 */     int result = super.hashCode();
/* 113 */     int prime = 31;
/* 114 */     result = result * 31 + (this.useAdaptersForContainers ? 1231 : 1237);
/* 115 */     result = result * 31 + (this.forceLegacyNonListCollections ? 1231 : 1237);
/* 116 */     result = result * 31 + (this.iterableSupport ? 1231 : 1237);
/* 117 */     result = result * 31 + (this.domNodeSupport ? 1231 : 1237);
/* 118 */     result = result * 31 + (this.jythonSupport ? 1231 : 1237);
/* 119 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object that) {
/* 124 */     if (!super.equals(that)) return false; 
/* 125 */     DefaultObjectWrapperConfiguration thatDowCfg = (DefaultObjectWrapperConfiguration)that;
/* 126 */     return (this.useAdaptersForContainers == thatDowCfg.getUseAdaptersForContainers() && this.forceLegacyNonListCollections == thatDowCfg.forceLegacyNonListCollections && this.iterableSupport == thatDowCfg.iterableSupport && this.domNodeSupport == thatDowCfg.domNodeSupport && this.jythonSupport == thatDowCfg.jythonSupport);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultObjectWrapperConfiguration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */