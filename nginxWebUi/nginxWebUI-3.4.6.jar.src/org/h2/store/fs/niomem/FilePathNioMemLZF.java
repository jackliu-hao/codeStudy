/*    */ package org.h2.store.fs.niomem;
/*    */ 
/*    */ import org.h2.store.fs.FilePath;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FilePathNioMemLZF
/*    */   extends FilePathNioMem
/*    */ {
/*    */   boolean compressed() {
/* 15 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public FilePathNioMem getPath(String paramString) {
/* 20 */     if (!paramString.startsWith(getScheme())) {
/* 21 */       throw new IllegalArgumentException(paramString + " doesn't start with " + 
/* 22 */           getScheme());
/*    */     }
/* 24 */     int i = paramString.indexOf(':');
/* 25 */     int j = paramString.lastIndexOf(':');
/* 26 */     FilePathNioMemLZF filePathNioMemLZF = new FilePathNioMemLZF();
/* 27 */     if (i != -1 && i != j) {
/* 28 */       filePathNioMemLZF.compressLaterCachePercent = Float.parseFloat(paramString.substring(i + 1, j));
/*    */     }
/* 30 */     filePathNioMemLZF.name = getCanonicalPath(paramString);
/* 31 */     return filePathNioMemLZF;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isRoot() {
/* 36 */     return (this.name.lastIndexOf(':') == this.name.length() - 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getScheme() {
/* 41 */     return "nioMemLZF";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\niomem\FilePathNioMemLZF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */