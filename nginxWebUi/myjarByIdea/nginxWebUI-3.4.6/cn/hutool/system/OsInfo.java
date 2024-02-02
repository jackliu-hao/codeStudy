package cn.hutool.system;

import java.io.Serializable;

public class OsInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String OS_VERSION = SystemUtil.get("os.version", false);
   private final String OS_ARCH = SystemUtil.get("os.arch", false);
   private final String OS_NAME = SystemUtil.get("os.name", false);
   private final boolean IS_OS_AIX = this.getOSMatches("AIX");
   private final boolean IS_OS_HP_UX = this.getOSMatches("HP-UX");
   private final boolean IS_OS_IRIX = this.getOSMatches("Irix");
   private final boolean IS_OS_LINUX = this.getOSMatches("Linux") || this.getOSMatches("LINUX");
   private final boolean IS_OS_MAC = this.getOSMatches("Mac");
   private final boolean IS_OS_MAC_OSX = this.getOSMatches("Mac OS X");
   private final boolean IS_OS_OS2 = this.getOSMatches("OS/2");
   private final boolean IS_OS_SOLARIS = this.getOSMatches("Solaris");
   private final boolean IS_OS_SUN_OS = this.getOSMatches("SunOS");
   private final boolean IS_OS_WINDOWS = this.getOSMatches("Windows");
   private final boolean IS_OS_WINDOWS_2000 = this.getOSMatches("Windows", "5.0");
   private final boolean IS_OS_WINDOWS_95 = this.getOSMatches("Windows 9", "4.0");
   private final boolean IS_OS_WINDOWS_98 = this.getOSMatches("Windows 9", "4.1");
   private final boolean IS_OS_WINDOWS_ME = this.getOSMatches("Windows", "4.9");
   private final boolean IS_OS_WINDOWS_NT = this.getOSMatches("Windows NT");
   private final boolean IS_OS_WINDOWS_XP = this.getOSMatches("Windows", "5.1");
   private final boolean IS_OS_WINDOWS_7 = this.getOSMatches("Windows", "6.1");
   private final boolean IS_OS_WINDOWS_8 = this.getOSMatches("Windows", "6.2");
   private final boolean IS_OS_WINDOWS_8_1 = this.getOSMatches("Windows", "6.3");
   private final boolean IS_OS_WINDOWS_10 = this.getOSMatches("Windows", "10.0");
   private final String FILE_SEPARATOR = SystemUtil.get("file.separator", false);
   private final String LINE_SEPARATOR = SystemUtil.get("line.separator", false);
   private final String PATH_SEPARATOR = SystemUtil.get("path.separator", false);

   public final String getArch() {
      return this.OS_ARCH;
   }

   public final String getName() {
      return this.OS_NAME;
   }

   public final String getVersion() {
      return this.OS_VERSION;
   }

   public final boolean isAix() {
      return this.IS_OS_AIX;
   }

   public final boolean isHpUx() {
      return this.IS_OS_HP_UX;
   }

   public final boolean isIrix() {
      return this.IS_OS_IRIX;
   }

   public final boolean isLinux() {
      return this.IS_OS_LINUX;
   }

   public final boolean isMac() {
      return this.IS_OS_MAC;
   }

   public final boolean isMacOsX() {
      return this.IS_OS_MAC_OSX;
   }

   public final boolean isOs2() {
      return this.IS_OS_OS2;
   }

   public final boolean isSolaris() {
      return this.IS_OS_SOLARIS;
   }

   public final boolean isSunOS() {
      return this.IS_OS_SUN_OS;
   }

   public final boolean isWindows() {
      return this.IS_OS_WINDOWS;
   }

   public final boolean isWindows2000() {
      return this.IS_OS_WINDOWS_2000;
   }

   public final boolean isWindows95() {
      return this.IS_OS_WINDOWS_95;
   }

   public final boolean isWindows98() {
      return this.IS_OS_WINDOWS_98;
   }

   public final boolean isWindowsME() {
      return this.IS_OS_WINDOWS_ME;
   }

   public final boolean isWindowsNT() {
      return this.IS_OS_WINDOWS_NT;
   }

   public final boolean isWindowsXP() {
      return this.IS_OS_WINDOWS_XP;
   }

   public final boolean isWindows7() {
      return this.IS_OS_WINDOWS_7;
   }

   public final boolean isWindows8() {
      return this.IS_OS_WINDOWS_8;
   }

   public final boolean isWindows8_1() {
      return this.IS_OS_WINDOWS_8_1;
   }

   public final boolean isWindows10() {
      return this.IS_OS_WINDOWS_10;
   }

   private boolean getOSMatches(String osNamePrefix) {
      return this.OS_NAME == null ? false : this.OS_NAME.startsWith(osNamePrefix);
   }

   private boolean getOSMatches(String osNamePrefix, String osVersionPrefix) {
      if (this.OS_NAME != null && this.OS_VERSION != null) {
         return this.OS_NAME.startsWith(osNamePrefix) && this.OS_VERSION.startsWith(osVersionPrefix);
      } else {
         return false;
      }
   }

   public final String getFileSeparator() {
      return this.FILE_SEPARATOR;
   }

   public final String getLineSeparator() {
      return this.LINE_SEPARATOR;
   }

   public final String getPathSeparator() {
      return this.PATH_SEPARATOR;
   }

   public final String toString() {
      StringBuilder builder = new StringBuilder();
      SystemUtil.append(builder, "OS Arch:        ", this.getArch());
      SystemUtil.append(builder, "OS Name:        ", this.getName());
      SystemUtil.append(builder, "OS Version:     ", this.getVersion());
      SystemUtil.append(builder, "File Separator: ", this.getFileSeparator());
      SystemUtil.append(builder, "Line Separator: ", this.getLineSeparator());
      SystemUtil.append(builder, "Path Separator: ", this.getPathSeparator());
      return builder.toString();
   }
}
