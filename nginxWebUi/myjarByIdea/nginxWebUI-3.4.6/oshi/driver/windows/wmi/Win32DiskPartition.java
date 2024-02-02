package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskPartition {
   private static final String WIN32_DISK_PARTITION = "Win32_DiskPartition";

   private Win32DiskPartition() {
   }

   public static WbemcliUtil.WmiResult<DiskPartitionProperty> queryPartition() {
      WbemcliUtil.WmiQuery<DiskPartitionProperty> partitionQuery = new WbemcliUtil.WmiQuery("Win32_DiskPartition", DiskPartitionProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(partitionQuery);
   }

   public static enum DiskPartitionProperty {
      INDEX,
      DESCRIPTION,
      DEVICEID,
      DISKINDEX,
      NAME,
      SIZE,
      TYPE;
   }
}
