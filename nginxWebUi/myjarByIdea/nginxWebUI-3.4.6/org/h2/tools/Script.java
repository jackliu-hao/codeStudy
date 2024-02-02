package org.h2.tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;

public class Script extends Tool {
   public static void main(String... var0) throws SQLException {
      (new Script()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = null;
      String var3 = "";
      String var4 = "";
      String var5 = "backup.sql";
      String var6 = "";
      String var7 = "";

      for(int var8 = 0; var1 != null && var8 < var1.length; ++var8) {
         String var9 = var1[var8];
         if (var9.equals("-url")) {
            ++var8;
            var2 = var1[var8];
         } else if (var9.equals("-user")) {
            ++var8;
            var3 = var1[var8];
         } else if (var9.equals("-password")) {
            ++var8;
            var4 = var1[var8];
         } else if (var9.equals("-script")) {
            ++var8;
            var5 = var1[var8];
         } else if (var9.equals("-options")) {
            StringBuilder var10 = new StringBuilder();
            StringBuilder var11 = new StringBuilder();
            ++var8;

            for(; var8 < var1.length; ++var8) {
               String var12 = var1[var8];
               String var13 = StringUtils.toUpperEnglish(var12);
               if (!"SIMPLE".equals(var13) && !var13.startsWith("NO") && !"DROP".equals(var13)) {
                  if ("BLOCKSIZE".equals(var13)) {
                     var10.append(' ');
                     var10.append(var1[var8]);
                     ++var8;
                     var10.append(' ');
                     var10.append(var1[var8]);
                  } else {
                     var11.append(' ');
                     var11.append(var1[var8]);
                  }
               } else {
                  var10.append(' ');
                  var10.append(var1[var8]);
               }
            }

            var6 = var10.toString();
            var7 = var11.toString();
         } else {
            if (var9.equals("-help") || var9.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var9);
         }
      }

      if (var2 == null) {
         this.showUsage();
         throw new SQLException("URL not set");
      } else {
         process(var2, var3, var4, var5, var6, var7);
      }
   }

   public static void process(String var0, String var1, String var2, String var3, String var4, String var5) throws SQLException {
      Connection var6 = JdbcUtils.getConnection((String)null, var0, var1, var2);
      Throwable var7 = null;

      try {
         process(var6, var3, var4, var5);
      } catch (Throwable var16) {
         var7 = var16;
         throw var16;
      } finally {
         if (var6 != null) {
            if (var7 != null) {
               try {
                  var6.close();
               } catch (Throwable var15) {
                  var7.addSuppressed(var15);
               }
            } else {
               var6.close();
            }
         }

      }

   }

   public static void process(Connection var0, String var1, String var2, String var3) throws SQLException {
      Statement var4 = var0.createStatement();
      Throwable var5 = null;

      try {
         String var6 = "SCRIPT " + var2 + " TO '" + var1 + "' " + var3;
         var4.execute(var6);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               var4.close();
            }
         }

      }

   }
}
