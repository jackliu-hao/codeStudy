package oshi.hardware.platform.unix.solaris;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;

@Immutable
final class SolarisBaseboard extends AbstractBaseboard {
   private final String manufacturer;
   private final String model;
   private final String serialNumber;
   private final String version;

   SolarisBaseboard(String manufacturer, String model, String serialNumber, String version) {
      this.manufacturer = manufacturer;
      this.model = model;
      this.serialNumber = serialNumber;
      this.version = version;
   }

   public String getManufacturer() {
      return this.manufacturer;
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
