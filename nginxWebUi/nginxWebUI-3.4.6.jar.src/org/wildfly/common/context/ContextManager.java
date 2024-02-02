/*     */ package org.wildfly.common.context;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Supplier;
/*     */ import org.wildfly.common.Assert;
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
/*     */ public final class ContextManager<C extends Contextual<C>>
/*     */   implements Supplier<C>
/*     */ {
/*  37 */   private final AtomicReference<Supplier<C>> globalDefaultSupplierRef = new AtomicReference<>();
/*  38 */   private final ConcurrentHashMap<ClassLoader, Supplier<C>> perClassLoaderDefault = new ConcurrentHashMap<>();
/*     */   private final Class<C> type;
/*     */   private final String name;
/*  41 */   private final ThreadLocal<State<C>> stateRef = ThreadLocal.withInitial(State::new);
/*     */ 
/*     */ 
/*     */   
/*     */   private final ContextPermission getPermission;
/*     */ 
/*     */ 
/*     */   
/*     */   public ContextManager(Class<C> type) {
/*  50 */     this(type, type.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContextManager(Class<C> type, String name) {
/*  60 */     Assert.checkNotNullParam("type", type);
/*  61 */     Assert.checkNotNullParam("name", name);
/*  62 */     Assert.checkNotEmptyParam("name", name);
/*  63 */     this.type = type;
/*  64 */     this.name = name;
/*     */     
/*  66 */     this.getPermission = new ContextPermission(name, "get");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public C getGlobalDefault() {
/*  76 */     SecurityManager sm = System.getSecurityManager();
/*  77 */     if (sm != null) {
/*  78 */       sm.checkPermission(new ContextPermission(this.name, "getGlobalDefault"));
/*     */     }
/*  80 */     Supplier<C> globalDefault = this.globalDefaultSupplierRef.get();
/*  81 */     return (globalDefault == null) ? null : globalDefault.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGlobalDefaultSupplier(Supplier<C> supplier) {
/*  91 */     SecurityManager sm = System.getSecurityManager();
/*  92 */     if (sm != null) {
/*  93 */       sm.checkPermission(new ContextPermission(this.name, "setGlobalDefaultSupplier"));
/*     */     }
/*  95 */     this.globalDefaultSupplierRef.set(supplier);
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
/*     */   public boolean setGlobalDefaultSupplierIfNotSet(Supplier<Supplier<C>> supplierSupplier) {
/* 107 */     SecurityManager sm = System.getSecurityManager();
/* 108 */     if (sm != null) {
/* 109 */       sm.checkPermission(new ContextPermission(this.name, "setGlobalDefaultSupplier"));
/*     */     }
/* 111 */     AtomicReference<Supplier<C>> ref = this.globalDefaultSupplierRef;
/*     */     
/* 113 */     return (ref.get() == null && ref.compareAndSet(null, supplierSupplier.get()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGlobalDefault(C globalDefault) {
/* 123 */     SecurityManager sm = System.getSecurityManager();
/* 124 */     if (sm != null) {
/* 125 */       sm.checkPermission(new ContextPermission(this.name, "getGlobalDefault"));
/*     */     }
/* 127 */     this.globalDefaultSupplierRef.set((globalDefault == null) ? null : (() -> globalDefault));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public C getClassLoaderDefault(ClassLoader classLoader) {
/* 138 */     SecurityManager sm = System.getSecurityManager();
/* 139 */     if (sm != null) {
/* 140 */       sm.checkPermission(new ContextPermission(this.name, "getClassLoaderDefault"));
/*     */     }
/*     */     
/* 143 */     if (classLoader == null) {
/* 144 */       return null;
/*     */     }
/* 146 */     Supplier<C> supplier = this.perClassLoaderDefault.get(classLoader);
/* 147 */     return (supplier == null) ? null : supplier.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClassLoaderDefaultSupplier(ClassLoader classLoader, Supplier<C> supplier) {
/* 158 */     Assert.checkNotNullParam("classLoader", classLoader);
/* 159 */     SecurityManager sm = System.getSecurityManager();
/* 160 */     if (sm != null) {
/* 161 */       sm.checkPermission(new ContextPermission(this.name, "setClassLoaderDefaultSupplier"));
/*     */     }
/* 163 */     if (supplier == null) {
/* 164 */       this.perClassLoaderDefault.remove(classLoader);
/*     */     } else {
/* 166 */       this.perClassLoaderDefault.put(classLoader, supplier);
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
/*     */   public void setClassLoaderDefault(ClassLoader classLoader, C classLoaderDefault) {
/* 178 */     Assert.checkNotNullParam("classLoader", classLoader);
/* 179 */     SecurityManager sm = System.getSecurityManager();
/* 180 */     if (sm != null) {
/* 181 */       sm.checkPermission(new ContextPermission(this.name, "setClassLoaderDefault"));
/*     */     }
/* 183 */     if (classLoaderDefault == null) {
/* 184 */       this.perClassLoaderDefault.remove(classLoader);
/*     */     } else {
/* 186 */       this.perClassLoaderDefault.put(classLoader, () -> classLoaderDefault);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public C getThreadDefault() {
/* 197 */     SecurityManager sm = System.getSecurityManager();
/* 198 */     if (sm != null) {
/* 199 */       sm.checkPermission(new ContextPermission(this.name, "getThreadDefault"));
/*     */     }
/* 201 */     Supplier<C> defaultSupplier = ((State)this.stateRef.get()).defaultSupplier;
/* 202 */     return (defaultSupplier == null) ? null : defaultSupplier.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreadDefaultSupplier(Supplier<C> supplier) {
/* 212 */     SecurityManager sm = System.getSecurityManager();
/* 213 */     if (sm != null) {
/* 214 */       sm.checkPermission(new ContextPermission(this.name, "setThreadDefaultSupplier"));
/*     */     }
/* 216 */     ((State)this.stateRef.get()).defaultSupplier = supplier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreadDefault(C threadDefault) {
/* 226 */     SecurityManager sm = System.getSecurityManager();
/* 227 */     if (sm != null) {
/* 228 */       sm.checkPermission(new ContextPermission(this.name, "setThreadDefault"));
/*     */     }
/* 230 */     ((State)this.stateRef.get()).defaultSupplier = (threadDefault == null) ? null : (() -> threadDefault);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public C get() {
/* 239 */     SecurityManager sm = System.getSecurityManager();
/* 240 */     if (sm != null) {
/* 241 */       sm.checkPermission(this.getPermission);
/*     */     }
/* 243 */     return getPrivileged();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Supplier<C> getPrivilegedSupplier() {
/* 253 */     SecurityManager sm = System.getSecurityManager();
/* 254 */     if (sm != null) {
/* 255 */       sm.checkPermission(new ContextPermission(this.name, "getPrivilegedSupplier"));
/*     */     }
/* 257 */     return this::getPrivileged;
/*     */   }
/*     */   private C getPrivileged() {
/*     */     ClassLoader classLoader;
/* 261 */     State<C> state = this.stateRef.get();
/* 262 */     Contextual contextual = (Contextual)state.current;
/* 263 */     if (contextual != null) return (C)contextual; 
/* 264 */     Thread currentThread = Thread.currentThread();
/* 265 */     SecurityManager sm = System.getSecurityManager();
/*     */     
/* 267 */     if (sm != null) {
/* 268 */       Objects.requireNonNull(currentThread); classLoader = AccessController.<ClassLoader>doPrivileged(currentThread::getContextClassLoader);
/*     */     } else {
/* 270 */       classLoader = currentThread.getContextClassLoader();
/*     */     } 
/*     */     
/* 273 */     if (classLoader != null) {
/* 274 */       Supplier<C> supplier1 = this.perClassLoaderDefault.get(classLoader);
/* 275 */       if (supplier1 != null) {
/* 276 */         contextual = (Contextual)supplier1.get();
/* 277 */         if (contextual != null) return (C)contextual; 
/*     */       } 
/*     */     } 
/* 280 */     Supplier<C> supplier = state.defaultSupplier;
/* 281 */     if (supplier != null) {
/* 282 */       contextual = (Contextual)supplier.get();
/* 283 */       if (contextual != null) return (C)contextual; 
/*     */     } 
/* 285 */     supplier = this.globalDefaultSupplierRef.get();
/* 286 */     return (supplier != null) ? supplier.get() : null;
/*     */   }
/*     */   
/*     */   C getAndSetCurrent(Contextual<C> newVal) {
/* 290 */     Contextual contextual = (Contextual)this.type.cast(newVal);
/* 291 */     State<C> state = this.stateRef.get();
/*     */     try {
/* 293 */       return (C)state.current;
/*     */     } finally {
/* 295 */       state.current = (T)contextual;
/*     */     } 
/*     */   }
/*     */   
/*     */   void restoreCurrent(C oldVal) {
/* 300 */     ((State)this.stateRef.get()).current = (T)oldVal;
/*     */   }
/*     */   
/*     */   static class State<T> {
/*     */     T current;
/*     */     Supplier<T> defaultSupplier;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\context\ContextManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */