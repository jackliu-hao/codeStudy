package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32PnPEntity {
   private static final String WIN32_PNP_ENTITY = "Win32_PnPEntity";

   private Win32PnPEntity() {
   }

   public static WbemcliUtil.WmiResult<PnPEntityProperty> queryDeviceId(String whereClause) {
      WbemcliUtil.WmiQuery<PnPEntityProperty> pnpEntityQuery = new WbemcliUtil.WmiQuery("Win32_PnPEntity" + whereClause, PnPEntityProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(pnpEntityQuery);
   }

   public static enum PnPEntityProperty {
      NAME,
      MANUFACTURER,
      PNPDEVICEID;
   }
}
