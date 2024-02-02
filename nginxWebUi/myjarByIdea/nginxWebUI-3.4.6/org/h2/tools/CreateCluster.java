package org.h2.tools;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.Tool;

public class CreateCluster extends Tool {
   public static void main(String... var0) throws SQLException {
      (new CreateCluster()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = null;
      String var3 = null;
      String var4 = "";
      String var5 = "";
      String var6 = null;

      for(int var7 = 0; var1 != null && var7 < var1.length; ++var7) {
         String var8 = var1[var7];
         if (var8.equals("-urlSource")) {
            ++var7;
            var2 = var1[var7];
         } else if (var8.equals("-urlTarget")) {
            ++var7;
            var3 = var1[var7];
         } else if (var8.equals("-user")) {
            ++var7;
            var4 = var1[var7];
         } else if (var8.equals("-password")) {
            ++var7;
            var5 = var1[var7];
         } else if (var8.equals("-serverList")) {
            ++var7;
            var6 = var1[var7];
         } else {
            if (var8.equals("-help") || var8.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var8);
         }
      }

      if (var2 != null && var3 != null && var6 != null) {
         process(var2, var3, var4, var5, var6);
      } else {
         this.showUsage();
         throw new SQLException("Source URL, target URL, or server list not set");
      }
   }

   public void execute(String var1, String var2, String var3, String var4, String var5) throws SQLException {
      process(var1, var2, var3, var4, var5);
   }

   private static void process(String var0, String var1, String var2, String var3, String var4) throws SQLException {
      JdbcConnection var5 = new JdbcConnection(var0 + ";CLUSTER=''", (Properties)null, var2, var3, false);
      Throwable var6 = null;

      try {
         Statement var7 = var5.createStatement();
         Throwable var8 = null;

         try {
            var7.execute("SET EXCLUSIVE 2");

            try {
               performTransfer(var7, var1, var2, var3, var4);
            } finally {
               var7.execute("SET EXCLUSIVE FALSE");
            }
         } catch (Throwable var41) {
            var8 = var41;
            throw var41;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var39) {
                     var8.addSuppressed(var39);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (Throwable var43) {
         var6 = var43;
         throw var43;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var38) {
                  var6.addSuppressed(var38);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   private static void performTransfer(Statement var0, String var1, String var2, String var3, String var4) throws SQLException {
      JdbcConnection var5 = new JdbcConnection(var1 + ";CLUSTER=''", (Properties)null, var2, var3, false);
      Throwable var6 = null;

      try {
         Statement var7 = var5.createStatement();
         Throwable var8 = null;

         try {
            var7.execute("DROP ALL OBJECTS DELETE FILES");
         } catch (Throwable var133) {
            var8 = var133;
            throw var133;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var126) {
                     var8.addSuppressed(var126);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (Throwable var135) {
         var6 = var135;
         throw var135;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var125) {
                  var6.addSuppressed(var125);
               }
            } else {
               var5.close();
            }
         }

      }

      try {
         PipedReader var143 = new PipedReader();
         var6 = null;

         try {
            Future var144 = startWriter(var143, var0);
            JdbcConnection var145 = new JdbcConnection(var1, (Properties)null, var2, var3, false);
            Throwable var9 = null;

            try {
               Statement var10 = var145.createStatement();
               Throwable var11 = null;

               try {
                  RunScript.execute(var145, var143);

                  try {
                     var144.get();
                  } catch (ExecutionException var130) {
                     throw new SQLException(var130.getCause());
                  } catch (InterruptedException var131) {
                     throw new SQLException(var131);
                  }

                  var0.executeUpdate("SET CLUSTER '" + var4 + "'");
                  var10.executeUpdate("SET CLUSTER '" + var4 + "'");
               } catch (Throwable var132) {
                  var11 = var132;
                  throw var132;
               } finally {
                  if (var10 != null) {
                     if (var11 != null) {
                        try {
                           var10.close();
                        } catch (Throwable var129) {
                           var11.addSuppressed(var129);
                        }
                     } else {
                        var10.close();
                     }
                  }

               }
            } catch (Throwable var138) {
               var9 = var138;
               throw var138;
            } finally {
               if (var145 != null) {
                  if (var9 != null) {
                     try {
                        var145.close();
                     } catch (Throwable var128) {
                        var9.addSuppressed(var128);
                     }
                  } else {
                     var145.close();
                  }
               }

            }
         } catch (Throwable var140) {
            var6 = var140;
            throw var140;
         } finally {
            if (var143 != null) {
               if (var6 != null) {
                  try {
                     var143.close();
                  } catch (Throwable var127) {
                     var6.addSuppressed(var127);
                  }
               } else {
                  var143.close();
               }
            }

         }

      } catch (IOException var142) {
         throw new SQLException(var142);
      }
   }

   private static Future<?> startWriter(PipedReader var0, Statement var1) throws IOException {
      ExecutorService var2 = Executors.newFixedThreadPool(1);
      PipedWriter var3 = new PipedWriter(var0);
      Future var4 = var2.submit(() -> {
         try {
            PipedWriter var2 = var3;
            Throwable var3x = null;

            try {
               ResultSet var4 = var1.executeQuery("SCRIPT");
               Throwable var5 = null;

               try {
                  while(var4.next()) {
                     var2.write(var4.getString(1) + "\n");
                  }
               } catch (Throwable var30) {
                  var5 = var30;
                  throw var30;
               } finally {
                  if (var4 != null) {
                     if (var5 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var29) {
                           var5.addSuppressed(var29);
                        }
                     } else {
                        var4.close();
                     }
                  }

               }
            } catch (Throwable var32) {
               var3x = var32;
               throw var32;
            } finally {
               if (var2 != null) {
                  if (var3x != null) {
                     try {
                        var2.close();
                     } catch (Throwable var28) {
                        var3x.addSuppressed(var28);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

         } catch (IOException | SQLException var34) {
            throw new IllegalStateException("Producing script from the source DB is failing.", var34);
         }
      });
      var2.shutdown();
      return var4;
   }
}
