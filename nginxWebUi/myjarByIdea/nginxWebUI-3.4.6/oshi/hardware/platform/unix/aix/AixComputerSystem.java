package oshi.hardware.platform.unix.aix;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
final class AixComputerSystem extends AbstractComputerSystem {
   private final Supplier<LsattrStrings> lsattrStrings = Memoizer.memoize(AixComputerSystem::readLsattr);
   private final Supplier<List<String>> lscfg;

   AixComputerSystem(Supplier<List<String>> lscfg) {
      this.lscfg = lscfg;
   }

   public String getManufacturer() {
      return ((LsattrStrings)this.lsattrStrings.get()).manufacturer;
   }

   public String getModel() {
      return ((LsattrStrings)this.lsattrStrings.get()).model;
   }

   public String getSerialNumber() {
      return ((LsattrStrings)this.lsattrStrings.get()).serialNumber;
   }

   public Firmware createFirmware() {
      return new AixFirmware(((LsattrStrings)this.lsattrStrings.get()).biosVendor, ((LsattrStrings)this.lsattrStrings.get()).biosPlatformVersion, ((LsattrStrings)this.lsattrStrings.get()).biosVersion);
   }

   public Baseboard createBaseboard() {
      return new AixBaseboard(this.lscfg);
   }

   private static LsattrStrings readLsattr() {
      String fwVendor = "IBM";
      String fwVersion = null;
      String fwPlatformVersion = null;
      String manufacturer = fwVendor;
      String model = null;
      String serialNumber = null;
      String fwVersionMarker = "fwversion";
      String modelMarker = "modelname";
      String systemIdMarker = "systemid";
      String fwPlatformVersionMarker = "Platform Firmware level is";
      Iterator var10 = ExecutingCommand.runNative("lsattr -El sys0").iterator();

      String checkLine;
      while(var10.hasNext()) {
         checkLine = (String)var10.next();
         int comma;
         if (checkLine.startsWith("fwversion")) {
            fwVersion = checkLine.split("fwversion")[1].trim();
            comma = fwVersion.indexOf(44);
            if (comma > 0 && fwVersion.length() > comma) {
               fwVendor = fwVersion.substring(0, comma);
               fwVersion = fwVersion.substring(comma + 1);
            }

            fwVersion = ParseUtil.whitespaces.split(fwVersion)[0];
         } else if (checkLine.startsWith("modelname")) {
            model = checkLine.split("modelname")[1].trim();
            comma = model.indexOf(44);
            if (comma > 0 && model.length() > comma) {
               manufacturer = model.substring(0, comma);
               model = model.substring(comma + 1);
            }

            model = ParseUtil.whitespaces.split(model)[0];
         } else if (checkLine.startsWith("systemid")) {
            serialNumber = checkLine.split("systemid")[1].trim();
            serialNumber = ParseUtil.whitespaces.split(serialNumber)[0];
         }
      }

      var10 = ExecutingCommand.runNative("lsmcode -c").iterator();

      while(var10.hasNext()) {
         checkLine = (String)var10.next();
         if (checkLine.startsWith("Platform Firmware level is")) {
            fwPlatformVersion = checkLine.split("Platform Firmware level is")[1].trim();
            break;
         }
      }

      return new LsattrStrings(fwVendor, fwPlatformVersion, fwVersion, manufacturer, model, serialNumber);
   }

   private static final class LsattrStrings {
      private final String biosVendor;
      private final String biosPlatformVersion;
      private final String biosVersion;
      private final String manufacturer;
      private final String model;
      private final String serialNumber;

      private LsattrStrings(String biosVendor, String biosPlatformVersion, String biosVersion, String manufacturer, String model, String serialNumber) {
         this.biosVendor = Util.isBlank(biosVendor) ? "unknown" : biosVendor;
         this.biosPlatformVersion = Util.isBlank(biosPlatformVersion) ? "unknown" : biosPlatformVersion;
         this.biosVersion = Util.isBlank(biosVersion) ? "unknown" : biosVersion;
         this.manufacturer = Util.isBlank(manufacturer) ? "unknown" : manufacturer;
         this.model = Util.isBlank(model) ? "unknown" : model;
         this.serialNumber = Util.isBlank(serialNumber) ? "unknown" : serialNumber;
      }

      // $FF: synthetic method
      LsattrStrings(String x0, String x1, String x2, String x3, String x4, String x5, Object x6) {
         this(x0, x1, x2, x3, x4, x5);
      }
   }
}
