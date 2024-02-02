package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.core.handle.HandlerLoader;
import org.noear.solon.core.util.ConvertUtil;

public abstract class BeanContainer {
   private final Props props;
   private final ClassLoader classLoader;
   private Map<Class<?>, Object> attrs = new HashMap();
   private final Map<Class<?>, BeanWrap> beanWraps = new HashMap();
   private final Set<BeanWrap> beanWrapSet = new HashSet();
   private final Map<String, BeanWrap> beans = new HashMap();
   private final Map<Class<?>, Class<?>> clzMapping = new HashMap();
   protected final Map<Class<?>, BeanBuilder<?>> beanBuilders = new HashMap();
   protected final Map<Class<?>, BeanInjector<?>> beanInjectors = new HashMap();
   protected final Map<Class<?>, BeanExtractor<?>> beanExtractors = new HashMap();
   protected final Map<Class<?>, InterceptorEntity> beanInterceptors = new HashMap();
   protected final Set<SubscriberEntity> beanSubscribers = new LinkedHashSet();

   public BeanContainer(ClassLoader classLoader, Props props) {
      this.classLoader = classLoader;
      this.props = props;
   }

   public Props getProps() {
      return (Props)(this.props == null ? Solon.cfg() : this.props);
   }

   public Map<Class<?>, Object> getAttrs() {
      return this.attrs;
   }

   public ClassLoader getClassLoader() {
      return (ClassLoader)(this.classLoader == null ? JarClassLoader.global() : this.classLoader);
   }

   public void clear() {
      this.beanWraps.clear();
      this.beanWrapSet.clear();
      this.beans.clear();
      this.clzMapping.clear();
      this.attrs.clear();
   }

   public void copyTo(BeanContainer container) {
      this.beanBuilders.forEach((k, v) -> {
         container.beanBuilders.putIfAbsent(k, v);
      });
      this.beanInjectors.forEach((k, v) -> {
         container.beanInjectors.putIfAbsent(k, v);
      });
      this.beanInterceptors.forEach((k, v) -> {
         container.beanInterceptors.putIfAbsent(k, v);
      });
      this.beanExtractors.forEach((k, v) -> {
         container.beanExtractors.putIfAbsent(k, v);
      });
   }

   public <T extends Annotation> void beanBuilderAdd(Class<T> anno, BeanBuilder<T> builder) {
      this.beanBuilders.put(anno, builder);
   }

   public <T extends Annotation> void beanInjectorAdd(Class<T> anno, BeanInjector<T> injector) {
      this.beanInjectors.put(anno, injector);
   }

   public <T extends Annotation> void beanExtractorAdd(Class<T> anno, BeanExtractor<T> extractor) {
      this.beanExtractors.put(anno, extractor);
   }

   public <T extends Annotation> void beanAroundAdd(Class<T> anno, Interceptor interceptor, int index) {
      this.beanInterceptors.put(anno, new InterceptorEntity(index, interceptor));
   }

   public <T extends Annotation> void beanAroundAdd(Class<T> anno, Interceptor interceptor) {
      this.beanAroundAdd(anno, interceptor, 0);
   }

   public <T extends Annotation> InterceptorEntity beanAroundGet(Class<T> anno) {
      return (InterceptorEntity)this.beanInterceptors.get(anno);
   }

   public void beanSubscribe(Object nameOrType, Consumer<BeanWrap> callback) {
      if (nameOrType != null) {
         this.beanSubscribers.add(new SubscriberEntity(nameOrType, callback));
      }

   }

   public void beanNotice(Object nameOrType, BeanWrap wrap) {
      if (wrap.raw() != null) {
         (new ArrayList(this.beanSubscribers)).forEach((s1) -> {
            if (s1.key.equals(nameOrType)) {
               s1.callback.accept(wrap);
            }

         });
      }
   }

   public synchronized void putWrap(String name, BeanWrap wrap) {
      if (!Utils.isEmpty(name) && wrap.raw() != null && !this.beans.containsKey(name)) {
         this.beans.put(name, wrap);
         this.beanNotice(name, wrap);
      }

   }

   public synchronized void putWrap(Class<?> type, BeanWrap wrap) {
      if (type != null && wrap.raw() != null && !this.beanWraps.containsKey(type)) {
         this.beanWraps.put(type, wrap);
         this.beanWrapSet.add(wrap);
         this.beanNotice(type, wrap);
      }

   }

   public boolean hasWrap(Object nameOrType) {
      return this.getWrap(nameOrType) != null;
   }

   public BeanWrap getWrap(Object nameOrType) {
      return nameOrType instanceof String ? (BeanWrap)this.beans.get(nameOrType) : (BeanWrap)this.beanWraps.get(nameOrType);
   }

   public void getWrapAsyn(Object nameOrType, Consumer<BeanWrap> callback) {
      BeanWrap bw = this.getWrap(nameOrType);
      if (bw != null && bw.raw() != null) {
         callback.accept(bw);
      } else {
         this.beanSubscribe(nameOrType, callback);
      }

   }

   public <T> T getBean(Object nameOrType) {
      BeanWrap bw = this.getWrap(nameOrType);
      return bw == null ? null : bw.get();
   }

   public <T> T getBeanOrNew(Class<T> type) {
      return this.wrapAndPut(type).get();
   }

   public <T> void getBeanAsyn(Object nameOrType, Consumer<T> callback) {
      this.getWrapAsyn(nameOrType, (bw) -> {
         callback.accept(bw.get());
      });
   }

   public BeanWrap wrap(Class<?> type, Object bean) {
      BeanWrap wrap = this.getWrap(type);
      if (wrap == null) {
         wrap = this.wrapCreate(type, bean);
      }

      return wrap;
   }

   public BeanWrap wrapAndPut(Class<?> type) {
      return this.wrapAndPut(type, (Object)null);
   }

   public BeanWrap wrapAndPut(Class<?> type, Object bean) {
      BeanWrap wrap = this.getWrap(type);
      if (wrap == null) {
         wrap = this.wrapCreate(type, bean);
         this.putWrap(type, wrap);
      }

      return wrap;
   }

   protected abstract BeanWrap wrapCreate(Class<?> type, Object bean);

   public void beanRegister(BeanWrap bw, String name, boolean typed) {
      if (Utils.isNotEmpty(name)) {
         this.putWrap(name, bw);
         if (!typed) {
            return;
         }
      }

      this.putWrap(bw.clz(), bw);
      this.putWrap(bw.clz().getName(), bw);
      this.beanRegisterSup0(bw);
      if (bw.remoting()) {
         HandlerLoader bww = new HandlerLoader(bw);
         if (bww.mapping() != null) {
            bww.load(Solon.app());
         }
      }

   }

   protected void beanRegisterSup0(BeanWrap bw) {
      Class<?>[] list = bw.clz().getInterfaces();
      Class[] var3 = list;
      int var4 = list.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         Class<?> c = var3[var5];
         if (!c.getName().contains("java.")) {
            this.clzMapping.putIfAbsent(c, bw.clz());
            this.putWrap(c, bw);
         }
      }

      Type[] list2 = bw.clz().getGenericInterfaces();
      Type[] var9 = list2;
      var5 = list2.length;

      for(int var10 = 0; var10 < var5; ++var10) {
         Type t = var9[var10];
         if (t instanceof ParameterizedType) {
            this.putWrap(t.getTypeName(), bw);
         }
      }

   }

   public void beanInject(VarHolder varH, String name) {
      this.beanInject(varH, name, false);
   }

   protected void beanInject(VarHolder varH, String name, boolean autoRefreshed) {
      if (Utils.isEmpty(name)) {
         if (AopContext.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this);
            return;
         }

         if (varH.getGenericType() != null) {
            this.getWrapAsyn(varH.getGenericType().getTypeName(), (bw) -> {
               varH.setValue(bw.get());
            });
         } else {
            this.getWrapAsyn(varH.getType(), (bw) -> {
               varH.setValue(bw.get());
            });
         }
      } else {
         String name2;
         if (name.startsWith("${classpath:")) {
            name2 = name.substring(12, name.length() - 1);
            Properties val = Utils.loadProperties(Utils.getResource(this.getClassLoader(), name2));
            if (val == null) {
               throw new IllegalStateException(name + "  failed to load!");
            }

            if (Properties.class == varH.getType()) {
               varH.setValue(val);
            } else if (Map.class == varH.getType()) {
               Map<String, String> val2 = new HashMap();
               val.forEach((k, v) -> {
                  if (k instanceof String && v instanceof String) {
                     val2.put((String)k, (String)v);
                  }

               });
               varH.setValue(val2);
            } else {
               Object val2 = PropsConverter.global().convert(val, (Object)null, varH.getType(), varH.getGenericType());
               varH.setValue(val2);
            }
         } else if (name.startsWith("${")) {
            name2 = name.substring(2, name.length() - 1).trim();
            this.beanInjectConfig(varH, name2);
            if (autoRefreshed && varH.isField()) {
               this.getProps().onChange((key, valx) -> {
                  if (key.startsWith(name2)) {
                     this.beanInjectConfig(varH, name2);
                  }

               });
            }
         } else {
            this.getWrapAsyn(name, (bw) -> {
               if (BeanWrap.class.isAssignableFrom(varH.getType())) {
                  varH.setValue(bw);
               } else {
                  varH.setValue(bw.get());
               }

            });
         }
      }

   }

   protected void beanInjectProperties(Class<?> clz, Object obj) {
      Inject typeInj = (Inject)clz.getAnnotation(Inject.class);
      if (typeInj != null && Utils.isNotEmpty(typeInj.value()) && typeInj.value().startsWith("${")) {
         Utils.injectProperties(obj, this.getProps().getPropByExpr(typeInj.value()));
      }

   }

   private void beanInjectConfig(VarHolder varH, String name) {
      if (Properties.class == varH.getType()) {
         Properties val = this.getProps().getProp(name);
         varH.setValue(val);
      } else {
         String def = null;
         int defIdx = name.indexOf(":");
         if (defIdx > 0) {
            if (name.length() > defIdx + 1) {
               def = name.substring(defIdx + 1).trim();
            } else {
               def = "";
            }

            name = name.substring(0, defIdx).trim();
         }

         String val = this.getProps().get(name);
         if (def != null && Utils.isEmpty(val)) {
            val = def;
         }

         if (val == null) {
            Class<?> pt = varH.getType();
            if (!pt.getName().startsWith("java.lang.") && !pt.isPrimitive()) {
               Properties val0 = this.getProps().getProp(name);
               if (val0.size() > 0) {
                  Object val2 = PropsConverter.global().convert(val0, (Object)null, pt, varH.getGenericType());
                  varH.setValue(val2);
               }
            }
         } else {
            Object val2 = ConvertUtil.to(varH.getType(), val);
            varH.setValue(val2);
         }
      }

   }

   @Note("遍历bean库 (拿到的是bean包装)")
   public void beanForeach(BiConsumer<String, BeanWrap> action) {
      this.beans.forEach(action);
   }

   @Note("遍历bean包装库")
   public void beanForeach(Consumer<BeanWrap> action) {
      (new ArrayList(this.beanWrapSet)).forEach((bw) -> {
         action.accept(bw);
      });
   }

   @Note("查找bean库 (拿到的是bean包装)")
   public List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> condition) {
      List<BeanWrap> list = new ArrayList();
      this.beanForeach((k, v) -> {
         if (condition.test(k, v)) {
            list.add(v);
         }

      });
      return list;
   }

   @Note("查找bean包装库")
   public List<BeanWrap> beanFind(Predicate<BeanWrap> condition) {
      List<BeanWrap> list = new ArrayList();
      this.beanForeach((v) -> {
         if (condition.test(v)) {
            list.add(v);
         }

      });
      return list;
   }

   class SubscriberEntity {
      public Object key;
      public Consumer<BeanWrap> callback;

      public SubscriberEntity(Object key, Consumer<BeanWrap> callback) {
         this.key = key;
         this.callback = callback;
      }
   }
}
