/*    */ package org.apache.commons.compress.parallel;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.file.Files;
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
/*    */ public class FileBasedScatterGatherBackingStore
/*    */   implements ScatterGatherBackingStore
/*    */ {
/*    */   private final File target;
/*    */   private final OutputStream os;
/*    */   private boolean closed;
/*    */   
/*    */   public FileBasedScatterGatherBackingStore(File target) throws FileNotFoundException {
/* 38 */     this.target = target;
/*    */     try {
/* 40 */       this.os = Files.newOutputStream(target.toPath(), new java.nio.file.OpenOption[0]);
/* 41 */     } catch (FileNotFoundException ex) {
/* 42 */       throw ex;
/* 43 */     } catch (IOException ex) {
/*    */       
/* 45 */       throw new RuntimeException(ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getInputStream() throws IOException {
/* 51 */     return Files.newInputStream(this.target.toPath(), new java.nio.file.OpenOption[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void closeForWriting() throws IOException {
/* 57 */     if (!this.closed) {
/* 58 */       this.os.close();
/* 59 */       this.closed = true;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeOut(byte[] data, int offset, int length) throws IOException {
/* 65 */     this.os.write(data, offset, length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/*    */     try {
/* 71 */       closeForWriting();
/*    */     } finally {
/* 73 */       if (this.target.exists() && !this.target.delete())
/* 74 */         this.target.deleteOnExit(); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\parallel\FileBasedScatterGatherBackingStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */