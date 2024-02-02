package javax.servlet;

import java.util.Enumeration;

public interface FilterConfig {
   String getFilterName();

   ServletContext getServletContext();

   String getInitParameter(String var1);

   Enumeration<String> getInitParameterNames();
}
