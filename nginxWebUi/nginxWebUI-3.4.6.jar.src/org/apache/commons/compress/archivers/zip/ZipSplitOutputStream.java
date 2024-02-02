/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.Files;
/*     */ import org.apache.commons.compress.utils.FileNameUtils;
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
/*     */ 
/*     */ 
/*     */ class ZipSplitOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private OutputStream outputStream;
/*     */   private File zipFile;
/*     */   private final long splitSize;
/*     */   private int currentSplitSegmentIndex;
/*     */   private long currentSplitSegmentBytesWritten;
/*     */   private boolean finished;
/*  39 */   private final byte[] singleByte = new byte[1];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long ZIP_SEGMENT_MIN_SIZE = 65536L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long ZIP_SEGMENT_MAX_SIZE = 4294967295L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipSplitOutputStream(File zipFile, long splitSize) throws IllegalArgumentException, IOException {
/*  61 */     if (splitSize < 65536L || splitSize > 4294967295L) {
/*  62 */       throw new IllegalArgumentException("zip split segment size should between 64K and 4,294,967,295");
/*     */     }
/*     */     
/*  65 */     this.zipFile = zipFile;
/*  66 */     this.splitSize = splitSize;
/*     */     
/*  68 */     this.outputStream = Files.newOutputStream(zipFile.toPath(), new java.nio.file.OpenOption[0]);
/*     */     
/*  70 */     writeZipSplitSignature();
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
/*     */   public void prepareToWriteUnsplittableContent(long unsplittableContentSize) throws IllegalArgumentException, IOException {
/*  85 */     if (unsplittableContentSize > this.splitSize) {
/*  86 */       throw new IllegalArgumentException("The unsplittable content size is bigger than the split segment size");
/*     */     }
/*     */     
/*  89 */     long bytesRemainingInThisSegment = this.splitSize - this.currentSplitSegmentBytesWritten;
/*  90 */     if (bytesRemainingInThisSegment < unsplittableContentSize) {
/*  91 */       openNewSplitSegment();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int i) throws IOException {
/*  97 */     this.singleByte[0] = (byte)(i & 0xFF);
/*  98 */     write(this.singleByte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 103 */     write(b, 0, b.length);
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
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 117 */     if (len <= 0) {
/*     */       return;
/*     */     }
/*     */     
/* 121 */     if (this.currentSplitSegmentBytesWritten >= this.splitSize) {
/* 122 */       openNewSplitSegment();
/* 123 */       write(b, off, len);
/* 124 */     } else if (this.currentSplitSegmentBytesWritten + len > this.splitSize) {
/* 125 */       int bytesToWriteForThisSegment = (int)this.splitSize - (int)this.currentSplitSegmentBytesWritten;
/* 126 */       write(b, off, bytesToWriteForThisSegment);
/* 127 */       openNewSplitSegment();
/* 128 */       write(b, off + bytesToWriteForThisSegment, len - bytesToWriteForThisSegment);
/*     */     } else {
/* 130 */       this.outputStream.write(b, off, len);
/* 131 */       this.currentSplitSegmentBytesWritten += len;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 137 */     if (!this.finished) {
/* 138 */       finish();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void finish() throws IOException {
/* 148 */     if (this.finished) {
/* 149 */       throw new IOException("This archive has already been finished");
/*     */     }
/*     */     
/* 152 */     String zipFileBaseName = FileNameUtils.getBaseName(this.zipFile.getName());
/* 153 */     File lastZipSplitSegmentFile = new File(this.zipFile.getParentFile(), zipFileBaseName + ".zip");
/* 154 */     this.outputStream.close();
/* 155 */     if (!this.zipFile.renameTo(lastZipSplitSegmentFile)) {
/* 156 */       throw new IOException("Failed to rename " + this.zipFile + " to " + lastZipSplitSegmentFile);
/*     */     }
/* 158 */     this.finished = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void openNewSplitSegment() throws IOException {
/* 168 */     if (this.currentSplitSegmentIndex == 0) {
/* 169 */       this.outputStream.close();
/* 170 */       File file = createNewSplitSegmentFile(Integer.valueOf(1));
/* 171 */       if (!this.zipFile.renameTo(file)) {
/* 172 */         throw new IOException("Failed to rename " + this.zipFile + " to " + file);
/*     */       }
/*     */     } 
/*     */     
/* 176 */     File newFile = createNewSplitSegmentFile(null);
/*     */     
/* 178 */     this.outputStream.close();
/* 179 */     this.outputStream = Files.newOutputStream(newFile.toPath(), new java.nio.file.OpenOption[0]);
/* 180 */     this.currentSplitSegmentBytesWritten = 0L;
/* 181 */     this.zipFile = newFile;
/* 182 */     this.currentSplitSegmentIndex++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeZipSplitSignature() throws IOException {
/* 192 */     this.outputStream.write(ZipArchiveOutputStream.DD_SIG);
/* 193 */     this.currentSplitSegmentBytesWritten += ZipArchiveOutputStream.DD_SIG.length;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private File createNewSplitSegmentFile(Integer zipSplitSegmentSuffixIndex) throws IOException {
/* 219 */     int newZipSplitSegmentSuffixIndex = (zipSplitSegmentSuffixIndex == null) ? (this.currentSplitSegmentIndex + 2) : zipSplitSegmentSuffixIndex.intValue();
/* 220 */     String baseName = FileNameUtils.getBaseName(this.zipFile.getName());
/* 221 */     String extension = ".z";
/* 222 */     if (newZipSplitSegmentSuffixIndex <= 9) {
/* 223 */       extension = extension + "0" + newZipSplitSegmentSuffixIndex;
/*     */     } else {
/* 225 */       extension = extension + newZipSplitSegmentSuffixIndex;
/*     */     } 
/*     */     
/* 228 */     File newFile = new File(this.zipFile.getParent(), baseName + extension);
/*     */     
/* 230 */     if (newFile.exists()) {
/* 231 */       throw new IOException("split zip segment " + baseName + extension + " already exists");
/*     */     }
/* 233 */     return newFile;
/*     */   }
/*     */   
/*     */   public int getCurrentSplitSegmentIndex() {
/* 237 */     return this.currentSplitSegmentIndex;
/*     */   }
/*     */   
/*     */   public long getCurrentSplitSegmentBytesWritten() {
/* 241 */     return this.currentSplitSegmentBytesWritten;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipSplitOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */