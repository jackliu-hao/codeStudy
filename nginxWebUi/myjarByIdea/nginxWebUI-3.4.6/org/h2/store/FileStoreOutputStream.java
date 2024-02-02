package org.h2.store;

import java.io.OutputStream;
import java.util.Arrays;
import org.h2.tools.CompressTool;

public class FileStoreOutputStream extends OutputStream {
   private FileStore store;
   private final Data page;
   private final String compressionAlgorithm;
   private final CompressTool compress;
   private final byte[] buffer = new byte[]{0};

   public FileStoreOutputStream(FileStore var1, String var2) {
      this.store = var1;
      if (var2 != null) {
         this.compress = CompressTool.getInstance();
         this.compressionAlgorithm = var2;
      } else {
         this.compress = null;
         this.compressionAlgorithm = null;
      }

      this.page = Data.create(16);
   }

   public void write(int var1) {
      this.buffer[0] = (byte)var1;
      this.write(this.buffer);
   }

   public void write(byte[] var1) {
      this.write(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) {
      if (var3 > 0) {
         this.page.reset();
         if (this.compress == null) {
            this.page.checkCapacity(4 + var3);
            this.page.writeInt(var3);
            this.page.write(var1, var2, var3);
         } else {
            if (var2 != 0 || var3 != var1.length) {
               var1 = Arrays.copyOfRange(var1, var2, var2 + var3);
               var2 = 0;
            }

            int var4 = var3;
            var1 = this.compress.compress(var1, this.compressionAlgorithm);
            var3 = var1.length;
            this.page.checkCapacity(8 + var3);
            this.page.writeInt(var3);
            this.page.writeInt(var4);
            this.page.write(var1, var2, var3);
         }

         this.page.fillAligned();
         this.store.write(this.page.getBytes(), 0, this.page.length());
      }

   }

   public void close() {
      if (this.store != null) {
         try {
            this.store.close();
         } finally {
            this.store = null;
         }
      }

   }
}
