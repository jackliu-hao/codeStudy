package cn.hutool.system;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;

public class JavaRuntimeInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String JAVA_RUNTIME_NAME = SystemUtil.get("java.runtime.name", false);
   private final String JAVA_RUNTIME_VERSION = SystemUtil.get("java.runtime.version", false);
   private final String JAVA_HOME = SystemUtil.get("java.home", false);
   private final String JAVA_EXT_DIRS = SystemUtil.get("java.ext.dirs", false);
   private final String JAVA_ENDORSED_DIRS = SystemUtil.get("java.endorsed.dirs", false);
   private final String JAVA_CLASS_PATH = SystemUtil.get("java.class.path", false);
   private final String JAVA_CLASS_VERSION = SystemUtil.get("java.class.version", false);
   private final String JAVA_LIBRARY_PATH = SystemUtil.get("java.library.path", false);
   private final String SUN_BOOT_CLASS_PATH = SystemUtil.get("sun.boot.class.path", false);
   private final String SUN_ARCH_DATA_MODEL = SystemUtil.get("sun.arch.data.model", false);

   public final String getSunBoothClassPath() {
      return this.SUN_BOOT_CLASS_PATH;
   }

   public final String getSunArchDataModel() {
      return this.SUN_ARCH_DATA_MODEL;
   }

   public final String getName() {
      return this.JAVA_RUNTIME_NAME;
   }

   public final String getVersion() {
      return this.JAVA_RUNTIME_VERSION;
   }

   public final String getHomeDir() {
      return this.JAVA_HOME;
   }

   public final String getExtDirs() {
      return this.JAVA_EXT_DIRS;
   }

   public final String getEndorsedDirs() {
      return this.JAVA_ENDORSED_DIRS;
   }

   public final String getClassPath() {
      return this.JAVA_CLASS_PATH;
   }

   public final String[] getClassPathArray() {
      return StrUtil.splitToArray(this.getClassPath(), SystemUtil.get("path.separator", false));
   }

   public final String getClassVersion() {
      return this.JAVA_CLASS_VERSION;
   }

   public final String getLibraryPath() {
      return this.JAVA_LIBRARY_PATH;
   }

   public final String[] getLibraryPathArray() {
      return StrUtil.splitToArray(this.getLibraryPath(), SystemUtil.get("path.separator", false));
   }

   public final String getProtocolPackages() {
      return SystemUtil.get("java.protocol.handler.pkgs", true);
   }

   public final String toString() {
      StringBuilder builder = new StringBuilder();
      SystemUtil.append(builder, "Java Runtime Name:      ", this.getName());
      SystemUtil.append(builder, "Java Runtime Version:   ", this.getVersion());
      SystemUtil.append(builder, "Java Home Dir:          ", this.getHomeDir());
      SystemUtil.append(builder, "Java Extension Dirs:    ", this.getExtDirs());
      SystemUtil.append(builder, "Java Endorsed Dirs:     ", this.getEndorsedDirs());
      SystemUtil.append(builder, "Java Class Path:        ", this.getClassPath());
      SystemUtil.append(builder, "Java Class Version:     ", this.getClassVersion());
      SystemUtil.append(builder, "Java Library Path:      ", this.getLibraryPath());
      SystemUtil.append(builder, "Java Protocol Packages: ", this.getProtocolPackages());
      return builder.toString();
   }
}
