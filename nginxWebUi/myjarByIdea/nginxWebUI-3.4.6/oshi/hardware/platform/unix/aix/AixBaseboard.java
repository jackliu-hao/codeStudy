package oshi.hardware.platform.unix.aix;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.unix.aix.Lscfg;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
final class AixBaseboard extends AbstractBaseboard {
   private static final String IBM = "IBM";
   private final String model;
   private final String serialNumber;
   private final String version;

   AixBaseboard(Supplier<List<String>> lscfg) {
      Triplet<String, String, String> msv = Lscfg.queryBackplaneModelSerialVersion((List)lscfg.get());
      this.model = Util.isBlank((String)msv.getA()) ? "unknown" : (String)msv.getA();
      this.serialNumber = Util.isBlank((String)msv.getB()) ? "unknown" : (String)msv.getB();
      this.version = Util.isBlank((String)msv.getC()) ? "unknown" : (String)msv.getC();
   }

   public String getManufacturer() {
      return "IBM";
   }

   public String getModel() {
      return this.model;
   }

   public String getSerialNumber() {
      return this.serialNumber;
   }

   public String getVersion() {
      return this.version;
   }
}
