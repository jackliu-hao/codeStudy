package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Bios {
   private static final String WIN32_BIOS_WHERE_PRIMARY_BIOS_TRUE = "Win32_BIOS where PrimaryBIOS=true";

   private Win32Bios() {
   }

   public static WbemcliUtil.WmiResult<BiosSerialProperty> querySerialNumber() {
      WbemcliUtil.WmiQuery<BiosSerialProperty> serialNumQuery = new WbemcliUtil.WmiQuery("Win32_BIOS where PrimaryBIOS=true", BiosSerialProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(serialNumQuery);
   }

   public static WbemcliUtil.WmiResult<BiosProperty> queryBiosInfo() {
      WbemcliUtil.WmiQuery<BiosProperty> biosQuery = new WbemcliUtil.WmiQuery("Win32_BIOS where PrimaryBIOS=true", BiosProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(biosQuery);
   }

   public static enum BiosSerialProperty {
      SERIALNUMBER;
   }

   public static enum BiosProperty {
      MANUFACTURER,
      NAME,
      DESCRIPTION,
      VERSION,
      RELEASEDATE;
   }
}
