/*    */ package org.noear.solon.web.staticfiles.repository;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.net.URL;
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
/*    */ public class FileStaticRepository
/*    */   implements StaticRepository
/*    */ {
/*    */   String location;
/*    */   
/*    */   public FileStaticRepository(String location) {
/* 23 */     setLocation(location);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setLocation(String location) {
/* 32 */     if (location == null) {
/*    */       return;
/*    */     }
/*    */     
/* 36 */     this.location = location;
/*    */   }
/*    */ 
/*    */   
/*    */   public URL find(String path) throws Exception {
/* 41 */     File file = new File(this.location, path);
/*    */     
/* 43 */     if (file.exists()) {
/* 44 */       return file.toURI().toURL();
/*    */     }
/* 46 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\repository\FileStaticRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */