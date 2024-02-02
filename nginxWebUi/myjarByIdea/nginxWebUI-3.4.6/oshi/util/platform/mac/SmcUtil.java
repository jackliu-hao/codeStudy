package oshi.util.platform.mac;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.IOKit;
import oshi.jna.platform.mac.SystemB;
import oshi.util.ParseUtil;

@ThreadSafe
public final class SmcUtil {
   private static final Logger LOG = LoggerFactory.getLogger(SmcUtil.class);
   private static final IOKit IO;
   private static Map<Integer, SMCKeyDataKeyInfo> keyInfoCache;
   private static final byte[] DATATYPE_SP78;
   private static final byte[] DATATYPE_FPE2;
   private static final byte[] DATATYPE_FLT;
   public static final String SMC_KEY_FAN_NUM = "FNum";
   public static final String SMC_KEY_FAN_SPEED = "F%dAc";
   public static final String SMC_KEY_CPU_TEMP = "TC0P";
   public static final String SMC_KEY_CPU_VOLTAGE = "VC0C";
   public static final byte SMC_CMD_READ_BYTES = 5;
   public static final byte SMC_CMD_READ_KEYINFO = 9;
   public static final int KERNEL_INDEX_SMC = 2;

   private SmcUtil() {
   }

   public static com.sun.jna.platform.mac.IOKit.IOConnect smcOpen() {
      com.sun.jna.platform.mac.IOKit.IOService smcService = IOKitUtil.getMatchingService("AppleSMC");
      if (smcService != null) {
         PointerByReference connPtr = new PointerByReference();
         int result = IO.IOServiceOpen(smcService, SystemB.INSTANCE.mach_task_self(), 0, connPtr);
         smcService.release();
         if (result == 0) {
            return new com.sun.jna.platform.mac.IOKit.IOConnect(connPtr.getValue());
         }

         if (LOG.isErrorEnabled()) {
            LOG.error(String.format("Unable to open connection to AppleSMC service. Error: 0x%08x", result));
         }
      } else {
         LOG.error("Unable to locate AppleSMC service");
      }

      return null;
   }

   public static int smcClose(com.sun.jna.platform.mac.IOKit.IOConnect conn) {
      return IO.IOServiceClose(conn);
   }

   public static double smcGetFloat(com.sun.jna.platform.mac.IOKit.IOConnect conn, String key) {
      SMCVal val = new SMCVal();
      int result = smcReadKey(conn, key, val);
      if (result == 0 && val.dataSize > 0) {
         if (Arrays.equals(val.dataType, DATATYPE_SP78) && val.dataSize == 2) {
            return (double)val.bytes[0] + (double)val.bytes[1] / 256.0;
         }

         if (Arrays.equals(val.dataType, DATATYPE_FPE2) && val.dataSize == 2) {
            return (double)ParseUtil.byteArrayToFloat(val.bytes, val.dataSize, 2);
         }

         if (Arrays.equals(val.dataType, DATATYPE_FLT) && val.dataSize == 4) {
            return (double)ByteBuffer.wrap(val.bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
         }
      }

      return 0.0;
   }

   public static long smcGetLong(com.sun.jna.platform.mac.IOKit.IOConnect conn, String key) {
      SMCVal val = new SMCVal();
      int result = smcReadKey(conn, key, val);
      return result == 0 ? ParseUtil.byteArrayToLong(val.bytes, val.dataSize) : 0L;
   }

   public static int smcGetKeyInfo(com.sun.jna.platform.mac.IOKit.IOConnect conn, SMCKeyData inputStructure, SMCKeyData outputStructure) {
      if (keyInfoCache.containsKey(inputStructure.key)) {
         SMCKeyDataKeyInfo keyInfo = (SMCKeyDataKeyInfo)keyInfoCache.get(inputStructure.key);
         outputStructure.keyInfo.dataSize = keyInfo.dataSize;
         outputStructure.keyInfo.dataType = keyInfo.dataType;
         outputStructure.keyInfo.dataAttributes = keyInfo.dataAttributes;
      } else {
         inputStructure.data8 = 9;
         int result = smcCall(conn, 2, inputStructure, outputStructure);
         if (result != 0) {
            return result;
         }

         SMCKeyDataKeyInfo keyInfo = new SMCKeyDataKeyInfo();
         keyInfo.dataSize = outputStructure.keyInfo.dataSize;
         keyInfo.dataType = outputStructure.keyInfo.dataType;
         keyInfo.dataAttributes = outputStructure.keyInfo.dataAttributes;
         keyInfoCache.put(inputStructure.key, keyInfo);
      }

      return 0;
   }

   public static int smcReadKey(com.sun.jna.platform.mac.IOKit.IOConnect conn, String key, SMCVal val) {
      SMCKeyData inputStructure = new SMCKeyData();
      SMCKeyData outputStructure = new SMCKeyData();
      inputStructure.key = (int)ParseUtil.strToLong(key, 4);
      int result = smcGetKeyInfo(conn, inputStructure, outputStructure);
      if (result == 0) {
         val.dataSize = outputStructure.keyInfo.dataSize;
         val.dataType = ParseUtil.longToByteArray((long)outputStructure.keyInfo.dataType, 4, 5);
         inputStructure.keyInfo.dataSize = val.dataSize;
         inputStructure.data8 = 5;
         result = smcCall(conn, 2, inputStructure, outputStructure);
         if (result == 0) {
            System.arraycopy(outputStructure.bytes, 0, val.bytes, 0, val.bytes.length);
            return 0;
         }
      }

      return result;
   }

   public static int smcCall(com.sun.jna.platform.mac.IOKit.IOConnect conn, int index, SMCKeyData inputStructure, SMCKeyData outputStructure) {
      return IO.IOConnectCallStructMethod(conn, index, inputStructure, new NativeLong((long)inputStructure.size()), outputStructure, new NativeLongByReference(new NativeLong((long)outputStructure.size())));
   }

   static {
      IO = IOKit.INSTANCE;
      keyInfoCache = new ConcurrentHashMap();
      DATATYPE_SP78 = ParseUtil.asciiStringToByteArray("sp78", 5);
      DATATYPE_FPE2 = ParseUtil.asciiStringToByteArray("fpe2", 5);
      DATATYPE_FLT = ParseUtil.asciiStringToByteArray("flt ", 5);
   }

   @Structure.FieldOrder({"key", "dataSize", "dataType", "bytes"})
   public static class SMCVal extends Structure {
      public byte[] key = new byte[5];
      public int dataSize;
      public byte[] dataType = new byte[5];
      public byte[] bytes = new byte[32];
   }

   @Structure.FieldOrder({"key", "vers", "pLimitData", "keyInfo", "result", "status", "data8", "data32", "bytes"})
   public static class SMCKeyData extends Structure {
      public int key;
      public SMCKeyDataVers vers;
      public SMCKeyDataPLimitData pLimitData;
      public SMCKeyDataKeyInfo keyInfo;
      public byte result;
      public byte status;
      public byte data8;
      public int data32;
      public byte[] bytes = new byte[32];
   }

   @Structure.FieldOrder({"dataSize", "dataType", "dataAttributes"})
   public static class SMCKeyDataKeyInfo extends Structure {
      public int dataSize;
      public int dataType;
      public byte dataAttributes;
   }

   @Structure.FieldOrder({"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
   public static class SMCKeyDataPLimitData extends Structure {
      public short version;
      public short length;
      public int cpuPLimit;
      public int gpuPLimit;
      public int memPLimit;
   }

   @Structure.FieldOrder({"major", "minor", "build", "reserved", "release"})
   public static class SMCKeyDataVers extends Structure {
      public byte major;
      public byte minor;
      public byte build;
      public byte reserved;
      public short release;
   }
}
