/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.platform.FileUtils;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
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
/*    */ public class W32FileUtils
/*    */   extends FileUtils
/*    */ {
/*    */   public boolean hasTrash() {
/* 35 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void moveToTrash(File... files) throws IOException {
/* 40 */     Shell32 shell = Shell32.INSTANCE;
/* 41 */     ShellAPI.SHFILEOPSTRUCT fileop = new ShellAPI.SHFILEOPSTRUCT();
/* 42 */     fileop.wFunc = 3;
/* 43 */     String[] paths = new String[files.length];
/* 44 */     for (int i = 0; i < paths.length; i++) {
/* 45 */       paths[i] = files[i].getAbsolutePath();
/*    */     }
/* 47 */     fileop.pFrom = fileop.encodePaths(paths);
/* 48 */     fileop.fFlags = 1620;
/* 49 */     int ret = shell.SHFileOperation(fileop);
/* 50 */     if (ret != 0) {
/* 51 */       throw new IOException("Move to trash failed: " + fileop.pFrom + ": " + 
/* 52 */           Kernel32Util.formatMessageFromLastErrorCode(ret));
/*    */     }
/* 54 */     if (fileop.fAnyOperationsAborted)
/* 55 */       throw new IOException("Move to trash aborted"); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\W32FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */