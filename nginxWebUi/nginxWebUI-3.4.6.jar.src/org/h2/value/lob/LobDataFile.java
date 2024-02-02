/*    */ package org.h2.value.lob;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.InputStream;
/*    */ import org.h2.engine.SysProperties;
/*    */ import org.h2.store.DataHandler;
/*    */ import org.h2.store.FileStore;
/*    */ import org.h2.store.FileStoreInputStream;
/*    */ import org.h2.store.fs.FileUtils;
/*    */ import org.h2.value.ValueLob;
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
/*    */ public final class LobDataFile
/*    */   extends LobData
/*    */ {
/*    */   private DataHandler handler;
/*    */   private final String fileName;
/*    */   private final FileStore tempFile;
/*    */   
/*    */   public LobDataFile(DataHandler paramDataHandler, String paramString, FileStore paramFileStore) {
/* 35 */     this.handler = paramDataHandler;
/* 36 */     this.fileName = paramString;
/* 37 */     this.tempFile = paramFileStore;
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(ValueLob paramValueLob) {
/* 42 */     if (this.fileName != null) {
/* 43 */       if (this.tempFile != null) {
/* 44 */         this.tempFile.stopAutoDelete();
/*    */       }
/*    */ 
/*    */       
/* 48 */       synchronized (this.handler.getLobSyncObject()) {
/* 49 */         FileUtils.delete(this.fileName);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getInputStream(long paramLong) {
/* 56 */     FileStore fileStore = this.handler.openFile(this.fileName, "r", true);
/* 57 */     boolean bool = SysProperties.lobCloseBetweenReads;
/* 58 */     return new BufferedInputStream((InputStream)new FileStoreInputStream(fileStore, false, bool), 4096);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DataHandler getDataHandler() {
/* 64 */     return this.handler;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return "lob-file: " + this.fileName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\lob\LobDataFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */