/*    */ package com.sun.mail.pop3;
/*    */ 
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
/*    */ class TempFile
/*    */ {
/*    */   private File file;
/*    */   private WritableSharedFile sf;
/*    */   
/*    */   public TempFile(File dir) throws IOException {
/* 64 */     this.file = File.createTempFile("pop3.", ".mbox", dir);
/*    */     
/* 66 */     this.file.deleteOnExit();
/* 67 */     this.sf = new WritableSharedFile(this.file);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AppendStream getAppendStream() throws IOException {
/* 74 */     return this.sf.getAppendStream();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {
/*    */     try {
/* 82 */       this.sf.close();
/* 83 */     } catch (IOException ex) {}
/*    */ 
/*    */     
/* 86 */     this.file.delete();
/*    */   }
/*    */   
/*    */   protected void finalize() throws Throwable {
/* 90 */     super.finalize();
/* 91 */     close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\TempFile.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */