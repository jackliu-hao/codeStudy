/*    */ package org.noear.solon.core.handle;
/*    */ 
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
/*    */ public class UploadedFile
/*    */   extends DownloadedFile
/*    */ {
/*    */   public long contentSize;
/*    */   public String extension;
/*    */   
/*    */   public UploadedFile() {}
/*    */   
/*    */   public UploadedFile(String contentType, InputStream content, String name) {
/* 45 */     super(contentType, content, name);
/*    */   }
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
/*    */   public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension) {
/* 58 */     super(contentType, content, name);
/* 59 */     this.contentSize = contentSize;
/* 60 */     this.extension = extension;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 67 */     return (this.contentSize == 0L);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\UploadedFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */