package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.linux.Devicetree;
import oshi.driver.linux.Dmidecode;
import oshi.driver.linux.Lshal;
import oshi.driver.linux.Lshw;
import oshi.driver.linux.Sysfs;
import oshi.driver.linux.proc.CpuInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;

@Immutable
final class LinuxComputerSystem extends AbstractComputerSystem {
   private final Supplier<String> manufacturer = Memoizer.memoize(LinuxComputerSystem::queryManufacturer);
   private final Supplier<String> model = Memoizer.memoize(LinuxComputerSystem::queryModel);
   private final Supplier<String> serialNumber = Memoizer.memoize(LinuxComputerSystem::querySerialNumber);

   public String getManufacturer() {
      return (String)this.manufacturer.get();
   }

   public String getModel() {
      return (String)this.model.get();
   }

   public String getSerialNumber() {
      return (String)this.serialNumber.get();
   }

   public Firmware createFirmware() {
      return new LinuxFirmware();
   }

   public Baseboard createBaseboard() {
      return new LinuxBaseboard();
   }

   private static String queryManufacturer() {
      String result = null;
      return (result = Sysfs.querySystemVendor()) == null && (result = CpuInfo.queryCpuManufacturer()) == null ? "unknown" : result;
   }

   private static String queryModel() {
      String result = null;
      return (result = Sysfs.queryProductModel()) == null && (result = Devicetree.queryModel()) == null && (result = Lshw.queryModel()) == null ? "unknown" : result;
   }

   private static String querySerialNumber() {
      String result = null;
      return (result = Sysfs.queryProductSerial()) == null && (result = Dmidecode.querySerialNumber()) == null && (result = Lshal.querySerialNumber()) == null && (result = Lshw.querySerialNumber()) == null ? "unknown" : result;
   }
}
