/*    */ package org.noear.solon.boot.undertow.http;
/*    */ 
/*    */ import io.undertow.servlet.api.ServletContainerInitializerInfo;
/*    */ import java.util.Set;
/*    */ import javax.servlet.ServletContainerInitializer;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import org.noear.solon.web.servlet.SolonServletInstaller;
/*    */ 
/*    */ public class UtContainerInitializerProxy
/*    */   implements ServletContainerInitializer
/*    */ {
/* 13 */   SolonServletInstaller initializer = new SolonServletInstaller();
/*    */ 
/*    */ 
/*    */   
/*    */   public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
/* 18 */     this.initializer.startup(set, sc);
/*    */   }
/*    */   
/*    */   public static ServletContainerInitializerInfo info() {
/* 22 */     return new ServletContainerInitializerInfo(UtContainerInitializerProxy.class, null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\http\UtContainerInitializerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */