/*     */ package oshi.util.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import java.time.OffsetDateTime;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.Constants;
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
/*     */ public final class WmiUtil
/*     */ {
/*     */   public static final String OHM_NAMESPACE = "ROOT\\OpenHardwareMonitor";
/*     */   private static final String CLASS_CAST_MSG = "%s is not a %s type. CIM Type is %d and VT type is %d";
/*     */   
/*     */   public static <T extends Enum<T>> String queryToString(WbemcliUtil.WmiQuery<T> query) {
/*  67 */     Enum[] arrayOfEnum = query.getPropertyEnum().getEnumConstants();
/*  68 */     StringBuilder sb = new StringBuilder("SELECT ");
/*  69 */     sb.append(arrayOfEnum[0].name());
/*  70 */     for (int i = 1; i < arrayOfEnum.length; i++) {
/*  71 */       sb.append(',').append(arrayOfEnum[i].name());
/*     */     }
/*  73 */     sb.append(" FROM ").append(query.getWmiClassName());
/*  74 */     return sb.toString();
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
/*     */   
/*     */   public static <T extends Enum<T>> String getString(WbemcliUtil.WmiResult<T> result, T property, int index) {
/*  92 */     if (result.getCIMType((Enum)property) == 8) {
/*  93 */       return getStr(result, property, index);
/*     */     }
/*  95 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "String", 
/*  96 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */   
/*     */   public static <T extends Enum<T>> String getDateString(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 114 */     OffsetDateTime dateTime = getDateTime(result, property, index);
/*     */     
/* 116 */     if (dateTime.equals(Constants.UNIX_EPOCH)) {
/* 117 */       return "";
/*     */     }
/* 119 */     return dateTime.toLocalDate().toString();
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
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T>> OffsetDateTime getDateTime(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 138 */     if (result.getCIMType((Enum)property) == 101) {
/* 139 */       return ParseUtil.parseCimDateTimeToOffset(getStr(result, property, index));
/*     */     }
/* 141 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "DateTime", 
/* 142 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */   
/*     */   public static <T extends Enum<T>> String getRefString(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 160 */     if (result.getCIMType((Enum)property) == 102) {
/* 161 */       return getStr(result, property, index);
/*     */     }
/* 163 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "Reference", 
/* 164 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
/*     */   }
/*     */   
/*     */   private static <T extends Enum<T>> String getStr(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 168 */     Object o = result.getValue((Enum)property, index);
/* 169 */     if (o == null)
/* 170 */       return ""; 
/* 171 */     if (result.getVtType((Enum)property) == 8) {
/* 172 */       return (String)o;
/*     */     }
/* 174 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "String-mapped", 
/* 175 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T>> long getUint64(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 195 */     Object o = result.getValue((Enum)property, index);
/* 196 */     if (o == null)
/* 197 */       return 0L; 
/* 198 */     if (result.getCIMType((Enum)property) == 21 && result.getVtType((Enum)property) == 8) {
/* 199 */       return ParseUtil.parseLongOrDefault((String)o, 0L);
/*     */     }
/* 201 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "UINT64", 
/* 202 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T>> int getUint32(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 222 */     if (result.getCIMType((Enum)property) == 19) {
/* 223 */       return getInt(result, property, index);
/*     */     }
/* 225 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "UINT32", 
/* 226 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */   
/*     */   public static <T extends Enum<T>> long getUint32asLong(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 244 */     if (result.getCIMType((Enum)property) == 19) {
/* 245 */       return getInt(result, property, index) & 0xFFFFFFFFL;
/*     */     }
/* 247 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "UINT32", 
/* 248 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T>> int getSint32(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 268 */     if (result.getCIMType((Enum)property) == 3) {
/* 269 */       return getInt(result, property, index);
/*     */     }
/* 271 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "SINT32", 
/* 272 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T>> int getUint16(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 292 */     if (result.getCIMType((Enum)property) == 18) {
/* 293 */       return getInt(result, property, index);
/*     */     }
/* 295 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "UINT16", 
/* 296 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
/*     */   }
/*     */   
/*     */   private static <T extends Enum<T>> int getInt(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 300 */     Object o = result.getValue((Enum)property, index);
/* 301 */     if (o == null)
/* 302 */       return 0; 
/* 303 */     if (result.getVtType((Enum)property) == 3) {
/* 304 */       return ((Integer)o).intValue();
/*     */     }
/* 306 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "32-bit integer", 
/* 307 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
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
/*     */   
/*     */   public static <T extends Enum<T>> float getFloat(WbemcliUtil.WmiResult<T> result, T property, int index) {
/* 325 */     Object o = result.getValue((Enum)property, index);
/* 326 */     if (o == null)
/* 327 */       return 0.0F; 
/* 328 */     if (result.getCIMType((Enum)property) == 4 && result.getVtType((Enum)property) == 4) {
/* 329 */       return ((Float)o).floatValue();
/*     */     }
/* 331 */     throw new ClassCastException(String.format("%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { property.name(), "Float", 
/* 332 */             Integer.valueOf(result.getCIMType(property)), Integer.valueOf(result.getVtType(property)) }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\windows\WmiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */