package org.h2.tools;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;

public class ConvertTraceFile extends Tool {
   private final HashMap<String, Stat> stats = new HashMap();
   private long timeTotal;

   public static void main(String... var0) throws SQLException {
      (new ConvertTraceFile()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = "test.trace.db";
      String var3 = "Test";
      String var4 = "test.sql";

      for(int var5 = 0; var1 != null && var5 < var1.length; ++var5) {
         String var6 = var1[var5];
         if (var6.equals("-traceFile")) {
            ++var5;
            var2 = var1[var5];
         } else if (var6.equals("-javaClass")) {
            ++var5;
            var3 = var1[var5];
         } else if (var6.equals("-script")) {
            ++var5;
            var4 = var1[var5];
         } else {
            if (var6.equals("-help") || var6.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var6);
         }
      }

      try {
         this.convertFile(var2, var3, var4);
      } catch (IOException var7) {
         throw DbException.convertIOException(var7, var2);
      }
   }

   private void convertFile(String var1, String var2, String var3) throws IOException {
      LineNumberReader var4 = new LineNumberReader(IOUtils.getReader(FileUtils.newInputStream(var1)));
      PrintWriter var5 = new PrintWriter(IOUtils.getBufferedWriter(FileUtils.newOutputStream(var2 + ".java", false)));
      PrintWriter var6 = new PrintWriter(IOUtils.getBufferedWriter(FileUtils.newOutputStream(var3, false)));
      var5.println("import java.io.*;");
      var5.println("import java.sql.*;");
      var5.println("import java.math.*;");
      var5.println("import java.util.Calendar;");
      String var7 = var2.replace('\\', '/');
      int var8 = var7.lastIndexOf(47);
      if (var8 > 0) {
         var7 = var7.substring(var8 + 1);
      }

      var5.println("public class " + var7 + " {");
      var5.println("    public static void main(String... args) throws Exception {");
      var5.println("        Class.forName(\"org.h2.Driver\");");

      while(true) {
         while(true) {
            String var9 = var4.readLine();
            if (var9 == null) {
               var5.println("    }");
               var5.println('}');
               var4.close();
               var5.close();
               if (this.stats.size() > 0) {
                  var6.println("-----------------------------------------");
                  var6.println("-- SQL Statement Statistics");
                  var6.println("-- time: total time in milliseconds (accumulated)");
                  var6.println("-- count: how many times the statement ran");
                  var6.println("-- result: total update count or row count");
                  var6.println("-----------------------------------------");
                  var6.println("-- self accu    time   count  result sql");
                  int var19 = 0;
                  ArrayList var20 = new ArrayList(this.stats.values());
                  Collections.sort(var20);
                  if (this.timeTotal == 0L) {
                     this.timeTotal = 1L;
                  }

                  Iterator var21 = var20.iterator();

                  while(var21.hasNext()) {
                     Stat var22 = (Stat)var21.next();
                     var19 = (int)((long)var19 + var22.time);
                     StringBuilder var23 = new StringBuilder(100);
                     var23.append("-- ").append(padNumberLeft(100L * var22.time / this.timeTotal, 3)).append("% ").append(padNumberLeft((long)(100 * var19) / this.timeTotal, 3)).append('%').append(padNumberLeft(var22.time, 8)).append(padNumberLeft((long)var22.executeCount, 8)).append(padNumberLeft(var22.resultCount, 8)).append(' ').append(removeNewlines(var22.sql));
                     var6.println(var23.toString());
                  }
               }

               var6.close();
               return;
            }

            if (var9.startsWith("/**/")) {
               var9 = "        " + var9.substring(4);
               var5.println(var9);
            } else if (var9.startsWith("/*SQL")) {
               int var10 = var9.indexOf("*/");
               String var11 = var9.substring(var10 + "*/".length());
               var11 = StringUtils.javaDecode(var11);
               var9 = var9.substring("/*SQL".length(), var10);
               if (var9.length() > 0) {
                  String var12 = var11;
                  int var13 = 0;
                  long var14 = 0L;
                  var9 = var9.trim();
                  if (var9.length() > 0) {
                     StringTokenizer var16 = new StringTokenizer(var9, " :");

                     while(var16.hasMoreElements()) {
                        String var17 = var16.nextToken();
                        if ("l".equals(var17)) {
                           int var18 = Integer.parseInt(var16.nextToken());
                           var12 = var11.substring(0, var18) + ";";
                        } else if ("#".equals(var17)) {
                           var13 = Integer.parseInt(var16.nextToken());
                        } else if ("t".equals(var17)) {
                           var14 = Long.parseLong(var16.nextToken());
                        }
                     }
                  }

                  this.addToStats(var12, var13, var14);
               }

               var6.println(var11);
            }
         }
      }
   }

   private static String removeNewlines(String var0) {
      return var0 == null ? var0 : var0.replace('\r', ' ').replace('\n', ' ');
   }

   private static String padNumberLeft(long var0, int var2) {
      return StringUtils.pad(Long.toString(var0), var2, " ", false);
   }

   private void addToStats(String var1, int var2, long var3) {
      Stat var5 = (Stat)this.stats.get(var1);
      if (var5 == null) {
         var5 = new Stat();
         var5.sql = var1;
         this.stats.put(var1, var5);
      }

      ++var5.executeCount;
      var5.resultCount += (long)var2;
      var5.time += var3;
      this.timeTotal += var3;
   }

   static class Stat implements Comparable<Stat> {
      String sql;
      int executeCount;
      long time;
      long resultCount;

      public int compareTo(Stat var1) {
         if (var1 == this) {
            return 0;
         } else {
            int var2 = Long.compare(var1.time, this.time);
            if (var2 == 0) {
               var2 = Integer.compare(var1.executeCount, this.executeCount);
               if (var2 == 0) {
                  var2 = this.sql.compareTo(var1.sql);
               }
            }

            return var2;
         }
      }
   }
}
