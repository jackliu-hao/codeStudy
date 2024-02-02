/*    */ package org.apache.commons.compress.changes;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.util.Objects;
/*    */ import org.apache.commons.compress.archivers.ArchiveEntry;
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
/*    */ class Change
/*    */ {
/*    */   private final String targetFile;
/*    */   private final ArchiveEntry entry;
/*    */   private final InputStream input;
/*    */   private final boolean replaceMode;
/*    */   private final int type;
/*    */   static final int TYPE_DELETE = 1;
/*    */   static final int TYPE_ADD = 2;
/*    */   static final int TYPE_MOVE = 3;
/*    */   static final int TYPE_DELETE_DIR = 4;
/*    */   
/*    */   Change(String fileName, int type) {
/* 51 */     Objects.requireNonNull(fileName, "fileName");
/* 52 */     this.targetFile = fileName;
/* 53 */     this.type = type;
/* 54 */     this.input = null;
/* 55 */     this.entry = null;
/* 56 */     this.replaceMode = true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Change(ArchiveEntry archiveEntry, InputStream inputStream, boolean replace) {
/* 66 */     Objects.requireNonNull(archiveEntry, "archiveEntry");
/* 67 */     Objects.requireNonNull(inputStream, "inputStream");
/* 68 */     this.entry = archiveEntry;
/* 69 */     this.input = inputStream;
/* 70 */     this.type = 2;
/* 71 */     this.targetFile = null;
/* 72 */     this.replaceMode = replace;
/*    */   }
/*    */   
/*    */   ArchiveEntry getEntry() {
/* 76 */     return this.entry;
/*    */   }
/*    */   
/*    */   InputStream getInput() {
/* 80 */     return this.input;
/*    */   }
/*    */   
/*    */   String targetFile() {
/* 84 */     return this.targetFile;
/*    */   }
/*    */   
/*    */   int type() {
/* 88 */     return this.type;
/*    */   }
/*    */   
/*    */   boolean isReplaceMode() {
/* 92 */     return this.replaceMode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\changes\Change.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */