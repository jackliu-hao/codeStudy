package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;

@ThreadSafe
public final class Devicetree {
   private Devicetree() {
   }

   public static String queryModel() {
      String modelStr = FileUtil.getStringFromFile("/sys/firmware/devicetree/base/model");
      return !modelStr.isEmpty() ? modelStr.replace("Machine: ", "") : null;
   }
}
