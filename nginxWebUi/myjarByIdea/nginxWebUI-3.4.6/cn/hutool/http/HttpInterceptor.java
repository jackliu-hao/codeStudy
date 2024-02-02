package cn.hutool.http;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@FunctionalInterface
public interface HttpInterceptor<T extends HttpBase<T>> {
   void process(T var1);

   public static class Chain<T extends HttpBase<T>> implements cn.hutool.core.lang.Chain<HttpInterceptor<T>, Chain<T>> {
      private final List<HttpInterceptor<T>> interceptors = new LinkedList();

      public Chain<T> addChain(HttpInterceptor<T> element) {
         this.interceptors.add(element);
         return this;
      }

      public Iterator<HttpInterceptor<T>> iterator() {
         return this.interceptors.iterator();
      }

      public Chain<T> clear() {
         this.interceptors.clear();
         return this;
      }
   }
}
