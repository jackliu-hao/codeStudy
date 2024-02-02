package org.noear.solon.boot.undertow;

import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import java.util.HashMap;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.noear.solon.boot.undertow.http.UtHandlerJspHandler;
import org.noear.solon.boot.undertow.jsp.JspResourceManager;
import org.noear.solon.boot.undertow.jsp.JspServletEx;
import org.noear.solon.boot.undertow.jsp.JspTldLocator;
import org.noear.solon.core.JarClassLoader;

class UndertowServerAddJsp extends UndertowServer {
   protected HttpHandler buildHandler() throws Exception {
      DeploymentInfo builder = this.initDeploymentInfo();
      String fileRoot = this.getResourceRoot();
      builder.setResourceManager(new JspResourceManager(JarClassLoader.global(), fileRoot)).addServlet((new ServletInfo("ACTServlet", UtHandlerJspHandler.class)).addMapping("/")).addServlet(JspServletEx.createServlet("JSPServlet", "*.jsp"));
      HashMap<String, TagLibraryInfo> tagLibraryMap = JspTldLocator.createTldInfos("WEB-INF");
      JspServletBuilder.setupDeployment(builder, new HashMap(), tagLibraryMap, new HackInstanceManager());
      ServletContainer container = ServletContainer.Factory.newInstance();
      DeploymentManager manager = container.addDeployment(builder);
      manager.deploy();
      return manager.start();
   }
}
