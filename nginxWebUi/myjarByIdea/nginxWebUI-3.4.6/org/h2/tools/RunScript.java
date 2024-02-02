package org.h2.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;
import org.h2.util.Tool;

public class RunScript extends Tool {
   private boolean showResults;
   private boolean checkResults;

   public static void main(String... var0) throws SQLException {
      (new RunScript()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = null;
      String var3 = "";
      String var4 = "";
      String var5 = "backup.sql";
      String var6 = null;
      boolean var7 = false;
      boolean var8 = false;

      for(int var9 = 0; var1 != null && var9 < var1.length; ++var9) {
         String var10 = var1[var9];
         if (var10.equals("-url")) {
            ++var9;
            var2 = var1[var9];
         } else if (var10.equals("-user")) {
            ++var9;
            var3 = var1[var9];
         } else if (var10.equals("-password")) {
            ++var9;
            var4 = var1[var9];
         } else if (var10.equals("-continueOnError")) {
            var7 = true;
         } else if (var10.equals("-checkResults")) {
            this.checkResults = true;
         } else if (var10.equals("-showResults")) {
            this.showResults = true;
         } else if (var10.equals("-script")) {
            ++var9;
            var5 = var1[var9];
         } else if (var10.equals("-time")) {
            var8 = true;
         } else if (var10.equals("-driver")) {
            ++var9;
            String var11 = var1[var9];
            JdbcUtils.loadUserClass(var11);
         } else if (var10.equals("-options")) {
            StringBuilder var13 = new StringBuilder();
            ++var9;

            while(var9 < var1.length) {
               var13.append(' ').append(var1[var9]);
               ++var9;
            }

            var6 = var13.toString();
         } else {
            if (var10.equals("-help") || var10.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var10);
         }
      }

      if (var2 == null) {
         this.showUsage();
         throw new SQLException("URL not set");
      } else {
         long var12 = System.nanoTime();
         if (var6 != null) {
            processRunscript(var2, var3, var4, var5, var6);
         } else {
            this.process(var2, var3, var4, var5, (Charset)null, var7);
         }

         if (var8) {
            var12 = System.nanoTime() - var12;
            this.out.println("Done in " + TimeUnit.NANOSECONDS.toMillis(var12) + " ms");
         }

      }
   }

   public static ResultSet execute(Connection var0, Reader var1) throws SQLException {
      Statement var2 = var0.createStatement();
      ResultSet var3 = null;
      ScriptReader var4 = new ScriptReader(var1);

      while(true) {
         String var5 = var4.readStatement();
         if (var5 == null) {
            return var3;
         }

         if (!StringUtils.isWhitespaceOrEmpty(var5)) {
            boolean var6 = var2.execute(var5);
            if (var6) {
               if (var3 != null) {
                  var3.close();
                  var3 = null;
               }

               var3 = var2.getResultSet();
            }
         }
      }
   }

   private void process(Connection var1, String var2, boolean var3, Charset var4) throws SQLException, IOException {
      BufferedReader var5 = FileUtils.newBufferedReader(var2, var4);

      try {
         this.process(var1, var3, FileUtils.getParent(var2), var5, var4);
      } finally {
         IOUtils.closeSilently(var5);
      }

   }

   private void process(Connection var1, boolean var2, String var3, Reader var4, Charset var5) throws SQLException, IOException {
      Statement var6 = var1.createStatement();
      ScriptReader var7 = new ScriptReader(var4);

      while(true) {
         while(true) {
            String var8;
            String var9;
            do {
               var8 = var7.readStatement();
               if (var8 == null) {
                  return;
               }

               var9 = var8.trim();
            } while(var9.isEmpty());

            if (var9.startsWith("@") && StringUtils.toUpperEnglish(var9).startsWith("@INCLUDE")) {
               var8 = StringUtils.trimSubstring(var8, "@INCLUDE".length());
               if (!FileUtils.isAbsolute(var8)) {
                  var8 = var3 + File.separatorChar + var8;
               }

               this.process(var1, var8, var2, var5);
            } else {
               try {
                  if (this.showResults && !var9.startsWith("-->")) {
                     this.out.print(var8 + ";");
                  }

                  if (!this.showResults && !this.checkResults) {
                     var6.execute(var8);
                  } else {
                     boolean var10 = var6.execute(var8);
                     if (var10) {
                        ResultSet var11 = var6.getResultSet();
                        int var12 = var11.getMetaData().getColumnCount();
                        StringBuilder var13 = new StringBuilder();

                        String var15;
                        while(var11.next()) {
                           var13.append("\n-->");

                           for(int var14 = 0; var14 < var12; ++var14) {
                              var15 = var11.getString(var14 + 1);
                              if (var15 != null) {
                                 var15 = StringUtils.replaceAll(var15, "\r\n", "\n");
                                 var15 = StringUtils.replaceAll(var15, "\n", "\n-->    ");
                                 var15 = StringUtils.replaceAll(var15, "\r", "\r-->    ");
                              }

                              var13.append(' ').append(var15);
                           }
                        }

                        var13.append("\n;");
                        String var17 = var13.toString();
                        if (this.showResults) {
                           this.out.print(var17);
                        }

                        if (this.checkResults) {
                           var15 = var7.readStatement() + ";";
                           var15 = StringUtils.replaceAll(var15, "\r\n", "\n");
                           var15 = StringUtils.replaceAll(var15, "\r", "\n");
                           if (!var15.equals(var17)) {
                              var15 = StringUtils.replaceAll(var15, " ", "+");
                              var17 = StringUtils.replaceAll(var17, " ", "+");
                              throw new SQLException("Unexpected output for:\n" + var8.trim() + "\nGot:\n" + var17 + "\nExpected:\n" + var15);
                           }
                        }
                     }
                  }
               } catch (Exception var16) {
                  if (!var2) {
                     throw DbException.toSQLException(var16);
                  }

                  var16.printStackTrace(this.out);
               }
            }
         }
      }
   }

   private static void processRunscript(String var0, String var1, String var2, String var3, String var4) throws SQLException {
      Connection var5 = JdbcUtils.getConnection((String)null, var0, var1, var2);
      Throwable var6 = null;

      try {
         Statement var7 = var5.createStatement();
         Throwable var8 = null;

         try {
            String var9 = "RUNSCRIPT FROM '" + var3 + "' " + var4;
            var7.execute(var9);
         } catch (Throwable var31) {
            var8 = var31;
            throw var31;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var30) {
                     var8.addSuppressed(var30);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (Throwable var33) {
         var6 = var33;
         throw var33;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var29) {
                  var6.addSuppressed(var29);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   public static void execute(String var0, String var1, String var2, String var3, Charset var4, boolean var5) throws SQLException {
      (new RunScript()).process(var0, var1, var2, var3, var4, var5);
   }

   void process(String var1, String var2, String var3, String var4, Charset var5, boolean var6) throws SQLException {
      if (var5 == null) {
         var5 = StandardCharsets.UTF_8;
      }

      try {
         Connection var7 = JdbcUtils.getConnection((String)null, var1, var2, var3);
         Throwable var8 = null;

         try {
            this.process(var7, var4, var6, var5);
         } catch (Throwable var18) {
            var8 = var18;
            throw var18;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var17) {
                     var8.addSuppressed(var17);
                  }
               } else {
                  var7.close();
               }
            }

         }

      } catch (IOException var20) {
         throw DbException.convertIOException(var20, var4);
      }
   }
}
