/*     */ package oshi.util.platform.mac;
/*     */ 
/*     */ import com.sun.jna.NativeLong;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
/*     */ import com.sun.jna.platform.mac.IOKit;
/*     */ import com.sun.jna.platform.mac.IOKitUtil;
/*     */ import com.sun.jna.ptr.NativeLongByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.jna.platform.mac.IOKit;
/*     */ import oshi.jna.platform.mac.SystemB;
/*     */ import oshi.util.ParseUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class SmcUtil
/*     */ {
/*  55 */   private static final Logger LOG = LoggerFactory.getLogger(SmcUtil.class);
/*     */   
/*  57 */   private static final IOKit IO = IOKit.INSTANCE;
/*     */ 
/*     */   
/*     */   @FieldOrder({"major", "minor", "build", "reserved", "release"})
/*     */   public static class SMCKeyDataVers
/*     */     extends Structure
/*     */   {
/*     */     public byte major;
/*     */     
/*     */     public byte minor;
/*     */     
/*     */     public byte build;
/*     */     
/*     */     public byte reserved;
/*     */     
/*     */     public short release;
/*     */   }
/*     */   
/*     */   @FieldOrder({"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
/*     */   public static class SMCKeyDataPLimitData
/*     */     extends Structure
/*     */   {
/*     */     public short version;
/*     */     public short length;
/*     */     public int cpuPLimit;
/*     */     public int gpuPLimit;
/*     */     public int memPLimit;
/*     */   }
/*     */   
/*     */   @FieldOrder({"dataSize", "dataType", "dataAttributes"})
/*     */   public static class SMCKeyDataKeyInfo
/*     */     extends Structure
/*     */   {
/*     */     public int dataSize;
/*     */     public int dataType;
/*     */     public byte dataAttributes;
/*     */   }
/*     */   
/*     */   @FieldOrder({"key", "vers", "pLimitData", "keyInfo", "result", "status", "data8", "data32", "bytes"})
/*     */   public static class SMCKeyData
/*     */     extends Structure
/*     */   {
/*     */     public int key;
/*     */     public SmcUtil.SMCKeyDataVers vers;
/*     */     public SmcUtil.SMCKeyDataPLimitData pLimitData;
/*     */     public SmcUtil.SMCKeyDataKeyInfo keyInfo;
/*     */     public byte result;
/*     */     public byte status;
/*     */     public byte data8;
/*     */     public int data32;
/* 107 */     public byte[] bytes = new byte[32];
/*     */   }
/*     */ 
/*     */   
/*     */   @FieldOrder({"key", "dataSize", "dataType", "bytes"})
/*     */   public static class SMCVal
/*     */     extends Structure
/*     */   {
/* 115 */     public byte[] key = new byte[5];
/*     */     public int dataSize;
/* 117 */     public byte[] dataType = new byte[5];
/* 118 */     public byte[] bytes = new byte[32];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 125 */   private static Map<Integer, SMCKeyDataKeyInfo> keyInfoCache = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 130 */   private static final byte[] DATATYPE_SP78 = ParseUtil.asciiStringToByteArray("sp78", 5);
/* 131 */   private static final byte[] DATATYPE_FPE2 = ParseUtil.asciiStringToByteArray("fpe2", 5);
/* 132 */   private static final byte[] DATATYPE_FLT = ParseUtil.asciiStringToByteArray("flt ", 5);
/*     */ 
/*     */   
/*     */   public static final String SMC_KEY_FAN_NUM = "FNum";
/*     */ 
/*     */   
/*     */   public static final String SMC_KEY_FAN_SPEED = "F%dAc";
/*     */   
/*     */   public static final String SMC_KEY_CPU_TEMP = "TC0P";
/*     */   
/*     */   public static final String SMC_KEY_CPU_VOLTAGE = "VC0C";
/*     */   
/*     */   public static final byte SMC_CMD_READ_BYTES = 5;
/*     */   
/*     */   public static final byte SMC_CMD_READ_KEYINFO = 9;
/*     */   
/*     */   public static final int KERNEL_INDEX_SMC = 2;
/*     */ 
/*     */   
/*     */   public static IOKit.IOConnect smcOpen() {
/* 152 */     IOKit.IOService smcService = IOKitUtil.getMatchingService("AppleSMC");
/* 153 */     if (smcService != null) {
/* 154 */       PointerByReference connPtr = new PointerByReference();
/* 155 */       int result = IO.IOServiceOpen(smcService, SystemB.INSTANCE.mach_task_self(), 0, connPtr);
/* 156 */       smcService.release();
/* 157 */       if (result == 0)
/* 158 */         return new IOKit.IOConnect(connPtr.getValue()); 
/* 159 */       if (LOG.isErrorEnabled()) {
/* 160 */         LOG.error(String.format("Unable to open connection to AppleSMC service. Error: 0x%08x", new Object[] { Integer.valueOf(result) }));
/*     */       }
/*     */     } else {
/* 163 */       LOG.error("Unable to locate AppleSMC service");
/*     */     } 
/* 165 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int smcClose(IOKit.IOConnect conn) {
/* 177 */     return IO.IOServiceClose(conn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double smcGetFloat(IOKit.IOConnect conn, String key) {
/* 190 */     SMCVal val = new SMCVal();
/* 191 */     int result = smcReadKey(conn, key, val);
/* 192 */     if (result == 0 && val.dataSize > 0) {
/* 193 */       if (Arrays.equals(val.dataType, DATATYPE_SP78) && val.dataSize == 2)
/*     */       {
/*     */         
/* 196 */         return val.bytes[0] + val.bytes[1] / 256.0D; } 
/* 197 */       if (Arrays.equals(val.dataType, DATATYPE_FPE2) && val.dataSize == 2)
/*     */       {
/* 199 */         return ParseUtil.byteArrayToFloat(val.bytes, val.dataSize, 2); } 
/* 200 */       if (Arrays.equals(val.dataType, DATATYPE_FLT) && val.dataSize == 4)
/*     */       {
/* 202 */         return ByteBuffer.wrap(val.bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
/*     */       }
/*     */     } 
/*     */     
/* 206 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long smcGetLong(IOKit.IOConnect conn, String key) {
/* 219 */     SMCVal val = new SMCVal();
/* 220 */     int result = smcReadKey(conn, key, val);
/* 221 */     if (result == 0) {
/* 222 */       return ParseUtil.byteArrayToLong(val.bytes, val.dataSize);
/*     */     }
/*     */     
/* 225 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int smcGetKeyInfo(IOKit.IOConnect conn, SMCKeyData inputStructure, SMCKeyData outputStructure) {
/* 240 */     if (keyInfoCache.containsKey(Integer.valueOf(inputStructure.key))) {
/* 241 */       SMCKeyDataKeyInfo keyInfo = keyInfoCache.get(Integer.valueOf(inputStructure.key));
/* 242 */       outputStructure.keyInfo.dataSize = keyInfo.dataSize;
/* 243 */       outputStructure.keyInfo.dataType = keyInfo.dataType;
/* 244 */       outputStructure.keyInfo.dataAttributes = keyInfo.dataAttributes;
/*     */     } else {
/* 246 */       inputStructure.data8 = 9;
/* 247 */       int result = smcCall(conn, 2, inputStructure, outputStructure);
/* 248 */       if (result != 0) {
/* 249 */         return result;
/*     */       }
/* 251 */       SMCKeyDataKeyInfo keyInfo = new SMCKeyDataKeyInfo();
/* 252 */       keyInfo.dataSize = outputStructure.keyInfo.dataSize;
/* 253 */       keyInfo.dataType = outputStructure.keyInfo.dataType;
/* 254 */       keyInfo.dataAttributes = outputStructure.keyInfo.dataAttributes;
/* 255 */       keyInfoCache.put(Integer.valueOf(inputStructure.key), keyInfo);
/*     */     } 
/* 257 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int smcReadKey(IOKit.IOConnect conn, String key, SMCVal val) {
/* 272 */     SMCKeyData inputStructure = new SMCKeyData();
/* 273 */     SMCKeyData outputStructure = new SMCKeyData();
/*     */     
/* 275 */     inputStructure.key = (int)ParseUtil.strToLong(key, 4);
/* 276 */     int result = smcGetKeyInfo(conn, inputStructure, outputStructure);
/* 277 */     if (result == 0) {
/* 278 */       val.dataSize = outputStructure.keyInfo.dataSize;
/* 279 */       val.dataType = ParseUtil.longToByteArray(outputStructure.keyInfo.dataType, 4, 5);
/*     */       
/* 281 */       inputStructure.keyInfo.dataSize = val.dataSize;
/* 282 */       inputStructure.data8 = 5;
/*     */       
/* 284 */       result = smcCall(conn, 2, inputStructure, outputStructure);
/* 285 */       if (result == 0) {
/* 286 */         System.arraycopy(outputStructure.bytes, 0, val.bytes, 0, val.bytes.length);
/* 287 */         return 0;
/*     */       } 
/*     */     } 
/* 290 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int smcCall(IOKit.IOConnect conn, int index, SMCKeyData inputStructure, SMCKeyData outputStructure) {
/* 307 */     return IO.IOConnectCallStructMethod(conn, index, inputStructure, new NativeLong(inputStructure.size()), outputStructure, new NativeLongByReference(new NativeLong(outputStructure
/* 308 */             .size())));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\mac\SmcUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */