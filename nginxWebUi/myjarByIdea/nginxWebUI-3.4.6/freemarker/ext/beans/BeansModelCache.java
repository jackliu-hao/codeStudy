package freemarker.ext.beans;

import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;
import freemarker.template.TemplateModel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeansModelCache extends ModelCache {
   private final Map<Class<?>, ModelFactory> classToFactory = new ConcurrentHashMap();
   private final Set<String> mappedClassNames = new HashSet();
   private final BeansWrapper wrapper;

   BeansModelCache(BeansWrapper wrapper) {
      this.wrapper = wrapper;
   }

   protected boolean isCacheable(Object object) {
      return object.getClass() != Boolean.class;
   }

   protected TemplateModel create(Object object) {
      Class clazz = object.getClass();
      ModelFactory factory = (ModelFactory)this.classToFactory.get(clazz);
      if (factory == null) {
         synchronized(this.classToFactory) {
            factory = (ModelFactory)this.classToFactory.get(clazz);
            if (factory == null) {
               String className = clazz.getName();
               if (!this.mappedClassNames.add(className)) {
                  this.classToFactory.clear();
                  this.mappedClassNames.clear();
                  this.mappedClassNames.add(className);
               }

               factory = this.wrapper.getModelFactory(clazz);
               this.classToFactory.put(clazz, factory);
            }
         }
      }

      return factory.create(object, this.wrapper);
   }
}
