/*      */ package freemarker.ext.beans;
/*      */ 
/*      */ import freemarker.core.BugException;
/*      */ import freemarker.core._DelayedFTLTypeDescription;
/*      */ import freemarker.core._DelayedShortClassName;
/*      */ import freemarker.core._TemplateModelException;
/*      */ import freemarker.ext.util.ModelCache;
/*      */ import freemarker.ext.util.ModelFactory;
/*      */ import freemarker.ext.util.WrapperTemplateModel;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.AdapterTemplateModel;
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.DefaultObjectWrapper;
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.ObjectWrapperAndUnwrapper;
/*      */ import freemarker.template.SimpleObjectWrapper;
/*      */ import freemarker.template.TemplateBooleanModel;
/*      */ import freemarker.template.TemplateCollectionModel;
/*      */ import freemarker.template.TemplateDateModel;
/*      */ import freemarker.template.TemplateHashModel;
/*      */ import freemarker.template.TemplateMethodModelEx;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.TemplateNumberModel;
/*      */ import freemarker.template.TemplateScalarModel;
/*      */ import freemarker.template.TemplateSequenceModel;
/*      */ import freemarker.template.Version;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.ClassUtil;
/*      */ import freemarker.template.utility.RichObjectWrapper;
/*      */ import freemarker.template.utility.WriteProtectable;
/*      */ import java.beans.PropertyDescriptor;
/*      */ import java.lang.reflect.AccessibleObject;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BeansWrapper
/*      */   implements RichObjectWrapper, WriteProtectable
/*      */ {
/*   89 */   private static final Logger LOG = Logger.getLogger("freemarker.beans");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*   96 */   static final Object CAN_NOT_UNWRAP = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSE_ALL = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSE_SAFE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSE_PROPERTIES_ONLY = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSE_NOTHING = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Object sharedIntrospectionLock;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ClassIntrospector classIntrospector;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final StaticModels staticModels;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ClassBasedModelFactory enumModels;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ModelCache modelCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final BooleanModel falseModel;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final BooleanModel trueModel;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile boolean writeProtected;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  183 */   private TemplateModel nullModel = null;
/*      */   private int defaultDateType;
/*  185 */   private ObjectWrapper outerIdentity = (ObjectWrapper)this;
/*      */   
/*      */   private boolean methodsShadowItems = true;
/*      */   
/*      */   private boolean simpleMapWrapper;
/*      */   
/*      */   private boolean strict;
/*      */   
/*      */   private boolean preferIndexedReadMethod;
/*      */   
/*      */   private final Version incompatibleImprovements;
/*      */   private static volatile boolean ftmaDeprecationWarnLogged;
/*      */   private final ModelFactory BOOLEAN_FACTORY;
/*      */   
/*      */   @Deprecated
/*      */   public BeansWrapper() {
/*  201 */     this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BeansWrapper(Version incompatibleImprovements) {
/*  268 */     this(new BeansWrapperConfiguration(incompatibleImprovements) {  }, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected) {
/*  282 */     this(bwConf, writeProtected, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void finalizeConstruction(boolean writeProtected) {
/*  385 */     if (writeProtected) {
/*  386 */       writeProtect();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  394 */     registerModelFactories();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeProtect() {
/*  409 */     this.writeProtected = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWriteProtected() {
/*  417 */     return this.writeProtected;
/*      */   }
/*      */   
/*      */   Object getSharedIntrospectionLock() {
/*  421 */     return this.sharedIntrospectionLock;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkModifiable() {
/*  431 */     if (this.writeProtected) throw new IllegalStateException("Can't modify the " + 
/*  432 */           getClass().getName() + " object, as it was write protected.");
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isStrict() {
/*  439 */     return this.strict;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setStrict(boolean strict) {
/*  464 */     checkModifiable();
/*  465 */     this.strict = strict;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOuterIdentity(ObjectWrapper outerIdentity) {
/*  478 */     checkModifiable();
/*  479 */     this.outerIdentity = outerIdentity;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWrapper getOuterIdentity() {
/*  487 */     return this.outerIdentity;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSimpleMapWrapper(boolean simpleMapWrapper) {
/*  514 */     checkModifiable();
/*  515 */     this.simpleMapWrapper = simpleMapWrapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSimpleMapWrapper() {
/*  525 */     return this.simpleMapWrapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getPreferIndexedReadMethod() {
/*  534 */     return this.preferIndexedReadMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
/*  558 */     checkModifiable();
/*  559 */     this.preferIndexedReadMethod = preferIndexedReadMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExposureLevel(int exposureLevel) {
/*  568 */     checkModifiable();
/*      */     
/*  570 */     if (this.classIntrospector.getExposureLevel() != exposureLevel) {
/*  571 */       ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
/*  572 */       builder.setExposureLevel(exposureLevel);
/*  573 */       replaceClassIntrospector(builder);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getExposureLevel() {
/*  581 */     return this.classIntrospector.getExposureLevel();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExposeFields(boolean exposeFields) {
/*  595 */     checkModifiable();
/*      */     
/*  597 */     if (this.classIntrospector.getExposeFields() != exposeFields) {
/*  598 */       ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
/*  599 */       builder.setExposeFields(exposeFields);
/*  600 */       replaceClassIntrospector(builder);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
/*  620 */     checkModifiable();
/*      */     
/*  622 */     if (this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() != treatDefaultMethodsAsBeanMembers) {
/*  623 */       ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
/*  624 */       builder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
/*  625 */       replaceClassIntrospector(builder);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isExposeFields() {
/*  637 */     return this.classIntrospector.getExposeFields();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getTreatDefaultMethodsAsBeanMembers() {
/*  644 */     return this.classIntrospector.getTreatDefaultMethodsAsBeanMembers();
/*      */   }
/*      */   
/*      */   public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
/*  648 */     return this.classIntrospector.getMethodAppearanceFineTuner();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
/*  656 */     checkModifiable();
/*      */     
/*  658 */     if (this.classIntrospector.getMethodAppearanceFineTuner() != methodAppearanceFineTuner) {
/*  659 */       ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
/*  660 */       builder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
/*  661 */       replaceClassIntrospector(builder);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MemberAccessPolicy getMemberAccessPolicy() {
/*  669 */     return this.classIntrospector.getMemberAccessPolicy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
/*  679 */     checkModifiable();
/*      */     
/*  681 */     if (this.classIntrospector.getMemberAccessPolicy() != memberAccessPolicy) {
/*  682 */       ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
/*  683 */       builder.setMemberAccessPolicy(memberAccessPolicy);
/*  684 */       replaceClassIntrospector(builder);
/*      */     } 
/*      */   }
/*      */   
/*      */   MethodSorter getMethodSorter() {
/*  689 */     return this.classIntrospector.getMethodSorter();
/*      */   }
/*      */   
/*      */   void setMethodSorter(MethodSorter methodSorter) {
/*  693 */     checkModifiable();
/*      */     
/*  695 */     if (this.classIntrospector.getMethodSorter() != methodSorter) {
/*  696 */       ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
/*  697 */       builder.setMethodSorter(methodSorter);
/*  698 */       replaceClassIntrospector(builder);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClassIntrospectionCacheRestricted() {
/*  713 */     return this.classIntrospector.getHasSharedInstanceRestrictions();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void replaceClassIntrospector(ClassIntrospectorBuilder builder) {
/*  721 */     checkModifiable();
/*      */     
/*  723 */     ClassIntrospector newCI = new ClassIntrospector(builder, this.sharedIntrospectionLock, false, false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  731 */     synchronized (this.sharedIntrospectionLock) {
/*  732 */       ClassIntrospector oldCI = this.classIntrospector;
/*  733 */       if (oldCI != null) {
/*      */ 
/*      */         
/*  736 */         if (this.staticModels != null) {
/*  737 */           oldCI.unregisterModelFactory(this.staticModels);
/*  738 */           this.staticModels.clearCache();
/*      */         } 
/*  740 */         if (this.enumModels != null) {
/*  741 */           oldCI.unregisterModelFactory(this.enumModels);
/*  742 */           this.enumModels.clearCache();
/*      */         } 
/*  744 */         if (this.modelCache != null) {
/*  745 */           oldCI.unregisterModelFactory(this.modelCache);
/*  746 */           this.modelCache.clearCache();
/*      */         } 
/*  748 */         if (this.trueModel != null) {
/*  749 */           this.trueModel.clearMemberCache();
/*      */         }
/*  751 */         if (this.falseModel != null) {
/*  752 */           this.falseModel.clearMemberCache();
/*      */         }
/*      */       } 
/*      */       
/*  756 */       this.classIntrospector = newCI;
/*      */       
/*  758 */       registerModelFactories();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void registerModelFactories() {
/*  763 */     if (this.staticModels != null) {
/*  764 */       this.classIntrospector.registerModelFactory(this.staticModels);
/*      */     }
/*  766 */     if (this.enumModels != null) {
/*  767 */       this.classIntrospector.registerModelFactory(this.enumModels);
/*      */     }
/*  769 */     if (this.modelCache != null) {
/*  770 */       this.classIntrospector.registerModelFactory(this.modelCache);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMethodsShadowItems(boolean methodsShadowItems) {
/*  787 */     synchronized (this) {
/*  788 */       checkModifiable();
/*  789 */       this.methodsShadowItems = methodsShadowItems;
/*      */     } 
/*      */   }
/*      */   
/*      */   boolean isMethodsShadowItems() {
/*  794 */     return this.methodsShadowItems;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDefaultDateType(int defaultDateType) {
/*  807 */     synchronized (this) {
/*  808 */       checkModifiable();
/*      */       
/*  810 */       this.defaultDateType = defaultDateType;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDefaultDateType() {
/*  820 */     return this.defaultDateType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUseCache(boolean useCache) {
/*  831 */     checkModifiable();
/*  832 */     this.modelCache.setUseCache(useCache);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getUseCache() {
/*  839 */     return this.modelCache.getUseCache();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setNullModel(TemplateModel nullModel) {
/*  853 */     checkModifiable();
/*  854 */     this.nullModel = nullModel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Version getIncompatibleImprovements() {
/*  864 */     return this.incompatibleImprovements;
/*      */   }
/*      */   
/*      */   boolean is2321Bugfixed() {
/*  868 */     return is2321Bugfixed(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   static boolean is2321Bugfixed(Version version) {
/*  872 */     return (version.intValue() >= _TemplateAPI.VERSION_INT_2_3_21);
/*      */   }
/*      */   
/*      */   boolean is2324Bugfixed() {
/*  876 */     return is2324Bugfixed(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   static boolean is2324Bugfixed(Version version) {
/*  880 */     return (version.intValue() >= _TemplateAPI.VERSION_INT_2_3_24);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
/*  888 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*  889 */     return (incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_27) ? Configuration.VERSION_2_3_27 : (
/*  890 */       (incompatibleImprovements.intValue() == _TemplateAPI.VERSION_INT_2_3_26) ? Configuration.VERSION_2_3_26 : (
/*  891 */       is2324Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_24 : (
/*  892 */       is2321Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static final BeansWrapper getDefaultInstance() {
/*  912 */     return BeansWrapperSingletonHolder.INSTANCE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel wrap(Object object) throws TemplateModelException {
/*  937 */     if (object == null) return this.nullModel; 
/*  938 */     return this.modelCache.getInstance(object);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateMethodModelEx wrap(Object object, Method method) {
/*  956 */     return new SimpleMethodModel(object, method, method.getParameterTypes(), this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
/*  964 */     return (TemplateHashModel)new APIModel(obj, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected TemplateModel getInstance(Object object, ModelFactory factory) {
/*  976 */     return factory.create(object, (ObjectWrapper)this);
/*      */   }
/*      */   
/*  979 */   protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected, boolean finalizeConstruction) { this.BOOLEAN_FACTORY = new ModelFactory() { public void process(BeansWrapper.MethodAppearanceDecisionInput in, BeansWrapper.MethodAppearanceDecision out) { BeansWrapper.this.finetuneMethodAppearance(in.getContainingClass(), in.getMethod(), out); } }
/*      */       ; if (bwConf.getMethodAppearanceFineTuner() == null) { Class<?> thisClass = getClass(); boolean overridden = false; boolean testFailed = false; try { while (!overridden && thisClass != DefaultObjectWrapper.class && thisClass != BeansWrapper.class && thisClass != SimpleObjectWrapper.class) { try { thisClass.getDeclaredMethod("finetuneMethodAppearance", new Class[] { Class.class, Method.class, MethodAppearanceDecision.class }); overridden = true; } catch (NoSuchMethodException e) { thisClass = thisClass.getSuperclass(); }  }  } catch (Throwable e) { LOG.info("Failed to check if finetuneMethodAppearance is overidden in " + thisClass.getName() + "; acting like if it was, but this way it won't utilize the shared class introspection cache.", e); overridden = true; testFailed = true; }
/*      */        if (overridden) { if (!testFailed && !ftmaDeprecationWarnLogged) { LOG.warn("Overriding " + BeansWrapper.class.getName() + ".finetuneMethodAppearance is deprecated and will be banned sometimes in the future. Use setMethodAppearanceFineTuner instead."); ftmaDeprecationWarnLogged = true; }
/*  982 */          bwConf = (BeansWrapperConfiguration)bwConf.clone(false); bwConf.setMethodAppearanceFineTuner(new MethodAppearanceFineTuner() { public TemplateModel create(Object object, ObjectWrapper wrapper) { return ((Boolean)object).booleanValue() ? (TemplateModel)BeansWrapper.this.trueModel : (TemplateModel)BeansWrapper.this.falseModel; } }); }
/*      */        }
/*      */      this.incompatibleImprovements = bwConf.getIncompatibleImprovements(); this.simpleMapWrapper = bwConf.isSimpleMapWrapper(); this.preferIndexedReadMethod = bwConf.getPreferIndexedReadMethod(); this.defaultDateType = bwConf.getDefaultDateType(); this.outerIdentity = (bwConf.getOuterIdentity() != null) ? bwConf.getOuterIdentity() : (ObjectWrapper)this; this.strict = bwConf.isStrict(); if (!writeProtected) { this.sharedIntrospectionLock = new Object(); this.classIntrospector = new ClassIntrospector(_BeansAPI.getClassIntrospectorBuilder(bwConf), this.sharedIntrospectionLock, false, false); }
/*      */     else { this.classIntrospector = _BeansAPI.getClassIntrospectorBuilder(bwConf).build(); this.sharedIntrospectionLock = this.classIntrospector.getSharedLock(); }
/*  986 */      this.falseModel = new BooleanModel(Boolean.FALSE, this); this.trueModel = new BooleanModel(Boolean.TRUE, this); this.staticModels = new StaticModels(this); this.enumModels = new _EnumModels(this); this.modelCache = new BeansModelCache(this); setUseCache(bwConf.getUseModelCache()); finalizeConstruction(writeProtected); } private static final ModelFactory ITERATOR_FACTORY = new ModelFactory()
/*      */     {
/*      */       public TemplateModel create(Object object, ObjectWrapper wrapper) {
/*  989 */         return (TemplateModel)new IteratorModel((Iterator)object, (BeansWrapper)wrapper);
/*      */       }
/*      */     };
/*      */   
/*  993 */   private static final ModelFactory ENUMERATION_FACTORY = new ModelFactory()
/*      */     {
/*      */       public TemplateModel create(Object object, ObjectWrapper wrapper) {
/*  996 */         return (TemplateModel)new EnumerationModel((Enumeration)object, (BeansWrapper)wrapper);
/*      */       }
/*      */     };
/*      */   
/*      */   protected ModelFactory getModelFactory(Class<?> clazz) {
/* 1001 */     if (Map.class.isAssignableFrom(clazz)) {
/* 1002 */       return this.simpleMapWrapper ? SimpleMapModel.FACTORY : MapModel.FACTORY;
/*      */     }
/* 1004 */     if (Collection.class.isAssignableFrom(clazz)) {
/* 1005 */       return CollectionModel.FACTORY;
/*      */     }
/* 1007 */     if (Number.class.isAssignableFrom(clazz)) {
/* 1008 */       return NumberModel.FACTORY;
/*      */     }
/* 1010 */     if (Date.class.isAssignableFrom(clazz)) {
/* 1011 */       return DateModel.FACTORY;
/*      */     }
/* 1013 */     if (Boolean.class == clazz) {
/* 1014 */       return this.BOOLEAN_FACTORY;
/*      */     }
/* 1016 */     if (ResourceBundle.class.isAssignableFrom(clazz)) {
/* 1017 */       return ResourceBundleModel.FACTORY;
/*      */     }
/* 1019 */     if (Iterator.class.isAssignableFrom(clazz)) {
/* 1020 */       return ITERATOR_FACTORY;
/*      */     }
/* 1022 */     if (Enumeration.class.isAssignableFrom(clazz)) {
/* 1023 */       return ENUMERATION_FACTORY;
/*      */     }
/* 1025 */     if (clazz.isArray()) {
/* 1026 */       return ArrayModel.FACTORY;
/*      */     }
/* 1028 */     return StringModel.FACTORY;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object unwrap(TemplateModel model) throws TemplateModelException {
/* 1046 */     return unwrap(model, Object.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object unwrap(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
/* 1064 */     Object obj = tryUnwrapTo(model, targetClass);
/* 1065 */     if (obj == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/* 1066 */       throw new TemplateModelException("Can not unwrap model of type " + model
/* 1067 */           .getClass().getName() + " to type " + targetClass.getName());
/*      */     }
/* 1069 */     return obj;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object tryUnwrapTo(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
/* 1077 */     return tryUnwrapTo(model, targetClass, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags) throws TemplateModelException {
/* 1089 */     Object res = tryUnwrapTo(model, targetClass, typeFlags, null);
/* 1090 */     if ((typeFlags & 0x1) != 0 && res instanceof Number)
/*      */     {
/* 1092 */       return OverloadedNumberUtil.addFallbackType((Number)res, typeFlags);
/*      */     }
/* 1094 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags, Map<Object, Object> recursionStops) throws TemplateModelException {
/* 1104 */     if (model == null || model == this.nullModel) {
/* 1105 */       return null;
/*      */     }
/*      */     
/* 1108 */     boolean is2321Bugfixed = is2321Bugfixed();
/*      */     
/* 1110 */     if (is2321Bugfixed && targetClass.isPrimitive()) {
/* 1111 */       targetClass = ClassUtil.primitiveClassToBoxingClass(targetClass);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1118 */     if (model instanceof AdapterTemplateModel) {
/* 1119 */       Object wrapped = ((AdapterTemplateModel)model).getAdaptedObject(targetClass);
/*      */       
/* 1121 */       if (targetClass == Object.class || targetClass.isInstance(wrapped)) {
/* 1122 */         return wrapped;
/*      */       }
/*      */ 
/*      */       
/* 1126 */       if (targetClass != Object.class && wrapped instanceof Number && ClassUtil.isNumerical(targetClass)) {
/* 1127 */         Number number = forceUnwrappedNumberToType((Number)wrapped, targetClass, is2321Bugfixed);
/* 1128 */         if (number != null) return number;
/*      */       
/*      */       } 
/*      */     } 
/* 1132 */     if (model instanceof WrapperTemplateModel) {
/* 1133 */       Object wrapped = ((WrapperTemplateModel)model).getWrappedObject();
/* 1134 */       if (targetClass == Object.class || targetClass.isInstance(wrapped)) {
/* 1135 */         return wrapped;
/*      */       }
/*      */ 
/*      */       
/* 1139 */       if (targetClass != Object.class && wrapped instanceof Number && ClassUtil.isNumerical(targetClass)) {
/* 1140 */         Number number = forceUnwrappedNumberToType((Number)wrapped, targetClass, is2321Bugfixed);
/* 1141 */         if (number != null) {
/* 1142 */           return number;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1151 */     if (targetClass != Object.class) {
/*      */ 
/*      */       
/* 1154 */       if (String.class == targetClass) {
/* 1155 */         if (model instanceof TemplateScalarModel) {
/* 1156 */           return ((TemplateScalarModel)model).getAsString();
/*      */         }
/*      */         
/* 1159 */         return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */       } 
/*      */ 
/*      */       
/* 1163 */       if (ClassUtil.isNumerical(targetClass) && 
/* 1164 */         model instanceof TemplateNumberModel) {
/* 1165 */         Number number = forceUnwrappedNumberToType(((TemplateNumberModel)model)
/* 1166 */             .getAsNumber(), targetClass, is2321Bugfixed);
/* 1167 */         if (number != null) {
/* 1168 */           return number;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1173 */       if (boolean.class == targetClass || Boolean.class == targetClass) {
/* 1174 */         if (model instanceof TemplateBooleanModel) {
/* 1175 */           return Boolean.valueOf(((TemplateBooleanModel)model).getAsBoolean());
/*      */         }
/*      */         
/* 1178 */         return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */       } 
/*      */       
/* 1181 */       if (Map.class == targetClass && 
/* 1182 */         model instanceof TemplateHashModel) {
/* 1183 */         return new HashAdapter((TemplateHashModel)model, this);
/*      */       }
/*      */ 
/*      */       
/* 1187 */       if (List.class == targetClass && 
/* 1188 */         model instanceof TemplateSequenceModel) {
/* 1189 */         return new SequenceAdapter((TemplateSequenceModel)model, this);
/*      */       }
/*      */ 
/*      */       
/* 1193 */       if (Set.class == targetClass && 
/* 1194 */         model instanceof TemplateCollectionModel) {
/* 1195 */         return new SetAdapter((TemplateCollectionModel)model, this);
/*      */       }
/*      */ 
/*      */       
/* 1199 */       if (Collection.class == targetClass || Iterable.class == targetClass) {
/* 1200 */         if (model instanceof TemplateCollectionModel) {
/* 1201 */           return new CollectionAdapter((TemplateCollectionModel)model, this);
/*      */         }
/*      */         
/* 1204 */         if (model instanceof TemplateSequenceModel) {
/* 1205 */           return new SequenceAdapter((TemplateSequenceModel)model, this);
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1210 */       if (targetClass.isArray()) {
/* 1211 */         if (model instanceof TemplateSequenceModel) {
/* 1212 */           return unwrapSequenceToArray((TemplateSequenceModel)model, targetClass, true, recursionStops);
/*      */         }
/*      */         
/* 1215 */         return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */       } 
/*      */ 
/*      */       
/* 1219 */       if (char.class == targetClass || targetClass == Character.class) {
/* 1220 */         if (model instanceof TemplateScalarModel) {
/* 1221 */           String s = ((TemplateScalarModel)model).getAsString();
/* 1222 */           if (s.length() == 1) {
/* 1223 */             return Character.valueOf(s.charAt(0));
/*      */           }
/*      */         } 
/*      */         
/* 1227 */         return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */       } 
/*      */       
/* 1230 */       if (Date.class.isAssignableFrom(targetClass) && model instanceof TemplateDateModel) {
/* 1231 */         Date date = ((TemplateDateModel)model).getAsDate();
/* 1232 */         if (targetClass.isInstance(date)) {
/* 1233 */           return date;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1242 */     int itf = typeFlags;
/*      */ 
/*      */     
/*      */     while (true) {
/* 1246 */       if ((itf == 0 || (itf & 0x800) != 0) && model instanceof TemplateNumberModel) {
/*      */         
/* 1248 */         Number number = ((TemplateNumberModel)model).getAsNumber();
/* 1249 */         if (itf != 0 || targetClass.isInstance(number)) {
/* 1250 */           return number;
/*      */         }
/*      */       } 
/* 1253 */       if ((itf == 0 || (itf & 0x1000) != 0) && model instanceof TemplateDateModel) {
/*      */         
/* 1255 */         Date date = ((TemplateDateModel)model).getAsDate();
/* 1256 */         if (itf != 0 || targetClass.isInstance(date)) {
/* 1257 */           return date;
/*      */         }
/*      */       } 
/* 1260 */       if ((itf == 0 || (itf & 0x82000) != 0) && model instanceof TemplateScalarModel && (itf != 0 || targetClass
/*      */         
/* 1262 */         .isAssignableFrom(String.class))) {
/* 1263 */         String strVal = ((TemplateScalarModel)model).getAsString();
/* 1264 */         if (itf == 0 || (itf & 0x80000) == 0) {
/* 1265 */           return strVal;
/*      */         }
/* 1267 */         if (strVal.length() == 1) {
/* 1268 */           if ((itf & 0x2000) != 0) {
/* 1269 */             return new CharacterOrString(strVal);
/*      */           }
/* 1271 */           return Character.valueOf(strVal.charAt(0));
/*      */         } 
/* 1273 */         if ((itf & 0x2000) != 0) {
/* 1274 */           return strVal;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1280 */       if ((itf == 0 || (itf & 0x4000) != 0) && model instanceof TemplateBooleanModel && (itf != 0 || targetClass
/*      */         
/* 1282 */         .isAssignableFrom(Boolean.class))) {
/* 1283 */         return Boolean.valueOf(((TemplateBooleanModel)model).getAsBoolean());
/*      */       }
/* 1285 */       if ((itf == 0 || (itf & 0x8000) != 0) && model instanceof TemplateHashModel && (itf != 0 || targetClass
/*      */         
/* 1287 */         .isAssignableFrom(HashAdapter.class))) {
/* 1288 */         return new HashAdapter((TemplateHashModel)model, this);
/*      */       }
/* 1290 */       if ((itf == 0 || (itf & 0x10000) != 0) && model instanceof TemplateSequenceModel && (itf != 0 || targetClass
/*      */         
/* 1292 */         .isAssignableFrom(SequenceAdapter.class))) {
/* 1293 */         return new SequenceAdapter((TemplateSequenceModel)model, this);
/*      */       }
/* 1295 */       if ((itf == 0 || (itf & 0x20000) != 0) && model instanceof TemplateCollectionModel && (itf != 0 || targetClass
/*      */         
/* 1297 */         .isAssignableFrom(SetAdapter.class))) {
/* 1298 */         return new SetAdapter((TemplateCollectionModel)model, this);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1304 */       if ((itf & 0x40000) != 0 && model instanceof TemplateSequenceModel)
/*      */       {
/* 1306 */         return new SequenceAdapter((TemplateSequenceModel)model, this);
/*      */       }
/*      */       
/* 1309 */       if (itf == 0) {
/*      */         break;
/*      */       }
/* 1312 */       itf = 0;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1317 */     if (targetClass.isInstance(model)) {
/* 1318 */       return model;
/*      */     }
/*      */     
/* 1321 */     return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object unwrapSequenceToArray(TemplateSequenceModel seq, Class<?> arrayClass, boolean tryOnly, Map<Object, Object> recursionStops) throws TemplateModelException {
/* 1333 */     if (recursionStops != null) {
/* 1334 */       Object retval = recursionStops.get(seq);
/* 1335 */       if (retval != null) {
/* 1336 */         return retval;
/*      */       }
/*      */     } else {
/* 1339 */       recursionStops = new IdentityHashMap<>();
/*      */     } 
/* 1341 */     Class<?> componentType = arrayClass.getComponentType();
/* 1342 */     int size = seq.size();
/* 1343 */     Object array = Array.newInstance(componentType, size);
/* 1344 */     recursionStops.put(seq, array);
/*      */     try {
/* 1346 */       for (int i = 0; i < size; i++) {
/* 1347 */         TemplateModel seqItem = seq.get(i);
/* 1348 */         Object val = tryUnwrapTo(seqItem, componentType, 0, recursionStops);
/* 1349 */         if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/* 1350 */           if (tryOnly) {
/* 1351 */             return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
/*      */           }
/* 1353 */           throw new _TemplateModelException(new Object[] { "Failed to convert ", new _DelayedFTLTypeDescription(seq), " object to ", new _DelayedShortClassName(array
/*      */                   
/* 1355 */                   .getClass()), ": Problematic sequence item at index ", 
/* 1356 */                 Integer.valueOf(i), " with value type: ", new _DelayedFTLTypeDescription(seqItem) });
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1361 */         Array.set(array, i, val);
/*      */       } 
/*      */     } finally {
/* 1364 */       recursionStops.remove(seq);
/*      */     } 
/* 1366 */     return array;
/*      */   }
/*      */ 
/*      */   
/*      */   Object listToArray(List<?> list, Class<?> arrayClass, Map<Object, Object> recursionStops) throws TemplateModelException {
/* 1371 */     if (list instanceof SequenceAdapter) {
/* 1372 */       return unwrapSequenceToArray(((SequenceAdapter)list)
/* 1373 */           .getTemplateSequenceModel(), arrayClass, false, recursionStops);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1378 */     if (recursionStops != null) {
/* 1379 */       Object retval = recursionStops.get(list);
/* 1380 */       if (retval != null) {
/* 1381 */         return retval;
/*      */       }
/*      */     } else {
/* 1384 */       recursionStops = new IdentityHashMap<>();
/*      */     } 
/* 1386 */     Class<?> componentType = arrayClass.getComponentType();
/* 1387 */     Object array = Array.newInstance(componentType, list.size());
/* 1388 */     recursionStops.put(list, array);
/*      */     try {
/* 1390 */       boolean isComponentTypeExamined = false;
/* 1391 */       boolean isComponentTypeNumerical = false;
/* 1392 */       boolean isComponentTypeList = false;
/* 1393 */       int i = 0;
/* 1394 */       for (Iterator<?> it = list.iterator(); it.hasNext(); ) {
/* 1395 */         Object<?> listItem = (Object<?>)it.next();
/* 1396 */         if (listItem != null && !componentType.isInstance(listItem)) {
/*      */           
/* 1398 */           if (!isComponentTypeExamined) {
/* 1399 */             isComponentTypeNumerical = ClassUtil.isNumerical(componentType);
/* 1400 */             isComponentTypeList = List.class.isAssignableFrom(componentType);
/* 1401 */             isComponentTypeExamined = true;
/*      */           } 
/* 1403 */           if (isComponentTypeNumerical && listItem instanceof Number) {
/* 1404 */             listItem = (Object<?>)forceUnwrappedNumberToType((Number)listItem, componentType, true);
/* 1405 */           } else if (componentType == String.class && listItem instanceof Character) {
/* 1406 */             listItem = (Object<?>)String.valueOf(((Character)listItem).charValue());
/* 1407 */           } else if ((componentType == Character.class || componentType == char.class) && listItem instanceof String) {
/*      */             
/* 1409 */             String listItemStr = (String)listItem;
/* 1410 */             if (listItemStr.length() == 1) {
/* 1411 */               listItem = (Object<?>)Character.valueOf(listItemStr.charAt(0));
/*      */             }
/* 1413 */           } else if (componentType.isArray()) {
/* 1414 */             if (listItem instanceof List) {
/* 1415 */               listItem = (Object<?>)listToArray((List)listItem, componentType, recursionStops);
/* 1416 */             } else if (listItem instanceof TemplateSequenceModel) {
/* 1417 */               listItem = (Object<?>)unwrapSequenceToArray((TemplateSequenceModel)listItem, componentType, false, recursionStops);
/*      */             } 
/* 1419 */           } else if (isComponentTypeList && listItem.getClass().isArray()) {
/* 1420 */             listItem = (Object<?>)arrayToList(listItem);
/*      */           } 
/*      */         } 
/*      */         try {
/* 1424 */           Array.set(array, i, listItem);
/* 1425 */         } catch (IllegalArgumentException e) {
/* 1426 */           throw new TemplateModelException("Failed to convert " + 
/* 1427 */               ClassUtil.getShortClassNameOfObject(list) + " object to " + 
/* 1428 */               ClassUtil.getShortClassNameOfObject(array) + ": Problematic List item at index " + i + " with value type: " + 
/*      */               
/* 1430 */               ClassUtil.getShortClassNameOfObject(listItem), e);
/*      */         } 
/* 1432 */         i++;
/*      */       } 
/*      */     } finally {
/* 1435 */       recursionStops.remove(list);
/*      */     } 
/* 1437 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   List<?> arrayToList(Object array) throws TemplateModelException {
/* 1444 */     if (array instanceof Object[]) {
/*      */ 
/*      */       
/* 1447 */       Object[] objArray = (Object[])array;
/* 1448 */       return (objArray.length == 0) ? Collections.EMPTY_LIST : new NonPrimitiveArrayBackedReadOnlyList(objArray);
/*      */     } 
/*      */     
/* 1451 */     return (Array.getLength(array) == 0) ? Collections.EMPTY_LIST : new PrimtiveArrayBackedReadOnlyList(array);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static Number forceUnwrappedNumberToType(Number n, Class<?> targetType, boolean bugfixed) {
/* 1462 */     if (targetType == n.getClass())
/* 1463 */       return n; 
/* 1464 */     if (targetType == int.class || targetType == Integer.class)
/* 1465 */       return (n instanceof Integer) ? n : Integer.valueOf(n.intValue()); 
/* 1466 */     if (targetType == long.class || targetType == Long.class)
/* 1467 */       return (n instanceof Long) ? n : Long.valueOf(n.longValue()); 
/* 1468 */     if (targetType == double.class || targetType == Double.class)
/* 1469 */       return (n instanceof Double) ? n : Double.valueOf(n.doubleValue()); 
/* 1470 */     if (targetType == BigDecimal.class) {
/* 1471 */       if (n instanceof BigDecimal)
/* 1472 */         return n; 
/* 1473 */       if (n instanceof BigInteger)
/* 1474 */         return new BigDecimal((BigInteger)n); 
/* 1475 */       if (n instanceof Long)
/*      */       {
/* 1477 */         return BigDecimal.valueOf(n.longValue());
/*      */       }
/* 1479 */       return new BigDecimal(n.doubleValue());
/*      */     } 
/* 1481 */     if (targetType == float.class || targetType == Float.class)
/* 1482 */       return (n instanceof Float) ? n : Float.valueOf(n.floatValue()); 
/* 1483 */     if (targetType == byte.class || targetType == Byte.class)
/* 1484 */       return (n instanceof Byte) ? n : Byte.valueOf(n.byteValue()); 
/* 1485 */     if (targetType == short.class || targetType == Short.class)
/* 1486 */       return (n instanceof Short) ? n : Short.valueOf(n.shortValue()); 
/* 1487 */     if (targetType == BigInteger.class) {
/* 1488 */       if (n instanceof BigInteger)
/* 1489 */         return n; 
/* 1490 */       if (bugfixed) {
/* 1491 */         if (n instanceof OverloadedNumberUtil.IntegerBigDecimal)
/* 1492 */           return ((OverloadedNumberUtil.IntegerBigDecimal)n).bigIntegerValue(); 
/* 1493 */         if (n instanceof BigDecimal) {
/* 1494 */           return ((BigDecimal)n).toBigInteger();
/*      */         }
/* 1496 */         return BigInteger.valueOf(n.longValue());
/*      */       } 
/*      */ 
/*      */       
/* 1500 */       return new BigInteger(n.toString());
/*      */     } 
/*      */ 
/*      */     
/* 1504 */     Number oriN = (n instanceof OverloadedNumberUtil.NumberWithFallbackType) ? ((OverloadedNumberUtil.NumberWithFallbackType)n).getSourceNumber() : n;
/* 1505 */     if (targetType.isInstance(oriN))
/*      */     {
/* 1507 */       return oriN;
/*      */     }
/*      */     
/* 1510 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TemplateModel invokeMethod(Object object, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, TemplateModelException {
/* 1552 */     Object retval = method.invoke(object, args);
/* 1553 */     return 
/* 1554 */       (method.getReturnType() == void.class) ? TemplateModel.NOTHING : 
/*      */       
/* 1556 */       getOuterIdentity().wrap(retval);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TemplateModel readField(Object object, Field field) throws IllegalAccessException, TemplateModelException {
/* 1574 */     return getOuterIdentity().wrap(field.get(object));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateHashModel getStaticModels() {
/* 1591 */     return this.staticModels;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateHashModel getEnumModels() {
/* 1610 */     if (this.enumModels == null) {
/* 1611 */       throw new UnsupportedOperationException("Enums not supported before J2SE 5.");
/*      */     }
/*      */     
/* 1614 */     return this.enumModels;
/*      */   }
/*      */ 
/*      */   
/*      */   ModelCache getModelCache() {
/* 1619 */     return this.modelCache;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object newInstance(Class<?> clazz, List arguments) throws TemplateModelException {
/*      */     try {
/* 1633 */       Object ctors = this.classIntrospector.get(clazz).get(ClassIntrospector.CONSTRUCTORS_KEY);
/* 1634 */       if (ctors == null) {
/* 1635 */         throw new TemplateModelException("Class " + clazz.getName() + " has no exposed constructors.");
/*      */       }
/*      */       
/* 1638 */       Constructor<?> ctor = null;
/*      */       
/* 1640 */       if (ctors instanceof SimpleMethod) {
/* 1641 */         SimpleMethod sm = (SimpleMethod)ctors;
/* 1642 */         ctor = (Constructor)sm.getMember();
/* 1643 */         Object[] objargs = sm.unwrapArguments(arguments, this);
/*      */         try {
/* 1645 */           return ctor.newInstance(objargs);
/* 1646 */         } catch (Exception e) {
/* 1647 */           if (e instanceof TemplateModelException) throw (TemplateModelException)e; 
/* 1648 */           throw _MethodUtil.newInvocationTemplateModelException(null, ctor, e);
/*      */         } 
/* 1650 */       }  if (ctors instanceof OverloadedMethods) {
/* 1651 */         MemberAndArguments mma = ((OverloadedMethods)ctors).getMemberAndArguments(arguments, this);
/*      */         try {
/* 1653 */           return mma.invokeConstructor(this);
/* 1654 */         } catch (Exception e) {
/* 1655 */           if (e instanceof TemplateModelException) throw (TemplateModelException)e;
/*      */           
/* 1657 */           throw _MethodUtil.newInvocationTemplateModelException(null, mma.getCallableMemberDescriptor(), e);
/*      */         } 
/*      */       } 
/*      */       
/* 1661 */       throw new BugException();
/*      */     }
/* 1663 */     catch (TemplateModelException e) {
/* 1664 */       throw e;
/* 1665 */     } catch (Exception e) {
/* 1666 */       throw new TemplateModelException("Error while creating new instance of class " + clazz
/* 1667 */           .getName() + "; see cause exception", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeFromClassIntrospectionCache(Class<?> clazz) {
/* 1680 */     this.classIntrospector.remove(clazz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void clearClassIntrospecitonCache() {
/* 1697 */     this.classIntrospector.clearCache();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearClassIntrospectionCache() {
/* 1711 */     this.classIntrospector.clearCache();
/*      */   }
/*      */   
/*      */   ClassIntrospector getClassIntrospector() {
/* 1715 */     return this.classIntrospector;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void finetuneMethodAppearance(Class<?> clazz, Method m, MethodAppearanceDecision decision) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void coerceBigDecimals(AccessibleObject callable, Object[] args) {
/* 1737 */     Class<?>[] formalTypes = null;
/* 1738 */     for (int i = 0; i < args.length; i++) {
/* 1739 */       Object arg = args[i];
/* 1740 */       if (arg instanceof BigDecimal) {
/* 1741 */         if (formalTypes == null) {
/* 1742 */           if (callable instanceof Method) {
/* 1743 */             formalTypes = ((Method)callable).getParameterTypes();
/* 1744 */           } else if (callable instanceof Constructor) {
/* 1745 */             formalTypes = ((Constructor)callable).getParameterTypes();
/*      */           } else {
/* 1747 */             throw new IllegalArgumentException("Expected method or  constructor; callable is " + callable
/*      */                 
/* 1749 */                 .getClass().getName());
/*      */           } 
/*      */         }
/* 1752 */         args[i] = coerceBigDecimal((BigDecimal)arg, formalTypes[i]);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void coerceBigDecimals(Class<?>[] formalTypes, Object[] args) {
/* 1762 */     int typeLen = formalTypes.length;
/* 1763 */     int argsLen = args.length;
/* 1764 */     int min = Math.min(typeLen, argsLen);
/* 1765 */     for (int i = 0; i < min; i++) {
/* 1766 */       Object arg = args[i];
/* 1767 */       if (arg instanceof BigDecimal) {
/* 1768 */         args[i] = coerceBigDecimal((BigDecimal)arg, formalTypes[i]);
/*      */       }
/*      */     } 
/* 1771 */     if (argsLen > typeLen) {
/* 1772 */       Class<?> varArgType = formalTypes[typeLen - 1];
/* 1773 */       for (int j = typeLen; j < argsLen; j++) {
/* 1774 */         Object arg = args[j];
/* 1775 */         if (arg instanceof BigDecimal) {
/* 1776 */           args[j] = coerceBigDecimal((BigDecimal)arg, varArgType);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object coerceBigDecimal(BigDecimal bd, Class<?> formalType) {
/* 1789 */     if (formalType == int.class || formalType == Integer.class)
/* 1790 */       return Integer.valueOf(bd.intValue()); 
/* 1791 */     if (formalType == double.class || formalType == Double.class)
/* 1792 */       return Double.valueOf(bd.doubleValue()); 
/* 1793 */     if (formalType == long.class || formalType == Long.class)
/* 1794 */       return Long.valueOf(bd.longValue()); 
/* 1795 */     if (formalType == float.class || formalType == Float.class)
/* 1796 */       return Float.valueOf(bd.floatValue()); 
/* 1797 */     if (formalType == short.class || formalType == Short.class)
/* 1798 */       return Short.valueOf(bd.shortValue()); 
/* 1799 */     if (formalType == byte.class || formalType == Byte.class)
/* 1800 */       return Byte.valueOf(bd.byteValue()); 
/* 1801 */     if (BigInteger.class.isAssignableFrom(formalType)) {
/* 1802 */       return bd.toBigInteger();
/*      */     }
/* 1804 */     return bd;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1816 */     String propsStr = toPropertiesString();
/* 1817 */     return ClassUtil.getShortClassNameOfObject(this) + "@" + System.identityHashCode(this) + "(" + this.incompatibleImprovements + ", " + (
/*      */       
/* 1819 */       (propsStr.length() != 0) ? (propsStr + ", ...") : "") + ")";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String toPropertiesString() {
/* 1833 */     return "simpleMapWrapper=" + this.simpleMapWrapper + ", exposureLevel=" + this.classIntrospector
/* 1834 */       .getExposureLevel() + ", exposeFields=" + this.classIntrospector
/* 1835 */       .getExposeFields() + ", preferIndexedReadMethod=" + this.preferIndexedReadMethod + ", treatDefaultMethodsAsBeanMembers=" + this.classIntrospector
/*      */ 
/*      */       
/* 1838 */       .getTreatDefaultMethodsAsBeanMembers() + ", sharedClassIntrospCache=" + (
/*      */       
/* 1840 */       this.classIntrospector.isShared() ? ("@" + System.identityHashCode(this.classIntrospector)) : "none");
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class MethodAppearanceDecision
/*      */   {
/*      */     private PropertyDescriptor exposeAsProperty;
/*      */     
/*      */     private boolean replaceExistingProperty;
/*      */     
/*      */     private String exposeMethodAs;
/*      */     
/*      */     private boolean methodShadowsProperty;
/*      */     
/*      */     void setDefaults(Method m) {
/* 1855 */       this.exposeAsProperty = null;
/* 1856 */       this.replaceExistingProperty = false;
/* 1857 */       this.exposeMethodAs = m.getName();
/* 1858 */       this.methodShadowsProperty = true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public PropertyDescriptor getExposeAsProperty() {
/* 1865 */       return this.exposeAsProperty;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setExposeAsProperty(PropertyDescriptor exposeAsProperty) {
/* 1874 */       this.exposeAsProperty = exposeAsProperty;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean getReplaceExistingProperty() {
/* 1883 */       return this.replaceExistingProperty;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setReplaceExistingProperty(boolean overrideExistingProperty) {
/* 1899 */       this.replaceExistingProperty = overrideExistingProperty;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getExposeMethodAs() {
/* 1906 */       return this.exposeMethodAs;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setExposeMethodAs(String exposeAsMethod) {
/* 1913 */       this.exposeMethodAs = exposeAsMethod;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean getMethodShadowsProperty() {
/* 1920 */       return this.methodShadowsProperty;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setMethodShadowsProperty(boolean shadowEarlierProperty) {
/* 1927 */       this.methodShadowsProperty = shadowEarlierProperty;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class MethodAppearanceDecisionInput
/*      */   {
/*      */     private Method method;
/*      */     
/*      */     private Class<?> containingClass;
/*      */ 
/*      */     
/*      */     void setMethod(Method method) {
/* 1940 */       this.method = method;
/*      */     }
/*      */     
/*      */     void setContainingClass(Class<?> containingClass) {
/* 1944 */       this.containingClass = containingClass;
/*      */     }
/*      */     
/*      */     public Method getMethod() {
/* 1948 */       return this.method;
/*      */     }
/*      */     
/*      */     public Class getContainingClass() {
/* 1952 */       return this.containingClass;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BeansWrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */