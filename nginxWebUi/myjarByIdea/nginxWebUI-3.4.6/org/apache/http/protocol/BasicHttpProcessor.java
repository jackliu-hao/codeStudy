package org.apache.http.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public final class BasicHttpProcessor implements HttpProcessor, HttpRequestInterceptorList, HttpResponseInterceptorList, Cloneable {
   protected final List<HttpRequestInterceptor> requestInterceptors = new ArrayList();
   protected final List<HttpResponseInterceptor> responseInterceptors = new ArrayList();

   public void addRequestInterceptor(HttpRequestInterceptor itcp) {
      if (itcp != null) {
         this.requestInterceptors.add(itcp);
      }
   }

   public void addRequestInterceptor(HttpRequestInterceptor itcp, int index) {
      if (itcp != null) {
         this.requestInterceptors.add(index, itcp);
      }
   }

   public void addResponseInterceptor(HttpResponseInterceptor itcp, int index) {
      if (itcp != null) {
         this.responseInterceptors.add(index, itcp);
      }
   }

   public void removeRequestInterceptorByClass(Class<? extends HttpRequestInterceptor> clazz) {
      Iterator<HttpRequestInterceptor> it = this.requestInterceptors.iterator();

      while(it.hasNext()) {
         Object request = it.next();
         if (request.getClass().equals(clazz)) {
            it.remove();
         }
      }

   }

   public void removeResponseInterceptorByClass(Class<? extends HttpResponseInterceptor> clazz) {
      Iterator<HttpResponseInterceptor> it = this.responseInterceptors.iterator();

      while(it.hasNext()) {
         Object request = it.next();
         if (request.getClass().equals(clazz)) {
            it.remove();
         }
      }

   }

   public final void addInterceptor(HttpRequestInterceptor interceptor) {
      this.addRequestInterceptor(interceptor);
   }

   public final void addInterceptor(HttpRequestInterceptor interceptor, int index) {
      this.addRequestInterceptor(interceptor, index);
   }

   public int getRequestInterceptorCount() {
      return this.requestInterceptors.size();
   }

   public HttpRequestInterceptor getRequestInterceptor(int index) {
      return index >= 0 && index < this.requestInterceptors.size() ? (HttpRequestInterceptor)this.requestInterceptors.get(index) : null;
   }

   public void clearRequestInterceptors() {
      this.requestInterceptors.clear();
   }

   public void addResponseInterceptor(HttpResponseInterceptor itcp) {
      if (itcp != null) {
         this.responseInterceptors.add(itcp);
      }
   }

   public final void addInterceptor(HttpResponseInterceptor interceptor) {
      this.addResponseInterceptor(interceptor);
   }

   public final void addInterceptor(HttpResponseInterceptor interceptor, int index) {
      this.addResponseInterceptor(interceptor, index);
   }

   public int getResponseInterceptorCount() {
      return this.responseInterceptors.size();
   }

   public HttpResponseInterceptor getResponseInterceptor(int index) {
      return index >= 0 && index < this.responseInterceptors.size() ? (HttpResponseInterceptor)this.responseInterceptors.get(index) : null;
   }

   public void clearResponseInterceptors() {
      this.responseInterceptors.clear();
   }

   public void setInterceptors(List<?> list) {
      Args.notNull(list, "Inteceptor list");
      this.requestInterceptors.clear();
      this.responseInterceptors.clear();
      Iterator i$ = list.iterator();

      while(i$.hasNext()) {
         Object obj = i$.next();
         if (obj instanceof HttpRequestInterceptor) {
            this.addInterceptor((HttpRequestInterceptor)obj);
         }

         if (obj instanceof HttpResponseInterceptor) {
            this.addInterceptor((HttpResponseInterceptor)obj);
         }
      }

   }

   public void clearInterceptors() {
      this.clearRequestInterceptors();
      this.clearResponseInterceptors();
   }

   public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
      Iterator i$ = this.requestInterceptors.iterator();

      while(i$.hasNext()) {
         HttpRequestInterceptor interceptor = (HttpRequestInterceptor)i$.next();
         interceptor.process(request, context);
      }

   }

   public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
      Iterator i$ = this.responseInterceptors.iterator();

      while(i$.hasNext()) {
         HttpResponseInterceptor interceptor = (HttpResponseInterceptor)i$.next();
         interceptor.process(response, context);
      }

   }

   protected void copyInterceptors(BasicHttpProcessor target) {
      target.requestInterceptors.clear();
      target.requestInterceptors.addAll(this.requestInterceptors);
      target.responseInterceptors.clear();
      target.responseInterceptors.addAll(this.responseInterceptors);
   }

   public BasicHttpProcessor copy() {
      BasicHttpProcessor clone = new BasicHttpProcessor();
      this.copyInterceptors(clone);
      return clone;
   }

   public Object clone() throws CloneNotSupportedException {
      BasicHttpProcessor clone = (BasicHttpProcessor)super.clone();
      this.copyInterceptors(clone);
      return clone;
   }
}
