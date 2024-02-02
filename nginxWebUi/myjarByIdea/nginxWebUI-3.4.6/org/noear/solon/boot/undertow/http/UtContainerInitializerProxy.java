package org.noear.solon.boot.undertow.http;

import io.undertow.servlet.api.ServletContainerInitializerInfo;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.noear.solon.web.servlet.SolonServletInstaller;

public class UtContainerInitializerProxy implements ServletContainerInitializer {
   SolonServletInstaller initializer = new SolonServletInstaller();

   public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
      this.initializer.startup(set, sc);
   }

   public static ServletContainerInitializerInfo info() {
      return new ServletContainerInitializerInfo(UtContainerInitializerProxy.class, (Set)null);
   }
}
