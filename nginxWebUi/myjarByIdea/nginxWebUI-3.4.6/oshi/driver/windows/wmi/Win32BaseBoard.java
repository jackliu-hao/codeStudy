package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32BaseBoard {
   private static final String WIN32_BASEBOARD = "Win32_BaseBoard";

   private Win32BaseBoard() {
   }

   public static WbemcliUtil.WmiResult<BaseBoardProperty> queryBaseboardInfo() {
      WbemcliUtil.WmiQuery<BaseBoardProperty> baseboardQuery = new WbemcliUtil.WmiQuery("Win32_BaseBoard", BaseBoardProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(baseboardQuery);
   }

   public static enum BaseBoardProperty {
      MANUFACTURER,
      MODEL,
      VERSION,
      SERIALNUMBER;
   }
}
