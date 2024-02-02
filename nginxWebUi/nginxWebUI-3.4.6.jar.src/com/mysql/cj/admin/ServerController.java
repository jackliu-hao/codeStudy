/*     */ package com.mysql.cj.admin;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
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
/*     */ public class ServerController
/*     */ {
/*     */   public static final String BASEDIR_KEY = "basedir";
/*     */   public static final String DATADIR_KEY = "datadir";
/*     */   public static final String DEFAULTS_FILE_KEY = "defaults-file";
/*     */   public static final String EXECUTABLE_NAME_KEY = "executable";
/*     */   public static final String EXECUTABLE_PATH_KEY = "executablePath";
/*  80 */   private Process serverProcess = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   private Properties serverProps = null;
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
/*     */   public ServerController(String baseDir) {
/* 101 */     setBaseDir(baseDir);
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
/*     */   public ServerController(String basedir, String datadir) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBaseDir(String baseDir) {
/* 123 */     getServerProps().setProperty("basedir", baseDir);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDataDir(String dataDir) {
/* 133 */     getServerProps().setProperty("datadir", dataDir);
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
/*     */   public Process start() throws IOException {
/* 146 */     if (this.serverProcess != null) {
/* 147 */       throw new IllegalArgumentException("Server already started");
/*     */     }
/* 149 */     this.serverProcess = Runtime.getRuntime().exec(getCommandLine());
/*     */     
/* 151 */     return this.serverProcess;
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
/*     */   public void stop(boolean forceIfNecessary) throws IOException {
/* 164 */     if (this.serverProcess != null) {
/*     */       
/* 166 */       String basedir = getServerProps().getProperty("basedir");
/*     */       
/* 168 */       StringBuilder pathBuf = new StringBuilder(basedir);
/*     */       
/* 170 */       if (!basedir.endsWith(File.separator)) {
/* 171 */         pathBuf.append(File.separator);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 176 */       pathBuf.append("bin");
/* 177 */       pathBuf.append(File.separator);
/* 178 */       pathBuf.append("mysqladmin shutdown");
/*     */       
/* 180 */       System.out.println(pathBuf.toString());
/*     */       
/* 182 */       Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
/*     */       
/* 184 */       int exitStatus = -1;
/*     */       
/*     */       try {
/* 187 */         exitStatus = mysqladmin.waitFor();
/* 188 */       } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 195 */       if (exitStatus != 0 && forceIfNecessary) {
/* 196 */         forceStop();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forceStop() {
/* 205 */     if (this.serverProcess != null) {
/* 206 */       this.serverProcess.destroy();
/* 207 */       this.serverProcess = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Properties getServerProps() {
/* 218 */     if (this.serverProps == null) {
/* 219 */       this.serverProps = new Properties();
/*     */     }
/*     */     
/* 222 */     return this.serverProps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getCommandLine() {
/* 232 */     StringBuilder commandLine = new StringBuilder(getFullExecutablePath());
/* 233 */     commandLine.append(buildOptionalCommandLine());
/*     */     
/* 235 */     return commandLine.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getFullExecutablePath() {
/* 244 */     StringBuilder pathBuf = new StringBuilder();
/*     */     
/* 246 */     String optionalExecutablePath = getServerProps().getProperty("executablePath");
/*     */     
/* 248 */     if (optionalExecutablePath == null) {
/*     */       
/* 250 */       String basedir = getServerProps().getProperty("basedir");
/* 251 */       pathBuf.append(basedir);
/*     */       
/* 253 */       if (!basedir.endsWith(File.separator)) {
/* 254 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */       
/* 257 */       if (runningOnWindows()) {
/* 258 */         pathBuf.append("bin");
/*     */       } else {
/* 260 */         pathBuf.append("libexec");
/*     */       } 
/*     */       
/* 263 */       pathBuf.append(File.separatorChar);
/*     */     } else {
/* 265 */       pathBuf.append(optionalExecutablePath);
/*     */       
/* 267 */       if (!optionalExecutablePath.endsWith(File.separator)) {
/* 268 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */     } 
/*     */     
/* 272 */     String executableName = getServerProps().getProperty("executable", "mysqld");
/*     */     
/* 274 */     pathBuf.append(executableName);
/*     */     
/* 276 */     return pathBuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String buildOptionalCommandLine() {
/* 286 */     StringBuilder commandLineBuf = new StringBuilder();
/*     */     
/* 288 */     if (this.serverProps != null)
/*     */     {
/* 290 */       for (Iterator<Object> iter = this.serverProps.keySet().iterator(); iter.hasNext(); ) {
/* 291 */         String key = (String)iter.next();
/* 292 */         String value = this.serverProps.getProperty(key);
/*     */         
/* 294 */         if (!isNonCommandLineArgument(key)) {
/* 295 */           if (value != null && value.length() > 0) {
/* 296 */             commandLineBuf.append(" \"");
/* 297 */             commandLineBuf.append("--");
/* 298 */             commandLineBuf.append(key);
/* 299 */             commandLineBuf.append("=");
/* 300 */             commandLineBuf.append(value);
/* 301 */             commandLineBuf.append("\""); continue;
/*     */           } 
/* 303 */           commandLineBuf.append(" --");
/* 304 */           commandLineBuf.append(key);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 310 */     return commandLineBuf.toString();
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
/*     */   private boolean isNonCommandLineArgument(String propName) {
/* 322 */     return (propName.equals("executable") || propName.equals("executablePath"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean runningOnWindows() {
/* 331 */     return (StringUtils.indexOfIgnoreCase(Constants.OS_NAME, "WINDOWS") != -1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\admin\ServerController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */