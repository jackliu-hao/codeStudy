package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class OhmHardware {
   private static final String HARDWARE = "Hardware";

   private OhmHardware() {
   }

   public static WbemcliUtil.WmiResult<IdentifierProperty> queryHwIdentifier(String typeToQuery, String typeName) {
      StringBuilder sb = new StringBuilder("Hardware");
      sb.append(" WHERE ").append(typeToQuery).append("Type=\"").append(typeName).append('"');
      WbemcliUtil.WmiQuery<IdentifierProperty> cpuIdentifierQuery = new WbemcliUtil.WmiQuery("ROOT\\OpenHardwareMonitor", sb.toString(), IdentifierProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(cpuIdentifierQuery);
   }

   public static enum IdentifierProperty {
      IDENTIFIER;
   }
}
