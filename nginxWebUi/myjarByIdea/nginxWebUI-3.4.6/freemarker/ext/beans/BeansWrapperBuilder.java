package freemarker.ext.beans;

import freemarker.template.Version;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class BeansWrapperBuilder extends BeansWrapperConfiguration {
   private static final Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> INSTANCE_CACHE = new WeakHashMap();
   private static final ReferenceQueue<BeansWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue();

   public BeansWrapperBuilder(Version incompatibleImprovements) {
      super(incompatibleImprovements);
   }

   static void clearInstanceCache() {
      synchronized(INSTANCE_CACHE) {
         INSTANCE_CACHE.clear();
      }
   }

   static Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> getInstanceCache() {
      return INSTANCE_CACHE;
   }

   public BeansWrapper build() {
      return _BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, BeansWrapperBuilder.BeansWrapperFactory.INSTANCE);
   }

   private static class BeansWrapperFactory implements _BeansAPI._BeansWrapperSubclassFactory<BeansWrapper, BeansWrapperConfiguration> {
      private static final BeansWrapperFactory INSTANCE = new BeansWrapperFactory();

      public BeansWrapper create(BeansWrapperConfiguration bwConf) {
         return new BeansWrapper(bwConf, true);
      }
   }
}
