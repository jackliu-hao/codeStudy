package oshi.software.os.windows;

import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSFileStore;
import oshi.software.os.OSFileStore;

@ThreadSafe
public class WindowsOSFileStore extends AbstractOSFileStore {
   private String logicalVolume;
   private String description;
   private String fsType;
   private long freeSpace;
   private long usableSpace;
   private long totalSpace;
   private long freeInodes;
   private long totalInodes;

   public WindowsOSFileStore(String name, String volume, String label, String mount, String options, String uuid, String logicalVolume, String description, String fsType, long freeSpace, long usableSpace, long totalSpace, long freeInodes, long totalInodes) {
      super(name, volume, label, mount, options, uuid);
      this.logicalVolume = logicalVolume;
      this.description = description;
      this.fsType = fsType;
      this.freeSpace = freeSpace;
      this.usableSpace = usableSpace;
      this.totalSpace = totalSpace;
      this.freeInodes = freeInodes;
      this.totalInodes = totalInodes;
   }

   public String getLogicalVolume() {
      return this.logicalVolume;
   }

   public String getDescription() {
      return this.description;
   }

   public String getType() {
      return this.fsType;
   }

   public long getFreeSpace() {
      return this.freeSpace;
   }

   public long getUsableSpace() {
      return this.usableSpace;
   }

   public long getTotalSpace() {
      return this.totalSpace;
   }

   public long getFreeInodes() {
      return this.freeInodes;
   }

   public long getTotalInodes() {
      return this.totalInodes;
   }

   public boolean updateAttributes() {
      List<OSFileStore> volumes = WindowsFileSystem.getLocalVolumes(this.getVolume());
      if (((List)volumes).isEmpty()) {
         String nameToMatch = this.getMount().length() < 2 ? null : this.getMount().substring(0, 2);
         volumes = WindowsFileSystem.getWmiVolumes(nameToMatch, false);
      }

      Iterator var4 = ((List)volumes).iterator();

      OSFileStore fileStore;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         fileStore = (OSFileStore)var4.next();
      } while(!this.getVolume().equals(fileStore.getVolume()) || !this.getMount().equals(fileStore.getMount()));

      this.logicalVolume = fileStore.getLogicalVolume();
      this.description = fileStore.getDescription();
      this.fsType = fileStore.getType();
      this.freeSpace = fileStore.getFreeSpace();
      this.usableSpace = fileStore.getUsableSpace();
      this.totalSpace = fileStore.getTotalSpace();
      this.freeInodes = fileStore.getFreeInodes();
      this.totalInodes = fileStore.getTotalInodes();
      return true;
   }
}
