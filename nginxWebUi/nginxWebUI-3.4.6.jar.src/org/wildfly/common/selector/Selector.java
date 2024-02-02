/*     */ package org.wildfly.common.selector;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class Selector<T>
/*     */ {
/*  38 */   private static final Selector<?> NULL = new Selector<Object>() {
/*     */       public Object get() {
/*  40 */         return null;
/*     */       }
/*     */     };
/*     */   
/*  44 */   private static final ClassValue<Holder<?>> selVal = new ClassValue<Holder<?>>() {
/*     */       protected Selector.Holder<?> computeValue(Class<?> type) {
/*  46 */         return doCompute(type);
/*     */       }
/*     */       
/*     */       private <S> Selector.Holder<S> doCompute(Class<S> type) {
/*  50 */         Selector<S> selector = null;
/*     */         try {
/*  52 */           DefaultSelector defaultSelector = type.<DefaultSelector>getAnnotation(DefaultSelector.class);
/*  53 */           if (defaultSelector != null) {
/*  54 */             Class<? extends Selector<?>> selectorType = defaultSelector.value();
/*  55 */             selector = selectorType.getConstructor(new Class[0]).newInstance(new Object[0]);
/*     */           } 
/*  57 */         } catch (InvocationTargetException|NoSuchMethodException|InstantiationException|IllegalAccessException invocationTargetException) {}
/*     */         
/*  59 */         Selector.Holder<S> holder = new Selector.Holder<>(type);
/*  60 */         holder.set(selector);
/*  61 */         return holder;
/*     */       }
/*     */     };
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
/*     */   public static <T> Selector<T> nullSelector() {
/*  83 */     return (Selector)NULL;
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
/*     */   
/*     */   public static <T> Selector<T> selectorFor(Class<T> clazz) {
/*  96 */     Assert.checkNotNullParam("clazz", clazz);
/*  97 */     Holder<T> holder = (Holder<T>)selVal.get(clazz);
/*  98 */     SecurityManager sm = System.getSecurityManager();
/*  99 */     if (sm != null) {
/* 100 */       sm.checkPermission(holder.getGetPermission());
/*     */     }
/* 102 */     Selector<T> sel = holder.get();
/* 103 */     return (sel == null) ? nullSelector() : sel;
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
/*     */ 
/*     */   
/*     */   public static <T> void setSelectorFor(Class<T> clazz, Selector<T> selector) {
/* 117 */     Assert.checkNotNullParam("clazz", clazz);
/* 118 */     Holder<T> holder = (Holder<T>)selVal.get(clazz);
/*     */     
/* 120 */     boolean set = false, change = false;
/*     */     while (true) {
/* 122 */       Selector<T> oldValue = holder.get();
/* 123 */       if (oldValue == selector) {
/*     */         return;
/*     */       }
/* 126 */       if (oldValue == null) {
/* 127 */         if (!set) {
/* 128 */           SecurityManager sm = System.getSecurityManager();
/* 129 */           if (sm != null) {
/* 130 */             sm.checkPermission(holder.getSetPermission());
/*     */           }
/* 132 */           set = true;
/*     */         } 
/* 134 */         if (holder.compareAndSet(null, selector))
/*     */           return; 
/*     */         continue;
/*     */       } 
/* 138 */       if (!change) {
/* 139 */         SecurityManager sm = System.getSecurityManager();
/* 140 */         if (sm != null) {
/* 141 */           sm.checkPermission(holder.getChangePermission());
/*     */         }
/* 143 */         change = true;
/*     */       } 
/* 145 */       if (holder.compareAndSet(oldValue, selector)) {
/*     */         break;
/*     */       }
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Getter<T> selectorGetterFor(Class<T> clazz) {
/* 162 */     Assert.checkNotNullParam("clazz", clazz);
/* 163 */     Holder<T> holder = (Holder<T>)selVal.get(clazz);
/* 164 */     SecurityManager sm = System.getSecurityManager();
/* 165 */     if (sm != null) {
/* 166 */       sm.checkPermission(holder.getGetPermission());
/*     */     }
/* 168 */     return new Getter<>(holder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> PrivilegedAction<Getter<T>> selectorGetterActionFor(Class<T> clazz) {
/* 179 */     Assert.checkNotNullParam("clazz", clazz);
/* 180 */     return () -> selectorGetterFor(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SelectorPermission getGetPermissionFor(Class<?> clazz) {
/* 190 */     Assert.checkNotNullParam("clazz", clazz);
/* 191 */     return ((Holder)selVal.get(clazz)).getGetPermission();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SelectorPermission getSetPermissionFor(Class<?> clazz) {
/* 201 */     Assert.checkNotNullParam("clazz", clazz);
/* 202 */     return ((Holder)selVal.get(clazz)).getSetPermission();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SelectorPermission getChangePermissionFor(Class<?> clazz) {
/* 212 */     Assert.checkNotNullParam("clazz", clazz);
/* 213 */     return ((Holder)selVal.get(clazz)).getChangePermission();
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract T get();
/*     */ 
/*     */   
/*     */   public static final class Getter<T>
/*     */   {
/*     */     private final Selector.Holder<T> holder;
/*     */     
/*     */     Getter(Selector.Holder<T> holder) {
/* 225 */       this.holder = holder;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Selector<T> getSelector() {
/* 234 */       Selector<T> sel = this.holder.get();
/* 235 */       return (sel == null) ? Selector.<T>nullSelector() : sel;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Holder<T>
/*     */     extends AtomicReference<Selector<T>> {
/*     */     private final Class<T> clazz;
/*     */     private final SelectorPermission getPermission;
/*     */     private final SelectorPermission setPermission;
/*     */     private final SelectorPermission changePermission;
/* 245 */     private final AtomicReference<Object> lockRef = new AtomicReference();
/*     */     
/*     */     Holder(Class<T> clazz) {
/* 248 */       Assert.assertNotNull(clazz);
/* 249 */       this.clazz = clazz;
/* 250 */       this.getPermission = new SelectorPermission(clazz.getName(), "get");
/* 251 */       this.setPermission = new SelectorPermission(clazz.getName(), "set");
/* 252 */       this.changePermission = new SelectorPermission(clazz.getName(), "change");
/*     */     }
/*     */     
/*     */     Class<T> getClazz() {
/* 256 */       return this.clazz;
/*     */     }
/*     */     
/*     */     SelectorPermission getGetPermission() {
/* 260 */       return this.getPermission;
/*     */     }
/*     */     
/*     */     SelectorPermission getSetPermission() {
/* 264 */       return this.setPermission;
/*     */     }
/*     */     
/*     */     SelectorPermission getChangePermission() {
/* 268 */       return this.changePermission;
/*     */     }
/*     */     
/*     */     void lock(Object key) {
/* 272 */       Assert.assertNotNull(key);
/* 273 */       if (!this.lockRef.compareAndSet(null, key)) {
/* 274 */         throw new SecurityException("Selector is locked");
/*     */       }
/*     */     }
/*     */     
/*     */     void unlock(Object key) {
/* 279 */       Assert.assertNotNull(key);
/* 280 */       if (!this.lockRef.compareAndSet(key, null))
/* 281 */         throw new SecurityException("Selector could not be unlocked"); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\selector\Selector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */