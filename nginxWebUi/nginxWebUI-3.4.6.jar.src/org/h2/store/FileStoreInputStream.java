/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.tools.CompressTool;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileStoreInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private FileStore store;
/*     */   private final Data page;
/*     */   private int remainingInBuffer;
/*     */   private final CompressTool compress;
/*     */   private boolean endOfFile;
/*     */   private final boolean alwaysClose;
/*     */   
/*     */   public FileStoreInputStream(FileStore paramFileStore, boolean paramBoolean1, boolean paramBoolean2) {
/*  28 */     this.store = paramFileStore;
/*  29 */     this.alwaysClose = paramBoolean2;
/*  30 */     if (paramBoolean1) {
/*  31 */       this.compress = CompressTool.getInstance();
/*     */     } else {
/*  33 */       this.compress = null;
/*     */     } 
/*  35 */     this.page = Data.create(16);
/*     */     try {
/*  37 */       if (paramFileStore.length() <= 48L) {
/*  38 */         close();
/*     */       } else {
/*  40 */         fillBuffer();
/*     */       } 
/*  42 */     } catch (IOException iOException) {
/*  43 */       throw DbException.convertIOException(iOException, paramFileStore.name);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() {
/*  49 */     return (this.remainingInBuffer <= 0) ? 0 : this.remainingInBuffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] paramArrayOfbyte) throws IOException {
/*  54 */     return read(paramArrayOfbyte, 0, paramArrayOfbyte.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/*  59 */     if (paramInt2 == 0) {
/*  60 */       return 0;
/*     */     }
/*  62 */     int i = 0;
/*  63 */     while (paramInt2 > 0) {
/*  64 */       int j = readBlock(paramArrayOfbyte, paramInt1, paramInt2);
/*  65 */       if (j < 0) {
/*     */         break;
/*     */       }
/*  68 */       i += j;
/*  69 */       paramInt1 += j;
/*  70 */       paramInt2 -= j;
/*     */     } 
/*  72 */     return (i == 0) ? -1 : i;
/*     */   }
/*     */   
/*     */   private int readBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/*  76 */     fillBuffer();
/*  77 */     if (this.endOfFile) {
/*  78 */       return -1;
/*     */     }
/*  80 */     int i = Math.min(this.remainingInBuffer, paramInt2);
/*  81 */     this.page.read(paramArrayOfbyte, paramInt1, i);
/*  82 */     this.remainingInBuffer -= i;
/*  83 */     return i;
/*     */   }
/*     */   
/*     */   private void fillBuffer() throws IOException {
/*  87 */     if (this.remainingInBuffer > 0 || this.endOfFile) {
/*     */       return;
/*     */     }
/*  90 */     this.page.reset();
/*  91 */     this.store.openFile();
/*  92 */     if (this.store.length() == this.store.getFilePointer()) {
/*  93 */       close();
/*     */       return;
/*     */     } 
/*  96 */     this.store.readFully(this.page.getBytes(), 0, 16);
/*  97 */     this.page.reset();
/*  98 */     this.remainingInBuffer = this.page.readInt();
/*  99 */     if (this.remainingInBuffer < 0) {
/* 100 */       close();
/*     */       return;
/*     */     } 
/* 103 */     this.page.checkCapacity(this.remainingInBuffer);
/*     */     
/* 105 */     if (this.compress != null) {
/* 106 */       this.page.checkCapacity(4);
/* 107 */       this.page.readInt();
/*     */     } 
/* 109 */     this.page.setPos(this.page.length() + this.remainingInBuffer);
/* 110 */     this.page.fillAligned();
/* 111 */     int i = this.page.length() - 16;
/* 112 */     this.page.reset();
/* 113 */     this.page.readInt();
/* 114 */     this.store.readFully(this.page.getBytes(), 16, i);
/* 115 */     this.page.reset();
/* 116 */     this.page.readInt();
/* 117 */     if (this.compress != null) {
/* 118 */       int j = this.page.readInt();
/* 119 */       byte[] arrayOfByte = Utils.newBytes(this.remainingInBuffer);
/* 120 */       this.page.read(arrayOfByte, 0, this.remainingInBuffer);
/* 121 */       this.page.reset();
/* 122 */       this.page.checkCapacity(j);
/* 123 */       CompressTool.expand(arrayOfByte, this.page.getBytes(), 0);
/* 124 */       this.remainingInBuffer = j;
/*     */     } 
/* 126 */     if (this.alwaysClose) {
/* 127 */       this.store.closeFile();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 133 */     if (this.store != null) {
/*     */       try {
/* 135 */         this.store.close();
/* 136 */         this.endOfFile = true;
/*     */       } finally {
/* 138 */         this.store = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() {
/* 145 */     close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 150 */     fillBuffer();
/* 151 */     if (this.endOfFile) {
/* 152 */       return -1;
/*     */     }
/* 154 */     int i = this.page.readByte() & 0xFF;
/* 155 */     this.remainingInBuffer--;
/* 156 */     return i;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\FileStoreInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */