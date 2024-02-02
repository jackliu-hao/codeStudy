/*    */ package org.noear.solon.boot.undertow;
/*    */ 
/*    */ import io.undertow.servlet.api.ClassIntrospecter;
/*    */ import io.undertow.servlet.api.DeploymentInfo;
/*    */ import io.undertow.servlet.util.DefaultClassIntrospector;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import javax.servlet.MultipartConfigElement;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.boot.undertow.http.UtContainerInitializerProxy;
/*    */ 
/*    */ abstract class UndertowServerBase {
/*    */   protected DeploymentInfo initDeploymentInfo() {
/* 17 */     MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 25 */     DeploymentInfo builder = (new DeploymentInfo()).setClassLoader(XPluginImp.class.getClassLoader()).setDeploymentName("solon").setContextPath("/").setDefaultEncoding(ServerProps.request_encoding).setDefaultMultipartConfig(configElement).setClassIntrospecter((ClassIntrospecter)DefaultClassIntrospector.INSTANCE);
/*    */ 
/*    */     
/* 28 */     builder.addServletContainerInitializer(UtContainerInitializerProxy.info());
/* 29 */     builder.setEagerFilterInit(true);
/*    */     
/* 31 */     if (ServerProps.session_timeout > 0) {
/* 32 */       builder.setDefaultSessionTimeout(ServerProps.session_timeout);
/*    */     }
/*    */     
/* 35 */     return builder;
/*    */   }
/*    */   
/*    */   protected String getResourceRoot() throws FileNotFoundException {
/* 39 */     URL rootURL = getRootPath();
/* 40 */     if (rootURL == null) {
/* 41 */       throw new FileNotFoundException("Unable to find root");
/*    */     }
/* 43 */     String resURL = rootURL.toString();
/*    */     
/* 45 */     if (Solon.cfg().isDebugMode() && !resURL.startsWith("jar:")) {
/* 46 */       int endIndex = resURL.indexOf("target");
/* 47 */       return resURL.substring(0, endIndex) + "src/main/resources/";
/*    */     } 
/*    */     
/* 50 */     return "";
/*    */   }
/*    */   
/*    */   protected URL getRootPath() {
/* 54 */     URL root = Utils.getResource("/");
/* 55 */     if (root != null) {
/* 56 */       return root;
/*    */     }
/*    */     try {
/* 59 */       String path = Utils.getResource("").toString();
/* 60 */       if (path.startsWith("jar:")) {
/* 61 */         int endIndex = path.indexOf("!");
/* 62 */         path = path.substring(0, endIndex + 1) + "/";
/*    */       } else {
/* 64 */         return null;
/*    */       } 
/* 66 */       return new URL(path);
/* 67 */     } catch (MalformedURLException e) {
/* 68 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\UndertowServerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */