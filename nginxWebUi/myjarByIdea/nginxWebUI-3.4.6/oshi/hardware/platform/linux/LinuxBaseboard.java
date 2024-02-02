package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.linux.Sysfs;
import oshi.driver.linux.proc.CpuInfo;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.tuples.Quartet;

@Immutable
final class LinuxBaseboard extends AbstractBaseboard {
   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
   private final Supplier<String> model = Memoizer.memoize(this::queryModel);
   private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
   private final Supplier<String> serialNumber = Memoizer.memoize(this::querySerialNumber);
   private final Supplier<Quartet<String, String, String, String>> manufacturerModelVersionSerial = Memoizer.memoize(CpuInfo::queryBoardInfo);

   public String getManufacturer() {
      return (String)this.manufacturer.get();
   }

   public String getModel() {
      return (String)this.model.get();
   }

   public String getVersion() {
      return (String)this.version.get();
   }

   public String getSerialNumber() {
      return (String)this.serialNumber.get();
   }

   private String queryManufacturer() {
      String result = null;
      return (result = Sysfs.queryBoardVendor()) == null && (result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getA()) == null ? "unknown" : result;
   }

   private String queryModel() {
      String result = null;
      return (result = Sysfs.queryBoardModel()) == null && (result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getB()) == null ? "unknown" : result;
   }

   private String queryVersion() {
      String result = null;
      return (result = Sysfs.queryBoardVersion()) == null && (result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getC()) == null ? "unknown" : result;
   }

   private String querySerialNumber() {
      String result = null;
      return (result = Sysfs.queryBoardSerial()) == null && (result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getD()) == null ? "unknown" : result;
   }
}
