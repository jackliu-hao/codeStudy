package org.h2.store.fs.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.h2.store.fs.FakeFileChannel;
import org.h2.store.fs.FileBase;
import org.h2.util.IOUtils;

class FileZip extends FileBase {
   private static final byte[] SKIP_BUFFER = new byte[1024];
   private final ZipFile file;
   private final ZipEntry entry;
   private long pos;
   private InputStream in;
   private long inPos;
   private final long length;
   private boolean skipUsingRead;

   FileZip(ZipFile var1, ZipEntry var2) {
      this.file = var1;
      this.entry = var2;
      this.length = var2.getSize();
   }

   public long position() {
      return this.pos;
   }

   public long size() {
      return this.length;
   }

   public int read(ByteBuffer var1) throws IOException {
      this.seek();
      int var2 = this.in.read(var1.array(), var1.arrayOffset() + var1.position(), var1.remaining());
      if (var2 > 0) {
         var1.position(var1.position() + var2);
         this.pos += (long)var2;
         this.inPos += (long)var2;
      }

      return var2;
   }

   private void seek() throws IOException {
      if (this.inPos > this.pos) {
         if (this.in != null) {
            this.in.close();
         }

         this.in = null;
      }

      if (this.in == null) {
         this.in = this.file.getInputStream(this.entry);
         this.inPos = 0L;
      }

      if (this.inPos < this.pos) {
         long var1 = this.pos - this.inPos;
         if (!this.skipUsingRead) {
            try {
               IOUtils.skipFully(this.in, var1);
            } catch (NullPointerException var4) {
               this.skipUsingRead = true;
            }
         }

         if (this.skipUsingRead) {
            while(var1 > 0L) {
               int var3 = (int)Math.min((long)SKIP_BUFFER.length, var1);
               var3 = this.in.read(SKIP_BUFFER, 0, var3);
               var1 -= (long)var3;
            }
         }

         this.inPos = this.pos;
      }

   }

   public FileChannel position(long var1) {
      this.pos = var1;
      return this;
   }

   public FileChannel truncate(long var1) throws IOException {
      throw new IOException("File is read-only");
   }

   public void force(boolean var1) throws IOException {
   }

   public int write(ByteBuffer var1) throws IOException {
      throw new NonWritableChannelException();
   }

   public synchronized FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      return var5 ? new FileLock(FakeFileChannel.INSTANCE, var1, var3, var5) {
         public boolean isValid() {
            return true;
         }

         public void release() throws IOException {
         }
      } : null;
   }

   protected void implCloseChannel() throws IOException {
      if (this.in != null) {
         this.in.close();
         this.in = null;
      }

      this.file.close();
   }
}
