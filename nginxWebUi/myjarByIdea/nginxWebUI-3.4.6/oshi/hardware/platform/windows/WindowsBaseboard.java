package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32BaseBoard;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Quartet;

@Immutable
final class WindowsBaseboard extends AbstractBaseboard {
   private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(WindowsBaseboard::queryManufModelVersSerial);

   public String getManufacturer() {
      return (String)((Quartet)this.manufModelVersSerial.get()).getA();
   }

   public String getModel() {
      return (String)((Quartet)this.manufModelVersSerial.get()).getB();
   }

   public String getVersion() {
      return (String)((Quartet)this.manufModelVersSerial.get()).getC();
   }

   public String getSerialNumber() {
      return (String)((Quartet)this.manufModelVersSerial.get()).getD();
   }

   private static Quartet<String, String, String, String> queryManufModelVersSerial() {
      String manufacturer = null;
      String model = null;
      String version = null;
      String serialNumber = null;
      WbemcliUtil.WmiResult<Win32BaseBoard.BaseBoardProperty> win32BaseBoard = Win32BaseBoard.queryBaseboardInfo();
      if (win32BaseBoard.getResultCount() > 0) {
         manufacturer = WmiUtil.getString(win32BaseBoard, Win32BaseBoard.BaseBoardProperty.MANUFACTURER, 0);
         model = WmiUtil.getString(win32BaseBoard, Win32BaseBoard.BaseBoardProperty.MODEL, 0);
         version = WmiUtil.getString(win32BaseBoard, Win32BaseBoard.BaseBoardProperty.VERSION, 0);
         serialNumber = WmiUtil.getString(win32BaseBoard, Win32BaseBoard.BaseBoardProperty.SERIALNUMBER, 0);
      }

      return new Quartet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, Util.isBlank(model) ? "unknown" : model, Util.isBlank(version) ? "unknown" : version, Util.isBlank(serialNumber) ? "unknown" : serialNumber);
   }
}
