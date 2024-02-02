package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.FileUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor extends ContextAwareBase {
   final CompressionMode compressionMode;
   static final int BUFFER_SIZE = 8192;

   public Compressor(CompressionMode compressionMode) {
      this.compressionMode = compressionMode;
   }

   public void compress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
      switch (this.compressionMode) {
         case GZ:
            this.gzCompress(nameOfFile2Compress, nameOfCompressedFile);
            break;
         case ZIP:
            this.zipCompress(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
            break;
         case NONE:
            throw new UnsupportedOperationException("compress method called in NONE compression mode");
      }

   }

   private void zipCompress(String nameOfFile2zip, String nameOfZippedFile, String innerEntryName) {
      File file2zip = new File(nameOfFile2zip);
      if (!file2zip.exists()) {
         this.addStatus(new WarnStatus("The file to compress named [" + nameOfFile2zip + "] does not exist.", this));
      } else if (innerEntryName == null) {
         this.addStatus(new WarnStatus("The innerEntryName parameter cannot be null", this));
      } else {
         if (!nameOfZippedFile.endsWith(".zip")) {
            nameOfZippedFile = nameOfZippedFile + ".zip";
         }

         File zippedFile = new File(nameOfZippedFile);
         if (zippedFile.exists()) {
            this.addStatus(new WarnStatus("The target compressed file named [" + nameOfZippedFile + "] exist already.", this));
         } else {
            this.addInfo("ZIP compressing [" + file2zip + "] as [" + zippedFile + "]");
            this.createMissingTargetDirsIfNecessary(zippedFile);
            BufferedInputStream bis = null;
            ZipOutputStream zos = null;

            try {
               bis = new BufferedInputStream(new FileInputStream(nameOfFile2zip));
               zos = new ZipOutputStream(new FileOutputStream(nameOfZippedFile));
               ZipEntry zipEntry = this.computeZipEntry(innerEntryName);
               zos.putNextEntry(zipEntry);
               byte[] inbuf = new byte[8192];

               int n;
               while((n = bis.read(inbuf)) != -1) {
                  zos.write(inbuf, 0, n);
               }

               bis.close();
               bis = null;
               zos.close();
               zos = null;
               if (!file2zip.delete()) {
                  this.addStatus(new WarnStatus("Could not delete [" + nameOfFile2zip + "].", this));
               }
            } catch (Exception var23) {
               this.addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2zip + "] into [" + nameOfZippedFile + "].", this, var23));
            } finally {
               if (bis != null) {
                  try {
                     bis.close();
                  } catch (IOException var22) {
                  }
               }

               if (zos != null) {
                  try {
                     zos.close();
                  } catch (IOException var21) {
                  }
               }

            }

         }
      }
   }

   ZipEntry computeZipEntry(File zippedFile) {
      return this.computeZipEntry(zippedFile.getName());
   }

   ZipEntry computeZipEntry(String filename) {
      String nameOfFileNestedWithinArchive = computeFileNameStrWithoutCompSuffix(filename, this.compressionMode);
      return new ZipEntry(nameOfFileNestedWithinArchive);
   }

   private void gzCompress(String nameOfFile2gz, String nameOfgzedFile) {
      File file2gz = new File(nameOfFile2gz);
      if (!file2gz.exists()) {
         this.addStatus(new WarnStatus("The file to compress named [" + nameOfFile2gz + "] does not exist.", this));
      } else {
         if (!nameOfgzedFile.endsWith(".gz")) {
            nameOfgzedFile = nameOfgzedFile + ".gz";
         }

         File gzedFile = new File(nameOfgzedFile);
         if (gzedFile.exists()) {
            this.addWarn("The target compressed file named [" + nameOfgzedFile + "] exist already. Aborting file compression.");
         } else {
            this.addInfo("GZ compressing [" + file2gz + "] as [" + gzedFile + "]");
            this.createMissingTargetDirsIfNecessary(gzedFile);
            BufferedInputStream bis = null;
            GZIPOutputStream gzos = null;

            try {
               bis = new BufferedInputStream(new FileInputStream(nameOfFile2gz));
               gzos = new GZIPOutputStream(new FileOutputStream(nameOfgzedFile));
               byte[] inbuf = new byte[8192];

               int n;
               while((n = bis.read(inbuf)) != -1) {
                  gzos.write(inbuf, 0, n);
               }

               bis.close();
               bis = null;
               gzos.close();
               gzos = null;
               if (!file2gz.delete()) {
                  this.addStatus(new WarnStatus("Could not delete [" + nameOfFile2gz + "].", this));
               }
            } catch (Exception var21) {
               this.addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2gz + "] into [" + nameOfgzedFile + "].", this, var21));
            } finally {
               if (bis != null) {
                  try {
                     bis.close();
                  } catch (IOException var20) {
                  }
               }

               if (gzos != null) {
                  try {
                     gzos.close();
                  } catch (IOException var19) {
                  }
               }

            }

         }
      }
   }

   public static String computeFileNameStrWithoutCompSuffix(String fileNamePatternStr, CompressionMode compressionMode) {
      int len = fileNamePatternStr.length();
      switch (compressionMode) {
         case GZ:
            if (fileNamePatternStr.endsWith(".gz")) {
               return fileNamePatternStr.substring(0, len - 3);
            }

            return fileNamePatternStr;
         case ZIP:
            if (fileNamePatternStr.endsWith(".zip")) {
               return fileNamePatternStr.substring(0, len - 4);
            }

            return fileNamePatternStr;
         case NONE:
            return fileNamePatternStr;
         default:
            throw new IllegalStateException("Execution should not reach this point");
      }
   }

   void createMissingTargetDirsIfNecessary(File file) {
      boolean result = FileUtil.createMissingParentDirectories(file);
      if (!result) {
         this.addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
      }

   }

   public String toString() {
      return this.getClass().getName();
   }

   public Future<?> asyncCompress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
      CompressionRunnable runnable = new CompressionRunnable(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
      ExecutorService executorService = this.context.getScheduledExecutorService();
      Future<?> future = executorService.submit(runnable);
      return future;
   }

   class CompressionRunnable implements Runnable {
      final String nameOfFile2Compress;
      final String nameOfCompressedFile;
      final String innerEntryName;

      public CompressionRunnable(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
         this.nameOfFile2Compress = nameOfFile2Compress;
         this.nameOfCompressedFile = nameOfCompressedFile;
         this.innerEntryName = innerEntryName;
      }

      public void run() {
         Compressor.this.compress(this.nameOfFile2Compress, this.nameOfCompressedFile, this.innerEntryName);
      }
   }
}
