/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.server.Service;
/*     */ import org.h2.server.ShutdownHandler;
/*     */ import org.h2.server.TcpServer;
/*     */ import org.h2.server.pg.PgServer;
/*     */ import org.h2.server.web.WebServer;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Tool;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Server
/*     */   extends Tool
/*     */   implements Runnable, ShutdownHandler
/*     */ {
/*     */   private final Service service;
/*     */   private Server web;
/*     */   private Server tcp;
/*     */   private Server pg;
/*     */   private ShutdownHandler shutdownHandler;
/*     */   private boolean started;
/*     */   
/*     */   public Server() {
/*  36 */     this.service = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Server(Service paramService, String... paramVarArgs) throws SQLException {
/*  47 */     verifyArgs(paramVarArgs);
/*  48 */     this.service = paramService;
/*     */     try {
/*  50 */       paramService.init(paramVarArgs);
/*  51 */     } catch (Exception exception) {
/*  52 */       throw DbException.toSQLException(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/* 126 */     (new Server()).runTool(paramVarArgs);
/*     */   }
/*     */   
/*     */   private void verifyArgs(String... paramVarArgs) throws SQLException {
/* 130 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/* 131 */       String str = paramVarArgs[b];
/* 132 */       if (str != null && 
/* 133 */         !"-?".equals(str) && !"-help".equals(str))
/*     */       {
/* 135 */         if (str.startsWith("-web")) {
/* 136 */           if (!"-web".equals(str))
/*     */           {
/* 138 */             if (!"-webAllowOthers".equals(str))
/*     */             {
/* 140 */               if ("-webExternalNames".equals(str)) {
/* 141 */                 b++;
/* 142 */               } else if (!"-webDaemon".equals(str)) {
/*     */                 
/* 144 */                 if (!"-webSSL".equals(str))
/*     */                 {
/* 146 */                   if ("-webPort".equals(str))
/* 147 */                   { b++; }
/* 148 */                   else if ("-webAdminPassword".equals(str))
/* 149 */                   { b++; }
/*     */                   else
/* 151 */                   { throwUnsupportedOption(str); }  } 
/*     */               }  }  } 
/* 153 */         } else if (!"-browser".equals(str)) {
/*     */           
/* 155 */           if (str.startsWith("-tcp")) {
/* 156 */             if (!"-tcp".equals(str))
/*     */             {
/* 158 */               if (!"-tcpAllowOthers".equals(str))
/*     */               {
/* 160 */                 if (!"-tcpDaemon".equals(str))
/*     */                 {
/* 162 */                   if (!"-tcpSSL".equals(str))
/*     */                   {
/* 164 */                     if ("-tcpPort".equals(str)) {
/* 165 */                       b++;
/* 166 */                     } else if ("-tcpPassword".equals(str)) {
/* 167 */                       b++;
/* 168 */                     } else if ("-tcpShutdown".equals(str)) {
/* 169 */                       b++;
/* 170 */                     } else if (!"-tcpShutdownForce".equals(str)) {
/*     */ 
/*     */                       
/* 173 */                       throwUnsupportedOption(str);
/*     */                     }  }  }  }  } 
/* 175 */           } else if (str.startsWith("-pg")) {
/* 176 */             if (!"-pg".equals(str))
/*     */             {
/* 178 */               if (!"-pgAllowOthers".equals(str))
/*     */               {
/* 180 */                 if (!"-pgDaemon".equals(str))
/*     */                 {
/* 182 */                   if ("-pgPort".equals(str)) {
/* 183 */                     b++;
/*     */                   } else {
/* 185 */                     throwUnsupportedOption(str);
/*     */                   }  }  }  } 
/* 187 */           } else if (str.startsWith("-ftp")) {
/* 188 */             if ("-ftpPort".equals(str)) {
/* 189 */               b++;
/* 190 */             } else if ("-ftpDir".equals(str)) {
/* 191 */               b++;
/* 192 */             } else if ("-ftpRead".equals(str)) {
/* 193 */               b++;
/* 194 */             } else if ("-ftpWrite".equals(str)) {
/* 195 */               b++;
/* 196 */             } else if ("-ftpWritePassword".equals(str)) {
/* 197 */               b++;
/* 198 */             } else if (!"-ftpTask".equals(str)) {
/*     */ 
/*     */               
/* 201 */               throwUnsupportedOption(str);
/*     */             } 
/* 203 */           } else if ("-properties".equals(str)) {
/* 204 */             b++;
/* 205 */           } else if (!"-trace".equals(str)) {
/*     */             
/* 207 */             if (!"-ifExists".equals(str))
/*     */             {
/* 209 */               if (!"-ifNotExists".equals(str))
/*     */               {
/* 211 */                 if ("-baseDir".equals(str)) {
/* 212 */                   b++;
/* 213 */                 } else if ("-key".equals(str)) {
/* 214 */                   b += 2;
/* 215 */                 } else if (!"-tool".equals(str)) {
/*     */ 
/*     */                   
/* 218 */                   throwUnsupportedOption(str);
/*     */                 }  }  } 
/*     */           } 
/*     */         }  } 
/*     */     } 
/*     */   }
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/* 225 */     boolean bool1 = false, bool2 = false, bool3 = false;
/* 226 */     boolean bool4 = false;
/* 227 */     boolean bool5 = false, bool6 = false;
/* 228 */     String str1 = "";
/* 229 */     String str2 = "";
/* 230 */     boolean bool7 = true;
/* 231 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/* 232 */       String str = paramVarArgs[b];
/* 233 */       if (str != null) {
/* 234 */         if ("-?".equals(str) || "-help".equals(str)) {
/* 235 */           showUsage(); return;
/*     */         } 
/* 237 */         if (str.startsWith("-web")) {
/* 238 */           if ("-web".equals(str)) {
/* 239 */             bool7 = false;
/* 240 */             bool3 = true;
/* 241 */           } else if (!"-webAllowOthers".equals(str)) {
/*     */             
/* 243 */             if (!"-webDaemon".equals(str))
/*     */             {
/* 245 */               if (!"-webSSL".equals(str))
/*     */               {
/* 247 */                 if ("-webPort".equals(str))
/* 248 */                 { b++; }
/* 249 */                 else if ("-webAdminPassword".equals(str))
/* 250 */                 { b++; }
/*     */                 else
/* 252 */                 { showUsageAndThrowUnsupportedOption(str); }  }  } 
/*     */           } 
/* 254 */         } else if ("-browser".equals(str)) {
/* 255 */           bool7 = false;
/* 256 */           bool4 = true;
/* 257 */         } else if (str.startsWith("-tcp")) {
/* 258 */           if ("-tcp".equals(str)) {
/* 259 */             bool7 = false;
/* 260 */             bool1 = true;
/* 261 */           } else if (!"-tcpAllowOthers".equals(str)) {
/*     */             
/* 263 */             if (!"-tcpDaemon".equals(str))
/*     */             {
/* 265 */               if (!"-tcpSSL".equals(str))
/*     */               {
/* 267 */                 if ("-tcpPort".equals(str))
/* 268 */                 { b++; }
/* 269 */                 else if ("-tcpPassword".equals(str))
/* 270 */                 { str1 = paramVarArgs[++b]; }
/* 271 */                 else if ("-tcpShutdown".equals(str))
/* 272 */                 { bool7 = false;
/* 273 */                   bool5 = true;
/* 274 */                   str2 = paramVarArgs[++b]; }
/* 275 */                 else if ("-tcpShutdownForce".equals(str))
/* 276 */                 { bool6 = true; }
/*     */                 else
/* 278 */                 { showUsageAndThrowUnsupportedOption(str); }  }  } 
/*     */           } 
/* 280 */         } else if (str.startsWith("-pg")) {
/* 281 */           if ("-pg".equals(str)) {
/* 282 */             bool7 = false;
/* 283 */             bool2 = true;
/* 284 */           } else if (!"-pgAllowOthers".equals(str)) {
/*     */             
/* 286 */             if (!"-pgDaemon".equals(str))
/*     */             {
/* 288 */               if ("-pgPort".equals(str))
/* 289 */               { b++; }
/*     */               else
/* 291 */               { showUsageAndThrowUnsupportedOption(str); }  } 
/*     */           } 
/* 293 */         } else if ("-properties".equals(str)) {
/* 294 */           b++;
/* 295 */         } else if (!"-trace".equals(str)) {
/*     */           
/* 297 */           if (!"-ifExists".equals(str))
/*     */           {
/* 299 */             if (!"-ifNotExists".equals(str))
/*     */             {
/* 301 */               if ("-baseDir".equals(str))
/* 302 */               { b++; }
/* 303 */               else if ("-key".equals(str))
/* 304 */               { b += 2; }
/*     */               else
/* 306 */               { showUsageAndThrowUnsupportedOption(str); }  }  } 
/*     */         } 
/*     */       } 
/* 309 */     }  verifyArgs(paramVarArgs);
/* 310 */     if (bool7) {
/* 311 */       bool1 = true;
/* 312 */       bool2 = true;
/* 313 */       bool3 = true;
/* 314 */       bool4 = true;
/*     */     } 
/*     */     
/* 317 */     if (bool5) {
/* 318 */       this.out.println("Shutting down TCP Server at " + str2);
/* 319 */       shutdownTcpServer(str2, str1, bool6, false);
/*     */     } 
/*     */     
/*     */     try {
/* 323 */       if (bool1) {
/* 324 */         this.tcp = createTcpServer(paramVarArgs);
/* 325 */         this.tcp.start();
/* 326 */         this.out.println(this.tcp.getStatus());
/* 327 */         this.tcp.setShutdownHandler(this);
/*     */       } 
/* 329 */       if (bool2) {
/* 330 */         this.pg = createPgServer(paramVarArgs);
/* 331 */         this.pg.start();
/* 332 */         this.out.println(this.pg.getStatus());
/*     */       } 
/* 334 */       if (bool3) {
/* 335 */         this.web = createWebServer(paramVarArgs);
/* 336 */         this.web.setShutdownHandler(this);
/* 337 */         SQLException sQLException = null;
/*     */         try {
/* 339 */           this.web.start();
/* 340 */         } catch (Exception exception) {
/* 341 */           sQLException = DbException.toSQLException(exception);
/*     */         } 
/* 343 */         this.out.println(this.web.getStatus());
/*     */ 
/*     */ 
/*     */         
/* 347 */         if (bool4) {
/*     */           try {
/* 349 */             openBrowser(this.web.getURL());
/* 350 */           } catch (Exception exception) {
/* 351 */             this.out.println(exception.getMessage());
/*     */           } 
/*     */         }
/* 354 */         if (sQLException != null) {
/* 355 */           throw sQLException;
/*     */         }
/* 357 */       } else if (bool4) {
/* 358 */         this.out.println("The browser can only start if a web server is started (-web)");
/*     */       } 
/* 360 */     } catch (SQLException sQLException) {
/* 361 */       stopAll();
/* 362 */       throw sQLException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void shutdownTcpServer(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) throws SQLException {
/* 388 */     TcpServer.shutdown(paramString1, paramString2, paramBoolean1, paramBoolean2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStatus() {
/* 397 */     StringBuilder stringBuilder = new StringBuilder();
/* 398 */     if (!this.started) {
/* 399 */       stringBuilder.append("Not started");
/* 400 */     } else if (isRunning(false)) {
/* 401 */       stringBuilder.append(this.service.getType())
/* 402 */         .append(" server running at ")
/* 403 */         .append(this.service.getURL())
/* 404 */         .append(" (");
/* 405 */       if (this.service.getAllowOthers()) {
/* 406 */         stringBuilder.append("others can connect");
/*     */       } else {
/* 408 */         stringBuilder.append("only local connections");
/*     */       } 
/* 410 */       stringBuilder.append(')');
/*     */     } else {
/* 412 */       stringBuilder.append("The ")
/* 413 */         .append(this.service.getType())
/* 414 */         .append(" server could not be started. Possible cause: another server is already running at ")
/*     */         
/* 416 */         .append(this.service.getURL());
/*     */     } 
/* 418 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Server createWebServer(String... paramVarArgs) throws SQLException {
/* 437 */     return createWebServer(paramVarArgs, (String)null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Server createWebServer(String[] paramArrayOfString, String paramString, boolean paramBoolean) throws SQLException {
/* 452 */     WebServer webServer = new WebServer();
/* 453 */     webServer.setKey(paramString);
/* 454 */     webServer.setAllowSecureCreation(paramBoolean);
/* 455 */     Server server = new Server((Service)webServer, paramArrayOfString);
/* 456 */     webServer.setShutdownHandler(server);
/* 457 */     return server;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Server createTcpServer(String... paramVarArgs) throws SQLException {
/* 482 */     TcpServer tcpServer = new TcpServer();
/* 483 */     Server server = new Server((Service)tcpServer, paramVarArgs);
/* 484 */     tcpServer.setShutdownHandler(server);
/* 485 */     return server;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Server createPgServer(String... paramVarArgs) throws SQLException {
/* 510 */     return new Server((Service)new PgServer(), paramVarArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Server start() throws SQLException {
/*     */     try {
/* 520 */       this.started = true;
/* 521 */       this.service.start();
/* 522 */       String str1 = this.service.getURL();
/* 523 */       int i = str1.indexOf('?');
/* 524 */       if (i >= 0) {
/* 525 */         str1 = str1.substring(0, i);
/*     */       }
/* 527 */       String str2 = this.service.getName() + " (" + str1 + ')';
/* 528 */       Thread thread = new Thread(this, str2);
/* 529 */       thread.setDaemon(this.service.isDaemon());
/* 530 */       thread.start(); int j;
/* 531 */       for (j = 1; j < 64; j += j) {
/* 532 */         wait(j);
/* 533 */         if (isRunning(false)) {
/* 534 */           return this;
/*     */         }
/*     */       } 
/* 537 */       if (isRunning(true)) {
/* 538 */         return this;
/*     */       }
/* 540 */       throw DbException.get(90061, new String[] { str2, "timeout; please check your network configuration, specially the file /etc/hosts" });
/*     */     
/*     */     }
/* 543 */     catch (DbException dbException) {
/* 544 */       throw DbException.toSQLException(dbException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void wait(int paramInt) {
/*     */     try {
/* 551 */       long l = paramInt * paramInt;
/* 552 */       Thread.sleep(l);
/* 553 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void stopAll() {
/* 559 */     Server server = this.web;
/* 560 */     if (server != null && server.isRunning(false)) {
/* 561 */       server.stop();
/* 562 */       this.web = null;
/*     */     } 
/* 564 */     server = this.tcp;
/* 565 */     if (server != null && server.isRunning(false)) {
/* 566 */       server.stop();
/* 567 */       this.tcp = null;
/*     */     } 
/* 569 */     server = this.pg;
/* 570 */     if (server != null && server.isRunning(false)) {
/* 571 */       server.stop();
/* 572 */       this.pg = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRunning(boolean paramBoolean) {
/* 583 */     return this.service.isRunning(paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 590 */     this.started = false;
/* 591 */     if (this.service != null) {
/* 592 */       this.service.stop();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 602 */     return this.service.getURL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 611 */     return this.service.getPort();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/* 620 */       this.service.listen();
/* 621 */     } catch (Exception exception) {
/* 622 */       DbException.traceThrowable(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShutdownHandler(ShutdownHandler paramShutdownHandler) {
/* 631 */     this.shutdownHandler = paramShutdownHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 639 */     if (this.shutdownHandler != null) {
/* 640 */       this.shutdownHandler.shutdown();
/*     */     } else {
/* 642 */       stopAll();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Service getService() {
/* 652 */     return this.service;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void openBrowser(String paramString) throws Exception {
/*     */     try {
/* 663 */       String str1 = StringUtils.toLowerEnglish(
/* 664 */           Utils.getProperty("os.name", "linux"));
/* 665 */       Runtime runtime = Runtime.getRuntime();
/* 666 */       String str2 = Utils.getProperty("h2.browser", null);
/* 667 */       if (str2 == null) {
/*     */         
/*     */         try {
/* 670 */           str2 = System.getenv("BROWSER");
/* 671 */         } catch (SecurityException securityException) {}
/*     */       }
/*     */ 
/*     */       
/* 675 */       if (str2 != null) {
/* 676 */         if (str2.startsWith("call:")) {
/* 677 */           str2 = str2.substring("call:".length());
/* 678 */           Utils.callStaticMethod(str2, new Object[] { paramString });
/* 679 */         } else if (str2.contains("%url")) {
/* 680 */           String[] arrayOfString = StringUtils.arraySplit(str2, ',', false);
/* 681 */           for (byte b = 0; b < arrayOfString.length; b++) {
/* 682 */             arrayOfString[b] = StringUtils.replaceAll(arrayOfString[b], "%url", paramString);
/*     */           }
/* 684 */           runtime.exec(arrayOfString);
/* 685 */         } else if (str1.contains("windows")) {
/* 686 */           runtime.exec(new String[] { "cmd.exe", "/C", str2, paramString });
/*     */         } else {
/* 688 */           runtime.exec(new String[] { str2, paramString });
/*     */         } 
/*     */         return;
/*     */       } 
/*     */       try {
/* 693 */         Class<?> clazz = Class.forName("java.awt.Desktop");
/*     */ 
/*     */ 
/*     */         
/* 697 */         Boolean bool = (Boolean)clazz.getMethod("isDesktopSupported", new Class[0]).invoke(null, new Object[0]);
/* 698 */         URI uRI = new URI(paramString);
/* 699 */         if (bool.booleanValue()) {
/*     */ 
/*     */           
/* 702 */           Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
/*     */           
/* 704 */           clazz.getMethod("browse", new Class[] { URI.class
/* 705 */               }).invoke(object, new Object[] { uRI });
/*     */           return;
/*     */         } 
/* 708 */       } catch (Exception exception) {}
/*     */ 
/*     */       
/* 711 */       if (str1.contains("windows")) {
/* 712 */         runtime.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", paramString });
/* 713 */       } else if (str1.contains("mac") || str1.contains("darwin")) {
/*     */         
/* 715 */         Runtime.getRuntime().exec(new String[] { "open", paramString });
/*     */       } else {
/* 717 */         String[] arrayOfString = { "xdg-open", "chromium", "google-chrome", "firefox", "mozilla-firefox", "mozilla", "konqueror", "netscape", "opera", "midori" };
/*     */ 
/*     */         
/* 720 */         boolean bool = false;
/* 721 */         for (String str : arrayOfString) {
/*     */           try {
/* 723 */             runtime.exec(new String[] { str, paramString });
/* 724 */             bool = true;
/*     */             break;
/* 726 */           } catch (Exception exception) {}
/*     */         } 
/*     */ 
/*     */         
/* 730 */         if (!bool)
/*     */         {
/* 732 */           throw new Exception("Browser detection failed, and java property 'h2.browser' and environment variable BROWSER are not set to a browser executable.");
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 737 */     catch (Exception exception) {
/* 738 */       throw new Exception("Failed to start a browser to open the URL " + paramString + ": " + exception
/*     */           
/* 740 */           .getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void startWebServer(Connection paramConnection) throws SQLException {
/* 754 */     startWebServer(paramConnection, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void startWebServer(Connection paramConnection, boolean paramBoolean) throws SQLException {
/*     */     String[] arrayOfString;
/* 769 */     WebServer webServer = new WebServer();
/*     */     
/* 771 */     if (paramBoolean) {
/* 772 */       arrayOfString = new String[] { "-webPort", "0", "-properties", "null" };
/*     */     } else {
/* 774 */       arrayOfString = new String[] { "-webPort", "0" };
/*     */     } 
/* 776 */     Server server1 = new Server((Service)webServer, arrayOfString);
/* 777 */     server1.start();
/* 778 */     Server server2 = new Server();
/* 779 */     server2.web = server1;
/* 780 */     webServer.setShutdownHandler(server2);
/* 781 */     String str = webServer.addSession(paramConnection);
/*     */     try {
/* 783 */       openBrowser(str);
/* 784 */       while (!webServer.isStopped()) {
/* 785 */         Thread.sleep(1000L);
/*     */       }
/* 787 */     } catch (Exception exception) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */