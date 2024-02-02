package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import org.h2.message.DbException;
import org.h2.tools.CompressTool;
import org.h2.util.Utils;

public class FileStoreInputStream extends InputStream {
   private FileStore store;
   private final Data page;
   private int remainingInBuffer;
   private final CompressTool compress;
   private boolean endOfFile;
   private final boolean alwaysClose;

   public FileStoreInputStream(FileStore var1, boolean var2, boolean var3) {
      this.store = var1;
      this.alwaysClose = var3;
      if (var2) {
         this.compress = CompressTool.getInstance();
      } else {
         this.compress = null;
      }

      this.page = Data.create(16);

      try {
         if (var1.length() <= 48L) {
            this.close();
         } else {
            this.fillBuffer();
         }

      } catch (IOException var5) {
         throw DbException.convertIOException(var5, var1.name);
      }
   }

   public int available() {
      return this.remainingInBuffer <= 0 ? 0 : this.remainingInBuffer;
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         return 0;
      } else {
         int var4;
         int var5;
         for(var4 = 0; var3 > 0; var3 -= var5) {
            var5 = this.readBlock(var1, var2, var3);
            if (var5 < 0) {
               break;
            }

            var4 += var5;
            var2 += var5;
         }

         return var4 == 0 ? -1 : var4;
      }
   }

   private int readBlock(byte[] var1, int var2, int var3) throws IOException {
      this.fillBuffer();
      if (this.endOfFile) {
         return -1;
      } else {
         int var4 = Math.min(this.remainingInBuffer, var3);
         this.page.read(var1, var2, var4);
         this.remainingInBuffer -= var4;
         return var4;
      }
   }

   private void fillBuffer() throws IOException {
      if (this.remainingInBuffer <= 0 && !this.endOfFile) {
         this.page.reset();
         this.store.openFile();
         if (this.store.length() == this.store.getFilePointer()) {
            this.close();
         } else {
            this.store.readFully(this.page.getBytes(), 0, 16);
            this.page.reset();
            this.remainingInBuffer = this.page.readInt();
            if (this.remainingInBuffer < 0) {
               this.close();
            } else {
               this.page.checkCapacity(this.remainingInBuffer);
               if (this.compress != null) {
                  this.page.checkCapacity(4);
                  this.page.readInt();
               }

               this.page.setPos(this.page.length() + this.remainingInBuffer);
               this.page.fillAligned();
               int var1 = this.page.length() - 16;
               this.page.reset();
               this.page.readInt();
               this.store.readFully(this.page.getBytes(), 16, var1);
               this.page.reset();
               this.page.readInt();
               if (this.compress != null) {
                  int var2 = this.page.readInt();
                  byte[] var3 = Utils.newBytes(this.remainingInBuffer);
                  this.page.read(var3, 0, this.remainingInBuffer);
                  this.page.reset();
                  this.page.checkCapacity(var2);
                  CompressTool.expand(var3, this.page.getBytes(), 0);
                  this.remainingInBuffer = var2;
               }

               if (this.alwaysClose) {
                  this.store.closeFile();
               }

            }
         }
      }
   }

   public void close() {
      if (this.store != null) {
         try {
            this.store.close();
            this.endOfFile = true;
         } finally {
            this.store = null;
         }
      }

   }

   protected void finalize() {
      this.close();
   }

   public int read() throws IOException {
      this.fillBuffer();
      if (this.endOfFile) {
         return -1;
      } else {
         int var1 = this.page.readByte() & 255;
         --this.remainingInBuffer;
         return var1;
      }
   }
}
