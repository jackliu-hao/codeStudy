/*     */ package cn.hutool.system;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaRuntimeInfo
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  13 */   private final String JAVA_RUNTIME_NAME = SystemUtil.get("java.runtime.name", false);
/*  14 */   private final String JAVA_RUNTIME_VERSION = SystemUtil.get("java.runtime.version", false);
/*  15 */   private final String JAVA_HOME = SystemUtil.get("java.home", false);
/*  16 */   private final String JAVA_EXT_DIRS = SystemUtil.get("java.ext.dirs", false);
/*  17 */   private final String JAVA_ENDORSED_DIRS = SystemUtil.get("java.endorsed.dirs", false);
/*  18 */   private final String JAVA_CLASS_PATH = SystemUtil.get("java.class.path", false);
/*  19 */   private final String JAVA_CLASS_VERSION = SystemUtil.get("java.class.version", false);
/*  20 */   private final String JAVA_LIBRARY_PATH = SystemUtil.get("java.library.path", false);
/*     */   
/*  22 */   private final String SUN_BOOT_CLASS_PATH = SystemUtil.get("sun.boot.class.path", false);
/*     */   
/*  24 */   private final String SUN_ARCH_DATA_MODEL = SystemUtil.get("sun.arch.data.model", false);
/*     */   
/*     */   public final String getSunBoothClassPath() {
/*  27 */     return this.SUN_BOOT_CLASS_PATH;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getSunArchDataModel() {
/*  36 */     return this.SUN_ARCH_DATA_MODEL;
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
/*     */   public final String getName() {
/*  51 */     return this.JAVA_RUNTIME_NAME;
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
/*     */   public final String getVersion() {
/*  66 */     return this.JAVA_RUNTIME_VERSION;
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
/*     */   public final String getHomeDir() {
/*  81 */     return this.JAVA_HOME;
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
/*     */   public final String getExtDirs() {
/*  96 */     return this.JAVA_EXT_DIRS;
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
/*     */   public final String getEndorsedDirs() {
/* 111 */     return this.JAVA_ENDORSED_DIRS;
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
/*     */   public final String getClassPath() {
/* 126 */     return this.JAVA_CLASS_PATH;
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
/*     */   public final String[] getClassPathArray() {
/* 141 */     return StrUtil.splitToArray(getClassPath(), SystemUtil.get("path.separator", false));
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
/*     */   public final String getClassVersion() {
/* 156 */     return this.JAVA_CLASS_VERSION;
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
/*     */   public final String getLibraryPath() {
/* 170 */     return this.JAVA_LIBRARY_PATH;
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
/*     */   public final String[] getLibraryPathArray() {
/* 185 */     return StrUtil.splitToArray(getLibraryPath(), SystemUtil.get("path.separator", false));
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
/*     */   public final String getProtocolPackages() {
/* 200 */     return SystemUtil.get("java.protocol.handler.pkgs", true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 210 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 212 */     SystemUtil.append(builder, "Java Runtime Name:      ", getName());
/* 213 */     SystemUtil.append(builder, "Java Runtime Version:   ", getVersion());
/* 214 */     SystemUtil.append(builder, "Java Home Dir:          ", getHomeDir());
/* 215 */     SystemUtil.append(builder, "Java Extension Dirs:    ", getExtDirs());
/* 216 */     SystemUtil.append(builder, "Java Endorsed Dirs:     ", getEndorsedDirs());
/* 217 */     SystemUtil.append(builder, "Java Class Path:        ", getClassPath());
/* 218 */     SystemUtil.append(builder, "Java Class Version:     ", getClassVersion());
/* 219 */     SystemUtil.append(builder, "Java Library Path:      ", getLibraryPath());
/* 220 */     SystemUtil.append(builder, "Java Protocol Packages: ", getProtocolPackages());
/*     */     
/* 222 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\JavaRuntimeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */