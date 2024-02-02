/*    */ package org.codehaus.plexus.util.io;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public class FileInputStreamFacade
/*    */   implements InputStreamFacade
/*    */ {
/*    */   private final File file;
/*    */   
/*    */   public FileInputStreamFacade(File file) {
/* 35 */     this.file = file;
/*    */   }
/*    */   
/*    */   public InputStream getInputStream() throws IOException {
/* 39 */     return new FileInputStream(this.file);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\io\FileInputStreamFacade.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */