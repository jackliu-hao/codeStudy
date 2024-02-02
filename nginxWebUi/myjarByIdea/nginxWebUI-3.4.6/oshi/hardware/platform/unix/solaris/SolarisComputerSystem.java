package oshi.hardware.platform.unix.solaris;

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

@Immutable
final class SolarisComputerSystem extends AbstractComputerSystem {
   private final Supplier<SmbiosStrings> smbiosStrings = Memoizer.memoize(SolarisComputerSystem::readSmbios);

   public String getManufacturer() {
      return ((SmbiosStrings)this.smbiosStrings.get()).manufacturer;
   }

   public String getModel() {
      return ((SmbiosStrings)this.smbiosStrings.get()).model;
   }

   public String getSerialNumber() {
      return ((SmbiosStrings)this.smbiosStrings.get()).serialNumber;
   }

   public Firmware createFirmware() {
      return new SolarisFirmware(((SmbiosStrings)this.smbiosStrings.get()).biosVendor, ((SmbiosStrings)this.smbiosStrings.get()).biosVersion, ((SmbiosStrings)this.smbiosStrings.get()).biosDate);
   }

   public Baseboard createBaseboard() {
      return new SolarisBaseboard(((SmbiosStrings)this.smbiosStrings.get()).boardManufacturer, ((SmbiosStrings)this.smbiosStrings.get()).boardModel, ((SmbiosStrings)this.smbiosStrings.get()).boardSerialNumber, ((SmbiosStrings)this.smbiosStrings.get()).boardVersion);
   }

   private static SmbiosStrings readSmbios() {
      String biosVendor = null;
      String biosVersion = null;
      String biosDate = null;
      String manufacturer = null;
      String model = null;
      String serialNumber = null;
      String boardManufacturer = null;
      String boardModel = null;
      String boardVersion = null;
      String boardSerialNumber = null;
      String vendorMarker = "Vendor:";
      String biosDateMarker = "Release Date:";
      String biosVersionMarker = "VersionString:";
      String manufacturerMarker = "Manufacturer:";
      String productMarker = "Product:";
      String serialNumMarker = "Serial Number:";
      String versionMarker = "Version:";
      int smbTypeId = -1;
      Iterator var18 = ExecutingCommand.runNative("smbios").iterator();

      while(var18.hasNext()) {
         String checkLine = (String)var18.next();
         if (checkLine.contains("SMB_TYPE_") && (smbTypeId = getSmbType(checkLine)) == Integer.MAX_VALUE) {
            break;
         }

         switch (smbTypeId) {
            case 0:
               if (checkLine.contains("Vendor:")) {
                  biosVendor = checkLine.split("Vendor:")[1].trim();
               } else if (checkLine.contains("VersionString:")) {
                  biosVersion = checkLine.split("VersionString:")[1].trim();
               } else if (checkLine.contains("Release Date:")) {
                  biosDate = checkLine.split("Release Date:")[1].trim();
               }
               break;
            case 1:
               if (checkLine.contains("Manufacturer:")) {
                  manufacturer = checkLine.split("Manufacturer:")[1].trim();
               } else if (checkLine.contains("Product:")) {
                  model = checkLine.split("Product:")[1].trim();
               } else if (checkLine.contains("Serial Number:")) {
                  serialNumber = checkLine.split("Serial Number:")[1].trim();
               }
               break;
            case 2:
               if (checkLine.contains("Manufacturer:")) {
                  boardManufacturer = checkLine.split("Manufacturer:")[1].trim();
               } else if (checkLine.contains("Product:")) {
                  boardModel = checkLine.split("Product:")[1].trim();
               } else if (checkLine.contains("Version:")) {
                  boardVersion = checkLine.split("Version:")[1].trim();
               } else if (checkLine.contains("Serial Number:")) {
                  boardSerialNumber = checkLine.split("Serial Number:")[1].trim();
               }
         }
      }

      if (Util.isBlank(serialNumber)) {
         serialNumber = readSerialNumber();
      }

      return new SmbiosStrings(biosVendor, biosVersion, biosDate, manufacturer, model, serialNumber, boardManufacturer, boardModel, boardVersion, boardSerialNumber);
   }

   private static int getSmbType(String checkLine) {
      if (checkLine.contains("SMB_TYPE_BIOS")) {
         return 0;
      } else if (checkLine.contains("SMB_TYPE_SYSTEM")) {
         return 1;
      } else {
         return checkLine.contains("SMB_TYPE_BASEBOARD") ? 2 : Integer.MAX_VALUE;
      }
   }

   private static String readSerialNumber() {
      String serialNumber = ExecutingCommand.getFirstAnswer("sneep");
      if (serialNumber.isEmpty()) {
         String marker = "chassis-sn:";
         Iterator var2 = ExecutingCommand.runNative("prtconf -pv").iterator();

         while(var2.hasNext()) {
            String checkLine = (String)var2.next();
            if (checkLine.contains(marker)) {
               serialNumber = ParseUtil.getSingleQuoteStringValue(checkLine);
               break;
            }
         }
      }

      return serialNumber;
   }

   private static final class SmbiosStrings {
      private final String biosVendor;
      private final String biosVersion;
      private final String biosDate;
      private final String manufacturer;
      private final String model;
      private final String serialNumber;
      private final String boardManufacturer;
      private final String boardModel;
      private final String boardVersion;
      private final String boardSerialNumber;

      private SmbiosStrings(String biosVendor, String biosVersion, String biosDate, String manufacturer, String model, String serialNumber, String boardManufacturer, String boardModel, String boardVersion, String boardSerialNumber) {
         this.biosVendor = Util.isBlank(biosVendor) ? "unknown" : biosVendor;
         this.biosVersion = Util.isBlank(biosVersion) ? "unknown" : biosVersion;
         this.biosDate = Util.isBlank(biosDate) ? "unknown" : biosDate;
         this.manufacturer = Util.isBlank(manufacturer) ? "unknown" : manufacturer;
         this.model = Util.isBlank(model) ? "unknown" : model;
         this.serialNumber = Util.isBlank(serialNumber) ? "unknown" : serialNumber;
         this.boardManufacturer = Util.isBlank(boardManufacturer) ? "unknown" : boardManufacturer;
         this.boardModel = Util.isBlank(boardModel) ? "unknown" : boardModel;
         this.boardVersion = Util.isBlank(boardVersion) ? "unknown" : boardVersion;
         this.boardSerialNumber = Util.isBlank(boardSerialNumber) ? "unknown" : boardSerialNumber;
      }

      // $FF: synthetic method
      SmbiosStrings(String x0, String x1, String x2, String x3, String x4, String x5, String x6, String x7, String x8, String x9, Object x10) {
         this(x0, x1, x2, x3, x4, x5, x6, x7, x8, x9);
      }
   }
}
