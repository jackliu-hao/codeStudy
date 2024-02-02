package org.noear.solon.core.aspect;

public class InterceptorEntity implements Interceptor {
   private final int index;
   private final Interceptor real;

   public InterceptorEntity(int index, Interceptor real) {
      this.index = index;
      this.real = real;
   }

   public int getIndex() {
      return this.index;
   }

   public Interceptor getReal() {
      return this.real;
   }

   public Object doIntercept(Invocation inv) throws Throwable {
      return this.real.doIntercept(inv);
   }
}
