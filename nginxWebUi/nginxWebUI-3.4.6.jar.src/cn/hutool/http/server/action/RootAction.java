/*    */ package cn.hutool.http.server.action;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.http.server.HttpServerRequest;
/*    */ import cn.hutool.http.server.HttpServerResponse;
/*    */ import java.io.File;
/*    */ import java.util.List;
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
/*    */ public class RootAction
/*    */   implements Action
/*    */ {
/*    */   public static final String DEFAULT_INDEX_FILE_NAME = "index.html";
/*    */   private final File rootDir;
/*    */   private final List<String> indexFileNames;
/*    */   
/*    */   public RootAction(String rootDir) {
/* 30 */     this(new File(rootDir));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RootAction(File rootDir) {
/* 39 */     this(rootDir, new String[] { "index.html" });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RootAction(String rootDir, String... indexFileNames) {
/* 49 */     this(new File(rootDir), indexFileNames);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RootAction(File rootDir, String... indexFileNames) {
/* 60 */     this.rootDir = rootDir;
/* 61 */     this.indexFileNames = CollUtil.toList((Object[])indexFileNames);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doAction(HttpServerRequest request, HttpServerResponse response) {
/* 66 */     String path = request.getPath();
/*    */     
/* 68 */     File file = FileUtil.file(this.rootDir, path);
/* 69 */     if (file.exists()) {
/* 70 */       if (file.isDirectory()) {
/* 71 */         for (String indexFileName : this.indexFileNames) {
/*    */           
/* 73 */           file = FileUtil.file(file, indexFileName);
/* 74 */           if (file.exists() && file.isFile()) {
/* 75 */             response.write(file);
/*    */           }
/*    */         } 
/*    */       } else {
/* 79 */         String name = request.getParam("name");
/* 80 */         response.write(file, name);
/*    */       } 
/*    */     }
/*    */     
/* 84 */     response.send404("404 Not Found !");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\action\RootAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */