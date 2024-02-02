/*    */ package io.undertow.server.handlers.resource;
/*    */ 
/*    */ import java.io.File;
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
/*    */ public class FileResource
/*    */   extends PathResource
/*    */ {
/*    */   public FileResource(File file, FileResourceManager manager, String path) {
/* 31 */     super(file.toPath(), manager, path);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\FileResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */