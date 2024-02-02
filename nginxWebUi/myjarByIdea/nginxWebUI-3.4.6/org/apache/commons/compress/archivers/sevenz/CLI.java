package org.apache.commons.compress.archivers.sevenz;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class CLI {
   public static void main(String[] args) throws Exception {
      if (args.length == 0) {
         usage();
      } else {
         Mode mode = grabMode(args);
         System.out.println(mode.getMessage() + " " + args[0]);
         File f = new File(args[0]);
         if (!f.isFile()) {
            System.err.println(f + " doesn't exist or is a directory");
         }

         SevenZFile archive = new SevenZFile(f);
         Throwable var4 = null;

         try {
            SevenZArchiveEntry ae;
            try {
               while((ae = archive.getNextEntry()) != null) {
                  mode.takeAction(archive, ae);
               }
            } catch (Throwable var13) {
               var4 = var13;
               throw var13;
            }
         } finally {
            if (archive != null) {
               if (var4 != null) {
                  try {
                     archive.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  archive.close();
               }
            }

         }

      }
   }

   private static void usage() {
      System.out.println("Parameters: archive-name [list]");
   }

   private static Mode grabMode(String[] args) {
      return args.length < 2 ? CLI.Mode.LIST : (Mode)Enum.valueOf(Mode.class, args[1].toUpperCase());
   }

   private static enum Mode {
      LIST("Analysing") {
         public void takeAction(SevenZFile archive, SevenZArchiveEntry entry) {
            System.out.print(entry.getName());
            if (entry.isDirectory()) {
               System.out.print(" dir");
            } else {
               System.out.print(" " + entry.getCompressedSize() + "/" + entry.getSize());
            }

            if (entry.getHasLastModifiedDate()) {
               System.out.print(" " + entry.getLastModifiedDate());
            } else {
               System.out.print(" no last modified date");
            }

            if (!entry.isDirectory()) {
               System.out.println(" " + this.getContentMethods(entry));
            } else {
               System.out.println();
            }

         }

         private String getContentMethods(SevenZArchiveEntry entry) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            Iterator var4 = entry.getContentMethods().iterator();

            while(var4.hasNext()) {
               SevenZMethodConfiguration m = (SevenZMethodConfiguration)var4.next();
               if (!first) {
                  sb.append(", ");
               }

               first = false;
               sb.append(m.getMethod());
               if (m.getOptions() != null) {
                  sb.append("(").append(m.getOptions()).append(")");
               }
            }

            return sb.toString();
         }
      };

      private final String message;

      private Mode(String message) {
         this.message = message;
      }

      public String getMessage() {
         return this.message;
      }

      public abstract void takeAction(SevenZFile var1, SevenZArchiveEntry var2) throws IOException;

      // $FF: synthetic method
      Mode(String x2, Object x3) {
         this(x2);
      }
   }
}
