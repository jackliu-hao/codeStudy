package io.undertow.servlet.spec;

import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.IteratorEnumeration;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletConfigImpl implements ServletConfig {
   private final ServletInfo servletInfo;
   private final ServletContext servletContext;

   public ServletConfigImpl(ServletInfo servletInfo, ServletContext servletContext) {
      this.servletInfo = servletInfo;
      this.servletContext = servletContext;
   }

   public String getServletName() {
      return this.servletInfo.getName();
   }

   public ServletContext getServletContext() {
      return this.servletContext;
   }

   public String getInitParameter(String name) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.nullName();
      } else {
         return (String)this.servletInfo.getInitParams().get(name);
      }
   }

   public Enumeration<String> getInitParameterNames() {
      return new IteratorEnumeration(this.servletInfo.getInitParams().keySet().iterator());
   }
}
