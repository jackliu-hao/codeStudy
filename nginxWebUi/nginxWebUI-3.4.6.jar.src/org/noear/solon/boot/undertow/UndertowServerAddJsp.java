/*    */ package org.noear.solon.boot.undertow;
/*    */ 
/*    */ import io.undertow.jsp.HackInstanceManager;
/*    */ import io.undertow.jsp.JspServletBuilder;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.handlers.resource.ResourceManager;
/*    */ import io.undertow.servlet.api.DeploymentInfo;
/*    */ import io.undertow.servlet.api.DeploymentManager;
/*    */ import io.undertow.servlet.api.ServletContainer;
/*    */ import io.undertow.servlet.api.ServletInfo;
/*    */ import java.util.HashMap;
/*    */ import org.apache.jasper.deploy.TagLibraryInfo;
/*    */ import org.apache.tomcat.InstanceManager;
/*    */ import org.noear.solon.boot.undertow.http.UtHandlerJspHandler;
/*    */ import org.noear.solon.boot.undertow.jsp.JspResourceManager;
/*    */ import org.noear.solon.boot.undertow.jsp.JspServletEx;
/*    */ import org.noear.solon.boot.undertow.jsp.JspTldLocator;
/*    */ import org.noear.solon.core.JarClassLoader;
/*    */ 
/*    */ 
/*    */ class UndertowServerAddJsp
/*    */   extends UndertowServer
/*    */ {
/*    */   protected HttpHandler buildHandler() throws Exception {
/* 25 */     DeploymentInfo builder = initDeploymentInfo();
/*    */ 
/*    */     
/* 28 */     String fileRoot = getResourceRoot();
/* 29 */     builder.setResourceManager((ResourceManager)new JspResourceManager((ClassLoader)JarClassLoader.global(), fileRoot))
/* 30 */       .addServlet((new ServletInfo("ACTServlet", UtHandlerJspHandler.class)).addMapping("/"))
/* 31 */       .addServlet(JspServletEx.createServlet("JSPServlet", "*.jsp"));
/*    */ 
/*    */ 
/*    */     
/* 35 */     HashMap<String, TagLibraryInfo> tagLibraryMap = JspTldLocator.createTldInfos("WEB-INF");
/* 36 */     JspServletBuilder.setupDeployment(builder, new HashMap<>(), tagLibraryMap, (InstanceManager)new HackInstanceManager());
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 41 */     ServletContainer container = ServletContainer.Factory.newInstance();
/* 42 */     DeploymentManager manager = container.addDeployment(builder);
/* 43 */     manager.deploy();
/*    */     
/* 45 */     return manager.start();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\UndertowServerAddJsp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */