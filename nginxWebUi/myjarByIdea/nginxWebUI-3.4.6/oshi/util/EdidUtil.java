package oshi.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class EdidUtil {
   private static final Logger LOG = LoggerFactory.getLogger(EdidUtil.class);

   private EdidUtil() {
   }

   public static String getManufacturerID(byte[] edid) {
      String temp = String.format("%8s%8s", Integer.toBinaryString(edid[8] & 255), Integer.toBinaryString(edid[9] & 255)).replace(' ', '0');
      LOG.debug((String)"Manufacurer ID: {}", (Object)temp);
      return String.format("%s%s%s", (char)(64 + Integer.parseInt(temp.substring(1, 6), 2)), (char)(64 + Integer.parseInt(temp.substring(7, 11), 2)), (char)(64 + Integer.parseInt(temp.substring(12, 16), 2))).replace("@", "");
   }

   public static String getProductID(byte[] edid) {
      return Integer.toHexString(ByteBuffer.wrap(Arrays.copyOfRange(edid, 10, 12)).order(ByteOrder.LITTLE_ENDIAN).getShort() & '\uffff');
   }

   public static String getSerialNo(byte[] edid) {
      if (LOG.isDebugEnabled()) {
         LOG.debug((String)"Serial number: {}", (Object)Arrays.toString(Arrays.copyOfRange(edid, 12, 16)));
      }

      return String.format("%s%s%s%s", getAlphaNumericOrHex(edid[15]), getAlphaNumericOrHex(edid[14]), getAlphaNumericOrHex(edid[13]), getAlphaNumericOrHex(edid[12]));
   }

   private static String getAlphaNumericOrHex(byte b) {
      return Character.isLetterOrDigit((char)b) ? String.format("%s", (char)b) : String.format("%02X", b);
   }

   public static byte getWeek(byte[] edid) {
      return edid[16];
   }

   public static int getYear(byte[] edid) {
      byte temp = edid[17];
      LOG.debug((String)"Year-1990: {}", (Object)temp);
      return temp + 1990;
   }

   public static String getVersion(byte[] edid) {
      return edid[18] + "." + edid[19];
   }

   public static boolean isDigital(byte[] edid) {
      return 1 == (edid[20] & 255) >> 7;
   }

   public static int getHcm(byte[] edid) {
      return edid[21];
   }

   public static int getVcm(byte[] edid) {
      return edid[22];
   }

   public static byte[][] getDescriptors(byte[] edid) {
      byte[][] desc = new byte[4][18];

      for(int i = 0; i < desc.length; ++i) {
         System.arraycopy(edid, 54 + 18 * i, desc[i], 0, 18);
      }

      return desc;
   }

   public static int getDescriptorType(byte[] desc) {
      return ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 4)).getInt();
   }

   public static String getTimingDescriptor(byte[] desc) {
      int clock = ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;
      int hActive = (desc[2] & 255) + ((desc[4] & 240) << 4);
      int vActive = (desc[5] & 255) + ((desc[7] & 240) << 4);
      return String.format("Clock %dMHz, Active Pixels %dx%d ", clock, hActive, vActive);
   }

   public static String getDescriptorRangeLimits(byte[] desc) {
      return String.format("Field Rate %d-%d Hz vertical, %d-%d Hz horizontal, Max clock: %d MHz", desc[5], desc[6], desc[7], desc[8], desc[9] * 10);
   }

   public static String getDescriptorText(byte[] desc) {
      return (new String(Arrays.copyOfRange(desc, 4, 18), StandardCharsets.US_ASCII)).trim();
   }

   public static String toString(byte[] var0) {
      // $FF: Couldn't be decompiled
   }
}
