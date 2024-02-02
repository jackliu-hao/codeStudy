package io.undertow.servlet.compat.rewrite;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.DateUtils;
import java.io.IOException;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

public class UndertowResolver extends Resolver {
   private final ServletRequestContext servletRequestContext;
   private final HttpServletRequest request;

   public UndertowResolver(ServletRequestContext servletRequestContext, HttpServletRequest request) {
      this.servletRequestContext = servletRequestContext;
      this.request = request;
   }

   public String resolve(String key) {
      if (key.equals("HTTP_USER_AGENT")) {
         return this.request.getHeader("user-agent");
      } else if (key.equals("HTTP_REFERER")) {
         return this.request.getHeader("referer");
      } else if (key.equals("HTTP_COOKIE")) {
         return this.request.getHeader("cookie");
      } else if (key.equals("HTTP_FORWARDED")) {
         return this.request.getHeader("forwarded");
      } else if (key.equals("HTTP_HOST")) {
         String host = this.request.getHeader("host");
         int index = host != null ? host.indexOf(58) : -1;
         if (index != -1) {
            host = host.substring(0, index);
         }

         return host;
      } else if (key.equals("HTTP_PROXY_CONNECTION")) {
         return this.request.getHeader("proxy-connection");
      } else if (key.equals("HTTP_ACCEPT")) {
         return this.request.getHeader("accept");
      } else if (key.equals("REMOTE_ADDR")) {
         return this.request.getRemoteAddr();
      } else if (key.equals("REMOTE_HOST")) {
         return this.request.getRemoteHost();
      } else if (key.equals("REMOTE_PORT")) {
         return String.valueOf(this.request.getRemotePort());
      } else if (key.equals("REMOTE_USER")) {
         return this.request.getRemoteUser();
      } else if (key.equals("REMOTE_IDENT")) {
         return this.request.getRemoteUser();
      } else if (key.equals("REQUEST_METHOD")) {
         return this.request.getMethod();
      } else if (key.equals("SCRIPT_FILENAME")) {
         return this.request.getRealPath(this.request.getServletPath());
      } else if (key.equals("REQUEST_PATH")) {
         return this.servletRequestContext.getExchange().getRelativePath();
      } else if (key.equals("CONTEXT_PATH")) {
         return this.request.getContextPath();
      } else if (key.equals("SERVLET_PATH")) {
         return emptyStringIfNull(this.request.getServletPath());
      } else if (key.equals("PATH_INFO")) {
         return emptyStringIfNull(this.request.getPathInfo());
      } else if (key.equals("QUERY_STRING")) {
         return emptyStringIfNull(this.request.getQueryString());
      } else if (key.equals("AUTH_TYPE")) {
         return this.request.getAuthType();
      } else if (key.equals("DOCUMENT_ROOT")) {
         return this.request.getRealPath("/");
      } else if (key.equals("SERVER_NAME")) {
         return this.request.getLocalName();
      } else if (key.equals("SERVER_ADDR")) {
         return this.request.getLocalAddr();
      } else if (key.equals("SERVER_PORT")) {
         return String.valueOf(this.request.getLocalPort());
      } else if (key.equals("SERVER_PROTOCOL")) {
         return this.request.getProtocol();
      } else if (key.equals("SERVER_SOFTWARE")) {
         return "tomcat";
      } else if (key.equals("THE_REQUEST")) {
         return this.request.getMethod() + " " + this.request.getRequestURI() + " " + this.request.getProtocol();
      } else if (key.equals("REQUEST_URI")) {
         return this.request.getRequestURI();
      } else if (key.equals("REQUEST_FILENAME")) {
         return this.request.getPathTranslated();
      } else if (key.equals("HTTPS")) {
         return this.request.isSecure() ? "on" : "off";
      } else if (key.equals("TIME_YEAR")) {
         return String.valueOf(Calendar.getInstance().get(1));
      } else if (key.equals("TIME_MON")) {
         return String.valueOf(Calendar.getInstance().get(2));
      } else if (key.equals("TIME_DAY")) {
         return String.valueOf(Calendar.getInstance().get(5));
      } else if (key.equals("TIME_HOUR")) {
         return String.valueOf(Calendar.getInstance().get(11));
      } else if (key.equals("TIME_MIN")) {
         return String.valueOf(Calendar.getInstance().get(12));
      } else if (key.equals("TIME_SEC")) {
         return String.valueOf(Calendar.getInstance().get(13));
      } else if (key.equals("TIME_WDAY")) {
         return String.valueOf(Calendar.getInstance().get(7));
      } else {
         return key.equals("TIME") ? DateUtils.getCurrentDateTime(this.servletRequestContext.getExchange()) : null;
      }
   }

   public String resolveEnv(String key) {
      Object result = this.request.getAttribute(key);
      return result != null ? result.toString() : System.getProperty(key);
   }

   public String resolveSsl(String key) {
      return null;
   }

   public String resolveHttp(String key) {
      return this.request.getHeader(key);
   }

   public boolean resolveResource(int type, String name) {
      Resource resource;
      try {
         resource = this.servletRequestContext.getDeployment().getDeploymentInfo().getResourceManager().getResource(name);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      switch (type) {
         case 0:
            return resource == null;
         case 1:
            return resource != null;
         case 2:
            return resource != null && resource.getContentLength() > 0L;
         default:
            return false;
      }
   }

   private static String emptyStringIfNull(String value) {
      return value == null ? "" : value;
   }
}
