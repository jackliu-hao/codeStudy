package cn.hutool.system;

import java.io.Serializable;

public class JavaSpecInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String JAVA_SPECIFICATION_NAME = SystemUtil.get("java.specification.name", false);
   private final String JAVA_SPECIFICATION_VERSION = SystemUtil.get("java.specification.version", false);
   private final String JAVA_SPECIFICATION_VENDOR = SystemUtil.get("java.specification.vendor", false);

   public final String getName() {
      return this.JAVA_SPECIFICATION_NAME;
   }

   public final String getVersion() {
      return this.JAVA_SPECIFICATION_VERSION;
   }

   public final String getVendor() {
      return this.JAVA_SPECIFICATION_VENDOR;
   }

   public final String toString() {
      StringBuilder builder = new StringBuilder();
      SystemUtil.append(builder, "Java Spec. Name:    ", this.getName());
      SystemUtil.append(builder, "Java Spec. Version: ", this.getVersion());
      SystemUtil.append(builder, "Java Spec. Vendor:  ", this.getVendor());
      return builder.toString();
   }
}
