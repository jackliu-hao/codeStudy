package org.h2.store;

import java.io.IOException;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.security.SecureFileStore;
import org.h2.store.fs.FileUtils;

public class FileStore {
   public static final int HEADER_LENGTH = 48;
   private static final String HEADER = "-- H2 0.5/B --      ".substring(0, 15) + "\n";
   private static final boolean ASSERT;
   protected String name;
   private final DataHandler handler;
   private FileChannel file;
   private long filePos;
   private long fileLength;
   private Reference<?> autoDeleteReference;
   private boolean checkedWriting = true;
   private final String mode;
   private java.nio.channels.FileLock lock;
   // $FF: synthetic field
   static final boolean $assertionsDisabled = !FileStore.class.desiredAssertionStatus();

   protected FileStore(DataHandler var1, String var2, String var3) {
      this.handler = var1;
      this.name = var2;

      try {
         boolean var4 = FileUtils.exists(var2);
         if (var4 && !FileUtils.canWrite(var2)) {
            var3 = "r";
         } else {
            FileUtils.createDirectories(FileUtils.getParent(var2));
         }

         this.file = FileUtils.open(var2, var3);
         if (var4) {
            this.fileLength = this.file.size();
         }
      } catch (IOException var5) {
         throw DbException.convertIOException(var5, "name: " + var2 + " mode: " + var3);
      }

      this.mode = var3;
   }

   public static FileStore open(DataHandler var0, String var1, String var2) {
      return open(var0, var1, var2, (String)null, (byte[])null, 0);
   }

   public static FileStore open(DataHandler var0, String var1, String var2, String var3, byte[] var4) {
      return open(var0, var1, var2, var3, var4, 1024);
   }

   public static FileStore open(DataHandler var0, String var1, String var2, String var3, byte[] var4, int var5) {
      Object var6;
      if (var3 == null) {
         var6 = new FileStore(var0, var1, var2);
      } else {
         var6 = new SecureFileStore(var0, var1, var2, var3, var4, var5);
      }

      return (FileStore)var6;
   }

   protected byte[] generateSalt() {
      return HEADER.getBytes(StandardCharsets.UTF_8);
   }

   protected void initKey(byte[] var1) {
   }

   public void setCheckedWriting(boolean var1) {
      this.checkedWriting = var1;
   }

   private void checkWritingAllowed() {
      if (this.handler != null && this.checkedWriting) {
         this.handler.checkWritingAllowed();
      }

   }

   private void checkPowerOff() {
      if (this.handler != null) {
         this.handler.checkPowerOff();
      }

   }

   public void init() {
      byte var1 = 16;
      byte[] var3 = HEADER.getBytes(StandardCharsets.UTF_8);
      byte[] var2;
      if (this.length() < 48L) {
         this.checkedWriting = false;
         this.writeDirect(var3, 0, var1);
         var2 = this.generateSalt();
         this.writeDirect(var2, 0, var1);
         this.initKey(var2);
         this.write(var3, 0, var1);
         this.checkedWriting = true;
      } else {
         this.seek(0L);
         byte[] var4 = new byte[var1];
         this.readFullyDirect(var4, 0, var1);
         if (!Arrays.equals(var4, var3)) {
            throw DbException.get(90048, this.name);
         }

         var2 = new byte[var1];
         this.readFullyDirect(var2, 0, var1);
         this.initKey(var2);
         this.readFully(var4, 0, 16);
         if (!Arrays.equals(var4, var3)) {
            throw DbException.get(90049, this.name);
         }
      }

   }

   public void close() {
      if (this.file != null) {
         try {
            trace("close", this.name, this.file);
            this.file.close();
         } catch (IOException var5) {
            throw DbException.convertIOException(var5, this.name);
         } finally {
            this.file = null;
         }
      }

   }

   public void closeSilently() {
      try {
         this.close();
      } catch (Exception var2) {
      }

   }

   public void closeAndDeleteSilently() {
      if (this.file != null) {
         this.closeSilently();
         this.handler.getTempFileDeleter().deleteFile(this.autoDeleteReference, this.name);
         this.name = null;
      }

   }

   public void readFullyDirect(byte[] var1, int var2, int var3) {
      this.readFully(var1, var2, var3);
   }

   public void readFully(byte[] var1, int var2, int var3) {
      if (var3 >= 0 && var3 % 16 == 0) {
         this.checkPowerOff();

         try {
            FileUtils.readFully(this.file, ByteBuffer.wrap(var1, var2, var3));
         } catch (IOException var5) {
            throw DbException.convertIOException(var5, this.name);
         }

         this.filePos += (long)var3;
      } else {
         throw DbException.getInternalError("unaligned read " + this.name + " len " + var3);
      }
   }

   public void seek(long var1) {
      if (var1 % 16L != 0L) {
         throw DbException.getInternalError("unaligned seek " + this.name + " pos " + var1);
      } else {
         try {
            if (var1 != this.filePos) {
               this.file.position(var1);
               this.filePos = var1;
            }

         } catch (IOException var4) {
            throw DbException.convertIOException(var4, this.name);
         }
      }
   }

   protected void writeDirect(byte[] var1, int var2, int var3) {
      this.write(var1, var2, var3);
   }

   public void write(byte[] var1, int var2, int var3) {
      if (var3 >= 0 && var3 % 16 == 0) {
         this.checkWritingAllowed();
         this.checkPowerOff();

         try {
            FileUtils.writeFully(this.file, ByteBuffer.wrap(var1, var2, var3));
         } catch (IOException var5) {
            this.closeFileSilently();
            throw DbException.convertIOException(var5, this.name);
         }

         this.filePos += (long)var3;
         this.fileLength = Math.max(this.filePos, this.fileLength);
      } else {
         throw DbException.getInternalError("unaligned write " + this.name + " len " + var3);
      }
   }

   public void setLength(long var1) {
      if (var1 % 16L != 0L) {
         throw DbException.getInternalError("unaligned setLength " + this.name + " pos " + var1);
      } else {
         this.checkPowerOff();
         this.checkWritingAllowed();

         try {
            if (var1 > this.fileLength) {
               long var3 = this.filePos;
               this.file.position(var1 - 1L);
               FileUtils.writeFully(this.file, ByteBuffer.wrap(new byte[1]));
               this.file.position(var3);
            } else {
               this.file.truncate(var1);
            }

            this.fileLength = var1;
         } catch (IOException var5) {
            this.closeFileSilently();
            throw DbException.convertIOException(var5, this.name);
         }
      }
   }

   public long length() {
      long var1 = this.fileLength;
      if (ASSERT) {
         try {
            var1 = this.file.size();
            if (var1 != this.fileLength) {
               throw DbException.getInternalError("file " + this.name + " length " + var1 + " expected " + this.fileLength);
            }

            if (var1 % 16L != 0L) {
               long var3 = var1 + 16L - var1 % 16L;
               this.file.truncate(var3);
               this.fileLength = var3;
               throw DbException.getInternalError("unaligned file length " + this.name + " len " + var1);
            }
         } catch (IOException var5) {
            throw DbException.convertIOException(var5, this.name);
         }
      }

      return var1;
   }

   public long getFilePointer() {
      if (ASSERT) {
         try {
            if (this.file.position() != this.filePos) {
               throw DbException.getInternalError(this.file.position() + " " + this.filePos);
            }
         } catch (IOException var2) {
            throw DbException.convertIOException(var2, this.name);
         }
      }

      return this.filePos;
   }

   public void sync() {
      try {
         this.file.force(true);
      } catch (IOException var2) {
         this.closeFileSilently();
         throw DbException.convertIOException(var2, this.name);
      }
   }

   public void autoDelete() {
      if (this.autoDeleteReference == null) {
         this.autoDeleteReference = this.handler.getTempFileDeleter().addFile(this.name, this);
      }

   }

   public void stopAutoDelete() {
      this.handler.getTempFileDeleter().stopAutoDelete(this.autoDeleteReference, this.name);
      this.autoDeleteReference = null;
   }

   public void closeFile() throws IOException {
      this.file.close();
      this.file = null;
   }

   private void closeFileSilently() {
      try {
         this.file.close();
      } catch (IOException var2) {
      }

   }

   public void openFile() throws IOException {
      if (this.file == null) {
         this.file = FileUtils.open(this.name, this.mode);
         this.file.position(this.filePos);
      }

   }

   private static void trace(String var0, String var1, Object var2) {
      if (SysProperties.TRACE_IO) {
         System.out.println("FileStore." + var0 + " " + var1 + " " + var2);
      }

   }

   public synchronized boolean tryLock() {
      try {
         this.lock = this.file.tryLock();
         return this.lock != null;
      } catch (Exception var2) {
         return false;
      }
   }

   public synchronized void releaseLock() {
      if (this.file != null && this.lock != null) {
         try {
            this.lock.release();
         } catch (Exception var2) {
         }

         this.lock = null;
      }

   }

   static {
      boolean var0 = false;
      if (!$assertionsDisabled) {
         var0 = true;
         if (false) {
            throw new AssertionError();
         }
      }

      ASSERT = var0;
   }
}
