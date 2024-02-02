package org.h2.store.fs.encrypt;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.security.AES;
import org.h2.security.SHA256;
import org.h2.store.fs.FileBaseDefault;
import org.h2.util.MathUtils;

public class FileEncrypt extends FileBaseDefault {
   public static final int BLOCK_SIZE = 4096;
   static final int BLOCK_SIZE_MASK = 4095;
   static final int HEADER_LENGTH = 4096;
   private static final byte[] HEADER;
   private static final int SALT_POS;
   private static final int SALT_LENGTH = 8;
   private static final int HASH_ITERATIONS = 10;
   private final FileChannel base;
   private volatile long size;
   private final String name;
   private volatile XTS xts;
   private byte[] encryptionKey;

   public FileEncrypt(String var1, byte[] var2, FileChannel var3) {
      this.name = var1;
      this.base = var3;
      this.encryptionKey = var2;
   }

   private XTS init() throws IOException {
      XTS var1 = this.xts;
      if (var1 == null) {
         var1 = this.createXTS();
      }

      return var1;
   }

   private synchronized XTS createXTS() throws IOException {
      XTS var1 = this.xts;
      if (var1 != null) {
         return var1;
      } else {
         this.size = this.base.size() - 4096L;
         boolean var2 = this.size < 0L;
         byte[] var3;
         if (var2) {
            byte[] var4 = Arrays.copyOf(HEADER, 4096);
            var3 = MathUtils.secureRandomBytes(8);
            System.arraycopy(var3, 0, var4, SALT_POS, var3.length);
            writeFully(this.base, 0L, ByteBuffer.wrap(var4));
            this.size = 0L;
         } else {
            var3 = new byte[8];
            readFully(this.base, (long)SALT_POS, ByteBuffer.wrap(var3));
            if ((this.size & 4095L) != 0L) {
               this.size -= 4096L;
            }
         }

         AES var5 = new AES();
         var5.setKey(SHA256.getPBKDF2(this.encryptionKey, var3, 10, 16));
         this.encryptionKey = null;
         return this.xts = new XTS(var5);
      }
   }

   protected void implCloseChannel() throws IOException {
      this.base.close();
   }

   public int read(ByteBuffer var1, long var2) throws IOException {
      int var4 = var1.remaining();
      if (var4 == 0) {
         return 0;
      } else {
         XTS var5 = this.init();
         var4 = (int)Math.min((long)var4, this.size - var2);
         if (var2 >= this.size) {
            return -1;
         } else if (var2 < 0L) {
            throw new IllegalArgumentException("pos: " + var2);
         } else if ((var2 & 4095L) == 0L && (var4 & 4095) == 0) {
            this.readInternal(var1, var2, var4, var5);
            return var4;
         } else {
            long var6 = var2 / 4096L * 4096L;
            int var8 = (int)(var2 - var6);
            int var9 = (var4 + var8 + 4096 - 1) / 4096 * 4096;
            ByteBuffer var10 = ByteBuffer.allocate(var9);
            this.readInternal(var10, var6, var9, var5);
            var10.flip().limit(var8 + var4).position(var8);
            var1.put(var10);
            return var4;
         }
      }
   }

   private void readInternal(ByteBuffer var1, long var2, int var4, XTS var5) throws IOException {
      int var6 = var1.position();
      readFully(this.base, var2 + 4096L, var1);

      for(long var7 = var2 / 4096L; var4 > 0; var4 -= 4096) {
         var5.decrypt(var7++, 4096, var1.array(), var1.arrayOffset() + var6);
         var6 += 4096;
      }

   }

   private static void readFully(FileChannel var0, long var1, ByteBuffer var3) throws IOException {
      do {
         int var4 = var0.read(var3, var1);
         if (var4 < 0) {
            throw new EOFException();
         }

         var1 += (long)var4;
      } while(var3.remaining() > 0);

   }

   public int write(ByteBuffer var1, long var2) throws IOException {
      XTS var4 = this.init();
      int var5 = var1.remaining();
      long var6;
      if ((var2 & 4095L) == 0L && (var5 & 4095) == 0) {
         this.writeInternal(var1, var2, var5, var4);
         var6 = var2 + (long)var5;
         this.size = Math.max(this.size, var6);
         return var5;
      } else {
         var6 = var2 / 4096L * 4096L;
         int var8 = (int)(var2 - var6);
         int var9 = (var5 + var8 + 4096 - 1) / 4096 * 4096;
         ByteBuffer var10 = ByteBuffer.allocate(var9);
         int var11 = (int)(this.size - var6 + 4096L - 1L) / 4096 * 4096;
         int var12 = Math.min(var9, var11);
         if (var12 > 0) {
            this.readInternal(var10, var6, var12, var4);
            var10.rewind();
         }

         var10.limit(var8 + var5).position(var8);
         var10.put(var1).limit(var9).rewind();
         this.writeInternal(var10, var6, var9, var4);
         long var13 = var2 + (long)var5;
         this.size = Math.max(this.size, var13);
         int var15 = (int)(this.size & 4095L);
         if (var15 > 0) {
            var10 = ByteBuffer.allocate(var15);
            writeFully(this.base, var6 + 4096L + (long)var9, var10);
         }

         return var5;
      }
   }

   private void writeInternal(ByteBuffer var1, long var2, int var4, XTS var5) throws IOException {
      ByteBuffer var6 = ByteBuffer.allocate(var4).put(var1);
      var6.flip();
      long var7 = var2 / 4096L;
      int var9 = 0;

      for(int var10 = var4; var10 > 0; var10 -= 4096) {
         var5.encrypt(var7++, 4096, var6.array(), var6.arrayOffset() + var9);
         var9 += 4096;
      }

      writeFully(this.base, var2 + 4096L, var6);
   }

   private static void writeFully(FileChannel var0, long var1, ByteBuffer var3) throws IOException {
      do {
         var1 += (long)var0.write(var3, var1);
      } while(var3.remaining() > 0);

   }

   public long size() throws IOException {
      this.init();
      return this.size;
   }

   protected void implTruncate(long var1) throws IOException {
      this.init();
      if (var1 <= this.size) {
         if (var1 < 0L) {
            throw new IllegalArgumentException("newSize: " + var1);
         } else {
            int var3 = (int)(var1 & 4095L);
            if (var3 > 0) {
               this.base.truncate(var1 + 4096L + 4096L);
            } else {
               this.base.truncate(var1 + 4096L);
            }

            this.size = var1;
         }
      }
   }

   public void force(boolean var1) throws IOException {
      this.base.force(var1);
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      return this.base.tryLock(var1, var3, var5);
   }

   public String toString() {
      return this.name;
   }

   static {
      HEADER = "H2encrypt\n".getBytes(StandardCharsets.ISO_8859_1);
      SALT_POS = HEADER.length;
   }
}
