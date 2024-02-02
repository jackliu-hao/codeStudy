package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Processor {
   private static final String WIN32_PROCESSOR = "Win32_Processor";

   private Win32Processor() {
   }

   public static WbemcliUtil.WmiResult<VoltProperty> queryVoltage() {
      WbemcliUtil.WmiQuery<VoltProperty> voltQuery = new WbemcliUtil.WmiQuery("Win32_Processor", VoltProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(voltQuery);
   }

   public static WbemcliUtil.WmiResult<ProcessorIdProperty> queryProcessorId() {
      WbemcliUtil.WmiQuery<ProcessorIdProperty> idQuery = new WbemcliUtil.WmiQuery("Win32_Processor", ProcessorIdProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(idQuery);
   }

   public static WbemcliUtil.WmiResult<BitnessProperty> queryBitness() {
      WbemcliUtil.WmiQuery<BitnessProperty> bitnessQuery = new WbemcliUtil.WmiQuery("Win32_Processor", BitnessProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(bitnessQuery);
   }

   public static enum VoltProperty {
      CURRENTVOLTAGE,
      VOLTAGECAPS;
   }

   public static enum ProcessorIdProperty {
      PROCESSORID;
   }

   public static enum BitnessProperty {
      ADDRESSWIDTH;
   }
}
