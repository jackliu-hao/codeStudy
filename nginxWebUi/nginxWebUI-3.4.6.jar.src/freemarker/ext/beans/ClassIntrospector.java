/*      */ package freemarker.ext.beans;
/*      */ 
/*      */ import freemarker.core.BugException;
/*      */ import freemarker.core._JavaVersions;
/*      */ import freemarker.ext.util.ModelCache;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.Version;
/*      */ import freemarker.template.utility.NullArgumentException;
/*      */ import freemarker.template.utility.SecurityUtilities;
/*      */ import java.beans.BeanInfo;
/*      */ import java.beans.IndexedPropertyDescriptor;
/*      */ import java.beans.IntrospectionException;
/*      */ import java.beans.Introspector;
/*      */ import java.beans.MethodDescriptor;
/*      */ import java.beans.PropertyDescriptor;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class ClassIntrospector
/*      */ {
/*   75 */   private static final Logger LOG = Logger.getLogger("freemarker.beans");
/*      */   
/*      */   private static final String JREBEL_SDK_CLASS_NAME = "org.zeroturnaround.javarebel.ClassEventListener";
/*      */   
/*      */   private static final String JREBEL_INTEGRATION_ERROR_MSG = "Error initializing JRebel integration. JRebel integration disabled.";
/*      */   
/*   81 */   private static final ExecutableMemberSignature GET_STRING_SIGNATURE = new ExecutableMemberSignature("get", new Class[] { String.class });
/*      */   
/*   83 */   private static final ExecutableMemberSignature GET_OBJECT_SIGNATURE = new ExecutableMemberSignature("get", new Class[] { Object.class });
/*      */   
/*   85 */   private static final ExecutableMemberSignature TO_STRING_SIGNATURE = new ExecutableMemberSignature("toString", new Class[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   92 */   static final boolean DEVELOPMENT_MODE = "true".equals(SecurityUtilities.getSystemProperty("freemarker.development", "false"));
/*      */   
/*      */   private static final ClassChangeNotifier CLASS_CHANGE_NOTIFIER;
/*      */ 
/*      */   
/*      */   static {
/*      */     try {
/*   99 */       Class.forName("org.zeroturnaround.javarebel.ClassEventListener");
/*  100 */       jRebelAvailable = true;
/*  101 */     } catch (Throwable e) {
/*  102 */       jRebelAvailable = false;
/*      */       try {
/*  104 */         if (!(e instanceof ClassNotFoundException)) {
/*  105 */           LOG.error("Error initializing JRebel integration. JRebel integration disabled.", e);
/*      */         }
/*  107 */       } catch (Throwable throwable) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  113 */     if (jRebelAvailable) {
/*      */       
/*      */       try {
/*  116 */         classChangeNotifier = (ClassChangeNotifier)Class.forName("freemarker.ext.beans.JRebelClassChangeNotifier").newInstance();
/*  117 */       } catch (Throwable e) {
/*  118 */         classChangeNotifier = null;
/*      */         try {
/*  120 */           LOG.error("Error initializing JRebel integration. JRebel integration disabled.", e);
/*  121 */         } catch (Throwable throwable) {}
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  126 */       classChangeNotifier = null;
/*      */     } 
/*      */     
/*  129 */     CLASS_CHANGE_NOTIFIER = classChangeNotifier;
/*      */   }
/*      */   static {
/*      */     boolean jRebelAvailable;
/*      */     ClassChangeNotifier classChangeNotifier;
/*      */   }
/*      */   
/*  136 */   private static final Object ARG_TYPES_BY_METHOD_KEY = new Object();
/*      */   
/*  138 */   static final Object CONSTRUCTORS_KEY = new Object();
/*      */   
/*  140 */   static final Object GENERIC_GET_KEY = new Object();
/*      */   
/*  142 */   static final Object TO_STRING_HIDDEN_FLAG_KEY = new Object();
/*      */ 
/*      */   
/*      */   final int exposureLevel;
/*      */ 
/*      */   
/*      */   final boolean exposeFields;
/*      */ 
/*      */   
/*      */   final MemberAccessPolicy memberAccessPolicy;
/*      */   
/*      */   final MethodAppearanceFineTuner methodAppearanceFineTuner;
/*      */   
/*      */   final MethodSorter methodSorter;
/*      */   
/*      */   final boolean treatDefaultMethodsAsBeanMembers;
/*      */   
/*      */   final Version incompatibleImprovements;
/*      */   
/*      */   private final boolean hasSharedInstanceRestrictions;
/*      */   
/*      */   private final boolean shared;
/*      */   
/*      */   private final Object sharedLock;
/*      */   
/*  167 */   private final Map<Class<?>, Map<Object, Object>> cache = new ConcurrentHashMap<>(0, 0.75F, 16);
/*      */   
/*  169 */   private final Set<String> cacheClassNames = new HashSet<>(0);
/*  170 */   private final Set<Class<?>> classIntrospectionsInProgress = new HashSet<>(0);
/*      */   
/*  172 */   private final List<WeakReference<Object>> modelFactories = new LinkedList<>();
/*      */   
/*  174 */   private final ReferenceQueue<Object> modelFactoriesRefQueue = new ReferenceQueue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int clearingCounter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   ClassIntrospector(ClassIntrospectorBuilder builder, Object sharedLock, boolean hasSharedInstanceRestrictions, boolean shared) {
/*  188 */     NullArgumentException.check("sharedLock", sharedLock);
/*      */     
/*  190 */     this.exposureLevel = builder.getExposureLevel();
/*  191 */     this.exposeFields = builder.getExposeFields();
/*  192 */     this.memberAccessPolicy = builder.getMemberAccessPolicy();
/*  193 */     this.methodAppearanceFineTuner = builder.getMethodAppearanceFineTuner();
/*  194 */     this.methodSorter = builder.getMethodSorter();
/*  195 */     this.treatDefaultMethodsAsBeanMembers = builder.getTreatDefaultMethodsAsBeanMembers();
/*  196 */     this.incompatibleImprovements = builder.getIncompatibleImprovements();
/*      */     
/*  198 */     this.sharedLock = sharedLock;
/*      */     
/*  200 */     this.hasSharedInstanceRestrictions = hasSharedInstanceRestrictions;
/*  201 */     this.shared = shared;
/*      */     
/*  203 */     if (CLASS_CHANGE_NOTIFIER != null) {
/*  204 */       CLASS_CHANGE_NOTIFIER.subscribe(this);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   ClassIntrospectorBuilder createBuilder() {
/*  214 */     return new ClassIntrospectorBuilder(this);
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
/*      */   Map<Object, Object> get(Class<?> clazz) {
/*      */     String className;
/*  229 */     Map<Object, Object> introspData = this.cache.get(clazz);
/*  230 */     if (introspData != null) return introspData;
/*      */ 
/*      */ 
/*      */     
/*  234 */     synchronized (this.sharedLock) {
/*  235 */       Map<Object, Object> map = this.cache.get(clazz);
/*  236 */       if (map != null) return map;
/*      */       
/*  238 */       className = clazz.getName();
/*  239 */       if (this.cacheClassNames.contains(className)) {
/*  240 */         onSameNameClassesDetected(className);
/*      */       }
/*      */       
/*  243 */       while (map == null && this.classIntrospectionsInProgress.contains(clazz)) {
/*      */ 
/*      */         
/*      */         try {
/*  247 */           this.sharedLock.wait();
/*  248 */           map = this.cache.get(clazz);
/*  249 */         } catch (InterruptedException e) {
/*  250 */           throw new RuntimeException("Class inrospection data lookup aborded: " + e);
/*      */         } 
/*      */       } 
/*      */       
/*  254 */       if (map != null) return map;
/*      */ 
/*      */       
/*  257 */       this.classIntrospectionsInProgress.add(clazz);
/*      */     } 
/*      */     try {
/*  260 */       Map<Object, Object> map = createClassIntrospectionData(clazz);
/*  261 */       synchronized (this.sharedLock) {
/*  262 */         this.cache.put(clazz, map);
/*  263 */         this.cacheClassNames.add(className);
/*      */       } 
/*  265 */       return map;
/*      */     } finally {
/*  267 */       synchronized (this.sharedLock) {
/*  268 */         this.classIntrospectionsInProgress.remove(clazz);
/*  269 */         this.sharedLock.notifyAll();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<Object, Object> createClassIntrospectionData(Class<?> clazz) {
/*  278 */     Map<Object, Object> introspData = new HashMap<>();
/*  279 */     MemberAccessPolicy effMemberAccessPolicy = getEffectiveMemberAccessPolicy();
/*  280 */     ClassMemberAccessPolicy effClassMemberAccessPolicy = effMemberAccessPolicy.forClass(clazz);
/*      */     
/*  282 */     if (this.exposeFields) {
/*  283 */       addFieldsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
/*      */     }
/*      */     
/*  286 */     Map<ExecutableMemberSignature, List<Method>> accessibleMethods = discoverAccessibleMethods(clazz);
/*      */     
/*  288 */     if (!effMemberAccessPolicy.isToStringAlwaysExposed()) {
/*  289 */       addToStringHiddenFlagToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
/*      */     }
/*      */     
/*  292 */     addGenericGetToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
/*      */     
/*  294 */     if (this.exposureLevel != 3) {
/*      */       try {
/*  296 */         addBeanInfoToClassIntrospectionData(introspData, clazz, accessibleMethods, effClassMemberAccessPolicy);
/*  297 */       } catch (IntrospectionException introspectionException) {
/*  298 */         LOG.warn("Couldn't properly perform introspection for class " + clazz, introspectionException);
/*  299 */         introspData.clear();
/*      */       } 
/*      */     }
/*      */     
/*  303 */     addConstructorsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
/*      */     
/*  305 */     if (introspData.size() > 1)
/*  306 */       return introspData; 
/*  307 */     if (introspData.size() == 0) {
/*  308 */       return Collections.emptyMap();
/*      */     }
/*  310 */     Map.Entry<Object, Object> e = introspData.entrySet().iterator().next();
/*  311 */     return Collections.singletonMap(e.getKey(), e.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addFieldsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws SecurityException {
/*  317 */     Field[] fields = clazz.getFields();
/*  318 */     for (int i = 0; i < fields.length; i++) {
/*  319 */       Field field = fields[i];
/*  320 */       if ((field.getModifiers() & 0x8) == 0 && 
/*  321 */         effClassMemberAccessPolicy.isFieldExposed(field)) {
/*  322 */         introspData.put(field.getName(), field);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addBeanInfoToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws IntrospectionException {
/*  332 */     BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
/*  333 */     List<PropertyDescriptor> pdas = getPropertyDescriptors(beanInfo, clazz);
/*  334 */     int pdasLength = pdas.size();
/*      */     
/*  336 */     for (int i = pdasLength - 1; i >= 0; i--) {
/*  337 */       addPropertyDescriptorToClassIntrospectionData(introspData, pdas
/*  338 */           .get(i), accessibleMethods, effClassMemberAccessPolicy);
/*      */     }
/*      */ 
/*      */     
/*  342 */     if (this.exposureLevel < 2) {
/*  343 */       BeansWrapper.MethodAppearanceDecision decision = new BeansWrapper.MethodAppearanceDecision();
/*  344 */       BeansWrapper.MethodAppearanceDecisionInput decisionInput = null;
/*  345 */       List<MethodDescriptor> mds = getMethodDescriptors(beanInfo, clazz);
/*  346 */       sortMethodDescriptors(mds);
/*  347 */       int mdsSize = mds.size();
/*  348 */       IdentityHashMap<Method, Void> argTypesUsedByIndexerPropReaders = null;
/*  349 */       for (int j = mdsSize - 1; j >= 0; j--) {
/*  350 */         Method method = getMatchingAccessibleMethod(((MethodDescriptor)mds.get(j)).getMethod(), accessibleMethods);
/*  351 */         if (method != null && effClassMemberAccessPolicy.isMethodExposed(method)) {
/*  352 */           decision.setDefaults(method);
/*  353 */           if (this.methodAppearanceFineTuner != null) {
/*  354 */             if (decisionInput == null) {
/*  355 */               decisionInput = new BeansWrapper.MethodAppearanceDecisionInput();
/*      */             }
/*  357 */             decisionInput.setContainingClass(clazz);
/*  358 */             decisionInput.setMethod(method);
/*      */             
/*  360 */             this.methodAppearanceFineTuner.process(decisionInput, decision);
/*      */           } 
/*      */           
/*  363 */           PropertyDescriptor propDesc = decision.getExposeAsProperty();
/*  364 */           if (propDesc != null && (decision
/*  365 */             .getReplaceExistingProperty() || 
/*  366 */             !(introspData.get(propDesc.getName()) instanceof FastPropertyDescriptor))) {
/*  367 */             addPropertyDescriptorToClassIntrospectionData(introspData, propDesc, accessibleMethods, effClassMemberAccessPolicy);
/*      */           }
/*      */ 
/*      */           
/*  371 */           String methodKey = decision.getExposeMethodAs();
/*  372 */           if (methodKey != null) {
/*  373 */             Object previous = introspData.get(methodKey);
/*  374 */             if (previous instanceof Method) {
/*      */ 
/*      */               
/*  377 */               OverloadedMethods overloadedMethods = new OverloadedMethods(is2321Bugfixed());
/*  378 */               overloadedMethods.addMethod((Method)previous);
/*  379 */               overloadedMethods.addMethod(method);
/*  380 */               introspData.put(methodKey, overloadedMethods);
/*      */               
/*  382 */               if (argTypesUsedByIndexerPropReaders == null || 
/*  383 */                 !argTypesUsedByIndexerPropReaders.containsKey(previous)) {
/*  384 */                 getArgTypesByMethod(introspData).remove(previous);
/*      */               }
/*  386 */             } else if (previous instanceof OverloadedMethods) {
/*      */               
/*  388 */               ((OverloadedMethods)previous).addMethod(method);
/*  389 */             } else if (decision.getMethodShadowsProperty() || !(previous instanceof FastPropertyDescriptor)) {
/*      */ 
/*      */               
/*  392 */               introspData.put(methodKey, method);
/*  393 */               Class<?>[] replaced = getArgTypesByMethod(introspData).put(method, method
/*  394 */                   .getParameterTypes());
/*  395 */               if (replaced != null) {
/*  396 */                 if (argTypesUsedByIndexerPropReaders == null) {
/*  397 */                   argTypesUsedByIndexerPropReaders = new IdentityHashMap<>();
/*      */                 }
/*  399 */                 argTypesUsedByIndexerPropReaders.put(method, null);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<PropertyDescriptor> getPropertyDescriptors(BeanInfo beanInfo, Class<?> clazz) {
/*  412 */     PropertyDescriptor[] introspectorPDsArray = beanInfo.getPropertyDescriptors();
/*      */     
/*  414 */     List<PropertyDescriptor> introspectorPDs = (introspectorPDsArray != null) ? Arrays.<PropertyDescriptor>asList(introspectorPDsArray) : Collections.<PropertyDescriptor>emptyList();
/*      */     
/*  416 */     if (!this.treatDefaultMethodsAsBeanMembers || _JavaVersions.JAVA_8 == null)
/*      */     {
/*  418 */       return introspectorPDs;
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
/*  431 */     LinkedHashMap<String, Object> mergedPRMPs = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  436 */     for (Method method : clazz.getMethods()) {
/*  437 */       if (_JavaVersions.JAVA_8.isDefaultMethod(method) && method.getReturnType() != void.class && 
/*  438 */         !method.isBridge()) {
/*  439 */         Class<?>[] paramTypes = method.getParameterTypes();
/*  440 */         if (paramTypes.length == 0 || (paramTypes.length == 1 && paramTypes[0] == int.class)) {
/*      */           
/*  442 */           String propName = _MethodUtil.getBeanPropertyNameFromReaderMethodName(method
/*  443 */               .getName(), method.getReturnType());
/*  444 */           if (propName != null) {
/*  445 */             if (mergedPRMPs == null)
/*      */             {
/*  447 */               mergedPRMPs = new LinkedHashMap<>();
/*      */             }
/*  449 */             if (paramTypes.length == 0) {
/*  450 */               mergeInPropertyReaderMethod(mergedPRMPs, propName, method);
/*      */             } else {
/*  452 */               mergeInPropertyReaderMethodPair(mergedPRMPs, propName, new PropertyReaderMethodPair(null, method));
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  460 */     if (mergedPRMPs == null)
/*      */     {
/*  462 */       return introspectorPDs;
/*      */     }
/*      */     
/*  465 */     for (PropertyDescriptor introspectorPD : introspectorPDs) {
/*  466 */       mergeInPropertyDescriptor(mergedPRMPs, introspectorPD);
/*      */     }
/*      */ 
/*      */     
/*  470 */     List<PropertyDescriptor> mergedPDs = new ArrayList<>(mergedPRMPs.size());
/*  471 */     for (Map.Entry<String, Object> entry : mergedPRMPs.entrySet()) {
/*  472 */       Method readMethod, indexedReadMethod; String propName = entry.getKey();
/*  473 */       Object propDescObj = entry.getValue();
/*  474 */       if (propDescObj instanceof PropertyDescriptor) {
/*  475 */         mergedPDs.add((PropertyDescriptor)propDescObj);
/*      */         
/*      */         continue;
/*      */       } 
/*  479 */       if (propDescObj instanceof Method) {
/*  480 */         readMethod = (Method)propDescObj;
/*  481 */         indexedReadMethod = null;
/*  482 */       } else if (propDescObj instanceof PropertyReaderMethodPair) {
/*  483 */         PropertyReaderMethodPair prmp = (PropertyReaderMethodPair)propDescObj;
/*  484 */         readMethod = prmp.readMethod;
/*  485 */         indexedReadMethod = prmp.indexedReadMethod;
/*  486 */         if (readMethod != null && indexedReadMethod != null && indexedReadMethod
/*  487 */           .getReturnType() != readMethod.getReturnType().getComponentType())
/*      */         {
/*      */           
/*  490 */           indexedReadMethod = null;
/*      */         }
/*      */       } else {
/*  493 */         throw new BugException();
/*      */       } 
/*      */       try {
/*  496 */         mergedPDs.add((indexedReadMethod != null) ? new IndexedPropertyDescriptor(propName, readMethod, null, indexedReadMethod, null) : new PropertyDescriptor(propName, readMethod, null));
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  501 */       catch (IntrospectionException e) {
/*  502 */         if (LOG.isWarnEnabled()) {
/*  503 */           LOG.warn("Failed creating property descriptor for " + clazz.getName() + " property " + propName, e);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  509 */     return mergedPDs;
/*      */   }
/*      */   
/*      */   private static class PropertyReaderMethodPair {
/*      */     private final Method readMethod;
/*      */     private final Method indexedReadMethod;
/*      */     
/*      */     PropertyReaderMethodPair(Method readerMethod, Method indexedReaderMethod) {
/*  517 */       this.readMethod = readerMethod;
/*  518 */       this.indexedReadMethod = indexedReaderMethod;
/*      */     }
/*      */     
/*      */     PropertyReaderMethodPair(PropertyDescriptor pd) {
/*  522 */       this(pd
/*  523 */           .getReadMethod(), (pd instanceof IndexedPropertyDescriptor) ? ((IndexedPropertyDescriptor)pd)
/*      */           
/*  525 */           .getIndexedReadMethod() : null);
/*      */     }
/*      */     
/*      */     static PropertyReaderMethodPair from(Object obj) {
/*  529 */       if (obj instanceof PropertyReaderMethodPair)
/*  530 */         return (PropertyReaderMethodPair)obj; 
/*  531 */       if (obj instanceof PropertyDescriptor)
/*  532 */         return new PropertyReaderMethodPair((PropertyDescriptor)obj); 
/*  533 */       if (obj instanceof Method) {
/*  534 */         return new PropertyReaderMethodPair((Method)obj, null);
/*      */       }
/*  536 */       throw new BugException("Unexpected obj type: " + obj.getClass().getName());
/*      */     }
/*      */ 
/*      */     
/*      */     static PropertyReaderMethodPair merge(PropertyReaderMethodPair oldMethods, PropertyReaderMethodPair newMethods) {
/*  541 */       return new PropertyReaderMethodPair((newMethods.readMethod != null) ? newMethods.readMethod : oldMethods.readMethod, (newMethods.indexedReadMethod != null) ? newMethods.indexedReadMethod : oldMethods.indexedReadMethod);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  549 */       int prime = 31;
/*  550 */       int result = 1;
/*  551 */       result = 31 * result + ((this.indexedReadMethod == null) ? 0 : this.indexedReadMethod.hashCode());
/*  552 */       result = 31 * result + ((this.readMethod == null) ? 0 : this.readMethod.hashCode());
/*  553 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  558 */       if (this == obj) return true; 
/*  559 */       if (obj == null) return false; 
/*  560 */       if (getClass() != obj.getClass()) return false; 
/*  561 */       PropertyReaderMethodPair other = (PropertyReaderMethodPair)obj;
/*  562 */       return (other.readMethod == this.readMethod && other.indexedReadMethod == this.indexedReadMethod);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void mergeInPropertyDescriptor(LinkedHashMap<String, Object> mergedPRMPs, PropertyDescriptor pd) {
/*  568 */     String propName = pd.getName();
/*  569 */     Object replaced = mergedPRMPs.put(propName, pd);
/*  570 */     if (replaced != null) {
/*  571 */       PropertyReaderMethodPair newPRMP = new PropertyReaderMethodPair(pd);
/*  572 */       putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRMP);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void mergeInPropertyReaderMethodPair(LinkedHashMap<String, Object> mergedPRMPs, String propName, PropertyReaderMethodPair newPRM) {
/*  578 */     Object replaced = mergedPRMPs.put(propName, newPRM);
/*  579 */     if (replaced != null) {
/*  580 */       putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRM);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void mergeInPropertyReaderMethod(LinkedHashMap<String, Object> mergedPRMPs, String propName, Method readerMethod) {
/*  586 */     Object replaced = mergedPRMPs.put(propName, readerMethod);
/*  587 */     if (replaced != null) {
/*  588 */       putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, new PropertyReaderMethodPair(readerMethod, null));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void putIfMergedPropertyReaderMethodPairDiffers(LinkedHashMap<String, Object> mergedPRMPs, String propName, Object replaced, PropertyReaderMethodPair newPRMP) {
/*  595 */     PropertyReaderMethodPair replacedPRMP = PropertyReaderMethodPair.from(replaced);
/*  596 */     PropertyReaderMethodPair mergedPRMP = PropertyReaderMethodPair.merge(replacedPRMP, newPRMP);
/*  597 */     if (!mergedPRMP.equals(newPRMP)) {
/*  598 */       mergedPRMPs.put(propName, mergedPRMP);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<MethodDescriptor> getMethodDescriptors(BeanInfo beanInfo, Class<?> clazz) {
/*  606 */     MethodDescriptor[] introspectorMDArray = beanInfo.getMethodDescriptors();
/*      */     
/*  608 */     List<MethodDescriptor> introspectionMDs = (introspectorMDArray != null && introspectorMDArray.length != 0) ? Arrays.<MethodDescriptor>asList(introspectorMDArray) : Collections.<MethodDescriptor>emptyList();
/*      */     
/*  610 */     if (!this.treatDefaultMethodsAsBeanMembers || _JavaVersions.JAVA_8 == null)
/*      */     {
/*  612 */       return introspectionMDs;
/*      */     }
/*      */     
/*  615 */     Map<String, List<Method>> defaultMethodsToAddByName = null;
/*  616 */     for (Method method : clazz.getMethods()) {
/*  617 */       if (_JavaVersions.JAVA_8.isDefaultMethod(method) && !method.isBridge()) {
/*  618 */         if (defaultMethodsToAddByName == null) {
/*  619 */           defaultMethodsToAddByName = new HashMap<>();
/*      */         }
/*  621 */         List<Method> overloads = defaultMethodsToAddByName.get(method.getName());
/*  622 */         if (overloads == null) {
/*  623 */           overloads = new ArrayList<>(0);
/*  624 */           defaultMethodsToAddByName.put(method.getName(), overloads);
/*      */         } 
/*  626 */         overloads.add(method);
/*      */       } 
/*      */     } 
/*      */     
/*  630 */     if (defaultMethodsToAddByName == null)
/*      */     {
/*  632 */       return introspectionMDs;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  637 */     ArrayList<MethodDescriptor> newIntrospectionMDs = new ArrayList<>(introspectionMDs.size() + 16);
/*  638 */     for (MethodDescriptor introspectorMD : introspectionMDs) {
/*  639 */       Method introspectorM = introspectorMD.getMethod();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  645 */       if (!containsMethodWithSameParameterTypes(defaultMethodsToAddByName
/*  646 */           .get(introspectorM.getName()), introspectorM)) {
/*  647 */         newIntrospectionMDs.add(introspectorMD);
/*      */       }
/*      */     } 
/*  650 */     introspectionMDs = newIntrospectionMDs;
/*      */ 
/*      */     
/*  653 */     for (Map.Entry<String, List<Method>> entry : defaultMethodsToAddByName.entrySet()) {
/*  654 */       for (Method method : entry.getValue()) {
/*  655 */         introspectionMDs.add(new MethodDescriptor(method));
/*      */       }
/*      */     } 
/*      */     
/*  659 */     return introspectionMDs;
/*      */   }
/*      */   
/*      */   private boolean containsMethodWithSameParameterTypes(List<Method> overloads, Method m) {
/*  663 */     if (overloads == null) {
/*  664 */       return false;
/*      */     }
/*      */     
/*  667 */     Class<?>[] paramTypes = m.getParameterTypes();
/*  668 */     for (Method overload : overloads) {
/*  669 */       if (Arrays.equals((Object[])overload.getParameterTypes(), (Object[])paramTypes)) {
/*  670 */         return true;
/*      */       }
/*      */     } 
/*  673 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addPropertyDescriptorToClassIntrospectionData(Map<Object, Object> introspData, PropertyDescriptor pd, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
/*  680 */     Method indexedReadMethod, readMethod = getMatchingAccessibleMethod(pd.getReadMethod(), accessibleMethods);
/*  681 */     if (readMethod != null && !effClassMemberAccessPolicy.isMethodExposed(readMethod)) {
/*  682 */       readMethod = null;
/*      */     }
/*      */ 
/*      */     
/*  686 */     if (pd instanceof IndexedPropertyDescriptor) {
/*  687 */       indexedReadMethod = getMatchingAccessibleMethod(((IndexedPropertyDescriptor)pd)
/*  688 */           .getIndexedReadMethod(), accessibleMethods);
/*  689 */       if (indexedReadMethod != null && !effClassMemberAccessPolicy.isMethodExposed(indexedReadMethod)) {
/*  690 */         indexedReadMethod = null;
/*      */       }
/*  692 */       if (indexedReadMethod != null) {
/*  693 */         getArgTypesByMethod(introspData).put(indexedReadMethod, indexedReadMethod
/*  694 */             .getParameterTypes());
/*      */       }
/*      */     } else {
/*  697 */       indexedReadMethod = null;
/*      */     } 
/*      */     
/*  700 */     if (readMethod != null || indexedReadMethod != null) {
/*  701 */       introspData.put(pd.getName(), new FastPropertyDescriptor(readMethod, indexedReadMethod));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addGenericGetToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
/*  708 */     Method genericGet = getFirstAccessibleMethod(GET_STRING_SIGNATURE, accessibleMethods);
/*  709 */     if (genericGet == null) {
/*  710 */       genericGet = getFirstAccessibleMethod(GET_OBJECT_SIGNATURE, accessibleMethods);
/*      */     }
/*  712 */     if (genericGet != null && effClassMemberAccessPolicy.isMethodExposed(genericGet)) {
/*  713 */       introspData.put(GENERIC_GET_KEY, genericGet);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addToStringHiddenFlagToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
/*  720 */     Method toStringMethod = getFirstAccessibleMethod(TO_STRING_SIGNATURE, accessibleMethods);
/*  721 */     if (toStringMethod == null) {
/*  722 */       throw new BugException("toString() method not found");
/*      */     }
/*      */     
/*  725 */     if (!effClassMemberAccessPolicy.isMethodExposed(toStringMethod)) {
/*  726 */       introspData.put(TO_STRING_HIDDEN_FLAG_KEY, Boolean.valueOf(true));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void addConstructorsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
/*      */     try {
/*  733 */       Constructor[] arrayOfConstructor = (Constructor[])clazz.getConstructors();
/*  734 */       List<Constructor<?>> ctors = new ArrayList<>(arrayOfConstructor.length);
/*  735 */       for (Constructor<?> ctor : arrayOfConstructor) {
/*  736 */         if (effClassMemberAccessPolicy.isConstructorExposed(ctor)) {
/*  737 */           ctors.add(ctor);
/*      */         }
/*      */       } 
/*      */       
/*  741 */       if (!ctors.isEmpty()) {
/*      */         Object ctorsIntrospData;
/*  743 */         if (ctors.size() == 1) {
/*  744 */           Constructor<?> ctor = ctors.get(0);
/*  745 */           ctorsIntrospData = new SimpleMethod(ctor, ctor.getParameterTypes());
/*      */         } else {
/*  747 */           OverloadedMethods overloadedCtors = new OverloadedMethods(is2321Bugfixed());
/*  748 */           for (Constructor<?> ctor : ctors) {
/*  749 */             overloadedCtors.addConstructor(ctor);
/*      */           }
/*  751 */           ctorsIntrospData = overloadedCtors;
/*      */         } 
/*  753 */         introspData.put(CONSTRUCTORS_KEY, ctorsIntrospData);
/*      */       } 
/*  755 */     } catch (SecurityException e) {
/*  756 */       LOG.warn("Can't discover constructors for class " + clazz.getName(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Map<ExecutableMemberSignature, List<Method>> discoverAccessibleMethods(Class<?> clazz) {
/*  766 */     Map<ExecutableMemberSignature, List<Method>> accessibles = new HashMap<>();
/*  767 */     discoverAccessibleMethods(clazz, accessibles);
/*  768 */     return accessibles;
/*      */   }
/*      */ 
/*      */   
/*      */   private static void discoverAccessibleMethods(Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibles) {
/*  773 */     if (Modifier.isPublic(clazz.getModifiers())) {
/*      */       try {
/*  775 */         Method[] methods = clazz.getMethods();
/*  776 */         for (int j = 0; j < methods.length; j++) {
/*  777 */           Method method = methods[j];
/*  778 */           if (Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
/*  779 */             ExecutableMemberSignature sig = new ExecutableMemberSignature(method);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  791 */             List<Method> methodList = accessibles.get(sig);
/*  792 */             if (methodList == null) {
/*      */               
/*  794 */               methodList = new LinkedList<>();
/*  795 */               accessibles.put(sig, methodList);
/*      */             } 
/*  797 */             methodList.add(method);
/*      */           } 
/*      */         } 
/*      */         return;
/*  801 */       } catch (SecurityException e) {
/*  802 */         LOG.warn("Could not discover accessible methods of class " + clazz
/*  803 */             .getName() + ", attemping superclasses/interfaces.", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  809 */     Class<?>[] interfaces = clazz.getInterfaces();
/*  810 */     for (int i = 0; i < interfaces.length; i++) {
/*  811 */       discoverAccessibleMethods(interfaces[i], accessibles);
/*      */     }
/*  813 */     Class<?> superclass = clazz.getSuperclass();
/*  814 */     if (superclass != null) {
/*  815 */       discoverAccessibleMethods(superclass, accessibles);
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
/*      */   private static Method getMatchingAccessibleMethod(Method m, Map<ExecutableMemberSignature, List<Method>> accessibles) {
/*  830 */     if (m == null) {
/*  831 */       return null;
/*      */     }
/*  833 */     List<Method> ams = accessibles.get(new ExecutableMemberSignature(m));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  838 */     return (ams != null) ? _MethodUtil.getMethodWithClosestNonSubReturnType(m.getReturnType(), ams) : null;
/*      */   }
/*      */ 
/*      */   
/*      */   private static Method getFirstAccessibleMethod(ExecutableMemberSignature sig, Map<ExecutableMemberSignature, List<Method>> accessibles) {
/*  843 */     List<Method> ams = accessibles.get(sig);
/*  844 */     if (ams == null || ams.isEmpty()) {
/*  845 */       return null;
/*      */     }
/*  847 */     return ams.get(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sortMethodDescriptors(List<MethodDescriptor> methodDescriptors) {
/*  854 */     if (this.methodSorter != null) {
/*  855 */       this.methodSorter.sortMethodDescriptors(methodDescriptors);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MemberAccessPolicy getEffectiveMemberAccessPolicy() {
/*  866 */     return (this.exposureLevel < 1) ? AllowAllMemberAccessPolicy.INSTANCE : this.memberAccessPolicy;
/*      */   }
/*      */   
/*      */   private boolean is2321Bugfixed() {
/*  870 */     return BeansWrapper.is2321Bugfixed(this.incompatibleImprovements);
/*      */   }
/*      */ 
/*      */   
/*      */   private static Map<Method, Class<?>[]> getArgTypesByMethod(Map<Object, Object> classInfo) {
/*  875 */     Map<Method, Class<?>[]> argTypes = (Map<Method, Class<?>[]>)classInfo.get(ARG_TYPES_BY_METHOD_KEY);
/*  876 */     if (argTypes == null) {
/*  877 */       argTypes = (Map)new HashMap<>();
/*  878 */       classInfo.put(ARG_TYPES_BY_METHOD_KEY, argTypes);
/*      */     } 
/*  880 */     return argTypes;
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
/*      */   void clearCache() {
/*  892 */     if (getHasSharedInstanceRestrictions()) {
/*  893 */       throw new IllegalStateException("It's not allowed to clear the whole cache in a read-only " + 
/*  894 */           getClass().getName() + "instance. Use removeFromClassIntrospectionCache(String prefix) instead.");
/*      */     }
/*      */     
/*  897 */     forcedClearCache();
/*      */   }
/*      */   
/*      */   private void forcedClearCache() {
/*  901 */     synchronized (this.sharedLock) {
/*  902 */       this.cache.clear();
/*  903 */       this.cacheClassNames.clear();
/*  904 */       this.clearingCounter++;
/*      */       
/*  906 */       for (WeakReference<Object> regedMfREf : this.modelFactories) {
/*  907 */         Object regedMf = regedMfREf.get();
/*  908 */         if (regedMf != null) {
/*  909 */           if (regedMf instanceof ClassBasedModelFactory) {
/*  910 */             ((ClassBasedModelFactory)regedMf).clearCache(); continue;
/*  911 */           }  if (regedMf instanceof ModelCache) {
/*  912 */             ((ModelCache)regedMf).clearCache(); continue;
/*      */           } 
/*  914 */           throw new BugException();
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  919 */       removeClearedModelFactoryReferences();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void remove(Class<?> clazz) {
/*  929 */     synchronized (this.sharedLock) {
/*  930 */       this.cache.remove(clazz);
/*  931 */       this.cacheClassNames.remove(clazz.getName());
/*  932 */       this.clearingCounter++;
/*      */       
/*  934 */       for (WeakReference<Object> regedMfREf : this.modelFactories) {
/*  935 */         Object regedMf = regedMfREf.get();
/*  936 */         if (regedMf != null) {
/*  937 */           if (regedMf instanceof ClassBasedModelFactory) {
/*  938 */             ((ClassBasedModelFactory)regedMf).removeFromCache(clazz); continue;
/*  939 */           }  if (regedMf instanceof ModelCache) {
/*  940 */             ((ModelCache)regedMf).clearCache(); continue;
/*      */           } 
/*  942 */           throw new BugException();
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  947 */       removeClearedModelFactoryReferences();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getClearingCounter() {
/*  955 */     synchronized (this.sharedLock) {
/*  956 */       return this.clearingCounter;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void onSameNameClassesDetected(String className) {
/*  965 */     if (LOG.isInfoEnabled()) {
/*  966 */       LOG.info("Detected multiple classes with the same name, \"" + className + "\". Assuming it was a class-reloading. Clearing class introspection caches to release old data.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  971 */     forcedClearCache();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void registerModelFactory(ClassBasedModelFactory mf) {
/*  978 */     registerModelFactory(mf);
/*      */   }
/*      */   
/*      */   void registerModelFactory(ModelCache mf) {
/*  982 */     registerModelFactory(mf);
/*      */   }
/*      */ 
/*      */   
/*      */   private void registerModelFactory(Object mf) {
/*  987 */     synchronized (this.sharedLock) {
/*  988 */       this.modelFactories.add(new WeakReference(mf, this.modelFactoriesRefQueue));
/*  989 */       removeClearedModelFactoryReferences();
/*      */     } 
/*      */   }
/*      */   
/*      */   void unregisterModelFactory(ClassBasedModelFactory mf) {
/*  994 */     unregisterModelFactory(mf);
/*      */   }
/*      */   
/*      */   void unregisterModelFactory(ModelCache mf) {
/*  998 */     unregisterModelFactory(mf);
/*      */   }
/*      */   
/*      */   void unregisterModelFactory(Object mf) {
/* 1002 */     synchronized (this.sharedLock) {
/* 1003 */       for (Iterator<WeakReference<Object>> it = this.modelFactories.iterator(); it.hasNext(); ) {
/* 1004 */         Object regedMf = ((WeakReference)it.next()).get();
/* 1005 */         if (regedMf == mf) {
/* 1006 */           it.remove();
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void removeClearedModelFactoryReferences() {
/*      */     Reference<?> cleardRef;
/* 1015 */     while ((cleardRef = this.modelFactoriesRefQueue.poll()) != null) {
/* 1016 */       synchronized (this.sharedLock) {
/* 1017 */         for (Iterator<WeakReference<Object>> it = this.modelFactories.iterator(); it.hasNext();) {
/* 1018 */           if (it.next() == cleardRef) {
/* 1019 */             it.remove();
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static Class<?>[] getArgTypes(Map<Object, Object> classInfo, Method method) {
/* 1032 */     Map<Method, Class<?>[]> argTypesByMethod = (Map<Method, Class<?>[]>)classInfo.get(ARG_TYPES_BY_METHOD_KEY);
/* 1033 */     return argTypesByMethod.get(method);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int keyCount(Class<?> clazz) {
/* 1041 */     Map<Object, Object> map = get(clazz);
/* 1042 */     int count = map.size();
/* 1043 */     if (map.containsKey(CONSTRUCTORS_KEY)) count--; 
/* 1044 */     if (map.containsKey(GENERIC_GET_KEY)) count--; 
/* 1045 */     if (map.containsKey(ARG_TYPES_BY_METHOD_KEY)) count--; 
/* 1046 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Set<Object> keySet(Class<?> clazz) {
/* 1054 */     Set<Object> set = new HashSet(get(clazz).keySet());
/* 1055 */     set.remove(CONSTRUCTORS_KEY);
/* 1056 */     set.remove(GENERIC_GET_KEY);
/* 1057 */     set.remove(ARG_TYPES_BY_METHOD_KEY);
/* 1058 */     return set;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getExposureLevel() {
/* 1065 */     return this.exposureLevel;
/*      */   }
/*      */   
/*      */   boolean getExposeFields() {
/* 1069 */     return this.exposeFields;
/*      */   }
/*      */   
/*      */   MemberAccessPolicy getMemberAccessPolicy() {
/* 1073 */     return this.memberAccessPolicy;
/*      */   }
/*      */   
/*      */   boolean getTreatDefaultMethodsAsBeanMembers() {
/* 1077 */     return this.treatDefaultMethodsAsBeanMembers;
/*      */   }
/*      */   
/*      */   MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
/* 1081 */     return this.methodAppearanceFineTuner;
/*      */   }
/*      */   
/*      */   MethodSorter getMethodSorter() {
/* 1085 */     return this.methodSorter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean getHasSharedInstanceRestrictions() {
/* 1093 */     return this.hasSharedInstanceRestrictions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isShared() {
/* 1102 */     return this.shared;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object getSharedLock() {
/* 1110 */     return this.sharedLock;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object[] getRegisteredModelFactoriesSnapshot() {
/* 1118 */     synchronized (this.sharedLock) {
/* 1119 */       return this.modelFactories.toArray();
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ClassIntrospector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */