/*     */ package oshi.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ public final class EdidUtil
/*     */ {
/*  42 */   private static final Logger LOG = LoggerFactory.getLogger(EdidUtil.class);
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
/*     */   public static String getManufacturerID(byte[] edid) {
/*  58 */     String temp = String.format("%8s%8s", new Object[] { Integer.toBinaryString(edid[8] & 0xFF), Integer.toBinaryString(edid[9] & 0xFF) }).replace(' ', '0');
/*  59 */     LOG.debug("Manufacurer ID: {}", temp);
/*  60 */     return String.format("%s%s%s", new Object[] { Character.valueOf((char)(64 + Integer.parseInt(temp.substring(1, 6), 2))), 
/*  61 */           Character.valueOf((char)(64 + Integer.parseInt(temp.substring(7, 11), 2))), 
/*  62 */           Character.valueOf((char)(64 + Integer.parseInt(temp.substring(12, 16), 2))) }).replace("@", "");
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
/*     */   public static String getProductID(byte[] edid) {
/*  74 */     return Integer.toHexString(
/*  75 */         ByteBuffer.wrap(Arrays.copyOfRange(edid, 10, 12)).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF);
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
/*     */   public static String getSerialNo(byte[] edid) {
/*  88 */     if (LOG.isDebugEnabled()) {
/*  89 */       LOG.debug("Serial number: {}", Arrays.toString(Arrays.copyOfRange(edid, 12, 16)));
/*     */     }
/*  91 */     return String.format("%s%s%s%s", new Object[] { getAlphaNumericOrHex(edid[15]), getAlphaNumericOrHex(edid[14]), 
/*  92 */           getAlphaNumericOrHex(edid[13]), getAlphaNumericOrHex(edid[12]) });
/*     */   }
/*     */   
/*     */   private static String getAlphaNumericOrHex(byte b) {
/*  96 */     return Character.isLetterOrDigit((char)b) ? String.format("%s", new Object[] { Character.valueOf((char)b) }) : String.format("%02X", new Object[] { Byte.valueOf(b) });
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
/*     */   public static byte getWeek(byte[] edid) {
/* 108 */     return edid[16];
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
/*     */   public static int getYear(byte[] edid) {
/* 120 */     byte temp = edid[17];
/* 121 */     LOG.debug("Year-1990: {}", Byte.valueOf(temp));
/* 122 */     return temp + 1990;
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
/*     */   public static String getVersion(byte[] edid) {
/* 134 */     return edid[18] + "." + edid[19];
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
/*     */   public static boolean isDigital(byte[] edid) {
/* 146 */     return (1 == (edid[20] & 0xFF) >> 7);
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
/*     */   public static int getHcm(byte[] edid) {
/* 158 */     return edid[21];
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
/*     */   public static int getVcm(byte[] edid) {
/* 170 */     return edid[22];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[][] getDescriptors(byte[] edid) {
/* 181 */     byte[][] desc = new byte[4][18];
/* 182 */     for (int i = 0; i < desc.length; i++) {
/* 183 */       System.arraycopy(edid, 54 + 18 * i, desc[i], 0, 18);
/*     */     }
/* 185 */     return desc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getDescriptorType(byte[] desc) {
/* 196 */     return ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 4)).getInt();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getTimingDescriptor(byte[] desc) {
/* 207 */     int clock = ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;
/* 208 */     int hActive = (desc[2] & 0xFF) + ((desc[4] & 0xF0) << 4);
/* 209 */     int vActive = (desc[5] & 0xFF) + ((desc[7] & 0xF0) << 4);
/* 210 */     return String.format("Clock %dMHz, Active Pixels %dx%d ", new Object[] { Integer.valueOf(clock), Integer.valueOf(hActive), Integer.valueOf(vActive) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDescriptorRangeLimits(byte[] desc) {
/* 221 */     return String.format("Field Rate %d-%d Hz vertical, %d-%d Hz horizontal, Max clock: %d MHz", new Object[] { Byte.valueOf(desc[5]), Byte.valueOf(desc[6]), 
/* 222 */           Byte.valueOf(desc[7]), Byte.valueOf(desc[8]), Integer.valueOf(desc[9] * 10) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDescriptorText(byte[] desc) {
/* 233 */     return (new String(Arrays.copyOfRange(desc, 4, 18), StandardCharsets.US_ASCII)).trim();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(byte[] edid) {
/* 244 */     StringBuilder sb = new StringBuilder();
/* 245 */     sb.append("  Manuf. ID=").append(getManufacturerID(edid));
/* 246 */     sb.append(", Product ID=").append(getProductID(edid));
/* 247 */     sb.append(", ").append(isDigital(edid) ? "Digital" : "Analog");
/* 248 */     sb.append(", Serial=").append(getSerialNo(edid));
/* 249 */     sb.append(", ManufDate=").append(getWeek(edid) * 12 / 52 + 1).append('/')
/* 250 */       .append(getYear(edid));
/* 251 */     sb.append(", EDID v").append(getVersion(edid));
/* 252 */     int hSize = getHcm(edid);
/* 253 */     int vSize = getVcm(edid);
/* 254 */     sb.append(String.format("%n  %d x %d cm (%.1f x %.1f in)", new Object[] { Integer.valueOf(hSize), Integer.valueOf(vSize), Double.valueOf(hSize / 2.54D), Double.valueOf(vSize / 2.54D) }));
/* 255 */     byte[][] desc = getDescriptors(edid);
/* 256 */     for (byte[] b : desc) {
/* 257 */       switch (getDescriptorType(b)) {
/*     */         case 255:
/* 259 */           sb.append("\n  Serial Number: ").append(getDescriptorText(b));
/*     */           break;
/*     */         case 254:
/* 262 */           sb.append("\n  Unspecified Text: ").append(getDescriptorText(b));
/*     */           break;
/*     */         case 253:
/* 265 */           sb.append("\n  Range Limits: ").append(getDescriptorRangeLimits(b));
/*     */           break;
/*     */         case 252:
/* 268 */           sb.append("\n  Monitor Name: ").append(getDescriptorText(b));
/*     */           break;
/*     */         case 251:
/* 271 */           sb.append("\n  White Point Data: ").append(ParseUtil.byteArrayToHexString(b));
/*     */           break;
/*     */         case 250:
/* 274 */           sb.append("\n  Standard Timing ID: ").append(ParseUtil.byteArrayToHexString(b));
/*     */           break;
/*     */         default:
/* 277 */           if (getDescriptorType(b) <= 15 && getDescriptorType(b) >= 0) {
/* 278 */             sb.append("\n  Manufacturer Data: ").append(ParseUtil.byteArrayToHexString(b)); break;
/*     */           } 
/* 280 */           sb.append("\n  Preferred Timing: ").append(getTimingDescriptor(b));
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 285 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\EdidUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */