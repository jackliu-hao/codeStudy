package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedShortClassName;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.RichObjectWrapper;
import freemarker.template.utility.WriteProtectable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class BeansWrapper implements RichObjectWrapper, WriteProtectable {
   private static final Logger LOG = Logger.getLogger("freemarker.beans");
   /** @deprecated */
   @Deprecated
   static final Object CAN_NOT_UNWRAP;
   public static final int EXPOSE_ALL = 0;
   public static final int EXPOSE_SAFE = 1;
   public static final int EXPOSE_PROPERTIES_ONLY = 2;
   public static final int EXPOSE_NOTHING = 3;
   private final Object sharedIntrospectionLock;
   private ClassIntrospector classIntrospector;
   private final StaticModels staticModels;
   private final ClassBasedModelFactory enumModels;
   private final ModelCache modelCache;
   private final BooleanModel falseModel;
   private final BooleanModel trueModel;
   private volatile boolean writeProtected;
   private TemplateModel nullModel;
   private int defaultDateType;
   private ObjectWrapper outerIdentity;
   private boolean methodsShadowItems;
   private boolean simpleMapWrapper;
   private boolean strict;
   private boolean preferIndexedReadMethod;
   private final Version incompatibleImprovements;
   private static volatile boolean ftmaDeprecationWarnLogged;
   private final ModelFactory BOOLEAN_FACTORY;
   private static final ModelFactory ITERATOR_FACTORY;
   private static final ModelFactory ENUMERATION_FACTORY;

   /** @deprecated */
   @Deprecated
   public BeansWrapper() {
      this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
   }

   public BeansWrapper(Version incompatibleImprovements) {
      this(new BeansWrapperConfiguration(incompatibleImprovements) {
      }, false);
   }

   protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected) {
      this(bwConf, writeProtected, true);
   }

   protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected, boolean finalizeConstruction) {
      this.nullModel = null;
      this.outerIdentity = this;
      this.methodsShadowItems = true;
      this.BOOLEAN_FACTORY = new ModelFactory() {
         public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return (Boolean)object ? BeansWrapper.this.trueModel : BeansWrapper.this.falseModel;
         }
      };
      if (bwConf.getMethodAppearanceFineTuner() == null) {
         Class<?> thisClass = this.getClass();
         boolean overridden = false;
         boolean testFailed = false;

         try {
            while(!overridden && thisClass != DefaultObjectWrapper.class && thisClass != BeansWrapper.class && thisClass != SimpleObjectWrapper.class) {
               try {
                  thisClass.getDeclaredMethod("finetuneMethodAppearance", Class.class, Method.class, MethodAppearanceDecision.class);
                  overridden = true;
               } catch (NoSuchMethodException var8) {
                  thisClass = thisClass.getSuperclass();
               }
            }
         } catch (Throwable var9) {
            LOG.info("Failed to check if finetuneMethodAppearance is overidden in " + thisClass.getName() + "; acting like if it was, but this way it won't utilize the shared class introspection cache.", var9);
            overridden = true;
            testFailed = true;
         }

         if (overridden) {
            if (!testFailed && !ftmaDeprecationWarnLogged) {
               LOG.warn("Overriding " + BeansWrapper.class.getName() + ".finetuneMethodAppearance is deprecated and will be banned sometimes in the future. Use setMethodAppearanceFineTuner instead.");
               ftmaDeprecationWarnLogged = true;
            }

            bwConf = (BeansWrapperConfiguration)bwConf.clone(false);
            bwConf.setMethodAppearanceFineTuner(new MethodAppearanceFineTuner() {
               public void process(MethodAppearanceDecisionInput in, MethodAppearanceDecision out) {
                  BeansWrapper.this.finetuneMethodAppearance(in.getContainingClass(), in.getMethod(), out);
               }
            });
         }
      }

      this.incompatibleImprovements = bwConf.getIncompatibleImprovements();
      this.simpleMapWrapper = bwConf.isSimpleMapWrapper();
      this.preferIndexedReadMethod = bwConf.getPreferIndexedReadMethod();
      this.defaultDateType = bwConf.getDefaultDateType();
      this.outerIdentity = (ObjectWrapper)(bwConf.getOuterIdentity() != null ? bwConf.getOuterIdentity() : this);
      this.strict = bwConf.isStrict();
      if (!writeProtected) {
         this.sharedIntrospectionLock = new Object();
         this.classIntrospector = new ClassIntrospector(_BeansAPI.getClassIntrospectorBuilder(bwConf), this.sharedIntrospectionLock, false, false);
      } else {
         this.classIntrospector = _BeansAPI.getClassIntrospectorBuilder(bwConf).build();
         this.sharedIntrospectionLock = this.classIntrospector.getSharedLock();
      }

      this.falseModel = new BooleanModel(Boolean.FALSE, this);
      this.trueModel = new BooleanModel(Boolean.TRUE, this);
      this.staticModels = new StaticModels(this);
      this.enumModels = new _EnumModels(this);
      this.modelCache = new BeansModelCache(this);
      this.setUseCache(bwConf.getUseModelCache());
      this.finalizeConstruction(writeProtected);
   }

   protected void finalizeConstruction(boolean writeProtected) {
      if (writeProtected) {
         this.writeProtect();
      }

      this.registerModelFactories();
   }

   public void writeProtect() {
      this.writeProtected = true;
   }

   public boolean isWriteProtected() {
      return this.writeProtected;
   }

   Object getSharedIntrospectionLock() {
      return this.sharedIntrospectionLock;
   }

   protected void checkModifiable() {
      if (this.writeProtected) {
         throw new IllegalStateException("Can't modify the " + this.getClass().getName() + " object, as it was write protected.");
      }
   }

   public boolean isStrict() {
      return this.strict;
   }

   public void setStrict(boolean strict) {
      this.checkModifiable();
      this.strict = strict;
   }

   public void setOuterIdentity(ObjectWrapper outerIdentity) {
      this.checkModifiable();
      this.outerIdentity = outerIdentity;
   }

   public ObjectWrapper getOuterIdentity() {
      return this.outerIdentity;
   }

   public void setSimpleMapWrapper(boolean simpleMapWrapper) {
      this.checkModifiable();
      this.simpleMapWrapper = simpleMapWrapper;
   }

   public boolean isSimpleMapWrapper() {
      return this.simpleMapWrapper;
   }

   public boolean getPreferIndexedReadMethod() {
      return this.preferIndexedReadMethod;
   }

   public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
      this.checkModifiable();
      this.preferIndexedReadMethod = preferIndexedReadMethod;
   }

   public void setExposureLevel(int exposureLevel) {
      this.checkModifiable();
      if (this.classIntrospector.getExposureLevel() != exposureLevel) {
         ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
         builder.setExposureLevel(exposureLevel);
         this.replaceClassIntrospector(builder);
      }

   }

   public int getExposureLevel() {
      return this.classIntrospector.getExposureLevel();
   }

   public void setExposeFields(boolean exposeFields) {
      this.checkModifiable();
      if (this.classIntrospector.getExposeFields() != exposeFields) {
         ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
         builder.setExposeFields(exposeFields);
         this.replaceClassIntrospector(builder);
      }

   }

   public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
      this.checkModifiable();
      if (this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() != treatDefaultMethodsAsBeanMembers) {
         ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
         builder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
         this.replaceClassIntrospector(builder);
      }

   }

   public boolean isExposeFields() {
      return this.classIntrospector.getExposeFields();
   }

   public boolean getTreatDefaultMethodsAsBeanMembers() {
      return this.classIntrospector.getTreatDefaultMethodsAsBeanMembers();
   }

   public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
      return this.classIntrospector.getMethodAppearanceFineTuner();
   }

   public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
      this.checkModifiable();
      if (this.classIntrospector.getMethodAppearanceFineTuner() != methodAppearanceFineTuner) {
         ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
         builder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
         this.replaceClassIntrospector(builder);
      }

   }

   public MemberAccessPolicy getMemberAccessPolicy() {
      return this.classIntrospector.getMemberAccessPolicy();
   }

   public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
      this.checkModifiable();
      if (this.classIntrospector.getMemberAccessPolicy() != memberAccessPolicy) {
         ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
         builder.setMemberAccessPolicy(memberAccessPolicy);
         this.replaceClassIntrospector(builder);
      }

   }

   MethodSorter getMethodSorter() {
      return this.classIntrospector.getMethodSorter();
   }

   void setMethodSorter(MethodSorter methodSorter) {
      this.checkModifiable();
      if (this.classIntrospector.getMethodSorter() != methodSorter) {
         ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
         builder.setMethodSorter(methodSorter);
         this.replaceClassIntrospector(builder);
      }

   }

   public boolean isClassIntrospectionCacheRestricted() {
      return this.classIntrospector.getHasSharedInstanceRestrictions();
   }

   private void replaceClassIntrospector(ClassIntrospectorBuilder builder) {
      this.checkModifiable();
      ClassIntrospector newCI = new ClassIntrospector(builder, this.sharedIntrospectionLock, false, false);
      synchronized(this.sharedIntrospectionLock) {
         ClassIntrospector oldCI = this.classIntrospector;
         if (oldCI != null) {
            if (this.staticModels != null) {
               oldCI.unregisterModelFactory((ClassBasedModelFactory)this.staticModels);
               this.staticModels.clearCache();
            }

            if (this.enumModels != null) {
               oldCI.unregisterModelFactory(this.enumModels);
               this.enumModels.clearCache();
            }

            if (this.modelCache != null) {
               oldCI.unregisterModelFactory(this.modelCache);
               this.modelCache.clearCache();
            }

            if (this.trueModel != null) {
               this.trueModel.clearMemberCache();
            }

            if (this.falseModel != null) {
               this.falseModel.clearMemberCache();
            }
         }

         this.classIntrospector = newCI;
         this.registerModelFactories();
      }
   }

   private void registerModelFactories() {
      if (this.staticModels != null) {
         this.classIntrospector.registerModelFactory((ClassBasedModelFactory)this.staticModels);
      }

      if (this.enumModels != null) {
         this.classIntrospector.registerModelFactory(this.enumModels);
      }

      if (this.modelCache != null) {
         this.classIntrospector.registerModelFactory(this.modelCache);
      }

   }

   public void setMethodsShadowItems(boolean methodsShadowItems) {
      synchronized(this) {
         this.checkModifiable();
         this.methodsShadowItems = methodsShadowItems;
      }
   }

   boolean isMethodsShadowItems() {
      return this.methodsShadowItems;
   }

   public void setDefaultDateType(int defaultDateType) {
      synchronized(this) {
         this.checkModifiable();
         this.defaultDateType = defaultDateType;
      }
   }

   public int getDefaultDateType() {
      return this.defaultDateType;
   }

   public void setUseCache(boolean useCache) {
      this.checkModifiable();
      this.modelCache.setUseCache(useCache);
   }

   public boolean getUseCache() {
      return this.modelCache.getUseCache();
   }

   /** @deprecated */
   @Deprecated
   public void setNullModel(TemplateModel nullModel) {
      this.checkModifiable();
      this.nullModel = nullModel;
   }

   public Version getIncompatibleImprovements() {
      return this.incompatibleImprovements;
   }

   boolean is2321Bugfixed() {
      return is2321Bugfixed(this.getIncompatibleImprovements());
   }

   static boolean is2321Bugfixed(Version version) {
      return version.intValue() >= _TemplateAPI.VERSION_INT_2_3_21;
   }

   boolean is2324Bugfixed() {
      return is2324Bugfixed(this.getIncompatibleImprovements());
   }

   static boolean is2324Bugfixed(Version version) {
      return version.intValue() >= _TemplateAPI.VERSION_INT_2_3_24;
   }

   protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
      _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
      return incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_27 ? Configuration.VERSION_2_3_27 : (incompatibleImprovements.intValue() == _TemplateAPI.VERSION_INT_2_3_26 ? Configuration.VERSION_2_3_26 : (is2324Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_24 : (is2321Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0)));
   }

   /** @deprecated */
   @Deprecated
   public static final BeansWrapper getDefaultInstance() {
      return BeansWrapperSingletonHolder.INSTANCE;
   }

   public TemplateModel wrap(Object object) throws TemplateModelException {
      return object == null ? this.nullModel : this.modelCache.getInstance(object);
   }

   public TemplateMethodModelEx wrap(Object object, Method method) {
      return new SimpleMethodModel(object, method, method.getParameterTypes(), this);
   }

   public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
      return new APIModel(obj, this);
   }

   /** @deprecated */
   @Deprecated
   protected TemplateModel getInstance(Object object, ModelFactory factory) {
      return factory.create(object, this);
   }

   protected ModelFactory getModelFactory(Class<?> clazz) {
      if (Map.class.isAssignableFrom(clazz)) {
         return this.simpleMapWrapper ? SimpleMapModel.FACTORY : MapModel.FACTORY;
      } else if (Collection.class.isAssignableFrom(clazz)) {
         return CollectionModel.FACTORY;
      } else if (Number.class.isAssignableFrom(clazz)) {
         return NumberModel.FACTORY;
      } else if (Date.class.isAssignableFrom(clazz)) {
         return DateModel.FACTORY;
      } else if (Boolean.class == clazz) {
         return this.BOOLEAN_FACTORY;
      } else if (ResourceBundle.class.isAssignableFrom(clazz)) {
         return ResourceBundleModel.FACTORY;
      } else if (Iterator.class.isAssignableFrom(clazz)) {
         return ITERATOR_FACTORY;
      } else if (Enumeration.class.isAssignableFrom(clazz)) {
         return ENUMERATION_FACTORY;
      } else {
         return clazz.isArray() ? ArrayModel.FACTORY : StringModel.FACTORY;
      }
   }

   public Object unwrap(TemplateModel model) throws TemplateModelException {
      return this.unwrap(model, Object.class);
   }

   public Object unwrap(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
      Object obj = this.tryUnwrapTo(model, targetClass);
      if (obj == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
         throw new TemplateModelException("Can not unwrap model of type " + model.getClass().getName() + " to type " + targetClass.getName());
      } else {
         return obj;
      }
   }

   public Object tryUnwrapTo(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
      return this.tryUnwrapTo(model, targetClass, 0);
   }

   Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags) throws TemplateModelException {
      Object res = this.tryUnwrapTo(model, targetClass, typeFlags, (Map)null);
      return (typeFlags & 1) != 0 && res instanceof Number ? OverloadedNumberUtil.addFallbackType((Number)res, typeFlags) : res;
   }

   private Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags, Map<Object, Object> recursionStops) throws TemplateModelException {
      if (model != null && model != this.nullModel) {
         boolean is2321Bugfixed = this.is2321Bugfixed();
         if (is2321Bugfixed && targetClass.isPrimitive()) {
            targetClass = ClassUtil.primitiveClassToBoxingClass(targetClass);
         }

         Object wrapped;
         Number number;
         if (model instanceof AdapterTemplateModel) {
            wrapped = ((AdapterTemplateModel)model).getAdaptedObject(targetClass);
            if (targetClass == Object.class || targetClass.isInstance(wrapped)) {
               return wrapped;
            }

            if (targetClass != Object.class && wrapped instanceof Number && ClassUtil.isNumerical(targetClass)) {
               number = forceUnwrappedNumberToType((Number)wrapped, targetClass, is2321Bugfixed);
               if (number != null) {
                  return number;
               }
            }
         }

         if (model instanceof WrapperTemplateModel) {
            wrapped = ((WrapperTemplateModel)model).getWrappedObject();
            if (targetClass == Object.class || targetClass.isInstance(wrapped)) {
               return wrapped;
            }

            if (targetClass != Object.class && wrapped instanceof Number && ClassUtil.isNumerical(targetClass)) {
               number = forceUnwrappedNumberToType((Number)wrapped, targetClass, is2321Bugfixed);
               if (number != null) {
                  return number;
               }
            }
         }

         if (targetClass != Object.class) {
            label326: {
               if (String.class == targetClass) {
                  return model instanceof TemplateScalarModel ? ((TemplateScalarModel)model).getAsString() : ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
               }

               if (ClassUtil.isNumerical(targetClass) && model instanceof TemplateNumberModel) {
                  Number number = forceUnwrappedNumberToType(((TemplateNumberModel)model).getAsNumber(), targetClass, is2321Bugfixed);
                  if (number != null) {
                     return number;
                  }
               }

               if (Boolean.TYPE != targetClass && Boolean.class != targetClass) {
                  if (Map.class == targetClass && model instanceof TemplateHashModel) {
                     return new HashAdapter((TemplateHashModel)model, this);
                  }

                  if (List.class == targetClass && model instanceof TemplateSequenceModel) {
                     return new SequenceAdapter((TemplateSequenceModel)model, this);
                  }

                  if (Set.class == targetClass && model instanceof TemplateCollectionModel) {
                     return new SetAdapter((TemplateCollectionModel)model, this);
                  }

                  if (Collection.class == targetClass || Iterable.class == targetClass) {
                     if (model instanceof TemplateCollectionModel) {
                        return new CollectionAdapter((TemplateCollectionModel)model, this);
                     }

                     if (model instanceof TemplateSequenceModel) {
                        return new SequenceAdapter((TemplateSequenceModel)model, this);
                     }
                  }

                  if (targetClass.isArray()) {
                     if (model instanceof TemplateSequenceModel) {
                        return this.unwrapSequenceToArray((TemplateSequenceModel)model, targetClass, true, recursionStops);
                     }

                     return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
                  }

                  if (Character.TYPE != targetClass && targetClass != Character.class) {
                     if (Date.class.isAssignableFrom(targetClass) && model instanceof TemplateDateModel) {
                        Date date = ((TemplateDateModel)model).getAsDate();
                        if (targetClass.isInstance(date)) {
                           return date;
                        }
                     }
                     break label326;
                  }

                  if (model instanceof TemplateScalarModel) {
                     String s = ((TemplateScalarModel)model).getAsString();
                     if (s.length() == 1) {
                        return s.charAt(0);
                     }
                  }

                  return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
               }

               if (model instanceof TemplateBooleanModel) {
                  return ((TemplateBooleanModel)model).getAsBoolean();
               }

               return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
            }
         }

         int itf = typeFlags;

         while(true) {
            if ((itf == 0 || (itf & 2048) != 0) && model instanceof TemplateNumberModel) {
               number = ((TemplateNumberModel)model).getAsNumber();
               if (itf != 0 || targetClass.isInstance(number)) {
                  return number;
               }
            }

            if ((itf == 0 || (itf & 4096) != 0) && model instanceof TemplateDateModel) {
               Date date = ((TemplateDateModel)model).getAsDate();
               if (itf != 0 || targetClass.isInstance(date)) {
                  return date;
               }
            }

            if ((itf == 0 || (itf & 532480) != 0) && model instanceof TemplateScalarModel && (itf != 0 || targetClass.isAssignableFrom(String.class))) {
               String strVal = ((TemplateScalarModel)model).getAsString();
               if (itf == 0 || (itf & 524288) == 0) {
                  return strVal;
               }

               if (strVal.length() == 1) {
                  if ((itf & 8192) != 0) {
                     return new CharacterOrString(strVal);
                  }

                  return strVal.charAt(0);
               }

               if ((itf & 8192) != 0) {
                  return strVal;
               }
            }

            if (itf != 0 && (itf & 16384) == 0 || !(model instanceof TemplateBooleanModel) || itf == 0 && !targetClass.isAssignableFrom(Boolean.class)) {
               if ((itf == 0 || (itf & '耀') != 0) && model instanceof TemplateHashModel && (itf != 0 || targetClass.isAssignableFrom(HashAdapter.class))) {
                  return new HashAdapter((TemplateHashModel)model, this);
               }

               if (itf != 0 && (itf & 65536) == 0 || !(model instanceof TemplateSequenceModel) || itf == 0 && !targetClass.isAssignableFrom(SequenceAdapter.class)) {
                  if (itf != 0 && (itf & 131072) == 0 || !(model instanceof TemplateCollectionModel) || itf == 0 && !targetClass.isAssignableFrom(SetAdapter.class)) {
                     if ((itf & 262144) != 0 && model instanceof TemplateSequenceModel) {
                        return new SequenceAdapter((TemplateSequenceModel)model, this);
                     }

                     if (itf == 0) {
                        if (targetClass.isInstance(model)) {
                           return model;
                        }

                        return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
                     }

                     itf = 0;
                     continue;
                  }

                  return new SetAdapter((TemplateCollectionModel)model, this);
               }

               return new SequenceAdapter((TemplateSequenceModel)model, this);
            }

            return ((TemplateBooleanModel)model).getAsBoolean();
         }
      } else {
         return null;
      }
   }

   Object unwrapSequenceToArray(TemplateSequenceModel seq, Class<?> arrayClass, boolean tryOnly, Map<Object, Object> recursionStops) throws TemplateModelException {
      if (recursionStops != null) {
         Object retval = ((Map)recursionStops).get(seq);
         if (retval != null) {
            return retval;
         }
      } else {
         recursionStops = new IdentityHashMap();
      }

      Class<?> componentType = arrayClass.getComponentType();
      int size = seq.size();
      Object array = Array.newInstance(componentType, size);
      ((Map)recursionStops).put(seq, array);

      try {
         for(int i = 0; i < size; ++i) {
            TemplateModel seqItem = seq.get(i);
            Object val = this.tryUnwrapTo(seqItem, componentType, 0, (Map)recursionStops);
            if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
               if (tryOnly) {
                  Object var11 = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
                  return var11;
               }

               throw new _TemplateModelException(new Object[]{"Failed to convert ", new _DelayedFTLTypeDescription(seq), " object to ", new _DelayedShortClassName(array.getClass()), ": Problematic sequence item at index ", i, " with value type: ", new _DelayedFTLTypeDescription(seqItem)});
            }

            Array.set(array, i, val);
         }
      } finally {
         ((Map)recursionStops).remove(seq);
      }

      return array;
   }

   Object listToArray(List<?> list, Class<?> arrayClass, Map<Object, Object> recursionStops) throws TemplateModelException {
      if (list instanceof SequenceAdapter) {
         return this.unwrapSequenceToArray(((SequenceAdapter)list).getTemplateSequenceModel(), arrayClass, false, (Map)recursionStops);
      } else {
         if (recursionStops != null) {
            Object retval = ((Map)recursionStops).get(list);
            if (retval != null) {
               return retval;
            }
         } else {
            recursionStops = new IdentityHashMap();
         }

         Class<?> componentType = arrayClass.getComponentType();
         Object array = Array.newInstance(componentType, list.size());
         ((Map)recursionStops).put(list, array);

         try {
            boolean isComponentTypeExamined = false;
            boolean isComponentTypeNumerical = false;
            boolean isComponentTypeList = false;
            int i = 0;

            for(Iterator<?> it = list.iterator(); it.hasNext(); ++i) {
               Object listItem = it.next();
               if (listItem != null && !componentType.isInstance(listItem)) {
                  if (!isComponentTypeExamined) {
                     isComponentTypeNumerical = ClassUtil.isNumerical(componentType);
                     isComponentTypeList = List.class.isAssignableFrom(componentType);
                     isComponentTypeExamined = true;
                  }

                  if (isComponentTypeNumerical && listItem instanceof Number) {
                     listItem = forceUnwrappedNumberToType((Number)listItem, componentType, true);
                  } else if (componentType == String.class && listItem instanceof Character) {
                     listItem = String.valueOf((Character)listItem);
                  } else if ((componentType == Character.class || componentType == Character.TYPE) && listItem instanceof String) {
                     String listItemStr = (String)listItem;
                     if (listItemStr.length() == 1) {
                        listItem = listItemStr.charAt(0);
                     }
                  } else if (componentType.isArray()) {
                     if (listItem instanceof List) {
                        listItem = this.listToArray((List)listItem, componentType, (Map)recursionStops);
                     } else if (listItem instanceof TemplateSequenceModel) {
                        listItem = this.unwrapSequenceToArray((TemplateSequenceModel)listItem, componentType, false, (Map)recursionStops);
                     }
                  } else if (isComponentTypeList && listItem.getClass().isArray()) {
                     listItem = this.arrayToList(listItem);
                  }
               }

               try {
                  Array.set(array, i, listItem);
               } catch (IllegalArgumentException var16) {
                  throw new TemplateModelException("Failed to convert " + ClassUtil.getShortClassNameOfObject(list) + " object to " + ClassUtil.getShortClassNameOfObject(array) + ": Problematic List item at index " + i + " with value type: " + ClassUtil.getShortClassNameOfObject(listItem), var16);
               }
            }
         } finally {
            ((Map)recursionStops).remove(list);
         }

         return array;
      }
   }

   List<?> arrayToList(Object array) throws TemplateModelException {
      if (array instanceof Object[]) {
         Object[] objArray = (Object[])((Object[])array);
         return (List)(objArray.length == 0 ? Collections.EMPTY_LIST : new NonPrimitiveArrayBackedReadOnlyList(objArray));
      } else {
         return (List)(Array.getLength(array) == 0 ? Collections.EMPTY_LIST : new PrimtiveArrayBackedReadOnlyList(array));
      }
   }

   static Number forceUnwrappedNumberToType(Number n, Class<?> targetType, boolean bugfixed) {
      if (targetType == n.getClass()) {
         return n;
      } else if (targetType != Integer.TYPE && targetType != Integer.class) {
         if (targetType != Long.TYPE && targetType != Long.class) {
            if (targetType != Double.TYPE && targetType != Double.class) {
               if (targetType == BigDecimal.class) {
                  if (n instanceof BigDecimal) {
                     return n;
                  } else if (n instanceof BigInteger) {
                     return new BigDecimal((BigInteger)n);
                  } else {
                     return n instanceof Long ? BigDecimal.valueOf(n.longValue()) : new BigDecimal(n.doubleValue());
                  }
               } else if (targetType != Float.TYPE && targetType != Float.class) {
                  if (targetType != Byte.TYPE && targetType != Byte.class) {
                     if (targetType != Short.TYPE && targetType != Short.class) {
                        if (targetType == BigInteger.class) {
                           if (n instanceof BigInteger) {
                              return n;
                           } else if (bugfixed) {
                              if (n instanceof OverloadedNumberUtil.IntegerBigDecimal) {
                                 return ((OverloadedNumberUtil.IntegerBigDecimal)n).bigIntegerValue();
                              } else {
                                 return n instanceof BigDecimal ? ((BigDecimal)n).toBigInteger() : BigInteger.valueOf(n.longValue());
                              }
                           } else {
                              return new BigInteger(n.toString());
                           }
                        } else {
                           Number oriN = n instanceof OverloadedNumberUtil.NumberWithFallbackType ? ((OverloadedNumberUtil.NumberWithFallbackType)n).getSourceNumber() : n;
                           return targetType.isInstance(oriN) ? oriN : null;
                        }
                     } else {
                        return n instanceof Short ? (Short)n : n.shortValue();
                     }
                  } else {
                     return n instanceof Byte ? (Byte)n : n.byteValue();
                  }
               } else {
                  return n instanceof Float ? (Float)n : n.floatValue();
               }
            } else {
               return n instanceof Double ? (Double)n : n.doubleValue();
            }
         } else {
            return n instanceof Long ? (Long)n : n.longValue();
         }
      } else {
         return n instanceof Integer ? (Integer)n : n.intValue();
      }
   }

   protected TemplateModel invokeMethod(Object object, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, TemplateModelException {
      Object retval = method.invoke(object, args);
      return method.getReturnType() == Void.TYPE ? TemplateModel.NOTHING : this.getOuterIdentity().wrap(retval);
   }

   protected TemplateModel readField(Object object, Field field) throws IllegalAccessException, TemplateModelException {
      return this.getOuterIdentity().wrap(field.get(object));
   }

   public TemplateHashModel getStaticModels() {
      return this.staticModels;
   }

   public TemplateHashModel getEnumModels() {
      if (this.enumModels == null) {
         throw new UnsupportedOperationException("Enums not supported before J2SE 5.");
      } else {
         return this.enumModels;
      }
   }

   ModelCache getModelCache() {
      return this.modelCache;
   }

   public Object newInstance(Class<?> clazz, List arguments) throws TemplateModelException {
      try {
         Object ctors = this.classIntrospector.get(clazz).get(ClassIntrospector.CONSTRUCTORS_KEY);
         if (ctors == null) {
            throw new TemplateModelException("Class " + clazz.getName() + " has no exposed constructors.");
         } else {
            Constructor<?> ctor = null;
            if (ctors instanceof SimpleMethod) {
               SimpleMethod sm = (SimpleMethod)ctors;
               ctor = (Constructor)sm.getMember();
               Object[] objargs = sm.unwrapArguments(arguments, this);

               try {
                  return ctor.newInstance(objargs);
               } catch (Exception var9) {
                  if (var9 instanceof TemplateModelException) {
                     throw (TemplateModelException)var9;
                  } else {
                     throw _MethodUtil.newInvocationTemplateModelException((Object)null, (Member)ctor, var9);
                  }
               }
            } else if (ctors instanceof OverloadedMethods) {
               MemberAndArguments mma = ((OverloadedMethods)ctors).getMemberAndArguments(arguments, this);

               try {
                  return mma.invokeConstructor(this);
               } catch (Exception var8) {
                  if (var8 instanceof TemplateModelException) {
                     throw (TemplateModelException)var8;
                  } else {
                     throw _MethodUtil.newInvocationTemplateModelException((Object)null, (CallableMemberDescriptor)mma.getCallableMemberDescriptor(), var8);
                  }
               }
            } else {
               throw new BugException();
            }
         }
      } catch (TemplateModelException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new TemplateModelException("Error while creating new instance of class " + clazz.getName() + "; see cause exception", var11);
      }
   }

   public void removeFromClassIntrospectionCache(Class<?> clazz) {
      this.classIntrospector.remove(clazz);
   }

   /** @deprecated */
   @Deprecated
   public void clearClassIntrospecitonCache() {
      this.classIntrospector.clearCache();
   }

   public void clearClassIntrospectionCache() {
      this.classIntrospector.clearCache();
   }

   ClassIntrospector getClassIntrospector() {
      return this.classIntrospector;
   }

   /** @deprecated */
   @Deprecated
   protected void finetuneMethodAppearance(Class<?> clazz, Method m, MethodAppearanceDecision decision) {
   }

   public static void coerceBigDecimals(AccessibleObject callable, Object[] args) {
      Class<?>[] formalTypes = null;

      for(int i = 0; i < args.length; ++i) {
         Object arg = args[i];
         if (arg instanceof BigDecimal) {
            if (formalTypes == null) {
               if (callable instanceof Method) {
                  formalTypes = ((Method)callable).getParameterTypes();
               } else {
                  if (!(callable instanceof Constructor)) {
                     throw new IllegalArgumentException("Expected method or  constructor; callable is " + callable.getClass().getName());
                  }

                  formalTypes = ((Constructor)callable).getParameterTypes();
               }
            }

            args[i] = coerceBigDecimal((BigDecimal)arg, formalTypes[i]);
         }
      }

   }

   public static void coerceBigDecimals(Class<?>[] formalTypes, Object[] args) {
      int typeLen = formalTypes.length;
      int argsLen = args.length;
      int min = Math.min(typeLen, argsLen);

      for(int i = 0; i < min; ++i) {
         Object arg = args[i];
         if (arg instanceof BigDecimal) {
            args[i] = coerceBigDecimal((BigDecimal)arg, formalTypes[i]);
         }
      }

      if (argsLen > typeLen) {
         Class<?> varArgType = formalTypes[typeLen - 1];

         for(int i = typeLen; i < argsLen; ++i) {
            Object arg = args[i];
            if (arg instanceof BigDecimal) {
               args[i] = coerceBigDecimal((BigDecimal)arg, varArgType);
            }
         }
      }

   }

   public static Object coerceBigDecimal(BigDecimal bd, Class<?> formalType) {
      if (formalType != Integer.TYPE && formalType != Integer.class) {
         if (formalType != Double.TYPE && formalType != Double.class) {
            if (formalType != Long.TYPE && formalType != Long.class) {
               if (formalType != Float.TYPE && formalType != Float.class) {
                  if (formalType != Short.TYPE && formalType != Short.class) {
                     if (formalType != Byte.TYPE && formalType != Byte.class) {
                        return BigInteger.class.isAssignableFrom(formalType) ? bd.toBigInteger() : bd;
                     } else {
                        return bd.byteValue();
                     }
                  } else {
                     return bd.shortValue();
                  }
               } else {
                  return bd.floatValue();
               }
            } else {
               return bd.longValue();
            }
         } else {
            return bd.doubleValue();
         }
      } else {
         return bd.intValue();
      }
   }

   public String toString() {
      String propsStr = this.toPropertiesString();
      return ClassUtil.getShortClassNameOfObject(this) + "@" + System.identityHashCode(this) + "(" + this.incompatibleImprovements + ", " + (propsStr.length() != 0 ? propsStr + ", ..." : "") + ")";
   }

   protected String toPropertiesString() {
      return "simpleMapWrapper=" + this.simpleMapWrapper + ", exposureLevel=" + this.classIntrospector.getExposureLevel() + ", exposeFields=" + this.classIntrospector.getExposeFields() + ", preferIndexedReadMethod=" + this.preferIndexedReadMethod + ", treatDefaultMethodsAsBeanMembers=" + this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() + ", sharedClassIntrospCache=" + (this.classIntrospector.isShared() ? "@" + System.identityHashCode(this.classIntrospector) : "none");
   }

   static {
      CAN_NOT_UNWRAP = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
      ITERATOR_FACTORY = new ModelFactory() {
         public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new IteratorModel((Iterator)object, (BeansWrapper)wrapper);
         }
      };
      ENUMERATION_FACTORY = new ModelFactory() {
         public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new EnumerationModel((Enumeration)object, (BeansWrapper)wrapper);
         }
      };
   }

   public static final class MethodAppearanceDecisionInput {
      private Method method;
      private Class<?> containingClass;

      void setMethod(Method method) {
         this.method = method;
      }

      void setContainingClass(Class<?> containingClass) {
         this.containingClass = containingClass;
      }

      public Method getMethod() {
         return this.method;
      }

      public Class getContainingClass() {
         return this.containingClass;
      }
   }

   public static final class MethodAppearanceDecision {
      private PropertyDescriptor exposeAsProperty;
      private boolean replaceExistingProperty;
      private String exposeMethodAs;
      private boolean methodShadowsProperty;

      void setDefaults(Method m) {
         this.exposeAsProperty = null;
         this.replaceExistingProperty = false;
         this.exposeMethodAs = m.getName();
         this.methodShadowsProperty = true;
      }

      public PropertyDescriptor getExposeAsProperty() {
         return this.exposeAsProperty;
      }

      public void setExposeAsProperty(PropertyDescriptor exposeAsProperty) {
         this.exposeAsProperty = exposeAsProperty;
      }

      public boolean getReplaceExistingProperty() {
         return this.replaceExistingProperty;
      }

      public void setReplaceExistingProperty(boolean overrideExistingProperty) {
         this.replaceExistingProperty = overrideExistingProperty;
      }

      public String getExposeMethodAs() {
         return this.exposeMethodAs;
      }

      public void setExposeMethodAs(String exposeAsMethod) {
         this.exposeMethodAs = exposeAsMethod;
      }

      public boolean getMethodShadowsProperty() {
         return this.methodShadowsProperty;
      }

      public void setMethodShadowsProperty(boolean shadowEarlierProperty) {
         this.methodShadowsProperty = shadowEarlierProperty;
      }
   }
}
