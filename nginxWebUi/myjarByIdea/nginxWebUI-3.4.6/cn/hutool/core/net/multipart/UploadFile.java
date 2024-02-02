package cn.hutool.core.net.multipart;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class UploadFile {
   private static final String TMP_FILE_PREFIX = "hutool-";
   private static final String TMP_FILE_SUFFIX = ".upload.tmp";
   private final UploadFileHeader header;
   private final UploadSetting setting;
   private long size = -1L;
   private byte[] data;
   private File tempFile;

   public UploadFile(UploadFileHeader header, UploadSetting setting) {
      this.header = header;
      this.setting = setting;
   }

   public void delete() {
      if (this.tempFile != null) {
         this.tempFile.delete();
      }

      if (this.data != null) {
         this.data = null;
      }

   }

   public File write(String destPath) throws IOException {
      return this.data == null && this.tempFile == null ? null : this.write(FileUtil.file(destPath));
   }

   public File write(File destination) throws IOException {
      this.assertValid();
      if (destination.isDirectory()) {
         destination = new File(destination, this.header.getFileName());
      }

      if (this.data != null) {
         FileUtil.writeBytes(this.data, destination);
         this.data = null;
      } else {
         if (null == this.tempFile) {
            throw new NullPointerException("Temp file is null !");
         }

         if (!this.tempFile.exists()) {
            throw new NoSuchFileException("Temp file: [" + this.tempFile.getAbsolutePath() + "] not exist!");
         }

         FileUtil.move(this.tempFile, destination, true);
      }

      return destination;
   }

   public byte[] getFileContent() throws IOException {
      this.assertValid();
      if (this.data != null) {
         return this.data;
      } else {
         return this.tempFile != null ? FileUtil.readBytes(this.tempFile) : null;
      }
   }

   public InputStream getFileInputStream() throws IOException {
      this.assertValid();
      if (this.data != null) {
         return IoUtil.toBuffered((InputStream)IoUtil.toStream(this.data));
      } else {
         return this.tempFile != null ? IoUtil.toBuffered((InputStream)IoUtil.toStream(this.tempFile)) : null;
      }
   }

   public UploadFileHeader getHeader() {
      return this.header;
   }

   public String getFileName() {
      return this.header == null ? null : this.header.getFileName();
   }

   public long size() {
      return this.size;
   }

   public boolean isUploaded() {
      return this.size > 0L;
   }

   public boolean isInMemory() {
      return this.data != null;
   }

   protected boolean processStream(MultipartRequestInputStream input) throws IOException {
      if (!this.isAllowedExtension()) {
         this.size = input.skipToBoundary();
         return false;
      } else {
         this.size = 0L;
         int memoryThreshold = this.setting.memoryThreshold;
         long maxFileSize;
         if (memoryThreshold > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(memoryThreshold);
            maxFileSize = input.copy(baos, (long)memoryThreshold);
            this.data = baos.toByteArray();
            if (maxFileSize <= (long)memoryThreshold) {
               this.size = (long)this.data.length;
               return true;
            }
         }

         this.tempFile = FileUtil.createTempFile("hutool-", ".upload.tmp", FileUtil.touch(this.setting.tmpUploadPath), false);
         BufferedOutputStream out = FileUtil.getOutputStream(this.tempFile);
         if (this.data != null) {
            this.size = (long)this.data.length;
            out.write(this.data);
            this.data = null;
         }

         maxFileSize = this.setting.maxFileSize;

         try {
            boolean var6;
            if (maxFileSize == -1L) {
               this.size += input.copy(out);
               var6 = true;
               return var6;
            }

            this.size += input.copy(out, maxFileSize - this.size + 1L);
            if (this.size > maxFileSize) {
               this.tempFile.delete();
               this.tempFile = null;
               input.skipToBoundary();
               var6 = false;
               return var6;
            }
         } finally {
            IoUtil.close(out);
         }

         return true;
      }
   }

   private boolean isAllowedExtension() {
      String[] exts = this.setting.fileExts;
      boolean isAllow = this.setting.isAllowFileExts;
      if (exts != null && exts.length != 0) {
         String fileNameExt = FileUtil.extName(this.getFileName());
         String[] var4 = this.setting.fileExts;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String fileExtension = var4[var6];
            if (fileNameExt.equalsIgnoreCase(fileExtension)) {
               return isAllow;
            }
         }

         return !isAllow;
      } else {
         return isAllow;
      }
   }

   private void assertValid() throws IOException {
      if (!this.isUploaded()) {
         throw new IOException(StrUtil.format("File [{}] upload fail", new Object[]{this.getFileName()}));
      }
   }
}
