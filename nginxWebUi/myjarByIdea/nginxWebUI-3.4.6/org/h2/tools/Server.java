package org.h2.tools;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.server.Service;
import org.h2.server.ShutdownHandler;
import org.h2.server.TcpServer;
import org.h2.server.pg.PgServer;
import org.h2.server.web.WebServer;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;

public class Server extends Tool implements Runnable, ShutdownHandler {
   private final Service service;
   private Server web;
   private Server tcp;
   private Server pg;
   private ShutdownHandler shutdownHandler;
   private boolean started;

   public Server() {
      this.service = null;
   }

   public Server(Service var1, String... var2) throws SQLException {
      this.verifyArgs(var2);
      this.service = var1;

      try {
         var1.init(var2);
      } catch (Exception var4) {
         throw DbException.toSQLException(var4);
      }
   }

   public static void main(String... var0) throws SQLException {
      (new Server()).runTool(var0);
   }

   private void verifyArgs(String... var1) throws SQLException {
      for(int var2 = 0; var1 != null && var2 < var1.length; ++var2) {
         String var3 = var1[var2];
         if (var3 != null && !"-?".equals(var3) && !"-help".equals(var3)) {
            if (var3.startsWith("-web")) {
               if (!"-web".equals(var3) && !"-webAllowOthers".equals(var3)) {
                  if ("-webExternalNames".equals(var3)) {
                     ++var2;
                  } else if (!"-webDaemon".equals(var3) && !"-webSSL".equals(var3)) {
                     if ("-webPort".equals(var3)) {
                        ++var2;
                     } else if ("-webAdminPassword".equals(var3)) {
                        ++var2;
                     } else {
                        this.throwUnsupportedOption(var3);
                     }
                  }
               }
            } else if (!"-browser".equals(var3)) {
               if (var3.startsWith("-tcp")) {
                  if (!"-tcp".equals(var3) && !"-tcpAllowOthers".equals(var3) && !"-tcpDaemon".equals(var3) && !"-tcpSSL".equals(var3)) {
                     if ("-tcpPort".equals(var3)) {
                        ++var2;
                     } else if ("-tcpPassword".equals(var3)) {
                        ++var2;
                     } else if ("-tcpShutdown".equals(var3)) {
                        ++var2;
                     } else if (!"-tcpShutdownForce".equals(var3)) {
                        this.throwUnsupportedOption(var3);
                     }
                  }
               } else if (var3.startsWith("-pg")) {
                  if (!"-pg".equals(var3) && !"-pgAllowOthers".equals(var3) && !"-pgDaemon".equals(var3)) {
                     if ("-pgPort".equals(var3)) {
                        ++var2;
                     } else {
                        this.throwUnsupportedOption(var3);
                     }
                  }
               } else if (var3.startsWith("-ftp")) {
                  if ("-ftpPort".equals(var3)) {
                     ++var2;
                  } else if ("-ftpDir".equals(var3)) {
                     ++var2;
                  } else if ("-ftpRead".equals(var3)) {
                     ++var2;
                  } else if ("-ftpWrite".equals(var3)) {
                     ++var2;
                  } else if ("-ftpWritePassword".equals(var3)) {
                     ++var2;
                  } else if (!"-ftpTask".equals(var3)) {
                     this.throwUnsupportedOption(var3);
                  }
               } else if ("-properties".equals(var3)) {
                  ++var2;
               } else if (!"-trace".equals(var3) && !"-ifExists".equals(var3) && !"-ifNotExists".equals(var3)) {
                  if ("-baseDir".equals(var3)) {
                     ++var2;
                  } else if ("-key".equals(var3)) {
                     var2 += 2;
                  } else if (!"-tool".equals(var3)) {
                     this.throwUnsupportedOption(var3);
                  }
               }
            }
         }
      }

   }

   public void runTool(String... var1) throws SQLException {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      String var8 = "";
      String var9 = "";
      boolean var10 = true;

      for(int var11 = 0; var1 != null && var11 < var1.length; ++var11) {
         String var12 = var1[var11];
         if (var12 != null) {
            if ("-?".equals(var12) || "-help".equals(var12)) {
               this.showUsage();
               return;
            }

            if (var12.startsWith("-web")) {
               if ("-web".equals(var12)) {
                  var10 = false;
                  var4 = true;
               } else if (!"-webAllowOthers".equals(var12) && !"-webDaemon".equals(var12) && !"-webSSL".equals(var12)) {
                  if ("-webPort".equals(var12)) {
                     ++var11;
                  } else if ("-webAdminPassword".equals(var12)) {
                     ++var11;
                  } else {
                     this.showUsageAndThrowUnsupportedOption(var12);
                  }
               }
            } else if ("-browser".equals(var12)) {
               var10 = false;
               var5 = true;
            } else if (var12.startsWith("-tcp")) {
               if ("-tcp".equals(var12)) {
                  var10 = false;
                  var2 = true;
               } else if (!"-tcpAllowOthers".equals(var12) && !"-tcpDaemon".equals(var12) && !"-tcpSSL".equals(var12)) {
                  if ("-tcpPort".equals(var12)) {
                     ++var11;
                  } else if ("-tcpPassword".equals(var12)) {
                     ++var11;
                     var8 = var1[var11];
                  } else if ("-tcpShutdown".equals(var12)) {
                     var10 = false;
                     var6 = true;
                     ++var11;
                     var9 = var1[var11];
                  } else if ("-tcpShutdownForce".equals(var12)) {
                     var7 = true;
                  } else {
                     this.showUsageAndThrowUnsupportedOption(var12);
                  }
               }
            } else if (var12.startsWith("-pg")) {
               if ("-pg".equals(var12)) {
                  var10 = false;
                  var3 = true;
               } else if (!"-pgAllowOthers".equals(var12) && !"-pgDaemon".equals(var12)) {
                  if ("-pgPort".equals(var12)) {
                     ++var11;
                  } else {
                     this.showUsageAndThrowUnsupportedOption(var12);
                  }
               }
            } else if ("-properties".equals(var12)) {
               ++var11;
            } else if (!"-trace".equals(var12) && !"-ifExists".equals(var12) && !"-ifNotExists".equals(var12)) {
               if ("-baseDir".equals(var12)) {
                  ++var11;
               } else if ("-key".equals(var12)) {
                  var11 += 2;
               } else {
                  this.showUsageAndThrowUnsupportedOption(var12);
               }
            }
         }
      }

      this.verifyArgs(var1);
      if (var10) {
         var2 = true;
         var3 = true;
         var4 = true;
         var5 = true;
      }

      if (var6) {
         this.out.println("Shutting down TCP Server at " + var9);
         shutdownTcpServer(var9, var8, var7, false);
      }

      try {
         if (var2) {
            this.tcp = createTcpServer(var1);
            this.tcp.start();
            this.out.println(this.tcp.getStatus());
            this.tcp.setShutdownHandler(this);
         }

         if (var3) {
            this.pg = createPgServer(var1);
            this.pg.start();
            this.out.println(this.pg.getStatus());
         }

         if (var4) {
            this.web = createWebServer(var1);
            this.web.setShutdownHandler(this);
            SQLException var16 = null;

            try {
               this.web.start();
            } catch (Exception var14) {
               var16 = DbException.toSQLException(var14);
            }

            this.out.println(this.web.getStatus());
            if (var5) {
               try {
                  openBrowser(this.web.getURL());
               } catch (Exception var13) {
                  this.out.println(var13.getMessage());
               }
            }

            if (var16 != null) {
               throw var16;
            }
         } else if (var5) {
            this.out.println("The browser can only start if a web server is started (-web)");
         }

      } catch (SQLException var15) {
         this.stopAll();
         throw var15;
      }
   }

   public static void shutdownTcpServer(String var0, String var1, boolean var2, boolean var3) throws SQLException {
      TcpServer.shutdown(var0, var1, var2, var3);
   }

   public String getStatus() {
      StringBuilder var1 = new StringBuilder();
      if (!this.started) {
         var1.append("Not started");
      } else if (this.isRunning(false)) {
         var1.append(this.service.getType()).append(" server running at ").append(this.service.getURL()).append(" (");
         if (this.service.getAllowOthers()) {
            var1.append("others can connect");
         } else {
            var1.append("only local connections");
         }

         var1.append(')');
      } else {
         var1.append("The ").append(this.service.getType()).append(" server could not be started. Possible cause: another server is already running at ").append(this.service.getURL());
      }

      return var1.toString();
   }

   public static Server createWebServer(String... var0) throws SQLException {
      return createWebServer(var0, (String)null, false);
   }

   static Server createWebServer(String[] var0, String var1, boolean var2) throws SQLException {
      WebServer var3 = new WebServer();
      var3.setKey(var1);
      var3.setAllowSecureCreation(var2);
      Server var4 = new Server(var3, var0);
      var3.setShutdownHandler(var4);
      return var4;
   }

   public static Server createTcpServer(String... var0) throws SQLException {
      TcpServer var1 = new TcpServer();
      Server var2 = new Server(var1, var0);
      var1.setShutdownHandler(var2);
      return var2;
   }

   public static Server createPgServer(String... var0) throws SQLException {
      return new Server(new PgServer(), var0);
   }

   public Server start() throws SQLException {
      try {
         this.started = true;
         this.service.start();
         String var1 = this.service.getURL();
         int var2 = var1.indexOf(63);
         if (var2 >= 0) {
            var1 = var1.substring(0, var2);
         }

         String var3 = this.service.getName() + " (" + var1 + ')';
         Thread var4 = new Thread(this, var3);
         var4.setDaemon(this.service.isDaemon());
         var4.start();

         for(int var5 = 1; var5 < 64; var5 += var5) {
            wait(var5);
            if (this.isRunning(false)) {
               return this;
            }
         }

         if (this.isRunning(true)) {
            return this;
         } else {
            throw DbException.get(90061, var3, "timeout; please check your network configuration, specially the file /etc/hosts");
         }
      } catch (DbException var6) {
         throw DbException.toSQLException(var6);
      }
   }

   private static void wait(int var0) {
      try {
         long var1 = (long)var0 * (long)var0;
         Thread.sleep(var1);
      } catch (InterruptedException var3) {
      }

   }

   private void stopAll() {
      Server var1 = this.web;
      if (var1 != null && var1.isRunning(false)) {
         var1.stop();
         this.web = null;
      }

      var1 = this.tcp;
      if (var1 != null && var1.isRunning(false)) {
         var1.stop();
         this.tcp = null;
      }

      var1 = this.pg;
      if (var1 != null && var1.isRunning(false)) {
         var1.stop();
         this.pg = null;
      }

   }

   public boolean isRunning(boolean var1) {
      return this.service.isRunning(var1);
   }

   public void stop() {
      this.started = false;
      if (this.service != null) {
         this.service.stop();
      }

   }

   public String getURL() {
      return this.service.getURL();
   }

   public int getPort() {
      return this.service.getPort();
   }

   public void run() {
      try {
         this.service.listen();
      } catch (Exception var2) {
         DbException.traceThrowable(var2);
      }

   }

   public void setShutdownHandler(ShutdownHandler var1) {
      this.shutdownHandler = var1;
   }

   public void shutdown() {
      if (this.shutdownHandler != null) {
         this.shutdownHandler.shutdown();
      } else {
         this.stopAll();
      }

   }

   public Service getService() {
      return this.service;
   }

   public static void openBrowser(String var0) throws Exception {
      try {
         String var1 = StringUtils.toLowerEnglish(Utils.getProperty("os.name", "linux"));
         Runtime var2 = Runtime.getRuntime();
         String var3 = Utils.getProperty("h2.browser", (String)null);
         if (var3 == null) {
            try {
               var3 = System.getenv("BROWSER");
            } catch (SecurityException var12) {
            }
         }

         String[] var15;
         if (var3 != null) {
            if (var3.startsWith("call:")) {
               var3 = var3.substring("call:".length());
               Utils.callStaticMethod(var3, var0);
            } else if (var3.contains("%url")) {
               var15 = StringUtils.arraySplit(var3, ',', false);

               for(int var17 = 0; var17 < var15.length; ++var17) {
                  var15[var17] = StringUtils.replaceAll(var15[var17], "%url", var0);
               }

               var2.exec(var15);
            } else if (var1.contains("windows")) {
               var2.exec(new String[]{"cmd.exe", "/C", var3, var0});
            } else {
               var2.exec(new String[]{var3, var0});
            }

         } else {
            try {
               Class var4 = Class.forName("java.awt.Desktop");
               Boolean var5 = (Boolean)var4.getMethod("isDesktopSupported").invoke((Object)null);
               URI var6 = new URI(var0);
               if (var5) {
                  Object var19 = var4.getMethod("getDesktop").invoke((Object)null);
                  var4.getMethod("browse", URI.class).invoke(var19, var6);
                  return;
               }
            } catch (Exception var11) {
            }

            if (var1.contains("windows")) {
               var2.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", var0});
            } else if (!var1.contains("mac") && !var1.contains("darwin")) {
               var15 = new String[]{"xdg-open", "chromium", "google-chrome", "firefox", "mozilla-firefox", "mozilla", "konqueror", "netscape", "opera", "midori"};
               boolean var16 = false;
               String[] var18 = var15;
               int var7 = var15.length;
               int var8 = 0;

               while(var8 < var7) {
                  String var9 = var18[var8];

                  try {
                     var2.exec(new String[]{var9, var0});
                     var16 = true;
                     break;
                  } catch (Exception var13) {
                     ++var8;
                  }
               }

               if (!var16) {
                  throw new Exception("Browser detection failed, and java property 'h2.browser' and environment variable BROWSER are not set to a browser executable.");
               }
            } else {
               Runtime.getRuntime().exec(new String[]{"open", var0});
            }

         }
      } catch (Exception var14) {
         throw new Exception("Failed to start a browser to open the URL " + var0 + ": " + var14.getMessage());
      }
   }

   public static void startWebServer(Connection var0) throws SQLException {
      startWebServer(var0, false);
   }

   public static void startWebServer(Connection var0, boolean var1) throws SQLException {
      WebServer var2 = new WebServer();
      String[] var3;
      if (var1) {
         var3 = new String[]{"-webPort", "0", "-properties", "null"};
      } else {
         var3 = new String[]{"-webPort", "0"};
      }

      Server var4 = new Server(var2, var3);
      var4.start();
      Server var5 = new Server();
      var5.web = var4;
      var2.setShutdownHandler(var5);
      String var6 = var2.addSession(var0);

      try {
         openBrowser(var6);

         while(!var2.isStopped()) {
            Thread.sleep(1000L);
         }
      } catch (Exception var8) {
      }

   }
}
