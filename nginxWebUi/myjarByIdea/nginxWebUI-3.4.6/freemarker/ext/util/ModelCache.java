package freemarker.ext.util;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;

public abstract class ModelCache {
   private boolean useCache = false;
   private Map<Object, ModelReference> modelCache = null;
   private ReferenceQueue<TemplateModel> refQueue = null;

   protected ModelCache() {
   }

   public synchronized void setUseCache(boolean useCache) {
      this.useCache = useCache;
      if (useCache) {
         this.modelCache = new java.util.IdentityHashMap();
         this.refQueue = new ReferenceQueue();
      } else {
         this.modelCache = null;
         this.refQueue = null;
      }

   }

   public synchronized boolean getUseCache() {
      return this.useCache;
   }

   public TemplateModel getInstance(Object object) {
      if (object instanceof TemplateModel) {
         return (TemplateModel)object;
      } else if (object instanceof TemplateModelAdapter) {
         return ((TemplateModelAdapter)object).getTemplateModel();
      } else if (this.useCache && this.isCacheable(object)) {
         TemplateModel model = this.lookup(object);
         if (model == null) {
            model = this.create(object);
            this.register(model, object);
         }

         return model;
      } else {
         return this.create(object);
      }
   }

   protected abstract TemplateModel create(Object var1);

   protected abstract boolean isCacheable(Object var1);

   public void clearCache() {
      if (this.modelCache != null) {
         synchronized(this.modelCache) {
            this.modelCache.clear();
         }
      }

   }

   private final TemplateModel lookup(Object object) {
      ModelReference ref = null;
      synchronized(this.modelCache) {
         ref = (ModelReference)this.modelCache.get(object);
      }

      return ref != null ? ref.getModel() : null;
   }

   private final void register(TemplateModel model, Object object) {
      synchronized(this.modelCache) {
         while(true) {
            ModelReference queuedRef = (ModelReference)this.refQueue.poll();
            if (queuedRef == null) {
               this.modelCache.put(object, new ModelReference(model, object, this.refQueue));
               return;
            }

            this.modelCache.remove(queuedRef.object);
         }
      }
   }

   private static final class ModelReference extends SoftReference<TemplateModel> {
      Object object;

      ModelReference(TemplateModel ref, Object object, ReferenceQueue<TemplateModel> refQueue) {
         super(ref, refQueue);
         this.object = object;
      }

      TemplateModel getModel() {
         return (TemplateModel)this.get();
      }
   }
}
