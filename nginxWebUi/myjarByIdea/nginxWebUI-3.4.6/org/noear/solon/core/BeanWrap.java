package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.util.IndexBuilder;
import org.noear.solon.core.wrap.ClassWrap;

public class BeanWrap {
   private Class<?> clz;
   private Method clzInit;
   private boolean clzInitDelay;
   private Object raw;
   private boolean singleton;
   private boolean remoting;
   private String name;
   private String tag;
   private String[] attrs;
   private Map<String, String> attrMap;
   private boolean typed;
   private Proxy proxy;
   private final Annotation[] annotations;
   private final AopContext context;

   public BeanWrap(AopContext context, Class<?> clz) {
      this(context, clz, (Object)null);
   }

   public BeanWrap(AopContext context, Class<?> clz, Object raw) {
      this.context = context;
      this.clz = clz;
      Singleton ano = (Singleton)clz.getAnnotation(Singleton.class);
      this.singleton = ano == null || ano.value();
      this.annotations = clz.getAnnotations();
      this.tryBuildInit();
      if (raw == null) {
         this.raw = this._new();
      } else {
         this.raw = raw;
      }

   }

   public BeanWrap(AopContext context, Class<?> clz, Object raw, String[] attrs) {
      this(context, clz, raw);
      this.attrsSet(attrs);
   }

   public AopContext context() {
      return this.context;
   }

   public Proxy proxy() {
      return this.proxy;
   }

   public void proxySet(Proxy proxy) {
      this.proxy = proxy;
      if (this.raw != null) {
         this.raw = proxy.getProxy(this.context(), this.raw);
      }

   }

   public boolean singleton() {
      return this.singleton;
   }

   public void singletonSet(boolean singleton) {
      this.singleton = singleton;
   }

   public boolean remoting() {
      return this.remoting;
   }

   public void remotingSet(boolean remoting) {
      this.remoting = remoting;
   }

   public Class<?> clz() {
      return this.clz;
   }

   public <T> T raw() {
      return this.raw;
   }

   protected void rawSet(Object raw) {
      this.raw = raw;
   }

   public String name() {
      return this.name;
   }

   protected void nameSet(String name) {
      this.name = name;
   }

   public String tag() {
      return this.tag;
   }

   protected void tagSet(String tag) {
      this.tag = tag;
   }

   public String[] attrs() {
      return this.attrs;
   }

   protected void attrsSet(String[] attrs) {
      this.attrs = attrs;
   }

   public String attrGet(String name) {
      if (this.attrs == null) {
         return null;
      } else {
         if (this.attrMap == null) {
            this.attrMap = new HashMap();
            String[] var2 = this.attrs;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String kv = var2[var4];
               String[] ss = kv.split("=");
               if (ss.length == 2) {
                  this.attrMap.put(ss[0], ss[1]);
               }
            }
         }

         return (String)this.attrMap.get(name);
      }
   }

   public boolean typed() {
      return this.typed;
   }

   protected void typedSet(boolean typed) {
      this.typed = typed;
   }

   public Annotation[] annotations() {
      return this.annotations;
   }

   public <T extends Annotation> T annotationGet(Class<T> annClz) {
      return this.clz.getAnnotation(annClz);
   }

   public <T> T get() {
      return this.singleton ? this.raw : this._new();
   }

   protected void init(Object bean) {
      this.context.beanInject(bean);
      if (this.clzInit != null) {
         if (this.clzInitDelay) {
            this.context.beanOnloaded(IndexBuilder.buildIndex(this.clz), (ctx) -> {
               this.initInvokeDo(bean);
            });
         } else {
            this.initInvokeDo(bean);
         }
      }

   }

   protected void initInvokeDo(Object bean) {
      try {
         this.clzInit.invoke(bean);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   protected Object _new() {
      if (this.clz.isInterface()) {
         return this.raw;
      } else {
         try {
            Object bean = this.clz.newInstance();
            this.init(bean);
            if (this.proxy != null) {
               bean = this.proxy.getProxy(this.context(), bean);
            }

            return bean;
         } catch (RuntimeException var2) {
            throw var2;
         } catch (Throwable var3) {
            throw new IllegalArgumentException("Instantiation failure: " + this.clz.getTypeName(), var3);
         }
      }
   }

   protected void tryBuildInit() {
      if (this.clzInit == null) {
         if (!this.clz.isInterface()) {
            ClassWrap clzWrap = ClassWrap.get(this.clz);
            Method[] var2 = clzWrap.getMethods();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Method m = var2[var4];
               Init initAnno = (Init)m.getAnnotation(Init.class);
               if (initAnno != null) {
                  if (m.getParameters().length == 0) {
                     this.clzInit = m;
                     this.clzInit.setAccessible(true);
                     this.clzInitDelay = initAnno.delay();
                  }
                  break;
               }
            }

         }
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof BeanWrap)) {
         return false;
      } else {
         BeanWrap beanWrap = (BeanWrap)o;
         return this.clz.equals(beanWrap.clz);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.clz});
   }

   @FunctionalInterface
   public interface Proxy {
      Object getProxy(AopContext ctx, Object bean);
   }
}
