package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDrive {
   private static final String WIN32_DISK_DRIVE = "Win32_DiskDrive";

   private Win32DiskDrive() {
   }

   public static WbemcliUtil.WmiResult<DiskDriveProperty> queryDiskDrive() {
      WbemcliUtil.WmiQuery<DiskDriveProperty> diskDriveQuery = new WbemcliUtil.WmiQuery("Win32_DiskDrive", DiskDriveProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(diskDriveQuery);
   }

   public static WbemcliUtil.WmiResult<DeviceIdProperty> queryDiskDriveId(String whereClause) {
      WbemcliUtil.WmiQuery<DeviceIdProperty> deviceIdQuery = new WbemcliUtil.WmiQuery("Win32_DiskDrive" + whereClause, DeviceIdProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(deviceIdQuery);
   }

   public static enum DiskDriveProperty {
      INDEX,
      MANUFACTURER,
      MODEL,
      NAME,
      SERIALNUMBER,
      SIZE;
   }

   public static enum DeviceIdProperty {
      PNPDEVICEID,
      SERIALNUMBER;
   }
}
