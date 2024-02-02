package oshi.hardware.platform.unix.freebsd;

import java.util.Iterator;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
final class FreeBsdFirmware extends AbstractFirmware {
   private final Supplier<Triplet<String, String, String>> manufVersRelease = Memoizer.memoize(FreeBsdFirmware::readDmiDecode);

   public String getManufacturer() {
      return (String)((Triplet)this.manufVersRelease.get()).getA();
   }

   public String getVersion() {
      return (String)((Triplet)this.manufVersRelease.get()).getB();
   }

   public String getReleaseDate() {
      return (String)((Triplet)this.manufVersRelease.get()).getC();
   }

   private static Triplet<String, String, String> readDmiDecode() {
      String manufacturer = null;
      String version = null;
      String releaseDate = "";
      String manufacturerMarker = "Vendor:";
      String versionMarker = "Version:";
      String releaseDateMarker = "Release Date:";
      Iterator var6 = ExecutingCommand.runNative("dmidecode -t bios").iterator();

      while(var6.hasNext()) {
         String checkLine = (String)var6.next();
         if (checkLine.contains("Vendor:")) {
            manufacturer = checkLine.split("Vendor:")[1].trim();
         } else if (checkLine.contains("Version:")) {
            version = checkLine.split("Version:")[1].trim();
         } else if (checkLine.contains("Release Date:")) {
            releaseDate = checkLine.split("Release Date:")[1].trim();
         }
      }

      releaseDate = ParseUtil.parseMmDdYyyyToYyyyMmDD(releaseDate);
      return new Triplet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, Util.isBlank(version) ? "unknown" : version, Util.isBlank(releaseDate) ? "unknown" : releaseDate);
   }
}
