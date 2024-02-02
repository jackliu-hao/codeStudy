package cn.hutool.system;

import java.io.Serializable;

public class JvmSpecInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String JAVA_VM_SPECIFICATION_NAME = SystemUtil.get("java.vm.specification.name", false);
   private final String JAVA_VM_SPECIFICATION_VERSION = SystemUtil.get("java.vm.specification.version", false);
   private final String JAVA_VM_SPECIFICATION_VENDOR = SystemUtil.get("java.vm.specification.vendor", false);

   public final String getName() {
      return this.JAVA_VM_SPECIFICATION_NAME;
   }

   public final String getVersion() {
      return this.JAVA_VM_SPECIFICATION_VERSION;
   }

   public final String getVendor() {
      return this.JAVA_VM_SPECIFICATION_VENDOR;
   }

   public final String toString() {
      StringBuilder builder = new StringBuilder();
      SystemUtil.append(builder, "JavaVM Spec. Name:    ", this.getName());
      SystemUtil.append(builder, "JavaVM Spec. Version: ", this.getVersion());
      SystemUtil.append(builder, "JavaVM Spec. Vendor:  ", this.getVendor());
      return builder.toString();
   }
}
