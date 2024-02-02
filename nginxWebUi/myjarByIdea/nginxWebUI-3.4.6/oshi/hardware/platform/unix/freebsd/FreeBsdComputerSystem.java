package oshi.hardware.platform.unix.freebsd;

import java.util.Iterator;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Quartet;

@Immutable
final class FreeBsdComputerSystem extends AbstractComputerSystem {
   private final Supplier<Quartet<String, String, String, String>> manufModelSerialVers = Memoizer.memoize(FreeBsdComputerSystem::readDmiDecode);

   public String getManufacturer() {
      return (String)((Quartet)this.manufModelSerialVers.get()).getA();
   }

   public String getModel() {
      return (String)((Quartet)this.manufModelSerialVers.get()).getB();
   }

   public String getSerialNumber() {
      return (String)((Quartet)this.manufModelSerialVers.get()).getC();
   }

   public Firmware createFirmware() {
      return new FreeBsdFirmware();
   }

   public Baseboard createBaseboard() {
      return new FreeBsdBaseboard((String)((Quartet)this.manufModelSerialVers.get()).getA(), (String)((Quartet)this.manufModelSerialVers.get()).getB(), (String)((Quartet)this.manufModelSerialVers.get()).getC(), (String)((Quartet)this.manufModelSerialVers.get()).getD());
   }

   private static Quartet<String, String, String, String> readDmiDecode() {
      String manufacturer = null;
      String model = null;
      String serialNumber = null;
      String version = null;
      String manufacturerMarker = "Manufacturer:";
      String productNameMarker = "Product Name:";
      String serialNumMarker = "Serial Number:";
      String versionMarker = "Version:";
      Iterator var8 = ExecutingCommand.runNative("dmidecode -t system").iterator();

      while(var8.hasNext()) {
         String checkLine = (String)var8.next();
         if (checkLine.contains("Manufacturer:")) {
            manufacturer = checkLine.split("Manufacturer:")[1].trim();
         } else if (checkLine.contains("Product Name:")) {
            model = checkLine.split("Product Name:")[1].trim();
         } else if (checkLine.contains("Serial Number:")) {
            serialNumber = checkLine.split("Serial Number:")[1].trim();
         } else if (checkLine.contains("Version:")) {
            version = checkLine.split("Version:")[1].trim();
         }
      }

      if (Util.isBlank(serialNumber)) {
         serialNumber = querySystemSerialNumber();
      }

      return new Quartet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, Util.isBlank(model) ? "unknown" : model, Util.isBlank(serialNumber) ? "unknown" : serialNumber, Util.isBlank(version) ? "unknown" : version);
   }

   private static String querySystemSerialNumber() {
      String marker = "system.hardware.serial =";
      Iterator var1 = ExecutingCommand.runNative("lshal").iterator();

      String checkLine;
      do {
         if (!var1.hasNext()) {
            return "unknown";
         }

         checkLine = (String)var1.next();
      } while(!checkLine.contains(marker));

      return ParseUtil.getSingleQuoteStringValue(checkLine);
   }
}
