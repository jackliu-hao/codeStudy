package org.apache.commons.compress.harmony.pack200;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PackingUtils {
   private static PackingLogger packingLogger = new PackingLogger("org.harmony.apache.pack200", (String)null);

   public static void config(PackingOptions options) throws IOException {
      String logFileName = options.getLogFile();
      if (logFileName != null) {
         FileHandler fileHandler = new FileHandler(logFileName, false);
         fileHandler.setFormatter(new SimpleFormatter());
         packingLogger.addHandler(fileHandler);
         packingLogger.setUseParentHandlers(false);
      }

      packingLogger.setVerbose(options.isVerbose());
   }

   public static void log(String message) {
      packingLogger.log(Level.INFO, message);
   }

   public static void copyThroughJar(JarInputStream jarInputStream, OutputStream outputStream) throws IOException {
      Manifest manifest = jarInputStream.getManifest();
      JarOutputStream jarOutputStream = new JarOutputStream(outputStream, manifest);
      jarOutputStream.setComment("PACK200");
      log("Packed META-INF/MANIFEST.MF");
      byte[] bytes = new byte[16384];

      JarEntry jarEntry;
      while((jarEntry = jarInputStream.getNextJarEntry()) != null) {
         jarOutputStream.putNextEntry(jarEntry);

         int bytesRead;
         while((bytesRead = jarInputStream.read(bytes)) != -1) {
            jarOutputStream.write(bytes, 0, bytesRead);
         }

         log("Packed " + jarEntry.getName());
      }

      jarInputStream.close();
      jarOutputStream.close();
   }

   public static void copyThroughJar(JarFile jarFile, OutputStream outputStream) throws IOException {
      JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
      jarOutputStream.setComment("PACK200");
      byte[] bytes = new byte[16384];
      Enumeration entries = jarFile.entries();

      while(entries.hasMoreElements()) {
         JarEntry jarEntry = (JarEntry)entries.nextElement();
         jarOutputStream.putNextEntry(jarEntry);
         InputStream inputStream = jarFile.getInputStream(jarEntry);

         int bytesRead;
         while((bytesRead = inputStream.read(bytes)) != -1) {
            jarOutputStream.write(bytes, 0, bytesRead);
         }

         jarOutputStream.closeEntry();
         log("Packed " + jarEntry.getName());
      }

      jarFile.close();
      jarOutputStream.close();
   }

   public static List getPackingFileListFromJar(JarInputStream jarInputStream, boolean keepFileOrder) throws IOException {
      List packingFileList = new ArrayList();
      Manifest manifest = jarInputStream.getManifest();
      if (manifest != null) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         manifest.write(baos);
         packingFileList.add(new Archive.PackingFile("META-INF/MANIFEST.MF", baos.toByteArray(), 0L));
      }

      JarEntry jarEntry;
      while((jarEntry = jarInputStream.getNextJarEntry()) != null) {
         byte[] bytes = readJarEntry(jarEntry, new BufferedInputStream(jarInputStream));
         packingFileList.add(new Archive.PackingFile(bytes, jarEntry));
      }

      if (!keepFileOrder) {
         reorderPackingFiles(packingFileList);
      }

      return packingFileList;
   }

   public static List getPackingFileListFromJar(JarFile jarFile, boolean keepFileOrder) throws IOException {
      List packingFileList = new ArrayList();
      Enumeration jarEntries = jarFile.entries();

      while(jarEntries.hasMoreElements()) {
         JarEntry jarEntry = (JarEntry)jarEntries.nextElement();
         byte[] bytes = readJarEntry(jarEntry, new BufferedInputStream(jarFile.getInputStream(jarEntry)));
         packingFileList.add(new Archive.PackingFile(bytes, jarEntry));
      }

      if (!keepFileOrder) {
         reorderPackingFiles(packingFileList);
      }

      return packingFileList;
   }

   private static byte[] readJarEntry(JarEntry jarEntry, InputStream inputStream) throws IOException {
      long size = jarEntry.getSize();
      if (size > 2147483647L) {
         throw new RuntimeException("Large Class!");
      } else {
         if (size < 0L) {
            size = 0L;
         }

         byte[] bytes = new byte[(int)size];
         if ((long)inputStream.read(bytes) != size) {
            throw new RuntimeException("Error reading from stream");
         } else {
            return bytes;
         }
      }
   }

   private static void reorderPackingFiles(List packingFileList) {
      Iterator iterator = packingFileList.iterator();

      while(iterator.hasNext()) {
         Archive.PackingFile packingFile = (Archive.PackingFile)iterator.next();
         if (packingFile.isDirectory()) {
            iterator.remove();
         }
      }

      Collections.sort(packingFileList, (arg0, arg1) -> {
         if (arg0 instanceof Archive.PackingFile && arg1 instanceof Archive.PackingFile) {
            String fileName0 = ((Archive.PackingFile)arg0).getName();
            String fileName1 = ((Archive.PackingFile)arg1).getName();
            if (fileName0.equals(fileName1)) {
               return 0;
            } else if ("META-INF/MANIFEST.MF".equals(fileName0)) {
               return -1;
            } else {
               return "META-INF/MANIFEST.MF".equals(fileName1) ? 1 : fileName0.compareTo(fileName1);
            }
         } else {
            throw new IllegalArgumentException();
         }
      });
   }

   static {
      LogManager.getLogManager().addLogger(packingLogger);
   }

   private static class PackingLogger extends Logger {
      private boolean verbose = false;

      protected PackingLogger(String name, String resourceBundleName) {
         super(name, resourceBundleName);
      }

      public void log(LogRecord logRecord) {
         if (this.verbose) {
            super.log(logRecord);
         }

      }

      public void setVerbose(boolean isVerbose) {
         this.verbose = isVerbose;
      }
   }
}
