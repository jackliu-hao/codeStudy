/*    */ package org.h2.store.fs.mem;
/*    */ 
/*    */ import org.h2.store.fs.FilePath;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FilePathMemLZF
/*    */   extends FilePathMem
/*    */ {
/*    */   public FilePathMem getPath(String paramString) {
/* 15 */     FilePathMemLZF filePathMemLZF = new FilePathMemLZF();
/* 16 */     filePathMemLZF.name = getCanonicalPath(paramString);
/* 17 */     return filePathMemLZF;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean compressed() {
/* 22 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getScheme() {
/* 27 */     return "memLZF";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\mem\FilePathMemLZF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */