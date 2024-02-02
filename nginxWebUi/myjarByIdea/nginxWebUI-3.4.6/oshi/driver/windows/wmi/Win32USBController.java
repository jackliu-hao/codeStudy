package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32USBController {
   private static final String WIN32_USB_CONTROLLER = "Win32_USBController";

   private Win32USBController() {
   }

   public static WbemcliUtil.WmiResult<USBControllerProperty> queryUSBControllers() {
      WbemcliUtil.WmiQuery<USBControllerProperty> usbControllerQuery = new WbemcliUtil.WmiQuery("Win32_USBController", USBControllerProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(usbControllerQuery);
   }

   public static enum USBControllerProperty {
      PNPDEVICEID;
   }
}
