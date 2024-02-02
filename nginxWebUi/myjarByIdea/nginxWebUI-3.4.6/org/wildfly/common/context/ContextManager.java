package org.wildfly.common.context;

import java.security.AccessController;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.wildfly.common.Assert;

public final class ContextManager<C extends Contextual<C>> implements Supplier<C> {
   private final AtomicReference<Supplier<C>> globalDefaultSupplierRef;
   private final ConcurrentHashMap<ClassLoader, Supplier<C>> perClassLoaderDefault;
   private final Class<C> type;
   private final String name;
   private final ThreadLocal<State<C>> stateRef;
   private final ContextPermission getPermission;

   public ContextManager(Class<C> type) {
      this(type, type.getName());
   }

   public ContextManager(Class<C> type, String name) {
      this.globalDefaultSupplierRef = new AtomicReference();
      this.perClassLoaderDefault = new ConcurrentHashMap();
      this.stateRef = ThreadLocal.withInitial(State::new);
      Assert.checkNotNullParam("type", type);
      Assert.checkNotNullParam("name", name);
      Assert.checkNotEmptyParam("name", name);
      this.type = type;
      this.name = name;
      this.getPermission = new ContextPermission(name, "get");
   }

   public C getGlobalDefault() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "getGlobalDefault"));
      }

      Supplier<C> globalDefault = (Supplier)this.globalDefaultSupplierRef.get();
      return globalDefault == null ? null : (Contextual)globalDefault.get();
   }

   public void setGlobalDefaultSupplier(Supplier<C> supplier) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "setGlobalDefaultSupplier"));
      }

      this.globalDefaultSupplierRef.set(supplier);
   }

   public boolean setGlobalDefaultSupplierIfNotSet(Supplier<Supplier<C>> supplierSupplier) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "setGlobalDefaultSupplier"));
      }

      AtomicReference<Supplier<C>> ref = this.globalDefaultSupplierRef;
      return ref.get() == null && ref.compareAndSet((Object)null, (Supplier)supplierSupplier.get());
   }

   public void setGlobalDefault(C globalDefault) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "getGlobalDefault"));
      }

      this.globalDefaultSupplierRef.set(globalDefault == null ? null : () -> {
         return globalDefault;
      });
   }

   public C getClassLoaderDefault(ClassLoader classLoader) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "getClassLoaderDefault"));
      }

      if (classLoader == null) {
         return null;
      } else {
         Supplier<C> supplier = (Supplier)this.perClassLoaderDefault.get(classLoader);
         return supplier == null ? null : (Contextual)supplier.get();
      }
   }

   public void setClassLoaderDefaultSupplier(ClassLoader classLoader, Supplier<C> supplier) {
      Assert.checkNotNullParam("classLoader", classLoader);
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "setClassLoaderDefaultSupplier"));
      }

      if (supplier == null) {
         this.perClassLoaderDefault.remove(classLoader);
      } else {
         this.perClassLoaderDefault.put(classLoader, supplier);
      }

   }

   public void setClassLoaderDefault(ClassLoader classLoader, C classLoaderDefault) {
      Assert.checkNotNullParam("classLoader", classLoader);
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "setClassLoaderDefault"));
      }

      if (classLoaderDefault == null) {
         this.perClassLoaderDefault.remove(classLoader);
      } else {
         this.perClassLoaderDefault.put(classLoader, () -> {
            return classLoaderDefault;
         });
      }

   }

   public C getThreadDefault() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "getThreadDefault"));
      }

      Supplier<C> defaultSupplier = ((State)this.stateRef.get()).defaultSupplier;
      return defaultSupplier == null ? null : (Contextual)defaultSupplier.get();
   }

   public void setThreadDefaultSupplier(Supplier<C> supplier) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "setThreadDefaultSupplier"));
      }

      ((State)this.stateRef.get()).defaultSupplier = supplier;
   }

   public void setThreadDefault(C threadDefault) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "setThreadDefault"));
      }

      ((State)this.stateRef.get()).defaultSupplier = threadDefault == null ? null : () -> {
         return threadDefault;
      };
   }

   public C get() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(this.getPermission);
      }

      return this.getPrivileged();
   }

   public Supplier<C> getPrivilegedSupplier() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new ContextPermission(this.name, "getPrivilegedSupplier"));
      }

      return this::getPrivileged;
   }

   private C getPrivileged() {
      State<C> state = (State)this.stateRef.get();
      C c = (Contextual)state.current;
      if (c != null) {
         return c;
      } else {
         Thread currentThread = Thread.currentThread();
         SecurityManager sm = System.getSecurityManager();
         ClassLoader classLoader;
         if (sm != null) {
            Objects.requireNonNull(currentThread);
            classLoader = (ClassLoader)AccessController.doPrivileged(currentThread::getContextClassLoader);
         } else {
            classLoader = currentThread.getContextClassLoader();
         }

         Supplier supplier;
         if (classLoader != null) {
            supplier = (Supplier)this.perClassLoaderDefault.get(classLoader);
            if (supplier != null) {
               c = (Contextual)supplier.get();
               if (c != null) {
                  return c;
               }
            }
         }

         supplier = state.defaultSupplier;
         if (supplier != null) {
            c = (Contextual)supplier.get();
            if (c != null) {
               return c;
            }
         }

         supplier = (Supplier)this.globalDefaultSupplierRef.get();
         return supplier != null ? (Contextual)supplier.get() : null;
      }
   }

   C getAndSetCurrent(Contextual<C> newVal) {
      C cast = (Contextual)this.type.cast(newVal);
      State<C> state = (State)this.stateRef.get();

      Contextual var4;
      try {
         var4 = (Contextual)state.current;
      } finally {
         state.current = cast;
      }

      return var4;
   }

   void restoreCurrent(C oldVal) {
      ((State)this.stateRef.get()).current = oldVal;
   }

   static class State<T> {
      T current;
      Supplier<T> defaultSupplier;
   }
}
