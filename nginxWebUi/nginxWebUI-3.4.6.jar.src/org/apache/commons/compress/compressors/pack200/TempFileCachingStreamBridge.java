/*    */ package org.apache.commons.compress.compressors.pack200;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ 
/*    */ 
/*    */ class TempFileCachingStreamBridge
/*    */   extends StreamBridge
/*    */ {
/*    */   private final File f;
/*    */   
/*    */   TempFileCachingStreamBridge() throws IOException {
/* 37 */     this.f = File.createTempFile("commons-compress", "packtemp");
/* 38 */     this.f.deleteOnExit();
/* 39 */     this.out = Files.newOutputStream(this.f.toPath(), new java.nio.file.OpenOption[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   InputStream getInputView() throws IOException {
/* 44 */     this.out.close();
/* 45 */     return new FilterInputStream(Files.newInputStream(this.f.toPath(), new java.nio.file.OpenOption[0]))
/*    */       {
/*    */         public void close() throws IOException {
/*    */           try {
/* 49 */             super.close();
/*    */           } finally {
/*    */             
/* 52 */             TempFileCachingStreamBridge.this.f.delete();
/*    */           } 
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\pack200\TempFileCachingStreamBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */