package org.apache.commons.compress.archivers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public final class Lister {
   private static final ArchiveStreamFactory FACTORY;

   public static void main(String[] args) throws Exception {
      if (args.length == 0) {
         usage();
      } else {
         System.out.println("Analysing " + args[0]);
         File f = new File(args[0]);
         if (!f.isFile()) {
            System.err.println(f + " doesn't exist or is a directory");
         }

         String format = args.length > 1 ? args[1] : detectFormat(f);
         if ("7z".equalsIgnoreCase(format)) {
            list7z(f);
         } else if ("zipfile".equals(format)) {
            listZipUsingZipFile(f);
         } else if ("tarfile".equals(format)) {
            listZipUsingTarFile(f);
         } else {
            listStream(f, args);
         }

      }
   }

   private static void listStream(File f, String[] args) throws ArchiveException, IOException {
      InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath()));
      Throwable var3 = null;

      try {
         ArchiveInputStream ais = createArchiveInputStream(args, fis);
         Throwable var5 = null;

         try {
            System.out.println("Created " + ais.toString());

            ArchiveEntry ae;
            while((ae = ais.getNextEntry()) != null) {
               System.out.println(ae.getName());
            }
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (ais != null) {
               if (var5 != null) {
                  try {
                     ais.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  ais.close();
               }
            }

         }
      } catch (Throwable var30) {
         var3 = var30;
         throw var30;
      } finally {
         if (fis != null) {
            if (var3 != null) {
               try {
                  fis.close();
               } catch (Throwable var26) {
                  var3.addSuppressed(var26);
               }
            } else {
               fis.close();
            }
         }

      }

   }

   private static ArchiveInputStream createArchiveInputStream(String[] args, InputStream fis) throws ArchiveException {
      return args.length > 1 ? FACTORY.createArchiveInputStream(args[1], fis) : FACTORY.createArchiveInputStream(fis);
   }

   private static String detectFormat(File f) throws ArchiveException, IOException {
      InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath()));
      Throwable var2 = null;

      String var3;
      try {
         var3 = ArchiveStreamFactory.detect(fis);
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (fis != null) {
            if (var2 != null) {
               try {
                  fis.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               fis.close();
            }
         }

      }

      return var3;
   }

   private static void list7z(File f) throws ArchiveException, IOException {
      SevenZFile z = new SevenZFile(f);
      Throwable var2 = null;

      try {
         System.out.println("Created " + z.toString());

         SevenZArchiveEntry ae;
         while((ae = z.getNextEntry()) != null) {
            String name = ae.getName() == null ? z.getDefaultName() + " (entry name was null)" : ae.getName();
            System.out.println(name);
         }
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (z != null) {
            if (var2 != null) {
               try {
                  z.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               z.close();
            }
         }

      }

   }

   private static void listZipUsingZipFile(File f) throws ArchiveException, IOException {
      ZipFile z = new ZipFile(f);
      Throwable var2 = null;

      try {
         System.out.println("Created " + z.toString());
         Enumeration<ZipArchiveEntry> en = z.getEntries();

         while(en.hasMoreElements()) {
            System.out.println(((ZipArchiveEntry)en.nextElement()).getName());
         }
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (z != null) {
            if (var2 != null) {
               try {
                  z.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               z.close();
            }
         }

      }

   }

   private static void listZipUsingTarFile(File f) throws ArchiveException, IOException {
      TarFile t = new TarFile(f);
      Throwable var2 = null;

      try {
         System.out.println("Created " + t.toString());
         Iterator var3 = t.getEntries().iterator();

         while(var3.hasNext()) {
            TarArchiveEntry en = (TarArchiveEntry)var3.next();
            System.out.println(en.getName());
         }
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (t != null) {
            if (var2 != null) {
               try {
                  t.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               t.close();
            }
         }

      }

   }

   private static void usage() {
      System.out.println("Parameters: archive-name [archive-type]\n");
      System.out.println("the magic archive-type 'zipfile' prefers ZipFile over ZipArchiveInputStream");
      System.out.println("the magic archive-type 'tarfile' prefers TarFile over TarArchiveInputStream");
   }

   static {
      FACTORY = ArchiveStreamFactory.DEFAULT;
   }
}
