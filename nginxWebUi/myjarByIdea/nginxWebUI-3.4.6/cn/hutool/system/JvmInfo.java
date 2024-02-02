package cn.hutool.system;

import java.io.Serializable;

public class JvmInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String JAVA_VM_NAME = SystemUtil.get("java.vm.name", false);
   private final String JAVA_VM_VERSION = SystemUtil.get("java.vm.version", false);
   private final String JAVA_VM_VENDOR = SystemUtil.get("java.vm.vendor", false);
   private final String JAVA_VM_INFO = SystemUtil.get("java.vm.info", false);

   public final String getName() {
      return this.JAVA_VM_NAME;
   }

   public final String getVersion() {
      return this.JAVA_VM_VERSION;
   }

   public final String getVendor() {
      return this.JAVA_VM_VENDOR;
   }

   public final String getInfo() {
      return this.JAVA_VM_INFO;
   }

   public final String toString() {
      StringBuilder builder = new StringBuilder();
      SystemUtil.append(builder, "JavaVM Name:    ", this.getName());
      SystemUtil.append(builder, "JavaVM Version: ", this.getVersion());
      SystemUtil.append(builder, "JavaVM Vendor:  ", this.getVendor());
      SystemUtil.append(builder, "JavaVM Info:    ", this.getInfo());
      return builder.toString();
   }
}
