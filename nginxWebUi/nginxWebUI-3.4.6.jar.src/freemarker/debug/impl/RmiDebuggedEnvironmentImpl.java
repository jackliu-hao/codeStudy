/*     */ package freemarker.debug.impl;
/*     */ 
/*     */ import freemarker.cache.CacheStorage;
/*     */ import freemarker.cache.SoftCacheStorage;
/*     */ import freemarker.core.Configurable;
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.debug.DebuggedEnvironment;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.SimpleCollection;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.UnicastRemoteObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ class RmiDebuggedEnvironmentImpl
/*     */   extends RmiDebugModelImpl
/*     */   implements DebuggedEnvironment
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  61 */   private static final CacheStorage storage = (CacheStorage)new SoftCacheStorage(new IdentityHashMap<>());
/*  62 */   private static final Object idLock = new Object();
/*  63 */   private static long nextId = 1L;
/*  64 */   private static Set remotes = new HashSet();
/*     */   
/*     */   private boolean stopped = false;
/*     */   
/*     */   private final long id;
/*     */   
/*     */   private RmiDebuggedEnvironmentImpl(Environment env) throws RemoteException {
/*  71 */     super((TemplateModel)new DebugEnvironmentModel(env), 2048);
/*  72 */     synchronized (idLock) {
/*  73 */       this.id = nextId++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static synchronized Object getCachedWrapperFor(Object key) throws RemoteException {
/*  79 */     Object value = storage.get(key);
/*  80 */     if (value == null) {
/*  81 */       if (key instanceof TemplateModel) {
/*     */         int extraTypes;
/*  83 */         if (key instanceof DebugConfigurationModel) {
/*  84 */           extraTypes = 8192;
/*  85 */         } else if (key instanceof DebugTemplateModel) {
/*  86 */           extraTypes = 4096;
/*     */         } else {
/*  88 */           extraTypes = 0;
/*     */         } 
/*  90 */         value = new RmiDebugModelImpl((TemplateModel)key, extraTypes);
/*  91 */       } else if (key instanceof Environment) {
/*  92 */         value = new RmiDebuggedEnvironmentImpl((Environment)key);
/*  93 */       } else if (key instanceof Template) {
/*  94 */         value = new DebugTemplateModel((Template)key);
/*  95 */       } else if (key instanceof Configuration) {
/*  96 */         value = new DebugConfigurationModel((Configuration)key);
/*     */       } 
/*     */     }
/*  99 */     if (value != null) {
/* 100 */       storage.put(key, value);
/*     */     }
/* 102 */     if (value instanceof Remote) {
/* 103 */       remotes.add(value);
/*     */     }
/* 105 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resume() {
/* 112 */     synchronized (this) {
/* 113 */       notify();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 119 */     this.stopped = true;
/* 120 */     resume();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getId() {
/* 125 */     return this.id;
/*     */   }
/*     */   
/*     */   boolean isStopped() {
/* 129 */     return this.stopped;
/*     */   }
/*     */   
/*     */   private static abstract class DebugMapModel
/*     */     implements TemplateHashModelEx {
/*     */     public int size() {
/* 135 */       return keySet().size();
/*     */     }
/*     */     private DebugMapModel() {}
/*     */     
/*     */     public TemplateCollectionModel keys() {
/* 140 */       return (TemplateCollectionModel)new SimpleCollection(keySet());
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel values() throws TemplateModelException {
/* 145 */       Collection keys = keySet();
/* 146 */       List<TemplateModel> list = new ArrayList(keys.size());
/*     */       
/* 148 */       for (Iterator<String> it = keys.iterator(); it.hasNext();) {
/* 149 */         list.add(get(it.next()));
/*     */       }
/* 151 */       return (TemplateCollectionModel)new SimpleCollection(list);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 156 */       return (size() == 0);
/*     */     }
/*     */     
/*     */     abstract Collection keySet();
/*     */     
/*     */     static List composeList(Collection<?> c1, Collection c2) {
/* 162 */       List<Comparable> list = new ArrayList(c1);
/* 163 */       list.addAll(c2);
/* 164 */       Collections.sort(list);
/* 165 */       return list;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DebugConfigurableModel extends DebugMapModel {
/* 170 */     static final List KEYS = Arrays.asList(new String[] { "arithmetic_engine", "boolean_format", "classic_compatible", "locale", "number_format", "object_wrapper", "template_exception_handler" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     final Configurable configurable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     DebugConfigurableModel(Configurable configurable) {
/* 184 */       this.configurable = configurable;
/*     */     }
/*     */ 
/*     */     
/*     */     Collection keySet() {
/* 189 */       return KEYS;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 194 */       String s = this.configurable.getSetting(key);
/* 195 */       return (s == null) ? null : (TemplateModel)new SimpleScalar(s);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DebugConfigurationModel
/*     */     extends DebugConfigurableModel {
/* 201 */     private static final List KEYS = composeList(RmiDebuggedEnvironmentImpl.DebugConfigurableModel.KEYS, Collections.singleton("sharedVariables"));
/*     */     
/* 203 */     private TemplateModel sharedVariables = (TemplateModel)new RmiDebuggedEnvironmentImpl.DebugMapModel()
/*     */       {
/*     */         Collection keySet()
/*     */         {
/* 207 */           return ((Configuration)RmiDebuggedEnvironmentImpl.DebugConfigurationModel.this.configurable).getSharedVariableNames();
/*     */         }
/*     */ 
/*     */         
/*     */         public TemplateModel get(String key) {
/* 212 */           return ((Configuration)RmiDebuggedEnvironmentImpl.DebugConfigurationModel.this.configurable).getSharedVariable(key);
/*     */         }
/*     */       };
/*     */     
/*     */     DebugConfigurationModel(Configuration config) {
/* 217 */       super((Configurable)config);
/*     */     }
/*     */ 
/*     */     
/*     */     Collection keySet() {
/* 222 */       return KEYS;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 227 */       if ("sharedVariables".equals(key)) {
/* 228 */         return this.sharedVariables;
/*     */       }
/* 230 */       return super.get(key);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DebugTemplateModel
/*     */     extends DebugConfigurableModel {
/* 236 */     private static final List KEYS = composeList(RmiDebuggedEnvironmentImpl.DebugConfigurableModel.KEYS, 
/* 237 */         Arrays.asList(new String[] { "configuration", "name" }));
/*     */ 
/*     */     
/*     */     private final SimpleScalar name;
/*     */ 
/*     */ 
/*     */     
/*     */     DebugTemplateModel(Template template) {
/* 245 */       super((Configurable)template);
/* 246 */       this.name = new SimpleScalar(template.getName());
/*     */     }
/*     */ 
/*     */     
/*     */     Collection keySet() {
/* 251 */       return KEYS;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 256 */       if ("configuration".equals(key)) {
/*     */         try {
/* 258 */           return (TemplateModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(((Template)this.configurable).getConfiguration());
/* 259 */         } catch (RemoteException e) {
/* 260 */           throw new TemplateModelException(e);
/*     */         } 
/*     */       }
/* 263 */       if ("name".equals(key)) {
/* 264 */         return (TemplateModel)this.name;
/*     */       }
/* 266 */       return super.get(key);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DebugEnvironmentModel extends DebugConfigurableModel {
/* 271 */     private static final List KEYS = composeList(RmiDebuggedEnvironmentImpl.DebugConfigurableModel.KEYS, 
/* 272 */         Arrays.asList(new String[] { "currentNamespace", "dataModel", "globalNamespace", "knownVariables", "mainNamespace", "template" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 281 */     private TemplateModel knownVariables = (TemplateModel)new RmiDebuggedEnvironmentImpl.DebugMapModel()
/*     */       {
/*     */         Collection keySet()
/*     */         {
/*     */           try {
/* 286 */             return ((Environment)RmiDebuggedEnvironmentImpl.DebugEnvironmentModel.this.configurable).getKnownVariableNames();
/* 287 */           } catch (TemplateModelException e) {
/* 288 */             throw new UndeclaredThrowableException(e);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public TemplateModel get(String key) throws TemplateModelException {
/* 294 */           return ((Environment)RmiDebuggedEnvironmentImpl.DebugEnvironmentModel.this.configurable).getVariable(key);
/*     */         }
/*     */       };
/*     */     
/*     */     DebugEnvironmentModel(Environment env) {
/* 299 */       super((Configurable)env);
/*     */     }
/*     */ 
/*     */     
/*     */     Collection keySet() {
/* 304 */       return KEYS;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 309 */       if ("currentNamespace".equals(key)) {
/* 310 */         return (TemplateModel)((Environment)this.configurable).getCurrentNamespace();
/*     */       }
/* 312 */       if ("dataModel".equals(key)) {
/* 313 */         return (TemplateModel)((Environment)this.configurable).getDataModel();
/*     */       }
/* 315 */       if ("globalNamespace".equals(key)) {
/* 316 */         return (TemplateModel)((Environment)this.configurable).getGlobalNamespace();
/*     */       }
/* 318 */       if ("knownVariables".equals(key)) {
/* 319 */         return this.knownVariables;
/*     */       }
/* 321 */       if ("mainNamespace".equals(key)) {
/* 322 */         return (TemplateModel)((Environment)this.configurable).getMainNamespace();
/*     */       }
/* 324 */       if ("template".equals(key)) {
/*     */         try {
/* 326 */           return (TemplateModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(((Environment)this.configurable).getTemplate());
/* 327 */         } catch (RemoteException e) {
/* 328 */           throw new TemplateModelException(e);
/*     */         } 
/*     */       }
/* 331 */       return super.get(key);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void cleanup() {
/* 336 */     for (Iterator i = remotes.iterator(); i.hasNext(); ) {
/* 337 */       Object remoteObject = i.next();
/*     */       try {
/* 339 */         UnicastRemoteObject.unexportObject((Remote)remoteObject, true);
/* 340 */       } catch (Exception exception) {}
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebuggedEnvironmentImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */