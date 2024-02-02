package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.ptr.PointerByReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;

@ThreadSafe
public final class MacPowerSource extends AbstractPowerSource {
   private static final CoreFoundation CF;
   private static final IOKit IO;

   public MacPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
      super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
   }

   public static List<PowerSource> getPowerSources() {
      String psDeviceName = "unknown";
      double psTimeRemainingInstant = 0.0;
      double psPowerUsageRate = 0.0;
      double psVoltage = -1.0;
      double psAmperage = 0.0;
      boolean psPowerOnLine = false;
      boolean psCharging = false;
      boolean psDischarging = false;
      PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
      int psCurrentCapacity = 0;
      int psMaxCapacity = 1;
      int psDesignCapacity = 1;
      int psCycleCount = -1;
      String psChemistry = "unknown";
      LocalDate psManufactureDate = null;
      String psManufacturer = "unknown";
      String psSerialNumber = "unknown";
      double psTemperature = 0.0;
      IOKit.IORegistryEntry smartBattery = IOKitUtil.getMatchingService("AppleSmartBattery");
      int day;
      if (smartBattery != null) {
         String s = smartBattery.getStringProperty("DeviceName");
         if (s != null) {
            psDeviceName = s;
         }

         s = smartBattery.getStringProperty("Manufacturer");
         if (s != null) {
            psManufacturer = s;
         }

         s = smartBattery.getStringProperty("BatterySerialNumber");
         if (s != null) {
            psSerialNumber = s;
         }

         Integer temp = smartBattery.getIntegerProperty("ManufactureDate");
         if (temp != null) {
            day = temp & 31;
            int month = temp >> 5 & 15;
            int year80 = temp >> 9 & 127;
            psManufactureDate = LocalDate.of(1980 + year80, month, day);
         }

         temp = smartBattery.getIntegerProperty("DesignCapacity");
         if (temp != null) {
            psDesignCapacity = temp;
         }

         temp = smartBattery.getIntegerProperty("MaxCapacity");
         if (temp != null) {
            psMaxCapacity = temp;
         }

         temp = smartBattery.getIntegerProperty("CurrentCapacity");
         if (temp != null) {
            psCurrentCapacity = temp;
         }

         psCapacityUnits = PowerSource.CapacityUnits.MAH;
         temp = smartBattery.getIntegerProperty("TimeRemaining");
         if (temp != null) {
            psTimeRemainingInstant = (double)temp * 60.0;
         }

         temp = smartBattery.getIntegerProperty("CycleCount");
         if (temp != null) {
            psCycleCount = temp;
         }

         temp = smartBattery.getIntegerProperty("Temperature");
         if (temp != null) {
            psTemperature = (double)temp / 100.0;
         }

         temp = smartBattery.getIntegerProperty("Voltage");
         if (temp != null) {
            psVoltage = (double)temp / 1000.0;
         }

         temp = smartBattery.getIntegerProperty("Amperage");
         if (temp != null) {
            psAmperage = (double)temp;
         }

         psPowerUsageRate = psVoltage * psAmperage;
         Boolean bool = smartBattery.getBooleanProperty("ExternalConnected");
         if (bool != null) {
            psPowerOnLine = bool;
         }

         bool = smartBattery.getBooleanProperty("IsCharging");
         if (bool != null) {
            psCharging = bool;
         }

         psDischarging = !psCharging;
         smartBattery.release();
      }

      CoreFoundation.CFTypeRef powerSourcesInfo = IO.IOPSCopyPowerSourcesInfo();
      CoreFoundation.CFArrayRef powerSourcesList = IO.IOPSCopyPowerSourcesList(powerSourcesInfo);
      day = powerSourcesList.getCount();
      double psTimeRemainingEstimated = IO.IOPSGetTimeRemainingEstimate();
      CoreFoundation.CFStringRef nameKey = CoreFoundation.CFStringRef.createCFString("Name");
      CoreFoundation.CFStringRef isPresentKey = CoreFoundation.CFStringRef.createCFString("Is Present");
      CoreFoundation.CFStringRef currentCapacityKey = CoreFoundation.CFStringRef.createCFString("Current Capacity");
      CoreFoundation.CFStringRef maxCapacityKey = CoreFoundation.CFStringRef.createCFString("Max Capacity");
      List<MacPowerSource> psList = new ArrayList(day);

      for(int ps = 0; ps < day; ++ps) {
         Pointer pwrSrcPtr = powerSourcesList.getValueAtIndex(ps);
         CoreFoundation.CFTypeRef powerSource = new CoreFoundation.CFTypeRef();
         powerSource.setPointer(pwrSrcPtr);
         CoreFoundation.CFDictionaryRef dictionary = IO.IOPSGetPowerSourceDescription(powerSourcesInfo, powerSource);
         Pointer result = dictionary.getValue(isPresentKey);
         if (result != null) {
            CoreFoundation.CFBooleanRef isPresentRef = new CoreFoundation.CFBooleanRef(result);
            if (0 != CF.CFBooleanGetValue(isPresentRef)) {
               result = dictionary.getValue(nameKey);
               CoreFoundation.CFStringRef cfName = new CoreFoundation.CFStringRef(result);
               String psName = cfName.stringValue();
               if (psName == null) {
                  psName = "unknown";
               }

               double currentCapacity = 0.0;
               if (dictionary.getValueIfPresent(currentCapacityKey, (PointerByReference)null)) {
                  result = dictionary.getValue(currentCapacityKey);
                  CoreFoundation.CFNumberRef cap = new CoreFoundation.CFNumberRef(result);
                  currentCapacity = (double)cap.intValue();
               }

               double maxCapacity = 1.0;
               if (dictionary.getValueIfPresent(maxCapacityKey, (PointerByReference)null)) {
                  result = dictionary.getValue(maxCapacityKey);
                  CoreFoundation.CFNumberRef cap = new CoreFoundation.CFNumberRef(result);
                  maxCapacity = (double)cap.intValue();
               }

               double psRemainingCapacityPercent = Math.min(1.0, currentCapacity / maxCapacity);
               psList.add(new MacPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature));
            }
         }
      }

      isPresentKey.release();
      nameKey.release();
      currentCapacityKey.release();
      maxCapacityKey.release();
      powerSourcesList.release();
      powerSourcesInfo.release();
      return Collections.unmodifiableList(psList);
   }

   static {
      CF = CoreFoundation.INSTANCE;
      IO = IOKit.INSTANCE;
   }
}
