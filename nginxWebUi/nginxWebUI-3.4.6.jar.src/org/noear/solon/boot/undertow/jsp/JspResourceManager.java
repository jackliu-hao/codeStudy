/*    */ package org.noear.solon.boot.undertow.jsp;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import io.undertow.server.handlers.resource.Resource;
/*    */ import io.undertow.server.handlers.resource.ResourceChangeListener;
/*    */ import io.undertow.server.handlers.resource.ResourceManager;
/*    */ import io.undertow.server.handlers.resource.URLResource;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JspResourceManager
/*    */   implements ResourceManager
/*    */ {
/*    */   private final ClassLoader classLoader;
/*    */   private final String prefix;
/*    */   
/*    */   public JspResourceManager(ClassLoader classLoader, String prefix) {
/* 22 */     this.classLoader = classLoader;
/* 23 */     if (prefix.isEmpty()) {
/* 24 */       this.prefix = "";
/* 25 */     } else if (prefix.endsWith("/")) {
/* 26 */       this.prefix = prefix;
/*    */     } else {
/* 28 */       this.prefix = prefix + "/";
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Resource getResource(String path) throws IOException {
/* 35 */     if (path == null || !path.endsWith(".jsp")) {
/* 36 */       return null;
/*    */     }
/*    */     
/* 39 */     if (Context.current() == null)
/*    */     {
/* 41 */       return null;
/*    */     }
/*    */     
/* 44 */     String modPath = path;
/* 45 */     if (path.startsWith("/")) {
/* 46 */       modPath = path.substring(1);
/*    */     }
/*    */     
/* 49 */     String realPath = this.prefix + modPath;
/* 50 */     URL resource = null;
/* 51 */     if (realPath.startsWith("file:")) {
/* 52 */       resource = URI.create(realPath).toURL();
/*    */     } else {
/* 54 */       resource = this.classLoader.getResource(realPath);
/*    */     } 
/*    */     
/* 57 */     return (resource == null) ? null : (Resource)new URLResource(resource, path);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isResourceChangeListenerSupported() {
/* 62 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void registerResourceChangeListener(ResourceChangeListener listener) {
/* 67 */     throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeResourceChangeListener(ResourceChangeListener listener) {
/* 72 */     throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
/*    */   }
/*    */   
/*    */   public void close() throws IOException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\jsp\JspResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */