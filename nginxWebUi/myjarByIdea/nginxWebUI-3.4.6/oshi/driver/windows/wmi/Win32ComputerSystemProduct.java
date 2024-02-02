package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32ComputerSystemProduct {
   private static final String WIN32_COMPUTER_SYSTEM_PRODUCT = "Win32_ComputerSystemProduct";

   private Win32ComputerSystemProduct() {
   }

   public static WbemcliUtil.WmiResult<ComputerSystemProductProperty> queryIdentifyingNumber() {
      WbemcliUtil.WmiQuery<ComputerSystemProductProperty> identifyingNumberQuery = new WbemcliUtil.WmiQuery("Win32_ComputerSystemProduct", ComputerSystemProductProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(identifyingNumberQuery);
   }

   public static enum ComputerSystemProductProperty {
      IDENTIFYINGNUMBER;
   }
}
