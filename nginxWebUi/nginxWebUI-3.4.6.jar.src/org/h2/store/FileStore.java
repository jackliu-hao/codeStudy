/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.SecureFileStore;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileStore
/*     */ {
/*     */   public static final int HEADER_LENGTH = 48;
/*  36 */   private static final String HEADER = "-- H2 0.5/B --      "
/*  37 */     .substring(0, 15) + "\n";
/*     */   
/*     */   private static final boolean ASSERT;
/*     */   
/*     */   static {
/*  42 */     boolean bool = false;
/*     */     
/*  44 */     assert bool = true;
/*  45 */     ASSERT = bool;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected String name;
/*     */ 
/*     */   
/*     */   private final DataHandler handler;
/*     */ 
/*     */   
/*     */   private FileChannel file;
/*     */ 
/*     */   
/*     */   private long filePos;
/*     */ 
/*     */   
/*     */   private long fileLength;
/*     */ 
/*     */   
/*     */   private Reference<?> autoDeleteReference;
/*     */   
/*     */   private boolean checkedWriting = true;
/*     */   
/*     */   private final String mode;
/*     */   
/*     */   private FileLock lock;
/*     */ 
/*     */   
/*     */   protected FileStore(DataHandler paramDataHandler, String paramString1, String paramString2) {
/*  75 */     this.handler = paramDataHandler;
/*  76 */     this.name = paramString1;
/*     */     try {
/*  78 */       boolean bool = FileUtils.exists(paramString1);
/*  79 */       if (bool && !FileUtils.canWrite(paramString1)) {
/*  80 */         paramString2 = "r";
/*     */       } else {
/*  82 */         FileUtils.createDirectories(FileUtils.getParent(paramString1));
/*     */       } 
/*  84 */       this.file = FileUtils.open(paramString1, paramString2);
/*  85 */       if (bool) {
/*  86 */         this.fileLength = this.file.size();
/*     */       }
/*  88 */     } catch (IOException iOException) {
/*  89 */       throw DbException.convertIOException(iOException, "name: " + paramString1 + " mode: " + paramString2);
/*     */     } 
/*     */     
/*  92 */     this.mode = paramString2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileStore open(DataHandler paramDataHandler, String paramString1, String paramString2) {
/* 104 */     return open(paramDataHandler, paramString1, paramString2, null, null, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileStore open(DataHandler paramDataHandler, String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte) {
/* 119 */     return open(paramDataHandler, paramString1, paramString2, paramString3, paramArrayOfbyte, 1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileStore open(DataHandler paramDataHandler, String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte, int paramInt) {
/*     */     SecureFileStore secureFileStore;
/* 137 */     if (paramString3 == null) {
/* 138 */       FileStore fileStore = new FileStore(paramDataHandler, paramString1, paramString2);
/*     */     } else {
/* 140 */       secureFileStore = new SecureFileStore(paramDataHandler, paramString1, paramString2, paramString3, paramArrayOfbyte, paramInt);
/*     */     } 
/*     */     
/* 143 */     return (FileStore)secureFileStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] generateSalt() {
/* 152 */     return HEADER.getBytes(StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initKey(byte[] paramArrayOfbyte) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCheckedWriting(boolean paramBoolean) {
/* 166 */     this.checkedWriting = paramBoolean;
/*     */   }
/*     */   
/*     */   private void checkWritingAllowed() {
/* 170 */     if (this.handler != null && this.checkedWriting) {
/* 171 */       this.handler.checkWritingAllowed();
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkPowerOff() {
/* 176 */     if (this.handler != null) {
/* 177 */       this.handler.checkPowerOff();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() {
/* 186 */     byte b = 16;
/*     */     
/* 188 */     byte[] arrayOfByte = HEADER.getBytes(StandardCharsets.UTF_8);
/* 189 */     if (length() < 48L) {
/*     */       
/* 191 */       this.checkedWriting = false;
/* 192 */       writeDirect(arrayOfByte, 0, b);
/* 193 */       byte[] arrayOfByte1 = generateSalt();
/* 194 */       writeDirect(arrayOfByte1, 0, b);
/* 195 */       initKey(arrayOfByte1);
/*     */       
/* 197 */       write(arrayOfByte, 0, b);
/* 198 */       this.checkedWriting = true;
/*     */     } else {
/*     */       
/* 201 */       seek(0L);
/* 202 */       byte[] arrayOfByte2 = new byte[b];
/* 203 */       readFullyDirect(arrayOfByte2, 0, b);
/* 204 */       if (!Arrays.equals(arrayOfByte2, arrayOfByte)) {
/* 205 */         throw DbException.get(90048, this.name);
/*     */       }
/* 207 */       byte[] arrayOfByte1 = new byte[b];
/* 208 */       readFullyDirect(arrayOfByte1, 0, b);
/* 209 */       initKey(arrayOfByte1);
/*     */       
/* 211 */       readFully(arrayOfByte2, 0, 16);
/* 212 */       if (!Arrays.equals(arrayOfByte2, arrayOfByte)) {
/* 213 */         throw DbException.get(90049, this.name);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 222 */     if (this.file != null) {
/*     */       try {
/* 224 */         trace("close", this.name, this.file);
/* 225 */         this.file.close();
/* 226 */       } catch (IOException iOException) {
/* 227 */         throw DbException.convertIOException(iOException, this.name);
/*     */       } finally {
/* 229 */         this.file = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeSilently() {
/*     */     try {
/* 240 */       close();
/* 241 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeAndDeleteSilently() {
/* 250 */     if (this.file != null) {
/* 251 */       closeSilently();
/* 252 */       this.handler.getTempFileDeleter().deleteFile(this.autoDeleteReference, this.name);
/* 253 */       this.name = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void readFullyDirect(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 265 */     readFully(paramArrayOfbyte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 276 */     if (paramInt2 < 0 || paramInt2 % 16 != 0) {
/* 277 */       throw DbException.getInternalError("unaligned read " + this.name + " len " + paramInt2);
/*     */     }
/* 279 */     checkPowerOff();
/*     */     try {
/* 281 */       FileUtils.readFully(this.file, ByteBuffer.wrap(paramArrayOfbyte, paramInt1, paramInt2));
/* 282 */     } catch (IOException iOException) {
/* 283 */       throw DbException.convertIOException(iOException, this.name);
/*     */     } 
/* 285 */     this.filePos += paramInt2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seek(long paramLong) {
/* 294 */     if (paramLong % 16L != 0L) {
/* 295 */       throw DbException.getInternalError("unaligned seek " + this.name + " pos " + paramLong);
/*     */     }
/*     */     try {
/* 298 */       if (paramLong != this.filePos) {
/* 299 */         this.file.position(paramLong);
/* 300 */         this.filePos = paramLong;
/*     */       } 
/* 302 */     } catch (IOException iOException) {
/* 303 */       throw DbException.convertIOException(iOException, this.name);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeDirect(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 315 */     write(paramArrayOfbyte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 326 */     if (paramInt2 < 0 || paramInt2 % 16 != 0) {
/* 327 */       throw DbException.getInternalError("unaligned write " + this.name + " len " + paramInt2);
/*     */     }
/* 329 */     checkWritingAllowed();
/* 330 */     checkPowerOff();
/*     */     try {
/* 332 */       FileUtils.writeFully(this.file, ByteBuffer.wrap(paramArrayOfbyte, paramInt1, paramInt2));
/* 333 */     } catch (IOException iOException) {
/* 334 */       closeFileSilently();
/* 335 */       throw DbException.convertIOException(iOException, this.name);
/*     */     } 
/* 337 */     this.filePos += paramInt2;
/* 338 */     this.fileLength = Math.max(this.filePos, this.fileLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLength(long paramLong) {
/* 347 */     if (paramLong % 16L != 0L) {
/* 348 */       throw DbException.getInternalError("unaligned setLength " + this.name + " pos " + paramLong);
/*     */     }
/* 350 */     checkPowerOff();
/* 351 */     checkWritingAllowed();
/*     */     try {
/* 353 */       if (paramLong > this.fileLength) {
/* 354 */         long l = this.filePos;
/* 355 */         this.file.position(paramLong - 1L);
/* 356 */         FileUtils.writeFully(this.file, ByteBuffer.wrap(new byte[1]));
/* 357 */         this.file.position(l);
/*     */       } else {
/* 359 */         this.file.truncate(paramLong);
/*     */       } 
/* 361 */       this.fileLength = paramLong;
/* 362 */     } catch (IOException iOException) {
/* 363 */       closeFileSilently();
/* 364 */       throw DbException.convertIOException(iOException, this.name);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long length() {
/* 374 */     long l = this.fileLength;
/* 375 */     if (ASSERT) {
/*     */       try {
/* 377 */         l = this.file.size();
/* 378 */         if (l != this.fileLength) {
/* 379 */           throw DbException.getInternalError("file " + this.name + " length " + l + " expected " + this.fileLength);
/*     */         }
/* 381 */         if (l % 16L != 0L) {
/* 382 */           long l1 = l + 16L - l % 16L;
/*     */           
/* 384 */           this.file.truncate(l1);
/* 385 */           this.fileLength = l1;
/* 386 */           throw DbException.getInternalError("unaligned file length " + this.name + " len " + l);
/*     */         } 
/* 388 */       } catch (IOException iOException) {
/* 389 */         throw DbException.convertIOException(iOException, this.name);
/*     */       } 
/*     */     }
/* 392 */     return l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getFilePointer() {
/* 401 */     if (ASSERT) {
/*     */       try {
/* 403 */         if (this.file.position() != this.filePos) {
/* 404 */           throw DbException.getInternalError(this.file.position() + " " + this.filePos);
/*     */         }
/* 406 */       } catch (IOException iOException) {
/* 407 */         throw DbException.convertIOException(iOException, this.name);
/*     */       } 
/*     */     }
/* 410 */     return this.filePos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync() {
/*     */     try {
/* 419 */       this.file.force(true);
/* 420 */     } catch (IOException iOException) {
/* 421 */       closeFileSilently();
/* 422 */       throw DbException.convertIOException(iOException, this.name);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void autoDelete() {
/* 430 */     if (this.autoDeleteReference == null) {
/* 431 */       this.autoDeleteReference = this.handler.getTempFileDeleter().addFile(this.name, this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopAutoDelete() {
/* 439 */     this.handler.getTempFileDeleter().stopAutoDelete(this.autoDeleteReference, this.name);
/* 440 */     this.autoDeleteReference = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeFile() throws IOException {
/* 448 */     this.file.close();
/* 449 */     this.file = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeFileSilently() {
/*     */     try {
/* 459 */       this.file.close();
/* 460 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void openFile() throws IOException {
/* 471 */     if (this.file == null) {
/* 472 */       this.file = FileUtils.open(this.name, this.mode);
/* 473 */       this.file.position(this.filePos);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void trace(String paramString1, String paramString2, Object paramObject) {
/* 478 */     if (SysProperties.TRACE_IO) {
/* 479 */       System.out.println("FileStore." + paramString1 + " " + paramString2 + " " + paramObject);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean tryLock() {
/*     */     try {
/* 490 */       this.lock = this.file.tryLock();
/* 491 */       return (this.lock != null);
/* 492 */     } catch (Exception exception) {
/*     */       
/* 494 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void releaseLock() {
/* 502 */     if (this.file != null && this.lock != null) {
/*     */       try {
/* 504 */         this.lock.release();
/* 505 */       } catch (Exception exception) {}
/*     */ 
/*     */       
/* 508 */       this.lock = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\FileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */