package io.undertow.servlet.api;

public class HttpMethodSecurityInfo extends SecurityInfo<HttpMethodSecurityInfo> implements Cloneable {
   private volatile String method;

   public String getMethod() {
      return this.method;
   }

   public HttpMethodSecurityInfo setMethod(String method) {
      this.method = method;
      return this;
   }

   protected HttpMethodSecurityInfo createInstance() {
      return new HttpMethodSecurityInfo();
   }

   public HttpMethodSecurityInfo clone() {
      HttpMethodSecurityInfo info = (HttpMethodSecurityInfo)super.clone();
      info.method = this.method;
      return info;
   }
}
