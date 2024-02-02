/*    */ package org.h2.store;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.util.Arrays;
/*    */ import org.h2.tools.CompressTool;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileStoreOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private FileStore store;
/*    */   private final Data page;
/*    */   private final String compressionAlgorithm;
/*    */   private final CompressTool compress;
/* 22 */   private final byte[] buffer = new byte[] { 0 };
/*    */   
/*    */   public FileStoreOutputStream(FileStore paramFileStore, String paramString) {
/* 25 */     this.store = paramFileStore;
/* 26 */     if (paramString != null) {
/* 27 */       this.compress = CompressTool.getInstance();
/* 28 */       this.compressionAlgorithm = paramString;
/*    */     } else {
/* 30 */       this.compress = null;
/* 31 */       this.compressionAlgorithm = null;
/*    */     } 
/* 33 */     this.page = Data.create(16);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int paramInt) {
/* 38 */     this.buffer[0] = (byte)paramInt;
/* 39 */     write(this.buffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] paramArrayOfbyte) {
/* 44 */     write(paramArrayOfbyte, 0, paramArrayOfbyte.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 49 */     if (paramInt2 > 0) {
/* 50 */       this.page.reset();
/* 51 */       if (this.compress != null) {
/* 52 */         if (paramInt1 != 0 || paramInt2 != paramArrayOfbyte.length) {
/* 53 */           paramArrayOfbyte = Arrays.copyOfRange(paramArrayOfbyte, paramInt1, paramInt1 + paramInt2);
/* 54 */           paramInt1 = 0;
/*    */         } 
/* 56 */         int i = paramInt2;
/* 57 */         paramArrayOfbyte = this.compress.compress(paramArrayOfbyte, this.compressionAlgorithm);
/* 58 */         paramInt2 = paramArrayOfbyte.length;
/* 59 */         this.page.checkCapacity(8 + paramInt2);
/* 60 */         this.page.writeInt(paramInt2);
/* 61 */         this.page.writeInt(i);
/* 62 */         this.page.write(paramArrayOfbyte, paramInt1, paramInt2);
/*    */       } else {
/* 64 */         this.page.checkCapacity(4 + paramInt2);
/* 65 */         this.page.writeInt(paramInt2);
/* 66 */         this.page.write(paramArrayOfbyte, paramInt1, paramInt2);
/*    */       } 
/* 68 */       this.page.fillAligned();
/* 69 */       this.store.write(this.page.getBytes(), 0, this.page.length());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 75 */     if (this.store != null)
/*    */       try {
/* 77 */         this.store.close();
/*    */       } finally {
/* 79 */         this.store = null;
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\FileStoreOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */