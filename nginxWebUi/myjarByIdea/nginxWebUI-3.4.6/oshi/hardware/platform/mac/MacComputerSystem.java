package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
final class MacComputerSystem extends AbstractComputerSystem {
   private final Supplier<Triplet<String, String, String>> manufacturerModelSerial = Memoizer.memoize(MacComputerSystem::platformExpert);

   public String getManufacturer() {
      return (String)((Triplet)this.manufacturerModelSerial.get()).getA();
   }

   public String getModel() {
      return (String)((Triplet)this.manufacturerModelSerial.get()).getB();
   }

   public String getSerialNumber() {
      return (String)((Triplet)this.manufacturerModelSerial.get()).getC();
   }

   public Firmware createFirmware() {
      return new MacFirmware();
   }

   public Baseboard createBaseboard() {
      return new MacBaseboard();
   }

   private static Triplet<String, String, String> platformExpert() {
      String manufacturer = null;
      String model = null;
      String serialNumber = null;
      IOKit.IORegistryEntry platformExpert = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (platformExpert != null) {
         byte[] data = platformExpert.getByteArrayProperty("manufacturer");
         if (data != null) {
            manufacturer = (new String(data, StandardCharsets.UTF_8)).trim();
         }

         data = platformExpert.getByteArrayProperty("model");
         if (data != null) {
            model = (new String(data, StandardCharsets.UTF_8)).trim();
         }

         serialNumber = platformExpert.getStringProperty("IOPlatformSerialNumber");
         platformExpert.release();
      }

      return new Triplet(Util.isBlank(manufacturer) ? "Apple Inc." : manufacturer, Util.isBlank(model) ? "unknown" : model, Util.isBlank(serialNumber) ? "unknown" : serialNumber);
   }
}
