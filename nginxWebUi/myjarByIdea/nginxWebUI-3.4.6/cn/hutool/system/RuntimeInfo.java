package cn.hutool.system;

import cn.hutool.core.io.FileUtil;
import java.io.Serializable;

public class RuntimeInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Runtime currentRuntime = Runtime.getRuntime();

   public final Runtime getRuntime() {
      return this.currentRuntime;
   }

   public final long getMaxMemory() {
      return this.currentRuntime.maxMemory();
   }

   public final long getTotalMemory() {
      return this.currentRuntime.totalMemory();
   }

   public final long getFreeMemory() {
      return this.currentRuntime.freeMemory();
   }

   public final long getUsableMemory() {
      return this.currentRuntime.maxMemory() - this.currentRuntime.totalMemory() + this.currentRuntime.freeMemory();
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      SystemUtil.append(builder, "Max Memory:    ", FileUtil.readableFileSize(this.getMaxMemory()));
      SystemUtil.append(builder, "Total Memory:     ", FileUtil.readableFileSize(this.getTotalMemory()));
      SystemUtil.append(builder, "Free Memory:     ", FileUtil.readableFileSize(this.getFreeMemory()));
      SystemUtil.append(builder, "Usable Memory:     ", FileUtil.readableFileSize(this.getUsableMemory()));
      return builder.toString();
   }
}
