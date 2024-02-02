package oshi.software.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSFileStore;

@ThreadSafe
public abstract class AbstractOSFileStore implements OSFileStore {
   private String name;
   private String volume;
   private String label;
   private String mount;
   private String options;
   private String uuid;

   protected AbstractOSFileStore(String name, String volume, String label, String mount, String options, String uuid) {
      this.name = name;
      this.volume = volume;
      this.label = label;
      this.mount = mount;
      this.options = options;
      this.uuid = uuid;
   }

   protected AbstractOSFileStore() {
   }

   public String getName() {
      return this.name;
   }

   public String getVolume() {
      return this.volume;
   }

   public String getLabel() {
      return this.label;
   }

   public String getMount() {
      return this.mount;
   }

   public String getOptions() {
      return this.options;
   }

   public String getUUID() {
      return this.uuid;
   }

   public String toString() {
      return "OSFileStore [name=" + this.getName() + ", volume=" + this.getVolume() + ", label=" + this.getLabel() + ", logicalVolume=" + this.getLogicalVolume() + ", mount=" + this.getMount() + ", description=" + this.getDescription() + ", fsType=" + this.getType() + ", options=\"" + this.getOptions() + "\", uuid=" + this.getUUID() + ", freeSpace=" + this.getFreeSpace() + ", usableSpace=" + this.getUsableSpace() + ", totalSpace=" + this.getTotalSpace() + ", freeInodes=" + this.getFreeInodes() + ", totalInodes=" + this.getTotalInodes() + "]";
   }
}
