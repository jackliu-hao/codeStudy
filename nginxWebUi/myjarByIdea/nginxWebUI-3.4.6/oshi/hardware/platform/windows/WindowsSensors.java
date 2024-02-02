package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.MSAcpiThermalZoneTemperature;
import oshi.driver.windows.wmi.OhmHardware;
import oshi.driver.windows.wmi.OhmSensor;
import oshi.driver.windows.wmi.Win32Fan;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.common.AbstractSensors;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
final class WindowsSensors extends AbstractSensors {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsSensors.class);

   public double queryCpuTemperature() {
      double tempC = getTempFromOHM();
      if (tempC > 0.0) {
         return tempC;
      } else {
         tempC = getTempFromWMI();
         return tempC;
      }
   }

   private static double getTempFromOHM() {
      WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier("Hardware", "CPU");
      if (ohmHardware.getResultCount() > 0) {
         LOG.debug("Found Temperature data in Open Hardware Monitor");
         String cpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         if (cpuIdentifier.length() > 0) {
            WbemcliUtil.WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(cpuIdentifier, "Temperature");
            if (ohmSensors.getResultCount() > 0) {
               double sum = 0.0;

               for(int i = 0; i < ohmSensors.getResultCount(); ++i) {
                  sum += (double)WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE, i);
               }

               return sum / (double)ohmSensors.getResultCount();
            }
         }
      }

      return 0.0;
   }

   private static double getTempFromWMI() {
      double tempC = 0.0;
      long tempK = 0L;
      WbemcliUtil.WmiResult<MSAcpiThermalZoneTemperature.TemperatureProperty> result = MSAcpiThermalZoneTemperature.queryCurrentTemperature();
      if (result.getResultCount() > 0) {
         LOG.debug("Found Temperature data in WMI");
         tempK = WmiUtil.getUint32asLong(result, MSAcpiThermalZoneTemperature.TemperatureProperty.CURRENTTEMPERATURE, 0);
      }

      if (tempK > 2732L) {
         tempC = (double)tempK / 10.0 - 273.15;
      } else if (tempK > 274L) {
         tempC = (double)tempK - 273.0;
      }

      return tempC < 0.0 ? 0.0 : tempC;
   }

   public int[] queryFanSpeeds() {
      int[] fanSpeeds = getFansFromOHM();
      if (fanSpeeds.length > 0) {
         return fanSpeeds;
      } else {
         fanSpeeds = getFansFromWMI();
         return fanSpeeds.length > 0 ? fanSpeeds : new int[0];
      }
   }

   private static int[] getFansFromOHM() {
      WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier("Hardware", "CPU");
      if (ohmHardware.getResultCount() > 0) {
         LOG.debug("Found Fan data in Open Hardware Monitor");
         String cpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         if (cpuIdentifier.length() > 0) {
            WbemcliUtil.WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(cpuIdentifier, "Fan");
            if (ohmSensors.getResultCount() > 0) {
               int[] fanSpeeds = new int[ohmSensors.getResultCount()];

               for(int i = 0; i < ohmSensors.getResultCount(); ++i) {
                  fanSpeeds[i] = (int)WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE, i);
               }

               return fanSpeeds;
            }
         }
      }

      return new int[0];
   }

   private static int[] getFansFromWMI() {
      WbemcliUtil.WmiResult<Win32Fan.SpeedProperty> fan = Win32Fan.querySpeed();
      if (fan.getResultCount() <= 1) {
         return new int[0];
      } else {
         LOG.debug("Found Fan data in WMI");
         int[] fanSpeeds = new int[fan.getResultCount()];

         for(int i = 0; i < fan.getResultCount(); ++i) {
            fanSpeeds[i] = (int)WmiUtil.getUint64(fan, Win32Fan.SpeedProperty.DESIREDSPEED, i);
         }

         return fanSpeeds;
      }
   }

   public double queryCpuVoltage() {
      double volts = getVoltsFromOHM();
      if (volts > 0.0) {
         return volts;
      } else {
         volts = getVoltsFromWMI();
         return volts;
      }
   }

   private static double getVoltsFromOHM() {
      WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier("Sensor", "Voltage");
      if (ohmHardware.getResultCount() > 0) {
         LOG.debug("Found Voltage data in Open Hardware Monitor");
         String cpuIdentifier = null;

         for(int i = 0; i < ohmHardware.getResultCount(); ++i) {
            String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
            if (id.toLowerCase().contains("cpu")) {
               cpuIdentifier = id;
               break;
            }
         }

         if (cpuIdentifier == null) {
            cpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         }

         WbemcliUtil.WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(cpuIdentifier, "Voltage");
         if (ohmSensors.getResultCount() > 0) {
            return (double)WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE, 0);
         }
      }

      return 0.0;
   }

   private static double getVoltsFromWMI() {
      WbemcliUtil.WmiResult<Win32Processor.VoltProperty> voltage = Win32Processor.queryVoltage();
      if (voltage.getResultCount() > 1) {
         LOG.debug("Found Voltage data in WMI");
         int decivolts = WmiUtil.getUint16(voltage, Win32Processor.VoltProperty.CURRENTVOLTAGE, 0);
         if (decivolts > 0) {
            if ((decivolts & 128) != 0) {
               return (double)(decivolts & 127) / 10.0;
            }

            decivolts = WmiUtil.getUint32(voltage, Win32Processor.VoltProperty.VOLTAGECAPS, 0);
            if ((decivolts & 1) > 0) {
               return 5.0;
            }

            if ((decivolts & 2) > 0) {
               return 3.3;
            }

            if ((decivolts & 4) > 0) {
               return 2.9;
            }
         }
      }

      return 0.0;
   }
}
