/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.server.ShutdownHandler;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.MathUtils;
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
/*     */ public class Console
/*     */   extends Tool
/*     */   implements ShutdownHandler
/*     */ {
/*     */   Server web;
/*     */   private Server tcp;
/*     */   private Server pg;
/*     */   boolean isWindows;
/*     */   
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*     */     Console console;
/*     */     try {
/*  68 */       console = (Console)Utils.newInstance("org.h2.tools.GUIConsole", new Object[0]);
/*  69 */     } catch (Exception|NoClassDefFoundError exception) {
/*  70 */       console = new Console();
/*     */     } 
/*  72 */     console.runTool(paramVarArgs);
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
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  84 */     this.isWindows = Utils.getProperty("os.name", "").startsWith("Windows");
/*  85 */     boolean bool1 = false, bool2 = false, bool3 = false, bool4 = false;
/*  86 */     boolean bool5 = false;
/*  87 */     boolean bool6 = true;
/*  88 */     boolean bool7 = (paramVarArgs != null && paramVarArgs.length > 0) ? true : false;
/*  89 */     String str1 = null, str2 = null, str3 = null, str4 = null;
/*  90 */     boolean bool8 = false, bool9 = false;
/*  91 */     String str5 = "";
/*  92 */     String str6 = "";
/*  93 */     boolean bool10 = false, bool11 = false;
/*  94 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  95 */       String str = paramVarArgs[b];
/*  96 */       if (str != null) {
/*  97 */         if ("-?".equals(str) || "-help".equals(str)) {
/*  98 */           showUsage(); return;
/*     */         } 
/* 100 */         if ("-url".equals(str)) {
/* 101 */           bool6 = false;
/* 102 */           str2 = paramVarArgs[++b];
/* 103 */         } else if ("-driver".equals(str)) {
/* 104 */           str1 = paramVarArgs[++b];
/* 105 */         } else if ("-user".equals(str)) {
/* 106 */           str3 = paramVarArgs[++b];
/* 107 */         } else if ("-password".equals(str)) {
/* 108 */           str4 = paramVarArgs[++b];
/* 109 */         } else if (str.startsWith("-web")) {
/* 110 */           if ("-web".equals(str)) {
/* 111 */             bool6 = false;
/* 112 */             bool3 = true;
/* 113 */           } else if ("-webAllowOthers".equals(str)) {
/*     */             
/* 115 */             bool11 = true;
/* 116 */           } else if ("-webExternalNames".equals(str)) {
/* 117 */             b++;
/* 118 */           } else if (!"-webDaemon".equals(str)) {
/*     */             
/* 120 */             if (!"-webSSL".equals(str))
/*     */             {
/* 122 */               if ("-webPort".equals(str))
/* 123 */               { b++; }
/*     */               else
/* 125 */               { showUsageAndThrowUnsupportedOption(str); }  } 
/*     */           } 
/* 127 */         } else if ("-tool".equals(str)) {
/* 128 */           bool6 = false;
/* 129 */           bool3 = true;
/* 130 */           bool4 = true;
/* 131 */         } else if ("-browser".equals(str)) {
/* 132 */           bool6 = false;
/* 133 */           bool3 = true;
/* 134 */           bool5 = true;
/* 135 */         } else if (str.startsWith("-tcp")) {
/* 136 */           if ("-tcp".equals(str)) {
/* 137 */             bool6 = false;
/* 138 */             bool1 = true;
/* 139 */           } else if (!"-tcpAllowOthers".equals(str)) {
/*     */             
/* 141 */             if (!"-tcpDaemon".equals(str))
/*     */             {
/* 143 */               if (!"-tcpSSL".equals(str))
/*     */               {
/* 145 */                 if ("-tcpPort".equals(str))
/* 146 */                 { b++; }
/* 147 */                 else if ("-tcpPassword".equals(str))
/* 148 */                 { str5 = paramVarArgs[++b]; }
/* 149 */                 else if ("-tcpShutdown".equals(str))
/* 150 */                 { bool6 = false;
/* 151 */                   bool8 = true;
/* 152 */                   str6 = paramVarArgs[++b]; }
/* 153 */                 else if ("-tcpShutdownForce".equals(str))
/* 154 */                 { bool9 = true; }
/*     */                 else
/* 156 */                 { showUsageAndThrowUnsupportedOption(str); }  }  } 
/*     */           } 
/* 158 */         } else if (str.startsWith("-pg")) {
/* 159 */           if ("-pg".equals(str)) {
/* 160 */             bool6 = false;
/* 161 */             bool2 = true;
/* 162 */           } else if (!"-pgAllowOthers".equals(str)) {
/*     */             
/* 164 */             if (!"-pgDaemon".equals(str))
/*     */             {
/* 166 */               if ("-pgPort".equals(str))
/* 167 */               { b++; }
/*     */               else
/* 169 */               { showUsageAndThrowUnsupportedOption(str); }  } 
/*     */           } 
/* 171 */         } else if ("-properties".equals(str)) {
/* 172 */           b++;
/* 173 */         } else if (!"-trace".equals(str)) {
/*     */           
/* 175 */           if ("-ifExists".equals(str))
/*     */           
/* 177 */           { bool10 = true; }
/* 178 */           else if ("-baseDir".equals(str))
/* 179 */           { b++; }
/*     */           else
/* 181 */           { showUsageAndThrowUnsupportedOption(str); } 
/*     */         } 
/*     */       } 
/* 184 */     }  if (bool6) {
/* 185 */       bool3 = true;
/* 186 */       bool4 = true;
/* 187 */       bool5 = true;
/* 188 */       bool1 = true;
/* 189 */       bool2 = true;
/*     */     } 
/* 191 */     if (bool8) {
/* 192 */       this.out.println("Shutting down TCP Server at " + str6);
/* 193 */       Server.shutdownTcpServer(str6, str5, bool9, false);
/*     */     } 
/*     */     
/* 196 */     SQLException sQLException = null;
/* 197 */     boolean bool12 = false;
/*     */     
/* 199 */     if (str2 != null) {
/* 200 */       Connection connection = JdbcUtils.getConnection(str1, str2, str3, str4);
/* 201 */       Server.startWebServer(connection);
/*     */     } 
/*     */     
/* 204 */     if (bool3) {
/*     */       
/*     */       try {
/* 207 */         String str = bool11 ? null : StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
/* 208 */         this.web = Server.createWebServer(paramVarArgs, str, !bool10);
/* 209 */         this.web.setShutdownHandler(this);
/* 210 */         this.web.start();
/* 211 */         if (bool7) {
/* 212 */           this.out.println(this.web.getStatus());
/*     */         }
/* 214 */         bool12 = true;
/* 215 */       } catch (SQLException sQLException1) {
/* 216 */         printProblem(sQLException1, this.web);
/* 217 */         sQLException = sQLException1;
/*     */       } 
/*     */     }
/*     */     
/* 221 */     if (bool4 && bool12) {
/* 222 */       show();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 228 */     if (bool5 && this.web != null) {
/* 229 */       openBrowser(this.web.getURL());
/*     */     }
/*     */     
/* 232 */     if (bool1) {
/*     */       try {
/* 234 */         this.tcp = Server.createTcpServer(paramVarArgs);
/* 235 */         this.tcp.start();
/* 236 */         if (bool7) {
/* 237 */           this.out.println(this.tcp.getStatus());
/*     */         }
/* 239 */         this.tcp.setShutdownHandler(this);
/* 240 */       } catch (SQLException sQLException1) {
/* 241 */         printProblem(sQLException1, this.tcp);
/* 242 */         if (sQLException == null) {
/* 243 */           sQLException = sQLException1;
/*     */         }
/*     */       } 
/*     */     }
/* 247 */     if (bool2) {
/*     */       try {
/* 249 */         this.pg = Server.createPgServer(paramVarArgs);
/* 250 */         this.pg.start();
/* 251 */         if (bool7) {
/* 252 */           this.out.println(this.pg.getStatus());
/*     */         }
/* 254 */       } catch (SQLException sQLException1) {
/* 255 */         printProblem(sQLException1, this.pg);
/* 256 */         if (sQLException == null) {
/* 257 */           sQLException = sQLException1;
/*     */         }
/*     */       } 
/*     */     }
/* 261 */     if (sQLException != null) {
/* 262 */       shutdown();
/* 263 */       throw sQLException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void show() {}
/*     */ 
/*     */ 
/*     */   
/*     */   private void printProblem(Exception paramException, Server paramServer) {
/* 274 */     if (paramServer == null) {
/* 275 */       paramException.printStackTrace();
/*     */     } else {
/* 277 */       this.out.println(paramServer.getStatus());
/* 278 */       this.out.println("Root cause: " + paramException.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 288 */     if (this.web != null && this.web.isRunning(false)) {
/* 289 */       this.web.stop();
/* 290 */       this.web = null;
/*     */     } 
/* 292 */     if (this.tcp != null && this.tcp.isRunning(false)) {
/* 293 */       this.tcp.stop();
/* 294 */       this.tcp = null;
/*     */     } 
/* 296 */     if (this.pg != null && this.pg.isRunning(false)) {
/* 297 */       this.pg.stop();
/* 298 */       this.pg = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void openBrowser(String paramString) {
/*     */     try {
/* 309 */       Server.openBrowser(paramString);
/* 310 */     } catch (Exception exception) {
/* 311 */       this.out.println(exception.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Console.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */