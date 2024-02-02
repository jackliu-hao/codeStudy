package cn.hutool.http;

import java.util.Iterator;

public enum GlobalInterceptor {
   INSTANCE;

   private final HttpInterceptor.Chain<HttpRequest> requestInterceptors = new HttpInterceptor.Chain();
   private final HttpInterceptor.Chain<HttpResponse> responseInterceptors = new HttpInterceptor.Chain();

   public synchronized GlobalInterceptor addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
      this.requestInterceptors.addChain(interceptor);
      return this;
   }

   public synchronized GlobalInterceptor addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
      this.responseInterceptors.addChain(interceptor);
      return this;
   }

   public GlobalInterceptor clear() {
      this.clearRequest();
      this.clearResponse();
      return this;
   }

   public synchronized GlobalInterceptor clearRequest() {
      this.requestInterceptors.clear();
      return this;
   }

   public synchronized GlobalInterceptor clearResponse() {
      this.responseInterceptors.clear();
      return this;
   }

   HttpInterceptor.Chain<HttpRequest> getCopiedRequestInterceptor() {
      HttpInterceptor.Chain<HttpRequest> copied = new HttpInterceptor.Chain();
      Iterator var2 = this.requestInterceptors.iterator();

      while(var2.hasNext()) {
         HttpInterceptor<HttpRequest> interceptor = (HttpInterceptor)var2.next();
         copied.addChain(interceptor);
      }

      return copied;
   }

   HttpInterceptor.Chain<HttpResponse> getCopiedResponseInterceptor() {
      HttpInterceptor.Chain<HttpResponse> copied = new HttpInterceptor.Chain();
      Iterator var2 = this.responseInterceptors.iterator();

      while(var2.hasNext()) {
         HttpInterceptor<HttpResponse> interceptor = (HttpInterceptor)var2.next();
         copied.addChain(interceptor);
      }

      return copied;
   }
}
