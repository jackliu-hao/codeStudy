/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.Version;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ final class ClassIntrospectorBuilder
/*     */   implements Cloneable
/*     */ {
/*  36 */   private static final Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> INSTANCE_CACHE = new HashMap<>();
/*     */   
/*  38 */   private static final ReferenceQueue<ClassIntrospector> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue<>();
/*     */ 
/*     */   
/*     */   private final Version incompatibleImprovements;
/*     */ 
/*     */   
/*  44 */   private int exposureLevel = 1;
/*     */   
/*     */   private boolean exposeFields;
/*     */   
/*     */   private MemberAccessPolicy memberAccessPolicy;
/*     */   
/*     */   private boolean treatDefaultMethodsAsBeanMembers;
/*     */   
/*     */   private MethodAppearanceFineTuner methodAppearanceFineTuner;
/*     */   
/*     */   private MethodSorter methodSorter;
/*     */   
/*     */   ClassIntrospectorBuilder(ClassIntrospector ci) {
/*  57 */     this.incompatibleImprovements = ci.incompatibleImprovements;
/*  58 */     this.exposureLevel = ci.exposureLevel;
/*  59 */     this.exposeFields = ci.exposeFields;
/*  60 */     this.memberAccessPolicy = ci.memberAccessPolicy;
/*  61 */     this.treatDefaultMethodsAsBeanMembers = ci.treatDefaultMethodsAsBeanMembers;
/*  62 */     this.methodAppearanceFineTuner = ci.methodAppearanceFineTuner;
/*  63 */     this.methodSorter = ci.methodSorter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ClassIntrospectorBuilder(Version incompatibleImprovements) {
/*  70 */     this.incompatibleImprovements = normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
/*  71 */     this
/*  72 */       .treatDefaultMethodsAsBeanMembers = (incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_26);
/*  73 */     this.memberAccessPolicy = DefaultMemberAccessPolicy.getInstance(this.incompatibleImprovements);
/*     */   }
/*     */   
/*     */   private static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
/*  77 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*     */     
/*  79 */     return (incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_30) ? Configuration.VERSION_2_3_30 : (
/*  80 */       (incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_21) ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object clone() {
/*     */     try {
/*  87 */       return super.clone();
/*  88 */     } catch (CloneNotSupportedException e) {
/*  89 */       throw new RuntimeException("Failed to clone ClassIntrospectorBuilder", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  95 */     int prime = 31;
/*  96 */     int result = 1;
/*  97 */     result = 31 * result + this.incompatibleImprovements.hashCode();
/*  98 */     result = 31 * result + (this.exposeFields ? 1231 : 1237);
/*  99 */     result = 31 * result + (this.treatDefaultMethodsAsBeanMembers ? 1231 : 1237);
/* 100 */     result = 31 * result + this.exposureLevel;
/* 101 */     result = 31 * result + this.memberAccessPolicy.hashCode();
/* 102 */     result = 31 * result + System.identityHashCode(this.methodAppearanceFineTuner);
/* 103 */     result = 31 * result + System.identityHashCode(this.methodSorter);
/* 104 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 109 */     if (this == obj) return true; 
/* 110 */     if (obj == null) return false; 
/* 111 */     if (getClass() != obj.getClass()) return false; 
/* 112 */     ClassIntrospectorBuilder other = (ClassIntrospectorBuilder)obj;
/*     */     
/* 114 */     if (!this.incompatibleImprovements.equals(other.incompatibleImprovements)) return false; 
/* 115 */     if (this.exposeFields != other.exposeFields) return false; 
/* 116 */     if (this.treatDefaultMethodsAsBeanMembers != other.treatDefaultMethodsAsBeanMembers) return false; 
/* 117 */     if (this.exposureLevel != other.exposureLevel) return false; 
/* 118 */     if (!this.memberAccessPolicy.equals(other.memberAccessPolicy)) return false; 
/* 119 */     if (this.methodAppearanceFineTuner != other.methodAppearanceFineTuner) return false; 
/* 120 */     if (this.methodSorter != other.methodSorter) return false;
/*     */     
/* 122 */     return true;
/*     */   }
/*     */   
/*     */   public int getExposureLevel() {
/* 126 */     return this.exposureLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExposureLevel(int exposureLevel) {
/* 131 */     if (exposureLevel < 0 || exposureLevel > 3) {
/* 132 */       throw new IllegalArgumentException("Illegal exposure level: " + exposureLevel);
/*     */     }
/*     */     
/* 135 */     this.exposureLevel = exposureLevel;
/*     */   }
/*     */   
/*     */   public boolean getExposeFields() {
/* 139 */     return this.exposeFields;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExposeFields(boolean exposeFields) {
/* 144 */     this.exposeFields = exposeFields;
/*     */   }
/*     */   
/*     */   public boolean getTreatDefaultMethodsAsBeanMembers() {
/* 148 */     return this.treatDefaultMethodsAsBeanMembers;
/*     */   }
/*     */   
/*     */   public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
/* 152 */     this.treatDefaultMethodsAsBeanMembers = treatDefaultMethodsAsBeanMembers;
/*     */   }
/*     */   
/*     */   public MemberAccessPolicy getMemberAccessPolicy() {
/* 156 */     return this.memberAccessPolicy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
/* 166 */     NullArgumentException.check(memberAccessPolicy);
/* 167 */     this.memberAccessPolicy = memberAccessPolicy;
/*     */   }
/*     */   
/*     */   public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
/* 171 */     return this.methodAppearanceFineTuner;
/*     */   }
/*     */   
/*     */   public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
/* 175 */     this.methodAppearanceFineTuner = methodAppearanceFineTuner;
/*     */   }
/*     */   
/*     */   public MethodSorter getMethodSorter() {
/* 179 */     return this.methodSorter;
/*     */   }
/*     */   
/*     */   public void setMethodSorter(MethodSorter methodSorter) {
/* 183 */     this.methodSorter = methodSorter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version getIncompatibleImprovements() {
/* 190 */     return this.incompatibleImprovements;
/*     */   }
/*     */   
/*     */   private static void removeClearedReferencesFromInstanceCache() {
/*     */     Reference<? extends ClassIntrospector> clearedRef;
/* 195 */     while ((clearedRef = INSTANCE_CACHE_REF_QUEUE.poll()) != null) {
/* 196 */       synchronized (INSTANCE_CACHE) {
/* 197 */         Iterator<Reference<ClassIntrospector>> it = INSTANCE_CACHE.values().iterator();
/* 198 */         while (it.hasNext()) {
/* 199 */           if (it.next() == clearedRef) {
/* 200 */             it.remove();
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static void clearInstanceCache() {
/* 210 */     synchronized (INSTANCE_CACHE) {
/* 211 */       INSTANCE_CACHE.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> getInstanceCache() {
/* 217 */     return INSTANCE_CACHE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ClassIntrospector build() {
/* 225 */     if ((this.methodAppearanceFineTuner == null || this.methodAppearanceFineTuner instanceof SingletonCustomizer) && (this.methodSorter == null || this.methodSorter instanceof SingletonCustomizer)) {
/*     */       ClassIntrospector instance;
/*     */ 
/*     */       
/* 229 */       synchronized (INSTANCE_CACHE) {
/* 230 */         Reference<ClassIntrospector> instanceRef = INSTANCE_CACHE.get(this);
/* 231 */         instance = (instanceRef != null) ? instanceRef.get() : null;
/* 232 */         if (instance == null) {
/* 233 */           ClassIntrospectorBuilder thisClone = (ClassIntrospectorBuilder)clone();
/* 234 */           instance = new ClassIntrospector(thisClone, new Object(), true, true);
/* 235 */           INSTANCE_CACHE.put(thisClone, new WeakReference<>(instance, INSTANCE_CACHE_REF_QUEUE));
/*     */         } 
/*     */       } 
/*     */       
/* 239 */       removeClearedReferencesFromInstanceCache();
/*     */       
/* 241 */       return instance;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 246 */     return new ClassIntrospector(this, new Object(), true, false);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ClassIntrospectorBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */