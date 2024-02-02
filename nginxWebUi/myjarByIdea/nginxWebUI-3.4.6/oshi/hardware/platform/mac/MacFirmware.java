package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quintet;

@Immutable
final class MacFirmware extends AbstractFirmware {
   private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(MacFirmware::queryEfi);

   public String getManufacturer() {
      return (String)((Quintet)this.manufNameDescVersRelease.get()).getA();
   }

   public String getName() {
      return (String)((Quintet)this.manufNameDescVersRelease.get()).getB();
   }

   public String getDescription() {
      return (String)((Quintet)this.manufNameDescVersRelease.get()).getC();
   }

   public String getVersion() {
      return (String)((Quintet)this.manufNameDescVersRelease.get()).getD();
   }

   public String getReleaseDate() {
      return (String)((Quintet)this.manufNameDescVersRelease.get()).getE();
   }

   private static Quintet<String, String, String, String, String> queryEfi() {
      String manufacturer = null;
      String name = null;
      String description = null;
      String version = null;
      String releaseDate = null;
      IOKit.IORegistryEntry platformExpert = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (platformExpert != null) {
         IOKit.IOIterator iter = platformExpert.getChildIterator("IODeviceTree");
         if (iter != null) {
            for(IOKit.IORegistryEntry entry = iter.next(); entry != null; entry = iter.next()) {
               byte[] data;
               switch (entry.getName()) {
                  case "rom":
                     data = entry.getByteArrayProperty("vendor");
                     if (data != null) {
                        manufacturer = (new String(data, StandardCharsets.UTF_8)).trim();
                     }

                     data = entry.getByteArrayProperty("version");
                     if (data != null) {
                        version = (new String(data, StandardCharsets.UTF_8)).trim();
                     }

                     data = entry.getByteArrayProperty("release-date");
                     if (data != null) {
                        releaseDate = (new String(data, StandardCharsets.UTF_8)).trim();
                     }
                     break;
                  case "chosen":
                     data = entry.getByteArrayProperty("booter-name");
                     if (data != null) {
                        name = (new String(data, StandardCharsets.UTF_8)).trim();
                     }
                     break;
                  case "efi":
                     data = entry.getByteArrayProperty("firmware-abi");
                     if (data != null) {
                        description = (new String(data, StandardCharsets.UTF_8)).trim();
                     }
               }

               entry.release();
            }

            iter.release();
         }

         platformExpert.release();
      }

      return new Quintet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, Util.isBlank(name) ? "unknown" : name, Util.isBlank(description) ? "unknown" : description, Util.isBlank(version) ? "unknown" : version, Util.isBlank(releaseDate) ? "unknown" : releaseDate);
   }
}
