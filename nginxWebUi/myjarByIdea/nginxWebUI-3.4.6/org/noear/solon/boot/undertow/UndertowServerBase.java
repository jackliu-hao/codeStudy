package org.noear.solon.boot.undertow;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.util.DefaultClassIntrospector;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.MultipartConfigElement;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.undertow.http.UtContainerInitializerProxy;

abstract class UndertowServerBase {
   protected DeploymentInfo initDeploymentInfo() {
      MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
      DeploymentInfo builder = (new DeploymentInfo()).setClassLoader(XPluginImp.class.getClassLoader()).setDeploymentName("solon").setContextPath("/").setDefaultEncoding(ServerProps.request_encoding).setDefaultMultipartConfig(configElement).setClassIntrospecter(DefaultClassIntrospector.INSTANCE);
      builder.addServletContainerInitializer(UtContainerInitializerProxy.info());
      builder.setEagerFilterInit(true);
      if (ServerProps.session_timeout > 0) {
         builder.setDefaultSessionTimeout(ServerProps.session_timeout);
      }

      return builder;
   }

   protected String getResourceRoot() throws FileNotFoundException {
      URL rootURL = this.getRootPath();
      if (rootURL == null) {
         throw new FileNotFoundException("Unable to find root");
      } else {
         String resURL = rootURL.toString();
         if (Solon.cfg().isDebugMode() && !resURL.startsWith("jar:")) {
            int endIndex = resURL.indexOf("target");
            return resURL.substring(0, endIndex) + "src/main/resources/";
         } else {
            return "";
         }
      }
   }

   protected URL getRootPath() {
      URL root = Utils.getResource("/");
      if (root != null) {
         return root;
      } else {
         try {
            String path = Utils.getResource("").toString();
            if (path.startsWith("jar:")) {
               int endIndex = path.indexOf("!");
               path = path.substring(0, endIndex + 1) + "/";
               return new URL(path);
            } else {
               return null;
            }
         } catch (MalformedURLException var4) {
            return null;
         }
      }
   }
}
