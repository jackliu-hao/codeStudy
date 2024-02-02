package org.h2.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;
import org.h2.util.Tool;

public class DeleteDbFiles extends Tool {
   public static void main(String... var0) throws SQLException {
      (new DeleteDbFiles()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = ".";
      String var3 = null;
      boolean var4 = false;

      for(int var5 = 0; var1 != null && var5 < var1.length; ++var5) {
         String var6 = var1[var5];
         if (var6.equals("-dir")) {
            ++var5;
            var2 = var1[var5];
         } else if (var6.equals("-db")) {
            ++var5;
            var3 = var1[var5];
         } else if (var6.equals("-quiet")) {
            var4 = true;
         } else {
            if (var6.equals("-help") || var6.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var6);
         }
      }

      this.process(var2, var3, var4);
   }

   public static void execute(String var0, String var1, boolean var2) {
      (new DeleteDbFiles()).process(var0, var1, var2);
   }

   private void process(String var1, String var2, boolean var3) {
      ArrayList var4 = FileLister.getDatabaseFiles(var1, var2, true);
      if (var4.isEmpty() && !var3) {
         this.printNoDatabaseFilesFound(var1, var2);
      }

      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         process(var6, var3);
         if (!var3) {
            this.out.println("Processed: " + var6);
         }
      }

   }

   private static void process(String var0, boolean var1) {
      if (FileUtils.isDirectory(var0)) {
         FileUtils.tryDelete(var0);
      } else if (!var1 && !var0.endsWith(".temp.db") && !var0.endsWith(".trace.db")) {
         FileUtils.delete(var0);
      } else {
         FileUtils.tryDelete(var0);
      }

   }
}
