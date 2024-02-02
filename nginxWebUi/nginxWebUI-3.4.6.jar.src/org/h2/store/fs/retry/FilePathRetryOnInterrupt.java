/*    */ package org.h2.store.fs.retry;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FilePathRetryOnInterrupt
/*    */   extends FilePathWrapper
/*    */ {
/*    */   static final String SCHEME = "retry";
/*    */   
/*    */   public FileChannel open(String paramString) throws IOException {
/* 26 */     return (FileChannel)new FileRetryOnInterrupt(this.name.substring(getScheme().length() + 1), paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getScheme() {
/* 31 */     return "retry";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\retry\FilePathRetryOnInterrupt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */