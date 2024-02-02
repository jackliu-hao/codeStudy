package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisPowerSource extends AbstractPowerSource {
   private static final String[] KSTAT_BATT_MOD = new String[]{null, "battery", "acpi_drv"};
   private static final int KSTAT_BATT_IDX;

   public SolarisPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
      super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
   }

   public static List<PowerSource> getPowerSources() {
      return Collections.unmodifiableList(Arrays.asList(getPowerSource("BAT0")));
   }

   private static SolarisPowerSource getPowerSource(String name) {
      String psDeviceName = "unknown";
      double psRemainingCapacityPercent = 1.0;
      double psTimeRemainingEstimated = -1.0;
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
      if (KSTAT_BATT_IDX > 0) {
         KstatUtil.KstatChain kc = KstatUtil.openChain();

         try {
            LibKstat.Kstat ksp = kc.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BIF0");
            long energyNow;
            long powerNow;
            if (ksp != null) {
               energyNow = KstatUtil.dataLookupLong(ksp, "bif_last_cap");
               if (energyNow == -1L || energyNow <= 0L) {
                  energyNow = KstatUtil.dataLookupLong(ksp, "bif_design_cap");
               }

               if (energyNow != -1L && energyNow > 0L) {
                  psMaxCapacity = (int)energyNow;
               }

               powerNow = KstatUtil.dataLookupLong(ksp, "bif_unit");
               if (powerNow == 0L) {
                  psCapacityUnits = PowerSource.CapacityUnits.MWH;
               } else if (powerNow == 1L) {
                  psCapacityUnits = PowerSource.CapacityUnits.MAH;
               }

               psDeviceName = KstatUtil.dataLookupString(ksp, "bif_model");
               psSerialNumber = KstatUtil.dataLookupString(ksp, "bif_serial");
               psChemistry = KstatUtil.dataLookupString(ksp, "bif_type");
               psManufacturer = KstatUtil.dataLookupString(ksp, "bif_oem_info");
            }

            ksp = kc.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BST0");
            if (ksp != null) {
               energyNow = KstatUtil.dataLookupLong(ksp, "bst_rem_cap");
               if (energyNow >= 0L) {
                  psCurrentCapacity = (int)energyNow;
               }

               powerNow = KstatUtil.dataLookupLong(ksp, "bst_rate");
               if (powerNow == -1L) {
                  powerNow = 0L;
               }

               boolean isCharging = (KstatUtil.dataLookupLong(ksp, "bst_state") & 16L) > 0L;
               if (!isCharging) {
                  psTimeRemainingEstimated = powerNow > 0L ? 3600.0 * (double)energyNow / (double)powerNow : -1.0;
               }

               long voltageNow = KstatUtil.dataLookupLong(ksp, "bst_voltage");
               if (voltageNow > 0L) {
                  psVoltage = (double)voltageNow / 1000.0;
                  psAmperage = psPowerUsageRate * 1000.0 / (double)voltageNow;
               }
            }
         } catch (Throwable var39) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var38) {
                  var39.addSuppressed(var38);
               }
            }

            throw var39;
         }

         if (kc != null) {
            kc.close();
         }
      }

      return new SolarisPowerSource(name, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, (LocalDate)psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
   }

   static {
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         if (kc.lookup(KSTAT_BATT_MOD[1], 0, (String)null) != null) {
            KSTAT_BATT_IDX = 1;
         } else if (kc.lookup(KSTAT_BATT_MOD[2], 0, (String)null) != null) {
            KSTAT_BATT_IDX = 2;
         } else {
            KSTAT_BATT_IDX = 0;
         }
      } catch (Throwable var4) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var3) {
               var4.addSuppressed(var3);
            }
         }

         throw var4;
      }

      if (kc != null) {
         kc.close();
      }

   }
}
