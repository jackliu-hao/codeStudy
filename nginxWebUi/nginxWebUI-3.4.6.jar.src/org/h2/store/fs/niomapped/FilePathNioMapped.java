/*    */ package org.h2.store.fs.niomapped;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.h2.store.fs.FilePathWrapper;
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
/*    */ public class FilePathNioMapped
/*    */   extends FilePathWrapper
/*    */ {
/*    */   public FileChannel open(String paramString) throws IOException {
/* 20 */     return (FileChannel)new FileNioMapped(this.name.substring(getScheme().length() + 1), paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getScheme() {
/* 25 */     return "nioMapped";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\niomapped\FilePathNioMapped.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */