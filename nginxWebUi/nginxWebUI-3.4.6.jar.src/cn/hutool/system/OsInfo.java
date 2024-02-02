/*     */ package cn.hutool.system;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OsInfo
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  11 */   private final String OS_VERSION = SystemUtil.get("os.version", false);
/*  12 */   private final String OS_ARCH = SystemUtil.get("os.arch", false);
/*  13 */   private final String OS_NAME = SystemUtil.get("os.name", false);
/*  14 */   private final boolean IS_OS_AIX = getOSMatches("AIX");
/*  15 */   private final boolean IS_OS_HP_UX = getOSMatches("HP-UX");
/*  16 */   private final boolean IS_OS_IRIX = getOSMatches("Irix");
/*  17 */   private final boolean IS_OS_LINUX = (getOSMatches("Linux") || getOSMatches("LINUX"));
/*  18 */   private final boolean IS_OS_MAC = getOSMatches("Mac");
/*  19 */   private final boolean IS_OS_MAC_OSX = getOSMatches("Mac OS X");
/*  20 */   private final boolean IS_OS_OS2 = getOSMatches("OS/2");
/*  21 */   private final boolean IS_OS_SOLARIS = getOSMatches("Solaris");
/*  22 */   private final boolean IS_OS_SUN_OS = getOSMatches("SunOS");
/*  23 */   private final boolean IS_OS_WINDOWS = getOSMatches("Windows");
/*  24 */   private final boolean IS_OS_WINDOWS_2000 = getOSMatches("Windows", "5.0");
/*  25 */   private final boolean IS_OS_WINDOWS_95 = getOSMatches("Windows 9", "4.0");
/*  26 */   private final boolean IS_OS_WINDOWS_98 = getOSMatches("Windows 9", "4.1");
/*  27 */   private final boolean IS_OS_WINDOWS_ME = getOSMatches("Windows", "4.9");
/*  28 */   private final boolean IS_OS_WINDOWS_NT = getOSMatches("Windows NT");
/*  29 */   private final boolean IS_OS_WINDOWS_XP = getOSMatches("Windows", "5.1");
/*     */   
/*  31 */   private final boolean IS_OS_WINDOWS_7 = getOSMatches("Windows", "6.1");
/*  32 */   private final boolean IS_OS_WINDOWS_8 = getOSMatches("Windows", "6.2");
/*  33 */   private final boolean IS_OS_WINDOWS_8_1 = getOSMatches("Windows", "6.3");
/*  34 */   private final boolean IS_OS_WINDOWS_10 = getOSMatches("Windows", "10.0");
/*     */ 
/*     */   
/*  37 */   private final String FILE_SEPARATOR = SystemUtil.get("file.separator", false);
/*  38 */   private final String LINE_SEPARATOR = SystemUtil.get("line.separator", false);
/*  39 */   private final String PATH_SEPARATOR = SystemUtil.get("path.separator", false);
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
/*     */   public final String getArch() {
/*  53 */     return this.OS_ARCH;
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
/*  68 */     return this.OS_NAME;
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
/*  83 */     return this.OS_VERSION;
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
/*     */   public final boolean isAix() {
/*  96 */     return this.IS_OS_AIX;
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
/*     */   public final boolean isHpUx() {
/* 109 */     return this.IS_OS_HP_UX;
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
/*     */   public final boolean isIrix() {
/* 122 */     return this.IS_OS_IRIX;
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
/*     */   public final boolean isLinux() {
/* 135 */     return this.IS_OS_LINUX;
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
/*     */   public final boolean isMac() {
/* 148 */     return this.IS_OS_MAC;
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
/*     */   public final boolean isMacOsX() {
/* 161 */     return this.IS_OS_MAC_OSX;
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
/*     */   public final boolean isOs2() {
/* 174 */     return this.IS_OS_OS2;
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
/*     */   public final boolean isSolaris() {
/* 187 */     return this.IS_OS_SOLARIS;
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
/*     */   public final boolean isSunOS() {
/* 200 */     return this.IS_OS_SUN_OS;
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
/*     */   public final boolean isWindows() {
/* 213 */     return this.IS_OS_WINDOWS;
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
/*     */   public final boolean isWindows2000() {
/* 226 */     return this.IS_OS_WINDOWS_2000;
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
/*     */   public final boolean isWindows95() {
/* 239 */     return this.IS_OS_WINDOWS_95;
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
/*     */   public final boolean isWindows98() {
/* 252 */     return this.IS_OS_WINDOWS_98;
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
/*     */   public final boolean isWindowsME() {
/* 265 */     return this.IS_OS_WINDOWS_ME;
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
/*     */   public final boolean isWindowsNT() {
/* 278 */     return this.IS_OS_WINDOWS_NT;
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
/*     */   public final boolean isWindowsXP() {
/* 291 */     return this.IS_OS_WINDOWS_XP;
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
/*     */   public final boolean isWindows7() {
/* 304 */     return this.IS_OS_WINDOWS_7;
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
/*     */   public final boolean isWindows8() {
/* 317 */     return this.IS_OS_WINDOWS_8;
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
/*     */   public final boolean isWindows8_1() {
/* 330 */     return this.IS_OS_WINDOWS_8_1;
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
/*     */   public final boolean isWindows10() {
/* 343 */     return this.IS_OS_WINDOWS_10;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean getOSMatches(String osNamePrefix) {
/* 354 */     if (this.OS_NAME == null) {
/* 355 */       return false;
/*     */     }
/*     */     
/* 358 */     return this.OS_NAME.startsWith(osNamePrefix);
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
/*     */   private boolean getOSMatches(String osNamePrefix, String osVersionPrefix) {
/* 370 */     if (this.OS_NAME == null || this.OS_VERSION == null) {
/* 371 */       return false;
/*     */     }
/*     */     
/* 374 */     return (this.OS_NAME.startsWith(osNamePrefix) && this.OS_VERSION.startsWith(osVersionPrefix));
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
/*     */   public final String getFileSeparator() {
/* 389 */     return this.FILE_SEPARATOR;
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
/*     */   public final String getLineSeparator() {
/* 404 */     return this.LINE_SEPARATOR;
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
/*     */   public final String getPathSeparator() {
/* 419 */     return this.PATH_SEPARATOR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 429 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 431 */     SystemUtil.append(builder, "OS Arch:        ", getArch());
/* 432 */     SystemUtil.append(builder, "OS Name:        ", getName());
/* 433 */     SystemUtil.append(builder, "OS Version:     ", getVersion());
/* 434 */     SystemUtil.append(builder, "File Separator: ", getFileSeparator());
/* 435 */     SystemUtil.append(builder, "Line Separator: ", getLineSeparator());
/* 436 */     SystemUtil.append(builder, "Path Separator: ", getPathSeparator());
/*     */     
/* 438 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\OsInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */