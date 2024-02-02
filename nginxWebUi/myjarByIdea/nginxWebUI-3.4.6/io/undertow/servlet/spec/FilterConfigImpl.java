package io.undertow.servlet.spec;

import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.util.IteratorEnumeration;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class FilterConfigImpl implements FilterConfig {
   private final FilterInfo filterInfo;
   private final ServletContext servletContext;

   public FilterConfigImpl(FilterInfo filterInfo, ServletContext servletContext) {
      this.filterInfo = filterInfo;
      this.servletContext = servletContext;
   }

   public String getFilterName() {
      return this.filterInfo.getName();
   }

   public ServletContext getServletContext() {
      return this.servletContext;
   }

   public String getInitParameter(String name) {
      return (String)this.filterInfo.getInitParams().get(name);
   }

   public Enumeration<String> getInitParameterNames() {
      return new IteratorEnumeration(this.filterInfo.getInitParams().keySet().iterator());
   }
}
