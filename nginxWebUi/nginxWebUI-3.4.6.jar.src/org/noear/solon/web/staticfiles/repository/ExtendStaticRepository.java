/*    */ package org.noear.solon.web.staticfiles.repository;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.net.URL;
/*    */ import org.noear.solon.core.ExtendLoader;
/*    */ import org.noear.solon.web.staticfiles.StaticRepository;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExtendStaticRepository
/*    */   implements StaticRepository
/*    */ {
/*    */   String location;
/*    */   
/*    */   public ExtendStaticRepository() {
/* 19 */     String path = ExtendLoader.path();
/* 20 */     if (path == null) {
/* 21 */       throw new RuntimeException("No extension directory exists");
/*    */     }
/*    */     
/* 24 */     this.location = path + "static";
/*    */   }
/*    */ 
/*    */   
/*    */   public URL find(String path) throws Exception {
/* 29 */     File file = new File(this.location, path);
/*    */     
/* 31 */     if (file.exists()) {
/* 32 */       return file.toURI().toURL();
/*    */     }
/* 34 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\repository\ExtendStaticRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */