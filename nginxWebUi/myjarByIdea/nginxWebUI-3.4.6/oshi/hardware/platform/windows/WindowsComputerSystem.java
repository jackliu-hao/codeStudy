package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32Bios;
import oshi.driver.windows.wmi.Win32ComputerSystem;
import oshi.driver.windows.wmi.Win32ComputerSystemProduct;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@Immutable
final class WindowsComputerSystem extends AbstractComputerSystem {
   private final Supplier<Pair<String, String>> manufacturerModel = Memoizer.memoize(WindowsComputerSystem::queryManufacturerModel);
   private final Supplier<String> serialNumber = Memoizer.memoize(WindowsComputerSystem::querySystemSerialNumber);

   public String getManufacturer() {
      return (String)((Pair)this.manufacturerModel.get()).getA();
   }

   public String getModel() {
      return (String)((Pair)this.manufacturerModel.get()).getB();
   }

   public String getSerialNumber() {
      return (String)this.serialNumber.get();
   }

   public Firmware createFirmware() {
      return new WindowsFirmware();
   }

   public Baseboard createBaseboard() {
      return new WindowsBaseboard();
   }

   private static Pair<String, String> queryManufacturerModel() {
      String manufacturer = null;
      String model = null;
      WbemcliUtil.WmiResult<Win32ComputerSystem.ComputerSystemProperty> win32ComputerSystem = Win32ComputerSystem.queryComputerSystem();
      if (win32ComputerSystem.getResultCount() > 0) {
         manufacturer = WmiUtil.getString(win32ComputerSystem, Win32ComputerSystem.ComputerSystemProperty.MANUFACTURER, 0);
         model = WmiUtil.getString(win32ComputerSystem, Win32ComputerSystem.ComputerSystemProperty.MODEL, 0);
      }

      return new Pair(Util.isBlank(manufacturer) ? "unknown" : manufacturer, Util.isBlank(model) ? "unknown" : model);
   }

   private static String querySystemSerialNumber() {
      String result;
      return ((result = querySerialFromBios()) != null || (result = querySerialFromCsProduct()) != null) && !Util.isBlank(result) ? result : "unknown";
   }

   private static String querySerialFromBios() {
      WbemcliUtil.WmiResult<Win32Bios.BiosSerialProperty> serialNum = Win32Bios.querySerialNumber();
      return serialNum.getResultCount() > 0 ? WmiUtil.getString(serialNum, Win32Bios.BiosSerialProperty.SERIALNUMBER, 0) : null;
   }

   private static String querySerialFromCsProduct() {
      WbemcliUtil.WmiResult<Win32ComputerSystemProduct.ComputerSystemProductProperty> identifyingNumber = Win32ComputerSystemProduct.queryIdentifyingNumber();
      return identifyingNumber.getResultCount() > 0 ? WmiUtil.getString(identifyingNumber, Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0) : null;
   }
}
