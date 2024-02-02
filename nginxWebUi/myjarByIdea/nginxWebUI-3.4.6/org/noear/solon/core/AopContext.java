package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Note;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.annotation.ServerEndpoint;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.HandlerLoader;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.MethodTypeUtil;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.core.wrap.VarGather;
import org.noear.solon.ext.BiConsumerEx;

public class AopContext extends BeanContainer {
   private final Map<Method, MethodWrap> methodCached;
   private final Set<Class<?>> tryCreateCached;
   private boolean loadDone;
   private final Set<RankEntity<Consumer<AopContext>>> loadEvents;

   public AopContext() {
      this((ClassLoader)null, (Props)null);
   }

   public AopContext(ClassLoader classLoader, Props props) {
      super(classLoader, props);
      this.methodCached = new HashMap();
      this.tryCreateCached = new HashSet();
      this.loadEvents = new LinkedHashSet();
      this.initialize();
   }

   public MethodWrap methodGet(Method method) {
      MethodWrap mw = (MethodWrap)this.methodCached.get(method);
      if (mw == null) {
         synchronized(method) {
            mw = (MethodWrap)this.methodCached.get(method);
            if (mw == null) {
               mw = new MethodWrap(this, method);
               this.methodCached.put(method, mw);
            }
         }
      }

      return mw;
   }

   public void clear() {
      super.clear();
      this.methodCached.clear();
      this.tryCreateCached.clear();
      this.loadDone = false;
      this.loadEvents.clear();
   }

   protected BeanWrap wrapCreate(Class<?> type, Object bean) {
      return new BeanWrap(this, type, bean);
   }

   public AopContext copy() {
      return this.copy((ClassLoader)null, (Props)null);
   }

   public AopContext copy(ClassLoader classLoader, Props props) {
      AopContext tmp = new AopContext(classLoader, props);
      this.copyTo(tmp);
      return tmp;
   }

   protected void initialize() {
      this.beanBuilderAdd(Configuration.class, (clz, bw, anno) -> {
         this.beanInjectProperties(clz, bw.raw());
         Method[] var4 = ClassWrap.get(bw.clz()).getMethods();
         int var5 = var4.length;

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            Method m = var4[var6];
            Bean m_an = (Bean)m.getAnnotation(Bean.class);
            if (m_an != null) {
               m.setAccessible(true);
               MethodWrap mWrap = this.methodGet(m);
               this.tryBuildBean(m_an, mWrap, bw);
            }
         }

         this.addBeanShape(clz, bw, 0);
         Annotation[] var10 = clz.getAnnotations();
         var5 = var10.length;

         for(var6 = 0; var6 < var5; ++var6) {
            Annotation a1 = var10[var6];
            if (a1 instanceof Import) {
               this.beanImport((Import)a1);
            } else {
               this.beanImport((Import)a1.annotationType().getAnnotation(Import.class));
            }
         }

         this.beanRegisterSup0(bw);
      });
      this.beanBuilderAdd(Component.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         bw.nameSet(beanName);
         bw.tagSet(anno.tag());
         bw.attrsSet(anno.attrs());
         bw.typedSet(anno.typed());
         this.addBeanShape(clz, bw, anno.index());
         this.beanRegister(bw, beanName, anno.typed());
         this.beanExtract(bw);
         if (bw.singleton()) {
            EventBus.push(bw.raw());
         }

      });
      this.beanBuilderAdd(Remoting.class, (clz, bw, anno) -> {
         bw.remotingSet(true);
         this.beanRegister(bw, "", false);
      });
      this.beanBuilderAdd(Controller.class, (clz, bw, anno) -> {
         (new HandlerLoader(bw)).load(Solon.app());
      });
      this.beanBuilderAdd(ServerEndpoint.class, (clz, wrap, anno) -> {
         if (Listener.class.isAssignableFrom(clz)) {
            Listener l = (Listener)wrap.raw();
            Solon.app().router().add(Utils.annoAlias(anno.value(), anno.path()), anno.method(), l);
         }

      });
      this.beanInjectorAdd(Inject.class, (fwT, anno) -> {
         this.beanInject(fwT, anno.value(), anno.autoRefreshed());
      });
   }

   private void addBeanShape(Class<?> clz, BeanWrap bw, int index) {
      if (Plugin.class.isAssignableFrom(clz)) {
         Solon.app().plug((Plugin)bw.raw());
      } else if (EventListener.class.isAssignableFrom(clz)) {
         this.addEventListener(clz, bw);
      } else {
         if (LoadBalance.Factory.class.isAssignableFrom(clz)) {
            Bridge.upstreamFactorySet((LoadBalance.Factory)bw.raw());
         }

         if (Handler.class.isAssignableFrom(clz)) {
            Mapping mapping = (Mapping)clz.getAnnotation(Mapping.class);
            if (mapping != null) {
               Handler handler = (Handler)bw.raw();
               Set<MethodType> v0 = MethodTypeUtil.findAndFill(new HashSet(), (t) -> {
                  return bw.annotationGet(t) != null;
               });
               if (((Set)v0).size() == 0) {
                  v0 = new HashSet(Arrays.asList(mapping.method()));
               }

               Solon.app().add(mapping, (Set)v0, handler);
            }
         }

         if (Filter.class.isAssignableFrom(clz)) {
            Solon.app().filter(index, (Filter)bw.raw());
         }

      }
   }

   private void addEventListener(Class<?> clz, BeanWrap bw) {
      Class<?>[] ets = GenericUtil.resolveTypeArguments(clz, EventListener.class);
      if (ets != null && ets.length > 0) {
         EventBus.subscribe(ets[0], (EventListener)bw.raw());
      }

   }

   public void beanExtract(BeanWrap bw) {
      if (bw != null) {
         if (this.beanExtractors.size() != 0) {
            ClassWrap clzWrap = ClassWrap.get(bw.clz());
            Method[] var3 = clzWrap.getMethods();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Method m = var3[var5];
               Annotation[] var7 = m.getAnnotations();
               int var8 = var7.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Annotation a = var7[var9];
                  BeanExtractor be = (BeanExtractor)this.beanExtractors.get(a.annotationType());
                  if (be != null) {
                     try {
                        be.doExtract(bw, m, a);
                     } catch (Throwable var13) {
                        Throwable e = Utils.throwableUnwrap(var13);
                        if (e instanceof RuntimeException) {
                           throw (RuntimeException)e;
                        }

                        throw new RuntimeException(e);
                     }
                  }
               }
            }

         }
      }
   }

   public void beanInject(Object obj) {
      if (obj != null) {
         ClassWrap clzWrap = ClassWrap.get(obj.getClass());
         Iterator var3 = clzWrap.getFieldAllWraps().entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, FieldWrap> kv = (Map.Entry)var3.next();
            Annotation[] annS = ((FieldWrap)kv.getValue()).annoS;
            if (annS.length > 0) {
               VarHolder varH = ((FieldWrap)kv.getValue()).holder(this, obj);
               this.tryInject(varH, annS);
            }
         }

      }
   }

   public void beanImport(Import anno) {
      if (anno != null) {
         Class[] var2 = anno.value();
         int var3 = var2.length;

         int var4;
         Class src;
         for(var4 = 0; var4 < var3; ++var4) {
            src = var2[var4];
            this.beanMake(src);
         }

         String[] var6 = anno.scanPackages();
         var3 = var6.length;

         for(var4 = 0; var4 < var3; ++var4) {
            String pkg = var6[var4];
            this.beanScan(pkg);
         }

         var2 = anno.scanPackageClasses();
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            src = var2[var4];
            this.beanScan(src);
         }
      }

   }

   public void beanScan(Class<?> source) {
      if (source.getPackage() != null) {
         this.beanScan(source.getClassLoader(), source.getPackage().getName());
      }

   }

   public void beanScan(String basePackage) {
      this.beanScan(this.getClassLoader(), basePackage);
   }

   public void beanScan(ClassLoader classLoader, String basePackage) {
      if (!Utils.isEmpty(basePackage)) {
         if (classLoader != null) {
            String dir = basePackage.replace('.', '/');
            ScanUtil.scan(classLoader, dir, (n) -> {
               return n.endsWith(".class");
            }).stream().sorted(Comparator.comparing((s) -> {
               return s.length();
            })).forEach((name) -> {
               String className = name.substring(0, name.length() - 6);
               Class<?> clz = Utils.loadClass(classLoader, className.replace("/", "."));
               if (clz != null) {
                  this.tryCreateBean(clz);
               }

            });
         }
      }
   }

   public BeanWrap beanMake(Class<?> clz) {
      BeanWrap bw = this.wrap(clz, (Object)null);
      this.tryCreateBean(bw);
      this.putWrap(clz, bw);
      return bw;
   }

   protected void tryInject(VarHolder varH, Annotation[] annS) {
      Annotation[] var3 = annS;
      int var4 = annS.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Annotation a = var3[var5];
         BeanInjector bi = (BeanInjector)this.beanInjectors.get(a.annotationType());
         if (bi != null) {
            bi.doInject(varH, a);
         }
      }

   }

   protected void tryCreateBean(Class<?> clz) {
      this.tryCreateBean0(clz, (c, a) -> {
         BeanWrap bw = this.wrap(clz, (Object)null);
         c.doBuild(clz, bw, a);
         this.putWrap(clz, bw);
      });
   }

   protected void tryCreateBean(BeanWrap bw) {
      this.tryCreateBean0(bw.clz(), (c, a) -> {
         c.doBuild(bw.clz(), bw, a);
      });
   }

   protected void tryCreateBean0(Class<?> clz, BiConsumerEx<BeanBuilder, Annotation> consumer) {
      Annotation[] annS = clz.getDeclaredAnnotations();
      if (annS.length > 0) {
         if (this.tryCreateCached.contains(clz)) {
            return;
         }

         this.tryCreateCached.add(clz);
         Annotation[] var4 = annS;
         int var5 = annS.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Annotation a = var4[var6];
            BeanBuilder builder = (BeanBuilder)this.beanBuilders.get(a.annotationType());
            if (builder != null) {
               try {
                  consumer.accept(builder, a);
               } catch (Throwable var10) {
                  Throwable e = Utils.throwableUnwrap(var10);
                  if (e instanceof RuntimeException) {
                     throw (RuntimeException)e;
                  }

                  throw new RuntimeException(e);
               }
            }
         }
      }

   }

   protected void tryBuildBean(Bean anno, MethodWrap mWrap, BeanWrap bw) throws Exception {
      int size2 = mWrap.getParamWraps().length;
      if (size2 == 0) {
         Object raw = mWrap.invoke(bw.raw(), new Object[0]);
         this.tryBuildBean0(mWrap, anno, raw);
      } else {
         VarGather gather = new VarGather(bw, size2, (args2) -> {
            try {
               Object raw = mWrap.invoke(bw.raw(), args2);
               this.tryBuildBean0(mWrap, anno, raw);
            } catch (Throwable var6) {
               Throwable e = Utils.throwableUnwrap(var6);
               if (e instanceof RuntimeException) {
                  throw (RuntimeException)e;
               } else {
                  throw new RuntimeException(e);
               }
            }
         });
         ParamWrap[] var6 = mWrap.getParamWraps();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ParamWrap p1 = var6[var8];
            VarHolder p2 = gather.add(p1.getParameter());
            this.tryParameterInject(p2, p1.getParameter());
         }
      }

   }

   protected void tryParameterInject(VarHolder varH, Parameter p) {
      Annotation[] annoS = p.getDeclaredAnnotations();
      if (annoS.length == 0) {
         this.beanInject(varH, (String)null);
      } else {
         Annotation[] var4 = annoS;
         int var5 = annoS.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Annotation anno = var4[var6];
            BeanInjector injector = (BeanInjector)this.beanInjectors.get(anno.annotationType());
            if (injector != null) {
               injector.doInject(varH, anno);
               break;
            }
         }
      }

   }

   protected void tryBuildBean0(MethodWrap mWrap, Bean anno, Object raw) {
      if (raw != null) {
         Class<?> beanClz = mWrap.getReturnType();
         Type beanGtp = mWrap.getGenericReturnType();
         BeanWrap m_bw = null;
         if (raw instanceof BeanWrap) {
            m_bw = (BeanWrap)raw;
         } else {
            EventBus.push(raw);
            m_bw = this.wrapCreate(beanClz, raw);
            m_bw.attrsSet(anno.attrs());
         }

         String beanName = Utils.annoAlias(anno.value(), anno.name());
         m_bw.nameSet(beanName);
         m_bw.tagSet(anno.tag());
         m_bw.typedSet(anno.typed());
         this.addBeanShape(m_bw.clz(), m_bw, anno.index());
         this.beanRegister(m_bw, beanName, anno.typed());
         if (beanGtp instanceof ParameterizedType) {
            this.putWrap(beanGtp.getTypeName(), m_bw);
         }

         EventBus.push(m_bw);
      }

   }

   @Note("添加bean加载完成事件")
   public void beanOnloaded(Consumer<AopContext> fun) {
      this.beanOnloaded(0, fun);
   }

   @Note("添加bean加载完成事件")
   public void beanOnloaded(int index, Consumer<AopContext> fun) {
      this.loadEvents.add(new RankEntity(fun, index));
      if (this.loadDone) {
         fun.accept(this);
      }

   }

   public void beanLoaded() {
      this.loadDone = true;
      this.loadEvents.stream().sorted(Comparator.comparingInt((m) -> {
         return m.index;
      })).forEach((m) -> {
         ((Consumer)m.target).accept(this);
      });
   }
}
