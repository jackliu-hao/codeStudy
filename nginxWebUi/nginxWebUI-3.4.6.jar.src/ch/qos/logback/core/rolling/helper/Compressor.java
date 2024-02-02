/*     */ package ch.qos.logback.core.rolling.helper;
/*     */ 
/*     */ import ch.qos.logback.core.rolling.RolloverFailure;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.WarnStatus;
/*     */ import ch.qos.logback.core.util.FileUtil;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
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
/*     */ 
/*     */ public class Compressor
/*     */   extends ContextAwareBase
/*     */ {
/*     */   final CompressionMode compressionMode;
/*     */   static final int BUFFER_SIZE = 8192;
/*     */   
/*     */   public Compressor(CompressionMode compressionMode) {
/*  46 */     this.compressionMode = compressionMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void compress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
/*  56 */     switch (this.compressionMode) {
/*     */       case GZ:
/*  58 */         gzCompress(nameOfFile2Compress, nameOfCompressedFile);
/*     */         break;
/*     */       case ZIP:
/*  61 */         zipCompress(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
/*     */         break;
/*     */       case NONE:
/*  64 */         throw new UnsupportedOperationException("compress method called in NONE compression mode");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void zipCompress(String nameOfFile2zip, String nameOfZippedFile, String innerEntryName) {
/*  69 */     File file2zip = new File(nameOfFile2zip);
/*     */     
/*  71 */     if (!file2zip.exists()) {
/*  72 */       addStatus((Status)new WarnStatus("The file to compress named [" + nameOfFile2zip + "] does not exist.", this));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  77 */     if (innerEntryName == null) {
/*  78 */       addStatus((Status)new WarnStatus("The innerEntryName parameter cannot be null", this));
/*     */       
/*     */       return;
/*     */     } 
/*  82 */     if (!nameOfZippedFile.endsWith(".zip")) {
/*  83 */       nameOfZippedFile = nameOfZippedFile + ".zip";
/*     */     }
/*     */     
/*  86 */     File zippedFile = new File(nameOfZippedFile);
/*     */     
/*  88 */     if (zippedFile.exists()) {
/*  89 */       addStatus((Status)new WarnStatus("The target compressed file named [" + nameOfZippedFile + "] exist already.", this));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  94 */     addInfo("ZIP compressing [" + file2zip + "] as [" + zippedFile + "]");
/*  95 */     createMissingTargetDirsIfNecessary(zippedFile);
/*     */     
/*  97 */     BufferedInputStream bis = null;
/*  98 */     ZipOutputStream zos = null;
/*     */     try {
/* 100 */       bis = new BufferedInputStream(new FileInputStream(nameOfFile2zip));
/* 101 */       zos = new ZipOutputStream(new FileOutputStream(nameOfZippedFile));
/*     */       
/* 103 */       ZipEntry zipEntry = computeZipEntry(innerEntryName);
/* 104 */       zos.putNextEntry(zipEntry);
/*     */       
/* 106 */       byte[] inbuf = new byte[8192];
/*     */       
/*     */       int n;
/* 109 */       while ((n = bis.read(inbuf)) != -1) {
/* 110 */         zos.write(inbuf, 0, n);
/*     */       }
/*     */       
/* 113 */       bis.close();
/* 114 */       bis = null;
/* 115 */       zos.close();
/* 116 */       zos = null;
/*     */       
/* 118 */       if (!file2zip.delete()) {
/* 119 */         addStatus((Status)new WarnStatus("Could not delete [" + nameOfFile2zip + "].", this));
/*     */       }
/* 121 */     } catch (Exception e) {
/* 122 */       addStatus((Status)new ErrorStatus("Error occurred while compressing [" + nameOfFile2zip + "] into [" + nameOfZippedFile + "].", this, e));
/*     */     } finally {
/* 124 */       if (bis != null) {
/*     */         try {
/* 126 */           bis.close();
/* 127 */         } catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */       
/* 131 */       if (zos != null) {
/*     */         try {
/* 133 */           zos.close();
/* 134 */         } catch (IOException iOException) {}
/*     */       }
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
/*     */   ZipEntry computeZipEntry(File zippedFile) {
/* 158 */     return computeZipEntry(zippedFile.getName());
/*     */   }
/*     */   
/*     */   ZipEntry computeZipEntry(String filename) {
/* 162 */     String nameOfFileNestedWithinArchive = computeFileNameStrWithoutCompSuffix(filename, this.compressionMode);
/* 163 */     return new ZipEntry(nameOfFileNestedWithinArchive);
/*     */   }
/*     */   
/*     */   private void gzCompress(String nameOfFile2gz, String nameOfgzedFile) {
/* 167 */     File file2gz = new File(nameOfFile2gz);
/*     */     
/* 169 */     if (!file2gz.exists()) {
/* 170 */       addStatus((Status)new WarnStatus("The file to compress named [" + nameOfFile2gz + "] does not exist.", this));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 175 */     if (!nameOfgzedFile.endsWith(".gz")) {
/* 176 */       nameOfgzedFile = nameOfgzedFile + ".gz";
/*     */     }
/*     */     
/* 179 */     File gzedFile = new File(nameOfgzedFile);
/*     */     
/* 181 */     if (gzedFile.exists()) {
/* 182 */       addWarn("The target compressed file named [" + nameOfgzedFile + "] exist already. Aborting file compression.");
/*     */       
/*     */       return;
/*     */     } 
/* 186 */     addInfo("GZ compressing [" + file2gz + "] as [" + gzedFile + "]");
/* 187 */     createMissingTargetDirsIfNecessary(gzedFile);
/*     */     
/* 189 */     BufferedInputStream bis = null;
/* 190 */     GZIPOutputStream gzos = null;
/*     */     try {
/* 192 */       bis = new BufferedInputStream(new FileInputStream(nameOfFile2gz));
/* 193 */       gzos = new GZIPOutputStream(new FileOutputStream(nameOfgzedFile));
/* 194 */       byte[] inbuf = new byte[8192];
/*     */       
/*     */       int n;
/* 197 */       while ((n = bis.read(inbuf)) != -1) {
/* 198 */         gzos.write(inbuf, 0, n);
/*     */       }
/*     */       
/* 201 */       bis.close();
/* 202 */       bis = null;
/* 203 */       gzos.close();
/* 204 */       gzos = null;
/*     */       
/* 206 */       if (!file2gz.delete()) {
/* 207 */         addStatus((Status)new WarnStatus("Could not delete [" + nameOfFile2gz + "].", this));
/*     */       }
/* 209 */     } catch (Exception e) {
/* 210 */       addStatus((Status)new ErrorStatus("Error occurred while compressing [" + nameOfFile2gz + "] into [" + nameOfgzedFile + "].", this, e));
/*     */     } finally {
/* 212 */       if (bis != null) {
/*     */         try {
/* 214 */           bis.close();
/* 215 */         } catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */       
/* 219 */       if (gzos != null) {
/*     */         try {
/* 221 */           gzos.close();
/* 222 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String computeFileNameStrWithoutCompSuffix(String fileNamePatternStr, CompressionMode compressionMode) {
/* 230 */     int len = fileNamePatternStr.length();
/* 231 */     switch (compressionMode) {
/*     */       case GZ:
/* 233 */         if (fileNamePatternStr.endsWith(".gz")) {
/* 234 */           return fileNamePatternStr.substring(0, len - 3);
/*     */         }
/* 236 */         return fileNamePatternStr;
/*     */       case ZIP:
/* 238 */         if (fileNamePatternStr.endsWith(".zip")) {
/* 239 */           return fileNamePatternStr.substring(0, len - 4);
/*     */         }
/* 241 */         return fileNamePatternStr;
/*     */       case NONE:
/* 243 */         return fileNamePatternStr;
/*     */     } 
/* 245 */     throw new IllegalStateException("Execution should not reach this point");
/*     */   }
/*     */   
/*     */   void createMissingTargetDirsIfNecessary(File file) {
/* 249 */     boolean result = FileUtil.createMissingParentDirectories(file);
/* 250 */     if (!result) {
/* 251 */       addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 257 */     return getClass().getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<?> asyncCompress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
/* 262 */     CompressionRunnable runnable = new CompressionRunnable(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
/* 263 */     ExecutorService executorService = this.context.getScheduledExecutorService();
/* 264 */     Future<?> future = executorService.submit(runnable);
/* 265 */     return future;
/*     */   }
/*     */   
/*     */   class CompressionRunnable
/*     */     implements Runnable {
/*     */     final String nameOfFile2Compress;
/*     */     final String nameOfCompressedFile;
/*     */     final String innerEntryName;
/*     */     
/*     */     public CompressionRunnable(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
/* 275 */       this.nameOfFile2Compress = nameOfFile2Compress;
/* 276 */       this.nameOfCompressedFile = nameOfCompressedFile;
/* 277 */       this.innerEntryName = innerEntryName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 282 */       Compressor.this.compress(this.nameOfFile2Compress, this.nameOfCompressedFile, this.innerEntryName);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\Compressor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */