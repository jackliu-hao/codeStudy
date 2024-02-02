package oshi.jna.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface PowrProf extends com.sun.jna.platform.win32.PowrProf {
   PowrProf INSTANCE = (PowrProf)Native.load("PowrProf", PowrProf.class);

   @Structure.FieldOrder({"Day", "Month", "Year"})
   public static class BATTERY_MANUFACTURE_DATE extends Structure {
      public byte Day;
      public byte Month;
      public short Year;
   }

   @Structure.FieldOrder({"PowerState", "Capacity", "Voltage", "Rate"})
   public static class BATTERY_STATUS extends Structure {
      public int PowerState;
      public int Capacity;
      public int Voltage;
      public int Rate;
   }

   @Structure.FieldOrder({"BatteryTag", "Timeout", "PowerState", "LowCapacity", "HighCapacity"})
   public static class BATTERY_WAIT_STATUS extends Structure {
      public int BatteryTag;
      public int Timeout;
      public int PowerState;
      public int LowCapacity;
      public int HighCapacity;
   }

   @Structure.FieldOrder({"Capabilities", "Technology", "Reserved", "Chemistry", "DesignedCapacity", "FullChargedCapacity", "DefaultAlert1", "DefaultAlert2", "CriticalBias", "CycleCount"})
   public static class BATTERY_INFORMATION extends Structure {
      public int Capabilities;
      public byte Technology;
      public byte[] Reserved = new byte[3];
      public byte[] Chemistry = new byte[4];
      public int DesignedCapacity;
      public int FullChargedCapacity;
      public int DefaultAlert1;
      public int DefaultAlert2;
      public int CriticalBias;
      public int CycleCount;
   }

   public static enum BATTERY_QUERY_INFORMATION_LEVEL {
      BatteryInformation,
      BatteryGranularityInformation,
      BatteryTemperature,
      BatteryEstimatedTime,
      BatteryDeviceName,
      BatteryManufactureDate,
      BatteryManufactureName,
      BatteryUniqueID,
      BatterySerialNumber;
   }

   @Structure.FieldOrder({"BatteryTag", "InformationLevel", "AtRate"})
   public static class BATTERY_QUERY_INFORMATION extends Structure {
      public int BatteryTag;
      public int InformationLevel;
      public int AtRate;
   }

   @Structure.FieldOrder({"number", "maxMhz", "currentMhz", "mhzLimit", "maxIdleState", "currentIdleState"})
   public static class ProcessorPowerInformation extends Structure {
      public int number;
      public int maxMhz;
      public int currentMhz;
      public int mhzLimit;
      public int maxIdleState;
      public int currentIdleState;

      public ProcessorPowerInformation(Pointer p) {
         super(p);
         this.read();
      }

      public ProcessorPowerInformation() {
      }
   }

   @Structure.FieldOrder({"acOnLine", "batteryPresent", "charging", "discharging", "spare1", "tag", "maxCapacity", "remainingCapacity", "rate", "estimatedTime", "defaultAlert1", "defaultAlert2"})
   public static class SystemBatteryState extends Structure {
      public byte acOnLine;
      public byte batteryPresent;
      public byte charging;
      public byte discharging;
      public byte[] spare1 = new byte[3];
      public byte tag;
      public int maxCapacity;
      public int remainingCapacity;
      public int rate;
      public int estimatedTime;
      public int defaultAlert1;
      public int defaultAlert2;

      public SystemBatteryState(Pointer p) {
         super(p);
         this.read();
      }

      public SystemBatteryState() {
      }
   }
}
