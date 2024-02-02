package freemarker.ext.beans;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class ClassBasedModelFactory implements TemplateHashModel {
   private final BeansWrapper wrapper;
   private final Map<String, TemplateModel> cache = new ConcurrentHashMap();
   private final Set<String> classIntrospectionsInProgress = new HashSet();

   protected ClassBasedModelFactory(BeansWrapper wrapper) {
      this.wrapper = wrapper;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      try {
         return this.getInternal(key);
      } catch (Exception var3) {
         if (var3 instanceof TemplateModelException) {
            throw (TemplateModelException)var3;
         } else {
            throw new _TemplateModelException(var3, new Object[]{"Failed to get value for key ", new _DelayedJQuote(key), "; see cause exception."});
         }
      }
   }

   private TemplateModel getInternal(String key) throws TemplateModelException, ClassNotFoundException {
      TemplateModel model = (TemplateModel)this.cache.get(key);
      if (model != null) {
         return model;
      } else {
         Object sharedLock = this.wrapper.getSharedIntrospectionLock();
         int classIntrospectorClearingCounter;
         TemplateModel model;
         ClassIntrospector classIntrospector;
         synchronized(sharedLock) {
            model = (TemplateModel)this.cache.get(key);
            if (model != null) {
               return model;
            }

            while(model == null && this.classIntrospectionsInProgress.contains(key)) {
               try {
                  sharedLock.wait();
                  model = (TemplateModel)this.cache.get(key);
               } catch (InterruptedException var24) {
                  throw new RuntimeException("Class inrospection data lookup aborted: " + var24);
               }
            }

            if (model != null) {
               return model;
            }

            this.classIntrospectionsInProgress.add(key);
            classIntrospector = this.wrapper.getClassIntrospector();
            classIntrospectorClearingCounter = classIntrospector.getClearingCounter();
         }

         boolean var20 = false;

         TemplateModel var7;
         try {
            var20 = true;
            Class<?> clazz = ClassUtil.forName(key);
            classIntrospector.get(clazz);
            model = this.createModel(clazz);
            if (model != null) {
               synchronized(sharedLock) {
                  if (classIntrospector == this.wrapper.getClassIntrospector() && classIntrospectorClearingCounter == classIntrospector.getClearingCounter()) {
                     this.cache.put(key, model);
                  }
               }
            }

            var7 = model;
            var20 = false;
         } finally {
            if (var20) {
               synchronized(sharedLock) {
                  this.classIntrospectionsInProgress.remove(key);
                  sharedLock.notifyAll();
               }
            }
         }

         synchronized(sharedLock) {
            this.classIntrospectionsInProgress.remove(key);
            sharedLock.notifyAll();
            return var7;
         }
      }
   }

   void clearCache() {
      synchronized(this.wrapper.getSharedIntrospectionLock()) {
         this.cache.clear();
      }
   }

   void removeFromCache(Class<?> clazz) {
      synchronized(this.wrapper.getSharedIntrospectionLock()) {
         this.cache.remove(clazz.getName());
      }
   }

   public boolean isEmpty() {
      return false;
   }

   protected abstract TemplateModel createModel(Class<?> var1) throws TemplateModelException;

   protected BeansWrapper getWrapper() {
      return this.wrapper;
   }
}
