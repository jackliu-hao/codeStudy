package org.h2.store.fs.split;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;

public class FilePathSplit extends FilePathWrapper {
   private static final String PART_SUFFIX = ".part";

   protected String getPrefix() {
      return this.getScheme() + ":" + this.parse(this.name)[0] + ":";
   }

   public FilePath unwrap(String var1) {
      return FilePath.get(this.parse(var1)[1]);
   }

   public boolean setReadOnly() {
      boolean var1 = false;
      int var2 = 0;

      while(true) {
         FilePath var3 = this.getBase(var2);
         if (!var3.exists()) {
            return var1;
         }

         var1 = var3.setReadOnly();
         ++var2;
      }
   }

   public void delete() {
      int var1 = 0;

      while(true) {
         FilePath var2 = this.getBase(var1);
         if (!var2.exists()) {
            return;
         }

         var2.delete();
         ++var1;
      }
   }

   public long lastModified() {
      long var1 = 0L;
      int var3 = 0;

      while(true) {
         FilePath var4 = this.getBase(var3);
         if (!var4.exists()) {
            return var1;
         }

         long var5 = var4.lastModified();
         var1 = Math.max(var1, var5);
         ++var3;
      }
   }

   public long size() {
      long var1 = 0L;
      int var3 = 0;

      while(true) {
         FilePath var4 = this.getBase(var3);
         if (!var4.exists()) {
            return var1;
         }

         var1 += var4.size();
         ++var3;
      }
   }

   public ArrayList<FilePath> newDirectoryStream() {
      List var1 = this.getBase().newDirectoryStream();
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         FilePath var4 = (FilePath)var3.next();
         if (!var4.getName().endsWith(".part")) {
            var2.add(this.wrap(var4));
         }
      }

      return var2;
   }

   public InputStream newInputStream() throws IOException {
      Object var1 = this.getBase().newInputStream();
      int var2 = 1;

      while(true) {
         FilePath var3 = this.getBase(var2);
         if (!var3.exists()) {
            return (InputStream)var1;
         }

         InputStream var4 = var3.newInputStream();
         var1 = new SequenceInputStream((InputStream)var1, var4);
         ++var2;
      }
   }

   public FileChannel open(String var1) throws IOException {
      ArrayList var2 = new ArrayList();
      var2.add(this.getBase().open(var1));
      int var3 = 1;

      while(true) {
         FilePath var4 = this.getBase(var3);
         if (!var4.exists()) {
            FileChannel[] var12 = (FileChannel[])var2.toArray(new FileChannel[0]);
            long var13 = var12[0].size();
            long var6 = var13;
            if (var12.length == 1) {
               long var8 = this.getDefaultMaxLength();
               if (var13 < var8) {
                  var13 = var8;
               }
            } else {
               if (var13 == 0L) {
                  this.closeAndThrow(0, var12, var12[0], var13);
               }

               for(int var14 = 1; var14 < var12.length - 1; ++var14) {
                  FileChannel var9 = var12[var14];
                  long var10 = var9.size();
                  var6 += var10;
                  if (var10 != var13) {
                     this.closeAndThrow(var14, var12, var9, var13);
                  }
               }

               FileChannel var15 = var12[var12.length - 1];
               long var16 = var15.size();
               var6 += var16;
               if (var16 > var13) {
                  this.closeAndThrow(var12.length - 1, var12, var15, var13);
               }
            }

            return new FileSplit(this, var1, var12, var6, var13);
         }

         var2.add(var4.open(var1));
         ++var3;
      }
   }

   private long getDefaultMaxLength() {
      return 1L << Integer.decode(this.parse(this.name)[0]);
   }

   private void closeAndThrow(int var1, FileChannel[] var2, FileChannel var3, long var4) throws IOException {
      String var6 = "Expected file length: " + var4 + " got: " + var3.size() + " for " + this.getName(var1);
      FileChannel[] var7 = var2;
      int var8 = var2.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         FileChannel var10 = var7[var9];
         var10.close();
      }

      throw new IOException(var6);
   }

   public OutputStream newOutputStream(boolean var1) throws IOException {
      return newFileChannelOutputStream(this.open("rw"), var1);
   }

   public void moveTo(FilePath var1, boolean var2) {
      FilePathSplit var3 = (FilePathSplit)var1;
      int var4 = 0;

      while(true) {
         FilePath var5 = this.getBase(var4);
         if (var5.exists()) {
            var5.moveTo(var3.getBase(var4), var2);
         } else {
            if (!var3.getBase(var4).exists()) {
               return;
            }

            var3.getBase(var4).delete();
         }

         ++var4;
      }
   }

   private String[] parse(String var1) {
      if (!var1.startsWith(this.getScheme())) {
         throw DbException.getInternalError(var1 + " doesn't start with " + this.getScheme());
      } else {
         var1 = var1.substring(this.getScheme().length() + 1);
         String var2;
         if (var1.length() > 0 && Character.isDigit(var1.charAt(0))) {
            int var3 = var1.indexOf(58);
            var2 = var1.substring(0, var3);

            try {
               var1 = var1.substring(var3 + 1);
            } catch (NumberFormatException var5) {
            }
         } else {
            var2 = Long.toString(SysProperties.SPLIT_FILE_SIZE_SHIFT);
         }

         return new String[]{var2, var1};
      }
   }

   FilePath getBase(int var1) {
      return FilePath.get(this.getName(var1));
   }

   private String getName(int var1) {
      return var1 > 0 ? this.getBase().name + "." + var1 + ".part" : this.getBase().name;
   }

   public String getScheme() {
      return "split";
   }
}
