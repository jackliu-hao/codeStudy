package com.cym.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilePermissionUtil {
   public static Boolean canRead(File file) {
      if (file.isDirectory()) {
         try {
            File[] listFiles = file.listFiles();
            return listFiles == null ? false : true;
         } catch (Exception var2) {
            return false;
         }
      } else {
         return !file.exists() ? false : checkRead(file);
      }
   }

   private static boolean checkRead(File file) {
      FileReader fd = null;

      boolean var3;
      try {
         fd = new FileReader(file);
         if (fd.read() != -1) {
         }

         boolean var2 = true;
         return var2;
      } catch (IOException var13) {
         var3 = false;
      } finally {
         try {
            fd.close();
         } catch (IOException var12) {
            var12.printStackTrace();
         }

      }

      return var3;
   }

   public static Boolean canWrite(File file) {
      if (file.isDirectory()) {
         try {
            file = new File(file, "canWriteTestDeleteOnExit.temp");
            if (file.exists()) {
               boolean checkWrite = checkWrite(file);
               if (!deleteFile(file)) {
                  file.deleteOnExit();
               }

               return checkWrite;
            } else if (file.createNewFile()) {
               if (!deleteFile(file)) {
                  file.deleteOnExit();
               }

               return true;
            } else {
               return false;
            }
         } catch (Exception var2) {
            return false;
         }
      } else {
         return checkWrite(file);
      }
   }

   private static boolean checkWrite(File file) {
      FileWriter fw = null;
      boolean delete = !file.exists();
      boolean result = false;

      boolean var5;
      try {
         fw = new FileWriter(file, true);
         fw.write("");
         fw.flush();
         result = true;
         boolean var4 = result;
         return var4;
      } catch (IOException var15) {
         var5 = false;
      } finally {
         try {
            fw.close();
         } catch (IOException var14) {
            var14.printStackTrace();
         }

         if (delete && result) {
            deleteFile(file);
         }

      }

      return var5;
   }

   public static boolean deleteFile(File file) {
      return deleteFile(file, true);
   }

   public static boolean deleteFile(File file, boolean delDir) {
      if (!file.exists()) {
         return true;
      } else if (file.isFile()) {
         return file.delete();
      } else {
         boolean result = true;
         File[] children = file.listFiles();

         for(int i = 0; i < children.length; ++i) {
            result = deleteFile(children[i], delDir);
            if (!result) {
               return false;
            }
         }

         if (delDir) {
            result = file.delete();
         }

         return result;
      }
   }
}
