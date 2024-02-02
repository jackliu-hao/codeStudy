package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Expand {
   private File dest;
   private File source;
   private boolean overwrite = true;

   public void execute() throws Exception {
      this.expandFile(this.source, this.dest);
   }

   protected void expandFile(File srcF, File dir) throws Exception {
      ZipInputStream zis = null;

      try {
         zis = new ZipInputStream(new FileInputStream(srcF));
         ZipEntry ze = null;

         while((ze = zis.getNextEntry()) != null) {
            this.extractFile(srcF, dir, zis, ze.getName(), new Date(ze.getTime()), ze.isDirectory());
         }
      } catch (IOException var13) {
         throw new Exception("Error while expanding " + srcF.getPath(), var13);
      } finally {
         if (zis != null) {
            try {
               zis.close();
            } catch (IOException var12) {
            }
         }

      }

   }

   protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory) throws Exception {
      File f = FileUtils.resolveFile(dir, entryName);

      try {
         if (this.overwrite || !f.exists() || f.lastModified() < entryDate.getTime()) {
            File dirF = f.getParentFile();
            dirF.mkdirs();
            if (isDirectory) {
               f.mkdirs();
            } else {
               byte[] buffer = new byte[1024];
               int length = false;
               FileOutputStream fos = null;

               try {
                  fos = new FileOutputStream(f);

                  int length;
                  while((length = compressedInputStream.read(buffer)) >= 0) {
                     fos.write(buffer, 0, length);
                  }

                  fos.close();
                  fos = null;
               } finally {
                  if (fos != null) {
                     try {
                        fos.close();
                     } catch (IOException var19) {
                     }
                  }

               }
            }

            f.setLastModified(entryDate.getTime());
         }
      } catch (FileNotFoundException var21) {
         throw new Exception("Can't extract file " + srcF.getPath(), var21);
      }
   }

   public void setDest(File d) {
      this.dest = d;
   }

   public void setSrc(File s) {
      this.source = s;
   }

   public void setOverwrite(boolean b) {
      this.overwrite = b;
   }
}
