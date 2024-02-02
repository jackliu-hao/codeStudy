package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDriveToDiskPartition {
   private static final String WIN32_DISK_DRIVE_TO_DISK_PARTITION = "Win32_DiskDriveToDiskPartition";

   private Win32DiskDriveToDiskPartition() {
   }

   public static WbemcliUtil.WmiResult<DriveToPartitionProperty> queryDriveToPartition() {
      WbemcliUtil.WmiQuery<DriveToPartitionProperty> driveToPartitionQuery = new WbemcliUtil.WmiQuery("Win32_DiskDriveToDiskPartition", DriveToPartitionProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(driveToPartitionQuery);
   }

   public static enum DriveToPartitionProperty {
      ANTECEDENT,
      DEPENDENT;
   }
}
