package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quartet;

@Immutable
final class MacBaseboard extends AbstractBaseboard {
   private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(MacBaseboard::queryPlatform);

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

   private static Quartet<String, String, String, String> queryPlatform() {
      String manufacturer = null;
      String model = null;
      String version = null;
      String serialNumber = null;
      IOKit.IORegistryEntry platformExpert = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (platformExpert != null) {
         byte[] data = platformExpert.getByteArrayProperty("manufacturer");
         if (data != null) {
            manufacturer = (new String(data, StandardCharsets.UTF_8)).trim();
         }

         data = platformExpert.getByteArrayProperty("board-id");
         if (data != null) {
            model = (new String(data, StandardCharsets.UTF_8)).trim();
         }

         data = platformExpert.getByteArrayProperty("version");
         if (data != null) {
            version = (new String(data, StandardCharsets.UTF_8)).trim();
         }

         serialNumber = platformExpert.getStringProperty("IOPlatformSerialNumber");
         platformExpert.release();
      }

      return new Quartet(Util.isBlank(manufacturer) ? "Apple Inc." : manufacturer, Util.isBlank(model) ? "unknown" : model, Util.isBlank(version) ? "unknown" : version, Util.isBlank(serialNumber) ? "unknown" : serialNumber);
   }
}
