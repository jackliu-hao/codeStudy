package org.wildfly.common.selector;

import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicReference;
import org.wildfly.common.Assert;

/** @deprecated */
@Deprecated
public abstract class Selector<T> {
   private static final Selector<?> NULL = new Selector<Object>() {
      public Object get() {
         return null;
      }
   };
   private static final ClassValue<Holder<?>> selVal = new ClassValue<Holder<?>>() {
      protected Holder<?> computeValue(Class<?> type) {
         return this.doCompute(type);
      }

      private <S> Holder<S> doCompute(Class<S> type) {
         Selector<S> selector = null;

         try {
            DefaultSelector defaultSelector = (DefaultSelector)type.getAnnotation(DefaultSelector.class);
            if (defaultSelector != null) {
               Class<? extends Selector<?>> selectorType = defaultSelector.value();
               selector = (Selector)selectorType.getConstructor().newInstance();
            }
         } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException var5) {
         }

         Holder<S> holder = new Holder(type);
         holder.set(selector);
         return holder;
      }
   };

   protected Selector() {
   }

   public abstract T get();

   public static <T> Selector<T> nullSelector() {
      return NULL;
   }

   public static <T> Selector<T> selectorFor(Class<T> clazz) {
      Assert.checkNotNullParam("clazz", clazz);
      Holder<T> holder = (Holder)selVal.get(clazz);
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(holder.getGetPermission());
      }

      Selector<T> sel = (Selector)holder.get();
      return sel == null ? nullSelector() : sel;
   }

   public static <T> void setSelectorFor(Class<T> clazz, Selector<T> selector) {
      Assert.checkNotNullParam("clazz", clazz);
      Holder<T> holder = (Holder)selVal.get(clazz);
      boolean set = false;
      boolean change = false;

      while(true) {
         Selector<T> oldValue = (Selector)holder.get();
         if (oldValue == selector) {
            return;
         }

         SecurityManager sm;
         if (oldValue == null) {
            if (!set) {
               sm = System.getSecurityManager();
               if (sm != null) {
                  sm.checkPermission(holder.getSetPermission());
               }

               set = true;
            }

            if (holder.compareAndSet((Object)null, selector)) {
               return;
            }
         } else {
            if (!change) {
               sm = System.getSecurityManager();
               if (sm != null) {
                  sm.checkPermission(holder.getChangePermission());
               }

               change = true;
            }

            if (holder.compareAndSet(oldValue, selector)) {
               return;
            }
         }
      }
   }

   public static <T> Getter<T> selectorGetterFor(Class<T> clazz) {
      Assert.checkNotNullParam("clazz", clazz);
      Holder<T> holder = (Holder)selVal.get(clazz);
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(holder.getGetPermission());
      }

      return new Getter(holder);
   }

   public static <T> PrivilegedAction<Getter<T>> selectorGetterActionFor(Class<T> clazz) {
      Assert.checkNotNullParam("clazz", clazz);
      return () -> {
         return selectorGetterFor(clazz);
      };
   }

   public static SelectorPermission getGetPermissionFor(Class<?> clazz) {
      Assert.checkNotNullParam("clazz", clazz);
      return ((Holder)selVal.get(clazz)).getGetPermission();
   }

   public static SelectorPermission getSetPermissionFor(Class<?> clazz) {
      Assert.checkNotNullParam("clazz", clazz);
      return ((Holder)selVal.get(clazz)).getSetPermission();
   }

   public static SelectorPermission getChangePermissionFor(Class<?> clazz) {
      Assert.checkNotNullParam("clazz", clazz);
      return ((Holder)selVal.get(clazz)).getChangePermission();
   }

   static final class Holder<T> extends AtomicReference<Selector<T>> {
      private final Class<T> clazz;
      private final SelectorPermission getPermission;
      private final SelectorPermission setPermission;
      private final SelectorPermission changePermission;
      private final AtomicReference<Object> lockRef = new AtomicReference();

      Holder(Class<T> clazz) {
         Assert.assertNotNull(clazz);
         this.clazz = clazz;
         this.getPermission = new SelectorPermission(clazz.getName(), "get");
         this.setPermission = new SelectorPermission(clazz.getName(), "set");
         this.changePermission = new SelectorPermission(clazz.getName(), "change");
      }

      Class<T> getClazz() {
         return this.clazz;
      }

      SelectorPermission getGetPermission() {
         return this.getPermission;
      }

      SelectorPermission getSetPermission() {
         return this.setPermission;
      }

      SelectorPermission getChangePermission() {
         return this.changePermission;
      }

      void lock(Object key) {
         Assert.assertNotNull(key);
         if (!this.lockRef.compareAndSet((Object)null, key)) {
            throw new SecurityException("Selector is locked");
         }
      }

      void unlock(Object key) {
         Assert.assertNotNull(key);
         if (!this.lockRef.compareAndSet(key, (Object)null)) {
            throw new SecurityException("Selector could not be unlocked");
         }
      }
   }

   public static final class Getter<T> {
      private final Holder<T> holder;

      Getter(Holder<T> holder) {
         this.holder = holder;
      }

      public Selector<T> getSelector() {
         Selector<T> sel = (Selector)this.holder.get();
         return sel == null ? Selector.nullSelector() : sel;
      }
   }
}
