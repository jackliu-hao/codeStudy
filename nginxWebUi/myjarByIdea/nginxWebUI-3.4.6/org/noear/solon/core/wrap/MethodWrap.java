package org.noear.solon.core.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.noear.solon.annotation.Around;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.core.aspect.Invocation;

public class MethodWrap implements Interceptor, MethodHolder {
   private final AopContext context;
   private final Class<?> entityClz;
   private final Method method;
   private final ParamWrap[] parameters;
   private final Annotation[] annotations;
   private final List<InterceptorEntity> arounds;
   private final Set<Interceptor> aroundsIdx;

   public MethodWrap(AopContext ctx, Method m) {
      this.context = ctx;
      this.entityClz = m.getDeclaringClass();
      this.method = m;
      this.parameters = this.paramsWrap(m.getParameters());
      this.annotations = m.getAnnotations();
      this.arounds = new ArrayList();
      this.aroundsIdx = new HashSet();
      Annotation[] var3 = this.annotations;
      int var4 = var3.length;

      int var5;
      Annotation anno;
      InterceptorEntity ie;
      for(var5 = 0; var5 < var4; ++var5) {
         anno = var3[var5];
         if (anno instanceof Around) {
            this.doAroundAdd((Around)anno);
         } else {
            ie = this.context.beanAroundGet(anno.annotationType());
            if (ie != null) {
               this.doAroundAdd(ie);
            } else {
               this.doAroundAdd((Around)anno.annotationType().getAnnotation(Around.class));
            }
         }
      }

      var3 = this.entityClz.getAnnotations();
      var4 = var3.length;

      for(var5 = 0; var5 < var4; ++var5) {
         anno = var3[var5];
         if (anno instanceof Around) {
            this.doAroundAdd((Around)anno);
         } else {
            ie = this.context.beanAroundGet(anno.annotationType());
            if (ie != null) {
               this.doAroundAdd(ie);
            } else {
               this.doAroundAdd((Around)anno.annotationType().getAnnotation(Around.class));
            }
         }
      }

      if (this.arounds.size() > 0) {
         this.arounds.sort(Comparator.comparing((x) -> {
            return x.getIndex();
         }));
      }

      this.arounds.add(new InterceptorEntity(0, this));
   }

   private ParamWrap[] paramsWrap(Parameter[] pAry) {
      ParamWrap[] tmp = new ParamWrap[pAry.length];
      int i = 0;

      for(int len = pAry.length; i < len; ++i) {
         tmp[i] = new ParamWrap(pAry[i]);
      }

      return tmp;
   }

   private void doAroundAdd(Around a) {
      if (a != null) {
         this.doAroundAdd(new InterceptorEntity(a.index(), (Interceptor)this.context.wrapAndPut(a.value()).get()));
      }

   }

   private void doAroundAdd(InterceptorEntity i) {
      if (i != null) {
         if (this.aroundsIdx.contains(i.getReal())) {
            return;
         }

         this.aroundsIdx.add(i.getReal());
         this.arounds.add(i);
      }

   }

   public String getName() {
      return this.method.getName();
   }

   public Method getMethod() {
      return this.method;
   }

   public Class<?> getReturnType() {
      return this.method.getReturnType();
   }

   public Type getGenericReturnType() {
      return this.method.getGenericReturnType();
   }

   public ParamWrap[] getParamWraps() {
      return this.parameters;
   }

   public Annotation[] getAnnotations() {
      return this.annotations;
   }

   public <T extends Annotation> T getAnnotation(Class<T> type) {
      return this.method.getAnnotation(type);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
      return this.method.isAnnotationPresent(annotationClass);
   }

   public List<InterceptorEntity> getArounds() {
      return Collections.unmodifiableList(this.arounds);
   }

   public Object doIntercept(Invocation inv) throws Exception {
      return this.invoke(inv.target(), inv.args());
   }

   public Object invoke(Object obj, Object[] args) throws Exception {
      try {
         return this.method.invoke(obj, args);
      } catch (InvocationTargetException var5) {
         Throwable ex = var5.getTargetException();
         if (ex instanceof Error) {
            throw (Error)ex;
         } else {
            throw (Exception)ex;
         }
      }
   }

   public Object invokeByAspect(Object obj, Object[] args) throws Throwable {
      Invocation inv = new Invocation(obj, args, this, this.arounds);
      return inv.invoke();
   }
}
