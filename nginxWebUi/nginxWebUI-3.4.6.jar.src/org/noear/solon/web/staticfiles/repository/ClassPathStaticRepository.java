/*    */ package org.noear.solon.web.staticfiles.repository;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.JarClassLoader;
/*    */ import org.noear.solon.web.staticfiles.StaticRepository;
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
/*    */ public class ClassPathStaticRepository
/*    */   implements StaticRepository
/*    */ {
/*    */   String location;
/*    */   String locationDebug;
/*    */   ClassLoader classLoader;
/*    */   
/*    */   public ClassPathStaticRepository(String location) {
/* 30 */     this((ClassLoader)JarClassLoader.global(), location);
/*    */   }
/*    */   
/*    */   public ClassPathStaticRepository(ClassLoader classLoader, String location) {
/* 34 */     this.classLoader = classLoader;
/*    */     
/* 36 */     setLocation(location);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setLocation(String location) {
/* 43 */     if (location == null) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 48 */     if (location.endsWith("/")) {
/* 49 */       location = location.substring(0, location.length() - 1);
/*    */     }
/*    */     
/* 52 */     if (location.startsWith("/")) {
/* 53 */       location = location.substring(1);
/*    */     }
/*    */     
/* 56 */     this.location = location;
/*    */     
/* 58 */     if (Solon.cfg().isDebugMode()) {
/* 59 */       URL rooturi = Utils.getResource(this.classLoader, "/");
/*    */       
/* 61 */       if (rooturi != null) {
/*    */         
/* 63 */         String rootdir = rooturi.toString().replace("target/classes/", "");
/*    */         
/* 65 */         if (rootdir.startsWith("file:")) {
/* 66 */           this.locationDebug = rootdir + "src/main/resources/" + location;
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public URL find(String path) throws Exception {
/* 74 */     if (this.locationDebug != null) {
/* 75 */       URI uri = URI.create(this.locationDebug + path);
/* 76 */       File file = new File(uri);
/*    */       
/* 78 */       if (file.exists()) {
/* 79 */         return uri.toURL();
/*    */       }
/*    */     } 
/*    */     
/* 83 */     return Utils.getResource(this.classLoader, this.location + path);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\repository\ClassPathStaticRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */