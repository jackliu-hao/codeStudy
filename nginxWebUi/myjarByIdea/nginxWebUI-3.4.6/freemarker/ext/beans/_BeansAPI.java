package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.CollectionUtils;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class _BeansAPI {
   private _BeansAPI() {
   }

   public static String getAsClassicCompatibleString(BeanModel bm) {
      return bm.getAsClassicCompatibleString();
   }

   public static Object newInstance(Class<?> pClass, Object[] args, BeansWrapper bw) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException {
      return newInstance(getConstructorDescriptor(pClass, args), args, bw);
   }

   private static CallableMemberDescriptor getConstructorDescriptor(Class<?> pClass, Object[] args) throws NoSuchMethodException {
      if (args == null) {
         args = CollectionUtils.EMPTY_OBJECT_ARRAY;
      }

      ArgumentTypes argTypes = new ArgumentTypes(args, true);
      List<ReflectionCallableMemberDescriptor> fixedArgMemberDescs = new ArrayList();
      List<ReflectionCallableMemberDescriptor> varArgsMemberDescs = new ArrayList();
      Constructor<?>[] constrs = pClass.getConstructors();

      for(int i = 0; i < constrs.length; ++i) {
         Constructor<?> constr = constrs[i];
         ReflectionCallableMemberDescriptor memberDesc = new ReflectionCallableMemberDescriptor(constr, constr.getParameterTypes());
         if (!_MethodUtil.isVarargs(constr)) {
            fixedArgMemberDescs.add(memberDesc);
         } else {
            varArgsMemberDescs.add(memberDesc);
         }
      }

      MaybeEmptyCallableMemberDescriptor contrDesc = argTypes.getMostSpecific(fixedArgMemberDescs, false);
      if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
         contrDesc = argTypes.getMostSpecific(varArgsMemberDescs, true);
      }

      if (contrDesc instanceof EmptyCallableMemberDescriptor) {
         if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
            throw new NoSuchMethodException("There's no public " + pClass.getName() + " constructor with compatible parameter list.");
         } else if (contrDesc == EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD) {
            throw new NoSuchMethodException("There are multiple public " + pClass.getName() + " constructors that match the compatible parameter list with the same preferability.");
         } else {
            throw new NoSuchMethodException();
         }
      } else {
         return (CallableMemberDescriptor)contrDesc;
      }
   }

   private static Object newInstance(CallableMemberDescriptor constrDesc, Object[] args, BeansWrapper bw) throws InstantiationException, IllegalAccessException, InvocationTargetException, IllegalArgumentException, TemplateModelException {
      if (args == null) {
         args = CollectionUtils.EMPTY_OBJECT_ARRAY;
      }

      Object[] packedArgs;
      if (constrDesc.isVarargs()) {
         Class<?>[] paramTypes = constrDesc.getParamTypes();
         int fixedArgCnt = paramTypes.length - 1;
         packedArgs = new Object[fixedArgCnt + 1];

         for(int i = 0; i < fixedArgCnt; ++i) {
            packedArgs[i] = args[i];
         }

         Class<?> compType = paramTypes[fixedArgCnt].getComponentType();
         int varArgCnt = args.length - fixedArgCnt;
         Object varArgsArray = Array.newInstance(compType, varArgCnt);

         for(int i = 0; i < varArgCnt; ++i) {
            Array.set(varArgsArray, i, args[fixedArgCnt + i]);
         }

         packedArgs[fixedArgCnt] = varArgsArray;
      } else {
         packedArgs = args;
      }

      return constrDesc.invokeConstructor(bw, packedArgs);
   }

   public static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> BW getBeansWrapperSubclassSingleton(BWC settings, Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue, _BeansWrapperSubclassFactory<BW, BWC> beansWrapperSubclassFactory) {
      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      Reference instanceRef;
      Object tcclScopedCache;
      synchronized(instanceCache) {
         tcclScopedCache = (Map)instanceCache.get(tccl);
         if (tcclScopedCache == null) {
            tcclScopedCache = new HashMap();
            instanceCache.put(tccl, tcclScopedCache);
            instanceRef = null;
         } else {
            instanceRef = (Reference)((Map)tcclScopedCache).get(settings);
         }
      }

      BW instance = instanceRef != null ? (BeansWrapper)instanceRef.get() : null;
      if (instance != null) {
         return instance;
      } else {
         settings = clone(settings);
         instance = beansWrapperSubclassFactory.create(settings);
         if (!instance.isWriteProtected()) {
            throw new BugException();
         } else {
            synchronized(instanceCache) {
               instanceRef = (Reference)((Map)tcclScopedCache).get(settings);
               BW concurrentInstance = instanceRef != null ? (BeansWrapper)instanceRef.get() : null;
               if (concurrentInstance == null) {
                  ((Map)tcclScopedCache).put(settings, new WeakReference(instance, instanceCacheRefQue));
               } else {
                  instance = concurrentInstance;
               }
            }

            removeClearedReferencesFromCache(instanceCache, instanceCacheRefQue);
            return instance;
         }
      }
   }

   private static <BWC extends BeansWrapperConfiguration> BWC clone(BWC settings) {
      return (BeansWrapperConfiguration)settings.clone(true);
   }

   private static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> void removeClearedReferencesFromCache(Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue) {
      Reference clearedRef;
      label36:
      while((clearedRef = instanceCacheRefQue.poll()) != null) {
         synchronized(instanceCache) {
            Iterator var4 = instanceCache.values().iterator();

            while(var4.hasNext()) {
               Map<BWC, WeakReference<BW>> tcclScopedCache = (Map)var4.next();
               Iterator<WeakReference<BW>> it2 = tcclScopedCache.values().iterator();

               while(it2.hasNext()) {
                  if (it2.next() == clearedRef) {
                     it2.remove();
                     continue label36;
                  }
               }
            }
         }
      }

   }

   public static ClassIntrospectorBuilder getClassIntrospectorBuilder(BeansWrapperConfiguration bwc) {
      return bwc.getClassIntrospectorBuilder();
   }

   public interface _BeansWrapperSubclassFactory<BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> {
      BW create(BWC var1);
   }
}
