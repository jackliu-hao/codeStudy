package freemarker.template;

import freemarker.ext.beans._BeansAPI;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class DefaultObjectWrapperBuilder extends DefaultObjectWrapperConfiguration {
   private static final Map<ClassLoader, Map<DefaultObjectWrapperConfiguration, WeakReference<DefaultObjectWrapper>>> INSTANCE_CACHE = new WeakHashMap();
   private static final ReferenceQueue<DefaultObjectWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue();

   public DefaultObjectWrapperBuilder(Version incompatibleImprovements) {
      super(incompatibleImprovements);
   }

   static void clearInstanceCache() {
      synchronized(INSTANCE_CACHE) {
         INSTANCE_CACHE.clear();
      }
   }

   public DefaultObjectWrapper build() {
      return (DefaultObjectWrapper)_BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, DefaultObjectWrapperBuilder.DefaultObjectWrapperFactory.INSTANCE);
   }

   private static class DefaultObjectWrapperFactory implements _BeansAPI._BeansWrapperSubclassFactory<DefaultObjectWrapper, DefaultObjectWrapperConfiguration> {
      private static final DefaultObjectWrapperFactory INSTANCE = new DefaultObjectWrapperFactory();

      public DefaultObjectWrapper create(DefaultObjectWrapperConfiguration bwConf) {
         return new DefaultObjectWrapper(bwConf, true);
      }
   }
}
