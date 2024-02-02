package io.undertow.servlet.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServletSecurityInfo extends SecurityInfo<ServletSecurityInfo> implements Cloneable {
   private final List<HttpMethodSecurityInfo> httpMethodSecurityInfo = new ArrayList();

   protected ServletSecurityInfo createInstance() {
      return new ServletSecurityInfo();
   }

   public ServletSecurityInfo addHttpMethodSecurityInfo(HttpMethodSecurityInfo info) {
      this.httpMethodSecurityInfo.add(info);
      return this;
   }

   public List<HttpMethodSecurityInfo> getHttpMethodSecurityInfo() {
      return new ArrayList(this.httpMethodSecurityInfo);
   }

   public ServletSecurityInfo clone() {
      ServletSecurityInfo info = (ServletSecurityInfo)super.clone();
      Iterator var2 = this.httpMethodSecurityInfo.iterator();

      while(var2.hasNext()) {
         HttpMethodSecurityInfo method = (HttpMethodSecurityInfo)var2.next();
         info.httpMethodSecurityInfo.add(method.clone());
      }

      return info;
   }
}
