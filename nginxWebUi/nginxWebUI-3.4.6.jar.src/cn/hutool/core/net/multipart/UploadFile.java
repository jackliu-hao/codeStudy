/*     */ package cn.hutool.core.net.multipart;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UploadFile
/*     */ {
/*     */   private static final String TMP_FILE_PREFIX = "hutool-";
/*     */   private static final String TMP_FILE_SUFFIX = ".upload.tmp";
/*     */   private final UploadFileHeader header;
/*     */   private final UploadSetting setting;
/*  27 */   private long size = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] data;
/*     */ 
/*     */ 
/*     */   
/*     */   private File tempFile;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UploadFile(UploadFileHeader header, UploadSetting setting) {
/*  41 */     this.header = header;
/*  42 */     this.setting = setting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void delete() {
/*  51 */     if (this.tempFile != null)
/*     */     {
/*  53 */       this.tempFile.delete();
/*     */     }
/*  55 */     if (this.data != null) {
/*  56 */       this.data = null;
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
/*     */   public File write(String destPath) throws IOException {
/*  69 */     if (this.data != null || this.tempFile != null) {
/*  70 */       return write(FileUtil.file(destPath));
/*     */     }
/*  72 */     return null;
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
/*     */   public File write(File destination) throws IOException {
/*  84 */     assertValid();
/*     */     
/*  86 */     if (destination.isDirectory() == true) {
/*  87 */       destination = new File(destination, this.header.getFileName());
/*     */     }
/*  89 */     if (this.data != null) {
/*     */       
/*  91 */       FileUtil.writeBytes(this.data, destination);
/*  92 */       this.data = null;
/*     */     } else {
/*     */       
/*  95 */       if (null == this.tempFile) {
/*  96 */         throw new NullPointerException("Temp file is null !");
/*     */       }
/*  98 */       if (false == this.tempFile.exists()) {
/*  99 */         throw new NoSuchFileException("Temp file: [" + this.tempFile.getAbsolutePath() + "] not exist!");
/*     */       }
/*     */       
/* 102 */       FileUtil.move(this.tempFile, destination, true);
/*     */     } 
/* 104 */     return destination;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getFileContent() throws IOException {
/* 112 */     assertValid();
/*     */     
/* 114 */     if (this.data != null) {
/* 115 */       return this.data;
/*     */     }
/* 117 */     if (this.tempFile != null) {
/* 118 */       return FileUtil.readBytes(this.tempFile);
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getFileInputStream() throws IOException {
/* 128 */     assertValid();
/*     */     
/* 130 */     if (this.data != null) {
/* 131 */       return IoUtil.toBuffered(IoUtil.toStream(this.data));
/*     */     }
/* 133 */     if (this.tempFile != null) {
/* 134 */       return IoUtil.toBuffered(IoUtil.toStream(this.tempFile));
/*     */     }
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UploadFileHeader getHeader() {
/* 145 */     return this.header;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileName() {
/* 152 */     return (this.header == null) ? null : this.header.getFileName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long size() {
/* 161 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUploaded() {
/* 168 */     return (this.size > 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInMemory() {
/* 175 */     return (this.data != null);
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
/*     */   protected boolean processStream(MultipartRequestInputStream input) throws IOException {
/* 188 */     if (!isAllowedExtension()) {
/*     */       
/* 190 */       this.size = input.skipToBoundary();
/* 191 */       return false;
/*     */     } 
/* 193 */     this.size = 0L;
/*     */ 
/*     */     
/* 196 */     int memoryThreshold = this.setting.memoryThreshold;
/* 197 */     if (memoryThreshold > 0) {
/* 198 */       ByteArrayOutputStream baos = new ByteArrayOutputStream(memoryThreshold);
/* 199 */       long written = input.copy(baos, memoryThreshold);
/* 200 */       this.data = baos.toByteArray();
/* 201 */       if (written <= memoryThreshold) {
/*     */         
/* 203 */         this.size = this.data.length;
/* 204 */         return true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 209 */     this.tempFile = FileUtil.createTempFile("hutool-", ".upload.tmp", FileUtil.touch(this.setting.tmpUploadPath), false);
/* 210 */     BufferedOutputStream out = FileUtil.getOutputStream(this.tempFile);
/* 211 */     if (this.data != null) {
/* 212 */       this.size = this.data.length;
/* 213 */       out.write(this.data);
/* 214 */       this.data = null;
/*     */     } 
/* 216 */     long maxFileSize = this.setting.maxFileSize;
/*     */     try {
/* 218 */       if (maxFileSize == -1L) {
/* 219 */         this.size += input.copy(out);
/* 220 */         return true;
/*     */       } 
/* 222 */       this.size += input.copy(out, maxFileSize - this.size + 1L);
/* 223 */       if (this.size > maxFileSize) {
/*     */ 
/*     */         
/* 226 */         this.tempFile.delete();
/* 227 */         this.tempFile = null;
/* 228 */         input.skipToBoundary();
/* 229 */         return false;
/*     */       } 
/*     */     } finally {
/* 232 */       IoUtil.close(out);
/*     */     } 
/* 234 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isAllowedExtension() {
/* 243 */     String[] exts = this.setting.fileExts;
/* 244 */     boolean isAllow = this.setting.isAllowFileExts;
/* 245 */     if (exts == null || exts.length == 0)
/*     */     {
/* 247 */       return isAllow;
/*     */     }
/*     */     
/* 250 */     String fileNameExt = FileUtil.extName(getFileName());
/* 251 */     for (String fileExtension : this.setting.fileExts) {
/* 252 */       if (fileNameExt.equalsIgnoreCase(fileExtension)) {
/* 253 */         return isAllow;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 258 */     return !isAllow;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void assertValid() throws IOException {
/* 267 */     if (false == isUploaded())
/* 268 */       throw new IOException(StrUtil.format("File [{}] upload fail", new Object[] { getFileName() })); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\multipart\UploadFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */