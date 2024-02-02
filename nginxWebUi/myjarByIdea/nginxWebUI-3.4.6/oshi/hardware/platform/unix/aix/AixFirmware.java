package oshi.hardware.platform.unix.aix;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;

@Immutable
final class AixFirmware extends AbstractFirmware {
   private final String manufacturer;
   private final String name;
   private final String version;

   AixFirmware(String manufacturer, String name, String version) {
      this.manufacturer = manufacturer;
      this.name = name;
      this.version = version;
   }

   public String getManufacturer() {
      return this.manufacturer;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }
}
