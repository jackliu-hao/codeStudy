package oshi.hardware.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.util.FormatUtil;

@ThreadSafe
public abstract class AbstractHWDiskStore implements HWDiskStore {
   private final String name;
   private final String model;
   private final String serial;
   private final long size;

   protected AbstractHWDiskStore(String name, String model, String serial, long size) {
      this.name = name;
      this.model = model;
      this.serial = serial;
      this.size = size;
   }

   public String getName() {
      return this.name;
   }

   public String getModel() {
      return this.model;
   }

   public String getSerial() {
      return this.serial;
   }

   public long getSize() {
      return this.size;
   }

   public String toString() {
      boolean readwrite = this.getReads() > 0L || this.getWrites() > 0L;
      StringBuilder sb = new StringBuilder();
      sb.append(this.getName()).append(": ");
      sb.append("(model: ").append(this.getModel());
      sb.append(" - S/N: ").append(this.getSerial()).append(") ");
      sb.append("size: ").append(this.getSize() > 0L ? FormatUtil.formatBytesDecimal(this.getSize()) : "?").append(", ");
      sb.append("reads: ").append(readwrite ? this.getReads() : "?");
      sb.append(" (").append(readwrite ? FormatUtil.formatBytes(this.getReadBytes()) : "?").append("), ");
      sb.append("writes: ").append(readwrite ? this.getWrites() : "?");
      sb.append(" (").append(readwrite ? FormatUtil.formatBytes(this.getWriteBytes()) : "?").append("), ");
      sb.append("xfer: ").append(readwrite ? this.getTransferTime() : "?");
      return sb.toString();
   }
}
