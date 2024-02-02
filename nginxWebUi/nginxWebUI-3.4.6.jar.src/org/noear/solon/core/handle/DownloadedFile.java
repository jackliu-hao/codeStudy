/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import org.noear.solon.Utils;
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
/*    */ public class DownloadedFile
/*    */ {
/*    */   public String contentType;
/*    */   public InputStream content;
/*    */   public String name;
/*    */   
/*    */   public DownloadedFile() {}
/*    */   
/*    */   public DownloadedFile(String contentType, InputStream content, String name) {
/* 39 */     this.contentType = contentType;
/* 40 */     this.content = content;
/* 41 */     this.name = name;
/*    */   }
/*    */   
/*    */   public DownloadedFile(String contentType, byte[] content, String name) {
/* 45 */     this.contentType = contentType;
/* 46 */     this.content = new ByteArrayInputStream(content);
/* 47 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void transferTo(File file) throws IOException {
/* 56 */     try (FileOutputStream stream = new FileOutputStream(file)) {
/* 57 */       Utils.transferTo(this.content, stream);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void transferTo(OutputStream stream) throws IOException {
/* 67 */     Utils.transferTo(this.content, stream);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\DownloadedFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */