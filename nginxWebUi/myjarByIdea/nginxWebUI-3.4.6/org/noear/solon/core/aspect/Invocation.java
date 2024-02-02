package org.noear.solon.core.aspect;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.noear.solon.core.wrap.MethodHolder;
import org.noear.solon.core.wrap.ParamWrap;

public class Invocation {
   private final Object target;
   private final Object[] args;
   private Map<String, Object> argsMap;
   private final MethodHolder method;
   private final List<InterceptorEntity> interceptors;
   private int interceptorIndex = 0;

   public Invocation(Object target, Object[] args, MethodHolder method, List<InterceptorEntity> interceptors) {
      this.target = target;
      this.args = args;
      this.method = method;
      this.interceptors = interceptors;
   }

   public Object target() {
      return this.target;
   }

   public Object[] args() {
      return this.args;
   }

   public Map<String, Object> argsAsMap() {
      if (this.argsMap == null) {
         Map<String, Object> tmp = new LinkedHashMap();
         ParamWrap[] params = this.method.getParamWraps();
         int i = 0;

         for(int len = params.length; i < len; ++i) {
            tmp.put(params[i].getName(), this.args[i]);
         }

         this.argsMap = Collections.unmodifiableMap(tmp);
      }

      return this.argsMap;
   }

   public MethodHolder method() {
      return this.method;
   }

   public Object invoke() throws Throwable {
      return ((InterceptorEntity)this.interceptors.get(this.interceptorIndex++)).doIntercept(this);
   }
}
