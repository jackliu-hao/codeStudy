package org.h2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Properties;
import org.h2.message.DbException;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;

public abstract class Tool {
   protected PrintStream out;
   private Properties resources;

   public Tool() {
      this.out = System.out;
   }

   public void setOut(PrintStream var1) {
      this.out = var1;
   }

   public abstract void runTool(String... var1) throws SQLException;

   protected SQLException showUsageAndThrowUnsupportedOption(String var1) throws SQLException {
      this.showUsage();
      throw this.throwUnsupportedOption(var1);
   }

   protected SQLException throwUnsupportedOption(String var1) throws SQLException {
      throw DbException.getJdbcSQLException(50100, var1);
   }

   protected void printNoDatabaseFilesFound(String var1, String var2) {
      var1 = FileLister.getDir(var1);
      StringBuilder var3;
      if (!FileUtils.isDirectory(var1)) {
         var3 = new StringBuilder("Directory not found: ");
         var3.append(var1);
      } else {
         var3 = new StringBuilder("No database files have been found");
         var3.append(" in directory ").append(var1);
         if (var2 != null) {
            var3.append(" for the database ").append(var2);
         }
      }

      this.out.println(var3.toString());
   }

   protected void showUsage() {
      String var1;
      if (this.resources == null) {
         this.resources = new Properties();
         var1 = "/org/h2/res/javadoc.properties";

         try {
            byte[] var2 = Utils.getResource(var1);
            if (var2 != null) {
               this.resources.load(new ByteArrayInputStream(var2));
            }
         } catch (IOException var3) {
            this.out.println("Cannot load " + var1);
         }
      }

      var1 = this.getClass().getName();
      this.out.println(this.resources.get(var1));
      this.out.println("Usage: java " + this.getClass().getName() + " <options>");
      this.out.println(this.resources.get(var1 + ".main"));
      this.out.println("See also https://h2database.com/javadoc/" + var1.replace('.', '/') + ".html");
   }

   public static boolean isOption(String var0, String var1) {
      if (var0.equals(var1)) {
         return true;
      } else if (var0.startsWith(var1)) {
         throw DbException.getUnsupportedException("expected: " + var1 + " got: " + var0);
      } else {
         return false;
      }
   }
}
