/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.Version;
/*     */ import freemarker.template._TemplateAPI;
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
/*     */ public abstract class BeansWrapperConfiguration
/*     */   implements Cloneable
/*     */ {
/*     */   private final Version incompatibleImprovements;
/*     */   private ClassIntrospectorBuilder classIntrospectorBuilder;
/*     */   private boolean simpleMapWrapper = false;
/*     */   private boolean preferIndexedReadMethod;
/*  50 */   private int defaultDateType = 0;
/*  51 */   private ObjectWrapper outerIdentity = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean strict = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean useModelCache = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeansWrapperConfiguration(Version incompatibleImprovements, boolean isIncompImprsAlreadyNormalized) {
/*  78 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*     */ 
/*     */     
/*  81 */     if (!isIncompImprsAlreadyNormalized) {
/*  82 */       _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.beans", "BeansWrapper");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     incompatibleImprovements = isIncompImprsAlreadyNormalized ? incompatibleImprovements : BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
/*  90 */     this.incompatibleImprovements = incompatibleImprovements;
/*     */     
/*  92 */     this.preferIndexedReadMethod = (incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_27);
/*     */     
/*  94 */     this.classIntrospectorBuilder = new ClassIntrospectorBuilder(incompatibleImprovements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeansWrapperConfiguration(Version incompatibleImprovements) {
/* 101 */     this(incompatibleImprovements, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 106 */     int prime = 31;
/* 107 */     int result = 1;
/* 108 */     result = 31 * result + this.incompatibleImprovements.hashCode();
/* 109 */     result = 31 * result + (this.simpleMapWrapper ? 1231 : 1237);
/* 110 */     result = 31 * result + (this.preferIndexedReadMethod ? 1231 : 1237);
/* 111 */     result = 31 * result + this.defaultDateType;
/* 112 */     result = 31 * result + ((this.outerIdentity != null) ? this.outerIdentity.hashCode() : 0);
/* 113 */     result = 31 * result + (this.strict ? 1231 : 1237);
/* 114 */     result = 31 * result + (this.useModelCache ? 1231 : 1237);
/* 115 */     result = 31 * result + this.classIntrospectorBuilder.hashCode();
/* 116 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 125 */     if (this == obj) return true; 
/* 126 */     if (obj == null) return false; 
/* 127 */     if (getClass() != obj.getClass()) return false; 
/* 128 */     BeansWrapperConfiguration other = (BeansWrapperConfiguration)obj;
/*     */     
/* 130 */     if (!this.incompatibleImprovements.equals(other.incompatibleImprovements)) return false; 
/* 131 */     if (this.simpleMapWrapper != other.simpleMapWrapper) return false; 
/* 132 */     if (this.preferIndexedReadMethod != other.preferIndexedReadMethod) return false; 
/* 133 */     if (this.defaultDateType != other.defaultDateType) return false; 
/* 134 */     if (this.outerIdentity != other.outerIdentity) return false; 
/* 135 */     if (this.strict != other.strict) return false; 
/* 136 */     if (this.useModelCache != other.useModelCache) return false; 
/* 137 */     if (!this.classIntrospectorBuilder.equals(other.classIntrospectorBuilder)) return false;
/*     */     
/* 139 */     return true;
/*     */   }
/*     */   
/*     */   protected Object clone(boolean deepCloneKey) {
/*     */     try {
/* 144 */       BeansWrapperConfiguration clone = (BeansWrapperConfiguration)clone();
/* 145 */       if (deepCloneKey) {
/* 146 */         clone
/* 147 */           .classIntrospectorBuilder = (ClassIntrospectorBuilder)this.classIntrospectorBuilder.clone();
/*     */       }
/* 149 */       return clone;
/* 150 */     } catch (CloneNotSupportedException e) {
/* 151 */       throw new RuntimeException("Failed to clone BeansWrapperConfiguration", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isSimpleMapWrapper() {
/* 156 */     return this.simpleMapWrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSimpleMapWrapper(boolean simpleMapWrapper) {
/* 161 */     this.simpleMapWrapper = simpleMapWrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getPreferIndexedReadMethod() {
/* 166 */     return this.preferIndexedReadMethod;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
/* 171 */     this.preferIndexedReadMethod = preferIndexedReadMethod;
/*     */   }
/*     */   
/*     */   public int getDefaultDateType() {
/* 175 */     return this.defaultDateType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultDateType(int defaultDateType) {
/* 180 */     this.defaultDateType = defaultDateType;
/*     */   }
/*     */   
/*     */   public ObjectWrapper getOuterIdentity() {
/* 184 */     return this.outerIdentity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOuterIdentity(ObjectWrapper outerIdentity) {
/* 192 */     this.outerIdentity = outerIdentity;
/*     */   }
/*     */   
/*     */   public boolean isStrict() {
/* 196 */     return this.strict;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStrict(boolean strict) {
/* 201 */     this.strict = strict;
/*     */   }
/*     */   
/*     */   public boolean getUseModelCache() {
/* 205 */     return this.useModelCache;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseModelCache(boolean useModelCache) {
/* 210 */     this.useModelCache = useModelCache;
/*     */   }
/*     */   
/*     */   public Version getIncompatibleImprovements() {
/* 214 */     return this.incompatibleImprovements;
/*     */   }
/*     */   
/*     */   public int getExposureLevel() {
/* 218 */     return this.classIntrospectorBuilder.getExposureLevel();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExposureLevel(int exposureLevel) {
/* 223 */     this.classIntrospectorBuilder.setExposureLevel(exposureLevel);
/*     */   }
/*     */   
/*     */   public boolean getExposeFields() {
/* 227 */     return this.classIntrospectorBuilder.getExposeFields();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExposeFields(boolean exposeFields) {
/* 232 */     this.classIntrospectorBuilder.setExposeFields(exposeFields);
/*     */   }
/*     */   
/*     */   public MemberAccessPolicy getMemberAccessPolicy() {
/* 236 */     return this.classIntrospectorBuilder.getMemberAccessPolicy();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
/* 241 */     this.classIntrospectorBuilder.setMemberAccessPolicy(memberAccessPolicy);
/*     */   }
/*     */   
/*     */   public boolean getTreatDefaultMethodsAsBeanMembers() {
/* 245 */     return this.classIntrospectorBuilder.getTreatDefaultMethodsAsBeanMembers();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
/* 250 */     this.classIntrospectorBuilder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
/*     */   }
/*     */   
/*     */   public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
/* 254 */     return this.classIntrospectorBuilder.getMethodAppearanceFineTuner();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
/* 263 */     this.classIntrospectorBuilder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
/*     */   }
/*     */   
/*     */   MethodSorter getMethodSorter() {
/* 267 */     return this.classIntrospectorBuilder.getMethodSorter();
/*     */   }
/*     */   
/*     */   void setMethodSorter(MethodSorter methodSorter) {
/* 271 */     this.classIntrospectorBuilder.setMethodSorter(methodSorter);
/*     */   }
/*     */   
/*     */   ClassIntrospectorBuilder getClassIntrospectorBuilder() {
/* 275 */     return this.classIntrospectorBuilder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BeansWrapperConfiguration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */