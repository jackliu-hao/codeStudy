package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class Sysfs {
   private Sysfs() {
   }

   public static String querySystemVendor() {
      String sysVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/sys_vendor").trim();
      return !sysVendor.isEmpty() ? sysVendor : null;
   }

   public static String queryProductModel() {
      String productName = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_name").trim();
      String productVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_version").trim();
      if (productName.isEmpty()) {
         return !productVersion.isEmpty() ? productVersion : null;
      } else {
         return !productVersion.isEmpty() && !"None".equals(productVersion) ? productName + " (version: " + productVersion + ")" : productName;
      }
   }

   public static String queryProductSerial() {
      String serial = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_serial");
      return !serial.isEmpty() && !"None".equals(serial) ? serial : queryBoardSerial();
   }

   public static String queryBoardVendor() {
      String boardVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_vendor").trim();
      return !boardVendor.isEmpty() ? boardVendor : null;
   }

   public static String queryBoardModel() {
      String boardName = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_name").trim();
      return !boardName.isEmpty() ? boardName : null;
   }

   public static String queryBoardVersion() {
      String boardVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_version").trim();
      return !boardVersion.isEmpty() ? boardVersion : null;
   }

   public static String queryBoardSerial() {
      String boardSerial = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial").trim();
      return !boardSerial.isEmpty() ? boardSerial : null;
   }

   public static String queryBiosVendor() {
      String biosVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_vendor").trim();
      return biosVendor.isEmpty() ? biosVendor : null;
   }

   public static String queryBiosDescription() {
      String modalias = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/modalias").trim();
      return !modalias.isEmpty() ? modalias : null;
   }

   public static String queryBiosVersion(String biosRevision) {
      String biosVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_version").trim();
      return !biosVersion.isEmpty() ? biosVersion + (Util.isBlank(biosRevision) ? "" : " (revision " + biosRevision + ")") : null;
   }

   public static String queryBiosReleaseDate() {
      String biosDate = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_date").trim();
      return !biosDate.isEmpty() ? ParseUtil.parseMmDdYyyyToYyyyMmDD(biosDate) : null;
   }
}
