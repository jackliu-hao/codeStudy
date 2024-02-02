/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core.BugException;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.CollectionUtils;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _BeansAPI
/*     */ {
/*     */   public static String getAsClassicCompatibleString(BeanModel bm) {
/*  49 */     return bm.getAsClassicCompatibleString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object newInstance(Class<?> pClass, Object[] args, BeansWrapper bw) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException {
/*  55 */     return newInstance(getConstructorDescriptor(pClass, args), args, bw);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static CallableMemberDescriptor getConstructorDescriptor(Class<?> pClass, Object[] args) throws NoSuchMethodException {
/*  65 */     if (args == null) args = CollectionUtils.EMPTY_OBJECT_ARRAY;
/*     */     
/*  67 */     ArgumentTypes argTypes = new ArgumentTypes(args, true);
/*  68 */     List<ReflectionCallableMemberDescriptor> fixedArgMemberDescs = new ArrayList<>();
/*     */     
/*  70 */     List<ReflectionCallableMemberDescriptor> varArgsMemberDescs = new ArrayList<>();
/*     */     
/*  72 */     Constructor[] arrayOfConstructor = (Constructor[])pClass.getConstructors();
/*  73 */     for (int i = 0; i < arrayOfConstructor.length; i++) {
/*  74 */       Constructor<?> constr = arrayOfConstructor[i];
/*  75 */       ReflectionCallableMemberDescriptor memberDesc = new ReflectionCallableMemberDescriptor(constr, constr.getParameterTypes());
/*  76 */       if (!_MethodUtil.isVarargs(constr)) {
/*  77 */         fixedArgMemberDescs.add(memberDesc);
/*     */       } else {
/*  79 */         varArgsMemberDescs.add(memberDesc);
/*     */       } 
/*     */     } 
/*     */     
/*  83 */     MaybeEmptyCallableMemberDescriptor contrDesc = argTypes.getMostSpecific(fixedArgMemberDescs, false);
/*  84 */     if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
/*  85 */       contrDesc = argTypes.getMostSpecific(varArgsMemberDescs, true);
/*     */     }
/*     */     
/*  88 */     if (contrDesc instanceof EmptyCallableMemberDescriptor) {
/*  89 */       if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
/*  90 */         throw new NoSuchMethodException("There's no public " + pClass
/*  91 */             .getName() + " constructor with compatible parameter list.");
/*     */       }
/*  93 */       if (contrDesc == EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD) {
/*  94 */         throw new NoSuchMethodException("There are multiple public " + pClass
/*  95 */             .getName() + " constructors that match the compatible parameter list with the same preferability.");
/*     */       }
/*     */       
/*  98 */       throw new NoSuchMethodException();
/*     */     } 
/*     */     
/* 101 */     return (CallableMemberDescriptor)contrDesc;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object newInstance(CallableMemberDescriptor constrDesc, Object[] args, BeansWrapper bw) throws InstantiationException, IllegalAccessException, InvocationTargetException, IllegalArgumentException, TemplateModelException {
/*     */     Object[] packedArgs;
/* 108 */     if (args == null) args = CollectionUtils.EMPTY_OBJECT_ARRAY;
/*     */ 
/*     */     
/* 111 */     if (constrDesc.isVarargs()) {
/*     */ 
/*     */       
/* 114 */       Class<?>[] paramTypes = constrDesc.getParamTypes();
/* 115 */       int fixedArgCnt = paramTypes.length - 1;
/*     */       
/* 117 */       packedArgs = new Object[fixedArgCnt + 1];
/* 118 */       for (int i = 0; i < fixedArgCnt; i++) {
/* 119 */         packedArgs[i] = args[i];
/*     */       }
/*     */       
/* 122 */       Class<?> compType = paramTypes[fixedArgCnt].getComponentType();
/* 123 */       int varArgCnt = args.length - fixedArgCnt;
/* 124 */       Object varArgsArray = Array.newInstance(compType, varArgCnt);
/* 125 */       for (int j = 0; j < varArgCnt; j++) {
/* 126 */         Array.set(varArgsArray, j, args[fixedArgCnt + j]);
/*     */       }
/* 128 */       packedArgs[fixedArgCnt] = varArgsArray;
/*     */     } else {
/* 130 */       packedArgs = args;
/*     */     } 
/*     */     
/* 133 */     return constrDesc.invokeConstructor(bw, packedArgs);
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
/*     */ 
/*     */   
/*     */   public static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> BW getBeansWrapperSubclassSingleton(BWC settings, Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue, _BeansWrapperSubclassFactory<BW, BWC> beansWrapperSubclassFactory) {
/*     */     Reference<BW> instanceRef;
/*     */     Map<BWC, WeakReference<BW>> tcclScopedCache;
/* 151 */     ClassLoader tccl = Thread.currentThread().getContextClassLoader();
/*     */ 
/*     */ 
/*     */     
/* 155 */     synchronized (instanceCache) {
/* 156 */       tcclScopedCache = instanceCache.get(tccl);
/* 157 */       if (tcclScopedCache == null) {
/* 158 */         tcclScopedCache = new HashMap<>();
/* 159 */         instanceCache.put(tccl, tcclScopedCache);
/* 160 */         instanceRef = null;
/*     */       } else {
/* 162 */         instanceRef = tcclScopedCache.get(settings);
/*     */       } 
/*     */     } 
/*     */     
/* 166 */     BeansWrapper beansWrapper = (instanceRef != null) ? (BeansWrapper)instanceRef.get() : null;
/* 167 */     if (beansWrapper != null) {
/* 168 */       return (BW)beansWrapper;
/*     */     }
/*     */ 
/*     */     
/* 172 */     settings = clone(settings);
/* 173 */     beansWrapper = (BeansWrapper)beansWrapperSubclassFactory.create(settings);
/* 174 */     if (!beansWrapper.isWriteProtected()) {
/* 175 */       throw new BugException();
/*     */     }
/*     */     
/* 178 */     synchronized (instanceCache) {
/* 179 */       instanceRef = tcclScopedCache.get(settings);
/* 180 */       BeansWrapper beansWrapper1 = (instanceRef != null) ? (BeansWrapper)instanceRef.get() : null;
/* 181 */       if (beansWrapper1 == null) {
/* 182 */         tcclScopedCache.put(settings, new WeakReference<>((BW)beansWrapper, instanceCacheRefQue));
/*     */       } else {
/* 184 */         beansWrapper = beansWrapper1;
/*     */       } 
/*     */     } 
/*     */     
/* 188 */     removeClearedReferencesFromCache(instanceCache, instanceCacheRefQue);
/*     */     
/* 190 */     return (BW)beansWrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   private static <BWC extends BeansWrapperConfiguration> BWC clone(BWC settings) {
/* 195 */     return (BWC)settings.clone(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> void removeClearedReferencesFromCache(Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue) {
/*     */     Reference<? extends BW> clearedRef;
/* 203 */     while ((clearedRef = instanceCacheRefQue.poll()) != null) {
/* 204 */       synchronized (instanceCache) {
/* 205 */         label20: for (Map<BWC, WeakReference<BW>> tcclScopedCache : instanceCache.values()) {
/* 206 */           for (Iterator<WeakReference<BW>> it2 = tcclScopedCache.values().iterator(); it2.hasNext();) {
/* 207 */             if (it2.next() == clearedRef) {
/* 208 */               it2.remove();
/*     */               break label20;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */   public static ClassIntrospectorBuilder getClassIntrospectorBuilder(BeansWrapperConfiguration bwc) {
/* 227 */     return bwc.getClassIntrospectorBuilder();
/*     */   }
/*     */   
/*     */   public static interface _BeansWrapperSubclassFactory<BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> {
/*     */     BW create(BWC param1BWC);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\_BeansAPI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */