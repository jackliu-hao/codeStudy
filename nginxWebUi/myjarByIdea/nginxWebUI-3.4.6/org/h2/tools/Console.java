package org.h2.tools;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.server.ShutdownHandler;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;

public class Console extends Tool implements ShutdownHandler {
   Server web;
   private Server tcp;
   private Server pg;
   boolean isWindows;

   public static void main(String... var0) throws SQLException {
      Console var1;
      try {
         var1 = (Console)Utils.newInstance("org.h2.tools.GUIConsole");
      } catch (NoClassDefFoundError | Exception var3) {
         var1 = new Console();
      }

      var1.runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      this.isWindows = Utils.getProperty("os.name", "").startsWith("Windows");
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = true;
      boolean var8 = var1 != null && var1.length > 0;
      String var9 = null;
      String var10 = null;
      String var11 = null;
      String var12 = null;
      boolean var13 = false;
      boolean var14 = false;
      String var15 = "";
      String var16 = "";
      boolean var17 = false;
      boolean var18 = false;

      for(int var19 = 0; var1 != null && var19 < var1.length; ++var19) {
         String var20 = var1[var19];
         if (var20 != null) {
            if ("-?".equals(var20) || "-help".equals(var20)) {
               this.showUsage();
               return;
            }

            if ("-url".equals(var20)) {
               var7 = false;
               ++var19;
               var10 = var1[var19];
            } else if ("-driver".equals(var20)) {
               ++var19;
               var9 = var1[var19];
            } else if ("-user".equals(var20)) {
               ++var19;
               var11 = var1[var19];
            } else if ("-password".equals(var20)) {
               ++var19;
               var12 = var1[var19];
            } else if (var20.startsWith("-web")) {
               if ("-web".equals(var20)) {
                  var7 = false;
                  var4 = true;
               } else if ("-webAllowOthers".equals(var20)) {
                  var18 = true;
               } else if ("-webExternalNames".equals(var20)) {
                  ++var19;
               } else if (!"-webDaemon".equals(var20) && !"-webSSL".equals(var20)) {
                  if ("-webPort".equals(var20)) {
                     ++var19;
                  } else {
                     this.showUsageAndThrowUnsupportedOption(var20);
                  }
               }
            } else if ("-tool".equals(var20)) {
               var7 = false;
               var4 = true;
               var5 = true;
            } else if ("-browser".equals(var20)) {
               var7 = false;
               var4 = true;
               var6 = true;
            } else if (var20.startsWith("-tcp")) {
               if ("-tcp".equals(var20)) {
                  var7 = false;
                  var2 = true;
               } else if (!"-tcpAllowOthers".equals(var20) && !"-tcpDaemon".equals(var20) && !"-tcpSSL".equals(var20)) {
                  if ("-tcpPort".equals(var20)) {
                     ++var19;
                  } else if ("-tcpPassword".equals(var20)) {
                     ++var19;
                     var15 = var1[var19];
                  } else if ("-tcpShutdown".equals(var20)) {
                     var7 = false;
                     var13 = true;
                     ++var19;
                     var16 = var1[var19];
                  } else if ("-tcpShutdownForce".equals(var20)) {
                     var14 = true;
                  } else {
                     this.showUsageAndThrowUnsupportedOption(var20);
                  }
               }
            } else if (var20.startsWith("-pg")) {
               if ("-pg".equals(var20)) {
                  var7 = false;
                  var3 = true;
               } else if (!"-pgAllowOthers".equals(var20) && !"-pgDaemon".equals(var20)) {
                  if ("-pgPort".equals(var20)) {
                     ++var19;
                  } else {
                     this.showUsageAndThrowUnsupportedOption(var20);
                  }
               }
            } else if ("-properties".equals(var20)) {
               ++var19;
            } else if (!"-trace".equals(var20)) {
               if ("-ifExists".equals(var20)) {
                  var17 = true;
               } else if ("-baseDir".equals(var20)) {
                  ++var19;
               } else {
                  this.showUsageAndThrowUnsupportedOption(var20);
               }
            }
         }
      }

      if (var7) {
         var4 = true;
         var5 = true;
         var6 = true;
         var2 = true;
         var3 = true;
      }

      if (var13) {
         this.out.println("Shutting down TCP Server at " + var16);
         Server.shutdownTcpServer(var16, var15, var14, false);
      }

      SQLException var27 = null;
      boolean var25 = false;
      if (var10 != null) {
         Connection var21 = JdbcUtils.getConnection(var9, var10, var11, var12);
         Server.startWebServer(var21);
      }

      if (var4) {
         try {
            String var26 = var18 ? null : StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
            this.web = Server.createWebServer(var1, var26, !var17);
            this.web.setShutdownHandler(this);
            this.web.start();
            if (var8) {
               this.out.println(this.web.getStatus());
            }

            var25 = true;
         } catch (SQLException var22) {
            this.printProblem(var22, this.web);
            var27 = var22;
         }
      }

      if (var5 && var25) {
         this.show();
      }

      if (var6 && this.web != null) {
         this.openBrowser(this.web.getURL());
      }

      if (var2) {
         try {
            this.tcp = Server.createTcpServer(var1);
            this.tcp.start();
            if (var8) {
               this.out.println(this.tcp.getStatus());
            }

            this.tcp.setShutdownHandler(this);
         } catch (SQLException var24) {
            this.printProblem(var24, this.tcp);
            if (var27 == null) {
               var27 = var24;
            }
         }
      }

      if (var3) {
         try {
            this.pg = Server.createPgServer(var1);
            this.pg.start();
            if (var8) {
               this.out.println(this.pg.getStatus());
            }
         } catch (SQLException var23) {
            this.printProblem(var23, this.pg);
            if (var27 == null) {
               var27 = var23;
            }
         }
      }

      if (var27 != null) {
         this.shutdown();
         throw var27;
      }
   }

   void show() {
   }

   private void printProblem(Exception var1, Server var2) {
      if (var2 == null) {
         var1.printStackTrace();
      } else {
         this.out.println(var2.getStatus());
         this.out.println("Root cause: " + var1.getMessage());
      }

   }

   public void shutdown() {
      if (this.web != null && this.web.isRunning(false)) {
         this.web.stop();
         this.web = null;
      }

      if (this.tcp != null && this.tcp.isRunning(false)) {
         this.tcp.stop();
         this.tcp = null;
      }

      if (this.pg != null && this.pg.isRunning(false)) {
         this.pg.stop();
         this.pg = null;
      }

   }

   void openBrowser(String var1) {
      try {
         Server.openBrowser(var1);
      } catch (Exception var3) {
         this.out.println(var3.getMessage());
      }

   }
}
