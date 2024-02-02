package freemarker.debug.impl;

import freemarker.cache.CacheStorage;
import freemarker.cache.SoftCacheStorage;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.debug.DebuggedEnvironment;
import freemarker.template.Configuration;
import freemarker.template.SimpleCollection;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class RmiDebuggedEnvironmentImpl extends RmiDebugModelImpl implements DebuggedEnvironment {
   private static final long serialVersionUID = 1L;
   private static final CacheStorage storage = new SoftCacheStorage(new IdentityHashMap());
   private static final Object idLock = new Object();
   private static long nextId = 1L;
   private static Set remotes = new HashSet();
   private boolean stopped = false;
   private final long id;

   private RmiDebuggedEnvironmentImpl(Environment env) throws RemoteException {
      super(new DebugEnvironmentModel(env), 2048);
      synchronized(idLock) {
         this.id = (long)(nextId++);
      }
   }

   static synchronized Object getCachedWrapperFor(Object key) throws RemoteException {
      Object value = storage.get(key);
      if (value == null) {
         if (key instanceof TemplateModel) {
            short extraTypes;
            if (key instanceof DebugConfigurationModel) {
               extraTypes = 8192;
            } else if (key instanceof DebugTemplateModel) {
               extraTypes = 4096;
            } else {
               extraTypes = 0;
            }

            value = new RmiDebugModelImpl((TemplateModel)key, extraTypes);
         } else if (key instanceof Environment) {
            value = new RmiDebuggedEnvironmentImpl((Environment)key);
         } else if (key instanceof Template) {
            value = new DebugTemplateModel((Template)key);
         } else if (key instanceof Configuration) {
            value = new DebugConfigurationModel((Configuration)key);
         }
      }

      if (value != null) {
         storage.put(key, value);
      }

      if (value instanceof Remote) {
         remotes.add(value);
      }

      return value;
   }

   public void resume() {
      synchronized(this) {
         this.notify();
      }
   }

   public void stop() {
      this.stopped = true;
      this.resume();
   }

   public long getId() {
      return this.id;
   }

   boolean isStopped() {
      return this.stopped;
   }

   public static void cleanup() {
      Iterator i = remotes.iterator();

      while(i.hasNext()) {
         Object remoteObject = i.next();

         try {
            UnicastRemoteObject.unexportObject((Remote)remoteObject, true);
         } catch (Exception var3) {
         }
      }

   }

   private static class DebugEnvironmentModel extends DebugConfigurableModel {
      private static final List KEYS;
      private TemplateModel knownVariables = new DebugMapModel() {
         Collection keySet() {
            try {
               return ((Environment)DebugEnvironmentModel.this.configurable).getKnownVariableNames();
            } catch (TemplateModelException var2) {
               throw new UndeclaredThrowableException(var2);
            }
         }

         public TemplateModel get(String key) throws TemplateModelException {
            return ((Environment)DebugEnvironmentModel.this.configurable).getVariable(key);
         }
      };

      DebugEnvironmentModel(Environment env) {
         super(env);
      }

      Collection keySet() {
         return KEYS;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         if ("currentNamespace".equals(key)) {
            return ((Environment)this.configurable).getCurrentNamespace();
         } else if ("dataModel".equals(key)) {
            return ((Environment)this.configurable).getDataModel();
         } else if ("globalNamespace".equals(key)) {
            return ((Environment)this.configurable).getGlobalNamespace();
         } else if ("knownVariables".equals(key)) {
            return this.knownVariables;
         } else if ("mainNamespace".equals(key)) {
            return ((Environment)this.configurable).getMainNamespace();
         } else if ("template".equals(key)) {
            try {
               return (TemplateModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(((Environment)this.configurable).getTemplate());
            } catch (RemoteException var3) {
               throw new TemplateModelException(var3);
            }
         } else {
            return super.get(key);
         }
      }

      static {
         KEYS = composeList(RmiDebuggedEnvironmentImpl.DebugConfigurableModel.KEYS, Arrays.asList("currentNamespace", "dataModel", "globalNamespace", "knownVariables", "mainNamespace", "template"));
      }
   }

   private static class DebugTemplateModel extends DebugConfigurableModel {
      private static final List KEYS;
      private final SimpleScalar name;

      DebugTemplateModel(Template template) {
         super(template);
         this.name = new SimpleScalar(template.getName());
      }

      Collection keySet() {
         return KEYS;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         if ("configuration".equals(key)) {
            try {
               return (TemplateModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(((Template)this.configurable).getConfiguration());
            } catch (RemoteException var3) {
               throw new TemplateModelException(var3);
            }
         } else {
            return (TemplateModel)("name".equals(key) ? this.name : super.get(key));
         }
      }

      static {
         KEYS = composeList(RmiDebuggedEnvironmentImpl.DebugConfigurableModel.KEYS, Arrays.asList("configuration", "name"));
      }
   }

   private static class DebugConfigurationModel extends DebugConfigurableModel {
      private static final List KEYS;
      private TemplateModel sharedVariables = new DebugMapModel() {
         Collection keySet() {
            return ((Configuration)DebugConfigurationModel.this.configurable).getSharedVariableNames();
         }

         public TemplateModel get(String key) {
            return ((Configuration)DebugConfigurationModel.this.configurable).getSharedVariable(key);
         }
      };

      DebugConfigurationModel(Configuration config) {
         super(config);
      }

      Collection keySet() {
         return KEYS;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         return "sharedVariables".equals(key) ? this.sharedVariables : super.get(key);
      }

      static {
         KEYS = composeList(RmiDebuggedEnvironmentImpl.DebugConfigurableModel.KEYS, Collections.singleton("sharedVariables"));
      }
   }

   private static class DebugConfigurableModel extends DebugMapModel {
      static final List KEYS = Arrays.asList("arithmetic_engine", "boolean_format", "classic_compatible", "locale", "number_format", "object_wrapper", "template_exception_handler");
      final Configurable configurable;

      DebugConfigurableModel(Configurable configurable) {
         super(null);
         this.configurable = configurable;
      }

      Collection keySet() {
         return KEYS;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         String s = this.configurable.getSetting(key);
         return s == null ? null : new SimpleScalar(s);
      }
   }

   private abstract static class DebugMapModel implements TemplateHashModelEx {
      private DebugMapModel() {
      }

      public int size() {
         return this.keySet().size();
      }

      public TemplateCollectionModel keys() {
         return new SimpleCollection(this.keySet());
      }

      public TemplateCollectionModel values() throws TemplateModelException {
         Collection keys = this.keySet();
         List list = new ArrayList(keys.size());
         Iterator it = keys.iterator();

         while(it.hasNext()) {
            list.add(this.get((String)it.next()));
         }

         return new SimpleCollection(list);
      }

      public boolean isEmpty() {
         return this.size() == 0;
      }

      abstract Collection keySet();

      static List composeList(Collection c1, Collection c2) {
         List list = new ArrayList(c1);
         list.addAll(c2);
         Collections.sort(list);
         return list;
      }

      // $FF: synthetic method
      DebugMapModel(Object x0) {
         this();
      }
   }
}
