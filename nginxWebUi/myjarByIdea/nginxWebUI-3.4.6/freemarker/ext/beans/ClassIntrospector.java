package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._JavaVersions;
import freemarker.ext.util.ModelCache;
import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ClassIntrospector {
   private static final Logger LOG = Logger.getLogger("freemarker.beans");
   private static final String JREBEL_SDK_CLASS_NAME = "org.zeroturnaround.javarebel.ClassEventListener";
   private static final String JREBEL_INTEGRATION_ERROR_MSG = "Error initializing JRebel integration. JRebel integration disabled.";
   private static final ExecutableMemberSignature GET_STRING_SIGNATURE = new ExecutableMemberSignature("get", new Class[]{String.class});
   private static final ExecutableMemberSignature GET_OBJECT_SIGNATURE = new ExecutableMemberSignature("get", new Class[]{Object.class});
   private static final ExecutableMemberSignature TO_STRING_SIGNATURE = new ExecutableMemberSignature("toString", new Class[0]);
   static final boolean DEVELOPMENT_MODE = "true".equals(SecurityUtilities.getSystemProperty("freemarker.development", "false"));
   private static final ClassChangeNotifier CLASS_CHANGE_NOTIFIER;
   private static final Object ARG_TYPES_BY_METHOD_KEY;
   static final Object CONSTRUCTORS_KEY;
   static final Object GENERIC_GET_KEY;
   static final Object TO_STRING_HIDDEN_FLAG_KEY;
   final int exposureLevel;
   final boolean exposeFields;
   final MemberAccessPolicy memberAccessPolicy;
   final MethodAppearanceFineTuner methodAppearanceFineTuner;
   final MethodSorter methodSorter;
   final boolean treatDefaultMethodsAsBeanMembers;
   final Version incompatibleImprovements;
   private final boolean hasSharedInstanceRestrictions;
   private final boolean shared;
   private final Object sharedLock;
   private final Map<Class<?>, Map<Object, Object>> cache = new ConcurrentHashMap(0, 0.75F, 16);
   private final Set<String> cacheClassNames = new HashSet(0);
   private final Set<Class<?>> classIntrospectionsInProgress = new HashSet(0);
   private final List<WeakReference<Object>> modelFactories = new LinkedList();
   private final ReferenceQueue<Object> modelFactoriesRefQueue = new ReferenceQueue();
   private int clearingCounter;

   ClassIntrospector(ClassIntrospectorBuilder builder, Object sharedLock, boolean hasSharedInstanceRestrictions, boolean shared) {
      NullArgumentException.check("sharedLock", sharedLock);
      this.exposureLevel = builder.getExposureLevel();
      this.exposeFields = builder.getExposeFields();
      this.memberAccessPolicy = builder.getMemberAccessPolicy();
      this.methodAppearanceFineTuner = builder.getMethodAppearanceFineTuner();
      this.methodSorter = builder.getMethodSorter();
      this.treatDefaultMethodsAsBeanMembers = builder.getTreatDefaultMethodsAsBeanMembers();
      this.incompatibleImprovements = builder.getIncompatibleImprovements();
      this.sharedLock = sharedLock;
      this.hasSharedInstanceRestrictions = hasSharedInstanceRestrictions;
      this.shared = shared;
      if (CLASS_CHANGE_NOTIFIER != null) {
         CLASS_CHANGE_NOTIFIER.subscribe(this);
      }

   }

   ClassIntrospectorBuilder createBuilder() {
      return new ClassIntrospectorBuilder(this);
   }

   Map<Object, Object> get(Class<?> clazz) {
      Map<Object, Object> introspData = (Map)this.cache.get(clazz);
      if (introspData != null) {
         return introspData;
      } else {
         Map introspData;
         String className;
         synchronized(this.sharedLock) {
            introspData = (Map)this.cache.get(clazz);
            if (introspData != null) {
               return introspData;
            }

            className = clazz.getName();
            if (this.cacheClassNames.contains(className)) {
               this.onSameNameClassesDetected(className);
            }

            while(introspData == null && this.classIntrospectionsInProgress.contains(clazz)) {
               try {
                  this.sharedLock.wait();
                  introspData = (Map)this.cache.get(clazz);
               } catch (InterruptedException var23) {
                  throw new RuntimeException("Class inrospection data lookup aborded: " + var23);
               }
            }

            if (introspData != null) {
               return introspData;
            }

            this.classIntrospectionsInProgress.add(clazz);
         }

         boolean var18 = false;

         try {
            var18 = true;
            Map<Object, Object> introspData = this.createClassIntrospectionData(clazz);
            synchronized(this.sharedLock) {
               this.cache.put(clazz, introspData);
               this.cacheClassNames.add(className);
            }

            introspData = introspData;
            var18 = false;
         } finally {
            if (var18) {
               synchronized(this.sharedLock) {
                  this.classIntrospectionsInProgress.remove(clazz);
                  this.sharedLock.notifyAll();
               }
            }
         }

         synchronized(this.sharedLock) {
            this.classIntrospectionsInProgress.remove(clazz);
            this.sharedLock.notifyAll();
            return introspData;
         }
      }
   }

   private Map<Object, Object> createClassIntrospectionData(Class<?> clazz) {
      Map<Object, Object> introspData = new HashMap();
      MemberAccessPolicy effMemberAccessPolicy = this.getEffectiveMemberAccessPolicy();
      ClassMemberAccessPolicy effClassMemberAccessPolicy = effMemberAccessPolicy.forClass(clazz);
      if (this.exposeFields) {
         this.addFieldsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
      }

      Map<ExecutableMemberSignature, List<Method>> accessibleMethods = discoverAccessibleMethods(clazz);
      if (!effMemberAccessPolicy.isToStringAlwaysExposed()) {
         this.addToStringHiddenFlagToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
      }

      this.addGenericGetToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
      if (this.exposureLevel != 3) {
         try {
            this.addBeanInfoToClassIntrospectionData(introspData, clazz, accessibleMethods, effClassMemberAccessPolicy);
         } catch (IntrospectionException var7) {
            LOG.warn("Couldn't properly perform introspection for class " + clazz, var7);
            introspData.clear();
         }
      }

      this.addConstructorsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
      if (introspData.size() > 1) {
         return introspData;
      } else if (introspData.size() == 0) {
         return Collections.emptyMap();
      } else {
         Map.Entry<Object, Object> e = (Map.Entry)introspData.entrySet().iterator().next();
         return Collections.singletonMap(e.getKey(), e.getValue());
      }
   }

   private void addFieldsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws SecurityException {
      Field[] fields = clazz.getFields();

      for(int i = 0; i < fields.length; ++i) {
         Field field = fields[i];
         if ((field.getModifiers() & 8) == 0 && effClassMemberAccessPolicy.isFieldExposed(field)) {
            introspData.put(field.getName(), field);
         }
      }

   }

   private void addBeanInfoToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws IntrospectionException {
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
      List<PropertyDescriptor> pdas = this.getPropertyDescriptors(beanInfo, clazz);
      int pdasLength = pdas.size();

      for(int i = pdasLength - 1; i >= 0; --i) {
         this.addPropertyDescriptorToClassIntrospectionData(introspData, (PropertyDescriptor)pdas.get(i), accessibleMethods, effClassMemberAccessPolicy);
      }

      if (this.exposureLevel < 2) {
         BeansWrapper.MethodAppearanceDecision decision = new BeansWrapper.MethodAppearanceDecision();
         BeansWrapper.MethodAppearanceDecisionInput decisionInput = null;
         List<MethodDescriptor> mds = this.getMethodDescriptors(beanInfo, clazz);
         this.sortMethodDescriptors(mds);
         int mdsSize = mds.size();
         IdentityHashMap<Method, Void> argTypesUsedByIndexerPropReaders = null;

         for(int i = mdsSize - 1; i >= 0; --i) {
            Method method = getMatchingAccessibleMethod(((MethodDescriptor)mds.get(i)).getMethod(), accessibleMethods);
            if (method != null && effClassMemberAccessPolicy.isMethodExposed(method)) {
               decision.setDefaults(method);
               if (this.methodAppearanceFineTuner != null) {
                  if (decisionInput == null) {
                     decisionInput = new BeansWrapper.MethodAppearanceDecisionInput();
                  }

                  decisionInput.setContainingClass(clazz);
                  decisionInput.setMethod(method);
                  this.methodAppearanceFineTuner.process(decisionInput, decision);
               }

               PropertyDescriptor propDesc = decision.getExposeAsProperty();
               if (propDesc != null && (decision.getReplaceExistingProperty() || !(introspData.get(propDesc.getName()) instanceof FastPropertyDescriptor))) {
                  this.addPropertyDescriptorToClassIntrospectionData(introspData, propDesc, accessibleMethods, effClassMemberAccessPolicy);
               }

               String methodKey = decision.getExposeMethodAs();
               if (methodKey != null) {
                  Object previous = introspData.get(methodKey);
                  if (previous instanceof Method) {
                     OverloadedMethods overloadedMethods = new OverloadedMethods(this.is2321Bugfixed());
                     overloadedMethods.addMethod((Method)previous);
                     overloadedMethods.addMethod(method);
                     introspData.put(methodKey, overloadedMethods);
                     if (argTypesUsedByIndexerPropReaders == null || !argTypesUsedByIndexerPropReaders.containsKey(previous)) {
                        getArgTypesByMethod(introspData).remove(previous);
                     }
                  } else if (previous instanceof OverloadedMethods) {
                     ((OverloadedMethods)previous).addMethod(method);
                  } else if (decision.getMethodShadowsProperty() || !(previous instanceof FastPropertyDescriptor)) {
                     introspData.put(methodKey, method);
                     Class<?>[] replaced = (Class[])getArgTypesByMethod(introspData).put(method, method.getParameterTypes());
                     if (replaced != null) {
                        if (argTypesUsedByIndexerPropReaders == null) {
                           argTypesUsedByIndexerPropReaders = new IdentityHashMap();
                        }

                        argTypesUsedByIndexerPropReaders.put(method, (Object)null);
                     }
                  }
               }
            }
         }
      }

   }

   private List<PropertyDescriptor> getPropertyDescriptors(BeanInfo beanInfo, Class<?> clazz) {
      PropertyDescriptor[] introspectorPDsArray = beanInfo.getPropertyDescriptors();
      List<PropertyDescriptor> introspectorPDs = introspectorPDsArray != null ? Arrays.asList(introspectorPDsArray) : Collections.emptyList();
      if (this.treatDefaultMethodsAsBeanMembers && _JavaVersions.JAVA_8 != null) {
         LinkedHashMap<String, Object> mergedPRMPs = null;
         Method[] var6 = clazz.getMethods();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Method method = var6[var8];
            if (_JavaVersions.JAVA_8.isDefaultMethod(method) && method.getReturnType() != Void.TYPE && !method.isBridge()) {
               Class<?>[] paramTypes = method.getParameterTypes();
               if (paramTypes.length == 0 || paramTypes.length == 1 && paramTypes[0] == Integer.TYPE) {
                  String propName = _MethodUtil.getBeanPropertyNameFromReaderMethodName(method.getName(), method.getReturnType());
                  if (propName != null) {
                     if (mergedPRMPs == null) {
                        mergedPRMPs = new LinkedHashMap();
                     }

                     if (paramTypes.length == 0) {
                        this.mergeInPropertyReaderMethod(mergedPRMPs, propName, method);
                     } else {
                        this.mergeInPropertyReaderMethodPair(mergedPRMPs, propName, new PropertyReaderMethodPair((Method)null, method));
                     }
                  }
               }
            }
         }

         if (mergedPRMPs == null) {
            return introspectorPDs;
         } else {
            Iterator var15 = introspectorPDs.iterator();

            while(var15.hasNext()) {
               PropertyDescriptor introspectorPD = (PropertyDescriptor)var15.next();
               this.mergeInPropertyDescriptor(mergedPRMPs, introspectorPD);
            }

            List<PropertyDescriptor> mergedPDs = new ArrayList(mergedPRMPs.size());
            Iterator var18 = mergedPRMPs.entrySet().iterator();

            while(true) {
               while(var18.hasNext()) {
                  Map.Entry<String, Object> entry = (Map.Entry)var18.next();
                  String propName = (String)entry.getKey();
                  Object propDescObj = entry.getValue();
                  if (propDescObj instanceof PropertyDescriptor) {
                     mergedPDs.add((PropertyDescriptor)propDescObj);
                  } else {
                     Method indexedReadMethod;
                     Method readMethod;
                     if (propDescObj instanceof Method) {
                        readMethod = (Method)propDescObj;
                        indexedReadMethod = null;
                     } else {
                        if (!(propDescObj instanceof PropertyReaderMethodPair)) {
                           throw new BugException();
                        }

                        PropertyReaderMethodPair prmp = (PropertyReaderMethodPair)propDescObj;
                        readMethod = prmp.readMethod;
                        indexedReadMethod = prmp.indexedReadMethod;
                        if (readMethod != null && indexedReadMethod != null && indexedReadMethod.getReturnType() != readMethod.getReturnType().getComponentType()) {
                           indexedReadMethod = null;
                        }
                     }

                     try {
                        mergedPDs.add(indexedReadMethod != null ? new IndexedPropertyDescriptor(propName, readMethod, (Method)null, indexedReadMethod, (Method)null) : new PropertyDescriptor(propName, readMethod, (Method)null));
                     } catch (IntrospectionException var14) {
                        if (LOG.isWarnEnabled()) {
                           LOG.warn("Failed creating property descriptor for " + clazz.getName() + " property " + propName, var14);
                        }
                     }
                  }
               }

               return mergedPDs;
            }
         }
      } else {
         return introspectorPDs;
      }
   }

   private void mergeInPropertyDescriptor(LinkedHashMap<String, Object> mergedPRMPs, PropertyDescriptor pd) {
      String propName = pd.getName();
      Object replaced = mergedPRMPs.put(propName, pd);
      if (replaced != null) {
         PropertyReaderMethodPair newPRMP = new PropertyReaderMethodPair(pd);
         this.putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRMP);
      }

   }

   private void mergeInPropertyReaderMethodPair(LinkedHashMap<String, Object> mergedPRMPs, String propName, PropertyReaderMethodPair newPRM) {
      Object replaced = mergedPRMPs.put(propName, newPRM);
      if (replaced != null) {
         this.putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRM);
      }

   }

   private void mergeInPropertyReaderMethod(LinkedHashMap<String, Object> mergedPRMPs, String propName, Method readerMethod) {
      Object replaced = mergedPRMPs.put(propName, readerMethod);
      if (replaced != null) {
         this.putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, new PropertyReaderMethodPair(readerMethod, (Method)null));
      }

   }

   private void putIfMergedPropertyReaderMethodPairDiffers(LinkedHashMap<String, Object> mergedPRMPs, String propName, Object replaced, PropertyReaderMethodPair newPRMP) {
      PropertyReaderMethodPair replacedPRMP = ClassIntrospector.PropertyReaderMethodPair.from(replaced);
      PropertyReaderMethodPair mergedPRMP = ClassIntrospector.PropertyReaderMethodPair.merge(replacedPRMP, newPRMP);
      if (!mergedPRMP.equals(newPRMP)) {
         mergedPRMPs.put(propName, mergedPRMP);
      }

   }

   private List<MethodDescriptor> getMethodDescriptors(BeanInfo beanInfo, Class<?> clazz) {
      MethodDescriptor[] introspectorMDArray = beanInfo.getMethodDescriptors();
      List<MethodDescriptor> introspectionMDs = introspectorMDArray != null && introspectorMDArray.length != 0 ? Arrays.asList(introspectorMDArray) : Collections.emptyList();
      if (this.treatDefaultMethodsAsBeanMembers && _JavaVersions.JAVA_8 != null) {
         Map<String, List<Method>> defaultMethodsToAddByName = null;
         Method[] var6 = clazz.getMethods();
         int var7 = var6.length;

         Method method;
         for(int var8 = 0; var8 < var7; ++var8) {
            method = var6[var8];
            if (_JavaVersions.JAVA_8.isDefaultMethod(method) && !method.isBridge()) {
               if (defaultMethodsToAddByName == null) {
                  defaultMethodsToAddByName = new HashMap();
               }

               List<Method> overloads = (List)defaultMethodsToAddByName.get(method.getName());
               if (overloads == null) {
                  overloads = new ArrayList(0);
                  defaultMethodsToAddByName.put(method.getName(), overloads);
               }

               ((List)overloads).add(method);
            }
         }

         if (defaultMethodsToAddByName == null) {
            return introspectionMDs;
         } else {
            ArrayList<MethodDescriptor> newIntrospectionMDs = new ArrayList(introspectionMDs.size() + 16);
            Iterator var13 = introspectionMDs.iterator();

            while(var13.hasNext()) {
               MethodDescriptor introspectorMD = (MethodDescriptor)var13.next();
               method = introspectorMD.getMethod();
               if (!this.containsMethodWithSameParameterTypes((List)defaultMethodsToAddByName.get(method.getName()), method)) {
                  newIntrospectionMDs.add(introspectorMD);
               }
            }

            List<MethodDescriptor> introspectionMDs = newIntrospectionMDs;
            var13 = defaultMethodsToAddByName.entrySet().iterator();

            while(var13.hasNext()) {
               Map.Entry<String, List<Method>> entry = (Map.Entry)var13.next();
               Iterator var16 = ((List)entry.getValue()).iterator();

               while(var16.hasNext()) {
                  Method method = (Method)var16.next();
                  introspectionMDs.add(new MethodDescriptor(method));
               }
            }

            return introspectionMDs;
         }
      } else {
         return introspectionMDs;
      }
   }

   private boolean containsMethodWithSameParameterTypes(List<Method> overloads, Method m) {
      if (overloads == null) {
         return false;
      } else {
         Class<?>[] paramTypes = m.getParameterTypes();
         Iterator var4 = overloads.iterator();

         Method overload;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            overload = (Method)var4.next();
         } while(!Arrays.equals(overload.getParameterTypes(), paramTypes));

         return true;
      }
   }

   private void addPropertyDescriptorToClassIntrospectionData(Map<Object, Object> introspData, PropertyDescriptor pd, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
      Method readMethod = getMatchingAccessibleMethod(pd.getReadMethod(), accessibleMethods);
      if (readMethod != null && !effClassMemberAccessPolicy.isMethodExposed(readMethod)) {
         readMethod = null;
      }

      Method indexedReadMethod;
      if (pd instanceof IndexedPropertyDescriptor) {
         indexedReadMethod = getMatchingAccessibleMethod(((IndexedPropertyDescriptor)pd).getIndexedReadMethod(), accessibleMethods);
         if (indexedReadMethod != null && !effClassMemberAccessPolicy.isMethodExposed(indexedReadMethod)) {
            indexedReadMethod = null;
         }

         if (indexedReadMethod != null) {
            getArgTypesByMethod(introspData).put(indexedReadMethod, indexedReadMethod.getParameterTypes());
         }
      } else {
         indexedReadMethod = null;
      }

      if (readMethod != null || indexedReadMethod != null) {
         introspData.put(pd.getName(), new FastPropertyDescriptor(readMethod, indexedReadMethod));
      }

   }

   private void addGenericGetToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
      Method genericGet = getFirstAccessibleMethod(GET_STRING_SIGNATURE, accessibleMethods);
      if (genericGet == null) {
         genericGet = getFirstAccessibleMethod(GET_OBJECT_SIGNATURE, accessibleMethods);
      }

      if (genericGet != null && effClassMemberAccessPolicy.isMethodExposed(genericGet)) {
         introspData.put(GENERIC_GET_KEY, genericGet);
      }

   }

   private void addToStringHiddenFlagToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
      Method toStringMethod = getFirstAccessibleMethod(TO_STRING_SIGNATURE, accessibleMethods);
      if (toStringMethod == null) {
         throw new BugException("toString() method not found");
      } else {
         if (!effClassMemberAccessPolicy.isMethodExposed(toStringMethod)) {
            introspData.put(TO_STRING_HIDDEN_FLAG_KEY, true);
         }

      }
   }

   private void addConstructorsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
      try {
         Constructor<?>[] ctorsUnfiltered = clazz.getConstructors();
         List<Constructor<?>> ctors = new ArrayList(ctorsUnfiltered.length);
         Constructor[] var6 = ctorsUnfiltered;
         int var7 = ctorsUnfiltered.length;

         Constructor ctor;
         for(int var8 = 0; var8 < var7; ++var8) {
            ctor = var6[var8];
            if (effClassMemberAccessPolicy.isConstructorExposed(ctor)) {
               ctors.add(ctor);
            }
         }

         if (!ctors.isEmpty()) {
            Object ctorsIntrospData;
            if (ctors.size() == 1) {
               Constructor<?> ctor = (Constructor)ctors.get(0);
               ctorsIntrospData = new SimpleMethod(ctor, ctor.getParameterTypes());
            } else {
               OverloadedMethods overloadedCtors = new OverloadedMethods(this.is2321Bugfixed());
               Iterator var14 = ctors.iterator();

               while(var14.hasNext()) {
                  ctor = (Constructor)var14.next();
                  overloadedCtors.addConstructor(ctor);
               }

               ctorsIntrospData = overloadedCtors;
            }

            introspData.put(CONSTRUCTORS_KEY, ctorsIntrospData);
         }
      } catch (SecurityException var10) {
         LOG.warn("Can't discover constructors for class " + clazz.getName(), var10);
      }

   }

   private static Map<ExecutableMemberSignature, List<Method>> discoverAccessibleMethods(Class<?> clazz) {
      Map<ExecutableMemberSignature, List<Method>> accessibles = new HashMap();
      discoverAccessibleMethods(clazz, accessibles);
      return accessibles;
   }

   private static void discoverAccessibleMethods(Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibles) {
      int i;
      if (Modifier.isPublic(clazz.getModifiers())) {
         try {
            Method[] methods = clazz.getMethods();

            for(i = 0; i < methods.length; ++i) {
               Method method = methods[i];
               if (Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                  ExecutableMemberSignature sig = new ExecutableMemberSignature(method);
                  List<Method> methodList = (List)accessibles.get(sig);
                  if (methodList == null) {
                     methodList = new LinkedList();
                     accessibles.put(sig, methodList);
                  }

                  ((List)methodList).add(method);
               }
            }

            return;
         } catch (SecurityException var7) {
            LOG.warn("Could not discover accessible methods of class " + clazz.getName() + ", attemping superclasses/interfaces.", var7);
         }
      }

      Class<?>[] interfaces = clazz.getInterfaces();

      for(i = 0; i < interfaces.length; ++i) {
         discoverAccessibleMethods(interfaces[i], accessibles);
      }

      Class<?> superclass = clazz.getSuperclass();
      if (superclass != null) {
         discoverAccessibleMethods(superclass, accessibles);
      }

   }

   private static Method getMatchingAccessibleMethod(Method m, Map<ExecutableMemberSignature, List<Method>> accessibles) {
      if (m == null) {
         return null;
      } else {
         List<Method> ams = (List)accessibles.get(new ExecutableMemberSignature(m));
         return ams != null ? _MethodUtil.getMethodWithClosestNonSubReturnType(m.getReturnType(), ams) : null;
      }
   }

   private static Method getFirstAccessibleMethod(ExecutableMemberSignature sig, Map<ExecutableMemberSignature, List<Method>> accessibles) {
      List<Method> ams = (List)accessibles.get(sig);
      return ams != null && !ams.isEmpty() ? (Method)ams.get(0) : null;
   }

   private void sortMethodDescriptors(List<MethodDescriptor> methodDescriptors) {
      if (this.methodSorter != null) {
         this.methodSorter.sortMethodDescriptors(methodDescriptors);
      }

   }

   MemberAccessPolicy getEffectiveMemberAccessPolicy() {
      return (MemberAccessPolicy)(this.exposureLevel < 1 ? AllowAllMemberAccessPolicy.INSTANCE : this.memberAccessPolicy);
   }

   private boolean is2321Bugfixed() {
      return BeansWrapper.is2321Bugfixed(this.incompatibleImprovements);
   }

   private static Map<Method, Class<?>[]> getArgTypesByMethod(Map<Object, Object> classInfo) {
      Map<Method, Class<?>[]> argTypes = (Map)classInfo.get(ARG_TYPES_BY_METHOD_KEY);
      if (argTypes == null) {
         argTypes = new HashMap();
         classInfo.put(ARG_TYPES_BY_METHOD_KEY, argTypes);
      }

      return (Map)argTypes;
   }

   void clearCache() {
      if (this.getHasSharedInstanceRestrictions()) {
         throw new IllegalStateException("It's not allowed to clear the whole cache in a read-only " + this.getClass().getName() + "instance. Use removeFromClassIntrospectionCache(String prefix) instead.");
      } else {
         this.forcedClearCache();
      }
   }

   private void forcedClearCache() {
      synchronized(this.sharedLock) {
         this.cache.clear();
         this.cacheClassNames.clear();
         ++this.clearingCounter;
         Iterator var2 = this.modelFactories.iterator();

         while(var2.hasNext()) {
            WeakReference<Object> regedMfREf = (WeakReference)var2.next();
            Object regedMf = regedMfREf.get();
            if (regedMf != null) {
               if (regedMf instanceof ClassBasedModelFactory) {
                  ((ClassBasedModelFactory)regedMf).clearCache();
               } else {
                  if (!(regedMf instanceof ModelCache)) {
                     throw new BugException();
                  }

                  ((ModelCache)regedMf).clearCache();
               }
            }
         }

         this.removeClearedModelFactoryReferences();
      }
   }

   void remove(Class<?> clazz) {
      synchronized(this.sharedLock) {
         this.cache.remove(clazz);
         this.cacheClassNames.remove(clazz.getName());
         ++this.clearingCounter;
         Iterator var3 = this.modelFactories.iterator();

         while(var3.hasNext()) {
            WeakReference<Object> regedMfREf = (WeakReference)var3.next();
            Object regedMf = regedMfREf.get();
            if (regedMf != null) {
               if (regedMf instanceof ClassBasedModelFactory) {
                  ((ClassBasedModelFactory)regedMf).removeFromCache(clazz);
               } else {
                  if (!(regedMf instanceof ModelCache)) {
                     throw new BugException();
                  }

                  ((ModelCache)regedMf).clearCache();
               }
            }
         }

         this.removeClearedModelFactoryReferences();
      }
   }

   int getClearingCounter() {
      synchronized(this.sharedLock) {
         return this.clearingCounter;
      }
   }

   private void onSameNameClassesDetected(String className) {
      if (LOG.isInfoEnabled()) {
         LOG.info("Detected multiple classes with the same name, \"" + className + "\". Assuming it was a class-reloading. Clearing class introspection caches to release old data.");
      }

      this.forcedClearCache();
   }

   void registerModelFactory(ClassBasedModelFactory mf) {
      this.registerModelFactory((Object)mf);
   }

   void registerModelFactory(ModelCache mf) {
      this.registerModelFactory((Object)mf);
   }

   private void registerModelFactory(Object mf) {
      synchronized(this.sharedLock) {
         this.modelFactories.add(new WeakReference(mf, this.modelFactoriesRefQueue));
         this.removeClearedModelFactoryReferences();
      }
   }

   void unregisterModelFactory(ClassBasedModelFactory mf) {
      this.unregisterModelFactory((Object)mf);
   }

   void unregisterModelFactory(ModelCache mf) {
      this.unregisterModelFactory((Object)mf);
   }

   void unregisterModelFactory(Object mf) {
      synchronized(this.sharedLock) {
         Iterator<WeakReference<Object>> it = this.modelFactories.iterator();

         while(it.hasNext()) {
            Object regedMf = ((WeakReference)it.next()).get();
            if (regedMf == mf) {
               it.remove();
            }
         }

      }
   }

   private void removeClearedModelFactoryReferences() {
      Reference cleardRef;
      while((cleardRef = this.modelFactoriesRefQueue.poll()) != null) {
         synchronized(this.sharedLock) {
            Iterator<WeakReference<Object>> it = this.modelFactories.iterator();

            while(it.hasNext()) {
               if (it.next() == cleardRef) {
                  it.remove();
                  break;
               }
            }
         }
      }

   }

   static Class<?>[] getArgTypes(Map<Object, Object> classInfo, Method method) {
      Map<Method, Class<?>[]> argTypesByMethod = (Map)classInfo.get(ARG_TYPES_BY_METHOD_KEY);
      return (Class[])argTypesByMethod.get(method);
   }

   int keyCount(Class<?> clazz) {
      Map<Object, Object> map = this.get(clazz);
      int count = map.size();
      if (map.containsKey(CONSTRUCTORS_KEY)) {
         --count;
      }

      if (map.containsKey(GENERIC_GET_KEY)) {
         --count;
      }

      if (map.containsKey(ARG_TYPES_BY_METHOD_KEY)) {
         --count;
      }

      return count;
   }

   Set<Object> keySet(Class<?> clazz) {
      Set<Object> set = new HashSet(this.get(clazz).keySet());
      set.remove(CONSTRUCTORS_KEY);
      set.remove(GENERIC_GET_KEY);
      set.remove(ARG_TYPES_BY_METHOD_KEY);
      return set;
   }

   int getExposureLevel() {
      return this.exposureLevel;
   }

   boolean getExposeFields() {
      return this.exposeFields;
   }

   MemberAccessPolicy getMemberAccessPolicy() {
      return this.memberAccessPolicy;
   }

   boolean getTreatDefaultMethodsAsBeanMembers() {
      return this.treatDefaultMethodsAsBeanMembers;
   }

   MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
      return this.methodAppearanceFineTuner;
   }

   MethodSorter getMethodSorter() {
      return this.methodSorter;
   }

   boolean getHasSharedInstanceRestrictions() {
      return this.hasSharedInstanceRestrictions;
   }

   boolean isShared() {
      return this.shared;
   }

   Object getSharedLock() {
      return this.sharedLock;
   }

   Object[] getRegisteredModelFactoriesSnapshot() {
      synchronized(this.sharedLock) {
         return this.modelFactories.toArray();
      }
   }

   static {
      boolean jRebelAvailable;
      try {
         Class.forName("org.zeroturnaround.javarebel.ClassEventListener");
         jRebelAvailable = true;
      } catch (Throwable var7) {
         Throwable e = var7;
         jRebelAvailable = false;

         try {
            if (!(e instanceof ClassNotFoundException)) {
               LOG.error("Error initializing JRebel integration. JRebel integration disabled.", e);
            }
         } catch (Throwable var6) {
         }
      }

      ClassChangeNotifier classChangeNotifier;
      if (jRebelAvailable) {
         try {
            classChangeNotifier = (ClassChangeNotifier)Class.forName("freemarker.ext.beans.JRebelClassChangeNotifier").newInstance();
         } catch (Throwable var5) {
            Throwable e = var5;
            classChangeNotifier = null;

            try {
               LOG.error("Error initializing JRebel integration. JRebel integration disabled.", e);
            } catch (Throwable var4) {
            }
         }
      } else {
         classChangeNotifier = null;
      }

      CLASS_CHANGE_NOTIFIER = classChangeNotifier;
      ARG_TYPES_BY_METHOD_KEY = new Object();
      CONSTRUCTORS_KEY = new Object();
      GENERIC_GET_KEY = new Object();
      TO_STRING_HIDDEN_FLAG_KEY = new Object();
   }

   private static class PropertyReaderMethodPair {
      private final Method readMethod;
      private final Method indexedReadMethod;

      PropertyReaderMethodPair(Method readerMethod, Method indexedReaderMethod) {
         this.readMethod = readerMethod;
         this.indexedReadMethod = indexedReaderMethod;
      }

      PropertyReaderMethodPair(PropertyDescriptor pd) {
         this(pd.getReadMethod(), pd instanceof IndexedPropertyDescriptor ? ((IndexedPropertyDescriptor)pd).getIndexedReadMethod() : null);
      }

      static PropertyReaderMethodPair from(Object obj) {
         if (obj instanceof PropertyReaderMethodPair) {
            return (PropertyReaderMethodPair)obj;
         } else if (obj instanceof PropertyDescriptor) {
            return new PropertyReaderMethodPair((PropertyDescriptor)obj);
         } else if (obj instanceof Method) {
            return new PropertyReaderMethodPair((Method)obj, (Method)null);
         } else {
            throw new BugException("Unexpected obj type: " + obj.getClass().getName());
         }
      }

      static PropertyReaderMethodPair merge(PropertyReaderMethodPair oldMethods, PropertyReaderMethodPair newMethods) {
         return new PropertyReaderMethodPair(newMethods.readMethod != null ? newMethods.readMethod : oldMethods.readMethod, newMethods.indexedReadMethod != null ? newMethods.indexedReadMethod : oldMethods.indexedReadMethod);
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         result = 31 * result + (this.indexedReadMethod == null ? 0 : this.indexedReadMethod.hashCode());
         result = 31 * result + (this.readMethod == null ? 0 : this.readMethod.hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            PropertyReaderMethodPair other = (PropertyReaderMethodPair)obj;
            return other.readMethod == this.readMethod && other.indexedReadMethod == this.indexedReadMethod;
         }
      }
   }
}
