package oshi.hardware.platform.unix.solaris;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;

@Immutable
final class SolarisFirmware extends AbstractFirmware {
   private final String manufacturer;
   private final String version;
   private final String releaseDate;

   SolarisFirmware(String manufacturer, String version, String releaseDate) {
      this.manufacturer = manufacturer;
      this.version = version;
      this.releaseDate = releaseDate;
   }

   public String getManufacturer() {
      return this.manufacturer;
   }

   public String getVersion() {
      return this.version;
   }

   public String getReleaseDate() {
      return this.releaseDate;
   }
}
