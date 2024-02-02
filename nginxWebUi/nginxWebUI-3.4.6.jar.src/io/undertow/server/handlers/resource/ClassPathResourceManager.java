/*    */ package io.undertow.server.handlers.resource;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.io.IOException;
/*    */ import java.net.URL;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClassPathResourceManager
/*    */   implements ResourceManager
/*    */ {
/*    */   private final ClassLoader classLoader;
/*    */   private final String prefix;
/*    */   
/*    */   public ClassPathResourceManager(ClassLoader loader, Package p) {
/* 41 */     this(loader, p.getName().replace(".", "/"));
/*    */   }
/*    */   
/*    */   public ClassPathResourceManager(ClassLoader classLoader, String prefix) {
/* 45 */     this.classLoader = classLoader;
/* 46 */     if (prefix.isEmpty()) {
/* 47 */       this.prefix = "";
/* 48 */     } else if (prefix.endsWith("/")) {
/* 49 */       this.prefix = prefix;
/*    */     } else {
/* 51 */       this.prefix = prefix + "/";
/*    */     } 
/*    */   }
/*    */   
/*    */   public ClassPathResourceManager(ClassLoader classLoader) {
/* 56 */     this(classLoader, "");
/*    */   }
/*    */ 
/*    */   
/*    */   public Resource getResource(String path) throws IOException {
/* 61 */     if (path == null) {
/* 62 */       return null;
/*    */     }
/* 64 */     String modPath = path;
/* 65 */     if (modPath.startsWith("/")) {
/* 66 */       modPath = path.substring(1);
/*    */     }
/* 68 */     String realPath = this.prefix + modPath;
/* 69 */     URL resource = this.classLoader.getResource(realPath);
/* 70 */     if (resource == null) {
/* 71 */       return null;
/*    */     }
/* 73 */     return new URLResource(resource, path);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isResourceChangeListenerSupported() {
/* 80 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void registerResourceChangeListener(ResourceChangeListener listener) {
/* 85 */     throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeResourceChangeListener(ResourceChangeListener listener) {
/* 90 */     throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
/*    */   }
/*    */   
/*    */   public void close() throws IOException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\ClassPathResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */