package org.h2.store;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.store.fs.Recorder;
import org.h2.store.fs.rec.FilePathRec;
import org.h2.tools.Recover;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class RecoverTester implements Recorder {
   private static final RecoverTester instance = new RecoverTester();
   private String testDatabase = "memFS:reopen";
   private int writeCount = Utils.getProperty("h2.recoverTestOffset", 0);
   private int testEvery = Utils.getProperty("h2.recoverTest", 64);
   private final long maxFileSize = (long)Utils.getProperty("h2.recoverTestMaxFileSize", Integer.MAX_VALUE) * 1024L * 1024L;
   private int verifyCount;
   private final HashSet<String> knownErrors = new HashSet();
   private volatile boolean testing;

   public static synchronized void init(String var0) {
      if (StringUtils.isNumber(var0)) {
         instance.setTestEvery(Integer.parseInt(var0));
      }

      FilePathRec.setRecorder(instance);
   }

   public void log(int var1, String var2, byte[] var3, long var4) {
      if (var1 == 8 || var1 == 7) {
         if (var2.endsWith(".mv.db")) {
            ++this.writeCount;
            if (this.writeCount % this.testEvery == 0) {
               if (FileUtils.size(var2) <= this.maxFileSize) {
                  if (!this.testing) {
                     this.testing = true;
                     PrintWriter var6 = null;

                     try {
                        var6 = new PrintWriter(new OutputStreamWriter(FileUtils.newOutputStream(var2 + ".log", true)));
                        this.testDatabase(var2, var6);
                     } catch (IOException var11) {
                        throw DbException.convertIOException(var11, (String)null);
                     } finally {
                        IOUtils.closeSilently(var6);
                        this.testing = false;
                     }

                  }
               }
            }
         }
      }
   }

   private synchronized void testDatabase(String var1, PrintWriter var2) {
      var2.println("+ write #" + this.writeCount + " verify #" + this.verifyCount);

      int var4;
      ConnectionInfo var12;
      Database var16;
      try {
         IOUtils.copyFiles(var1, this.testDatabase + ".mv.db");
         ++this.verifyCount;
         var12 = new ConnectionInfo("jdbc:h2:" + this.testDatabase + ";FILE_LOCK=NO;TRACE_LEVEL_FILE=0", (Properties)null, "", "");
         var16 = new Database(var12, (String)null);
         SessionLocal var15 = var16.getSystemSession();
         var15.prepare("script to '" + this.testDatabase + ".sql'").query(0L);
         var15.prepare("shutdown immediately").update();
         var16.removeSession((SessionLocal)null);
         return;
      } catch (DbException var10) {
         SQLException var13 = DbException.toSQLException(var10);
         int var5 = var13.getErrorCode();
         if (var5 == 28000) {
            return;
         }

         if (var5 == 90049) {
            return;
         }

         var10.printStackTrace(System.out);
      } catch (Exception var11) {
         var4 = 0;
         if (var11 instanceof SQLException) {
            var4 = ((SQLException)var11).getErrorCode();
         }

         if (var4 == 28000) {
            return;
         }

         if (var4 == 90049) {
            return;
         }

         var11.printStackTrace(System.out);
      }

      var2.println("begin ------------------------------ " + this.writeCount);

      try {
         Recover.execute(var1.substring(0, var1.lastIndexOf(47)), (String)null);
      } catch (SQLException var8) {
      }

      this.testDatabase = this.testDatabase + "X";

      try {
         IOUtils.copyFiles(var1, this.testDatabase + ".mv.db");
         var12 = new ConnectionInfo("jdbc:h2:" + this.testDatabase + ";FILE_LOCK=NO", (Properties)null, (String)null, (Object)null);
         var16 = new Database(var12, (String)null);
         var16.removeSession((SessionLocal)null);
      } catch (Exception var9) {
         Object var3 = var9;
         var4 = 0;
         if (var9 instanceof DbException) {
            var3 = ((DbException)var9).getSQLException();
            var4 = ((SQLException)var3).getErrorCode();
         }

         if (var4 == 28000) {
            return;
         }

         if (var4 == 90049) {
            return;
         }

         StringBuilder var14 = new StringBuilder();
         StackTraceElement[] var6 = ((Exception)var3).getStackTrace();

         for(int var7 = 0; var7 < 10 && var7 < var6.length; ++var7) {
            var14.append(var6[var7].toString()).append('\n');
         }

         String var17 = var14.toString();
         if (!this.knownErrors.contains(var17)) {
            var2.println(this.writeCount + " code: " + var4 + " " + ((Exception)var3).toString());
            ((Exception)var3).printStackTrace(System.out);
            this.knownErrors.add(var17);
         } else {
            var2.println(this.writeCount + " code: " + var4);
         }
      }

   }

   public void setTestEvery(int var1) {
      this.testEvery = var1;
   }
}
