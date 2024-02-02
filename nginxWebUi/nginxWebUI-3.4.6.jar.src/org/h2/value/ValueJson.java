/*     */ package org.h2.value;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.json.JSONByteArrayTarget;
/*     */ import org.h2.util.json.JSONBytesSource;
/*     */ import org.h2.util.json.JSONItemType;
/*     */ import org.h2.util.json.JSONStringSource;
/*     */ import org.h2.util.json.JSONStringTarget;
/*     */ import org.h2.util.json.JSONTarget;
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
/*     */ public final class ValueJson
/*     */   extends ValueBytesBase
/*     */ {
/*  28 */   private static final byte[] NULL_BYTES = "null".getBytes(StandardCharsets.ISO_8859_1);
/*  29 */   private static final byte[] TRUE_BYTES = "true".getBytes(StandardCharsets.ISO_8859_1);
/*  30 */   private static final byte[] FALSE_BYTES = "false".getBytes(StandardCharsets.ISO_8859_1);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  35 */   public static final ValueJson NULL = new ValueJson(NULL_BYTES);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   public static final ValueJson TRUE = new ValueJson(TRUE_BYTES);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final ValueJson FALSE = new ValueJson(FALSE_BYTES);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final ValueJson ZERO = new ValueJson(new byte[] { 48 });
/*     */   
/*     */   private ValueJson(byte[] paramArrayOfbyte) {
/*  53 */     super(paramArrayOfbyte);
/*  54 */     int i = paramArrayOfbyte.length;
/*  55 */     if (i > 1048576) {
/*  56 */       throw DbException.getValueTooLongException(getTypeName(getValueType()), 
/*  57 */           StringUtils.convertBytesToHex(paramArrayOfbyte, 41), i);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  63 */     String str = (String)JSONBytesSource.parse(this.value, (JSONTarget)new JSONStringTarget(true));
/*  64 */     return paramStringBuilder.append("JSON '").append(str).append('\'');
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  69 */     return TypeInfo.TYPE_JSON;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  74 */     return 38;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  79 */     return new String(this.value, StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONItemType getItemType() {
/*  88 */     switch (this.value[0]) {
/*     */       case 91:
/*  90 */         return JSONItemType.ARRAY;
/*     */       case 123:
/*  92 */         return JSONItemType.OBJECT;
/*     */     } 
/*  94 */     return JSONItemType.SCALAR;
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
/*     */   public static ValueJson fromJson(String paramString) {
/*     */     byte[] arrayOfByte;
/*     */     try {
/* 110 */       arrayOfByte = JSONStringSource.normalize(paramString);
/* 111 */     } catch (RuntimeException runtimeException) {
/* 112 */       if (paramString.length() > 80) {
/* 113 */         paramString = (new StringBuilder(83)).append(paramString, 0, 80).append("...").toString();
/*     */       }
/* 115 */       throw DbException.get(22018, paramString);
/*     */     } 
/* 117 */     return getInternal(arrayOfByte);
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
/*     */   public static ValueJson fromJson(byte[] paramArrayOfbyte) {
/*     */     try {
/* 131 */       paramArrayOfbyte = JSONBytesSource.normalize(paramArrayOfbyte);
/* 132 */     } catch (RuntimeException runtimeException) {
/* 133 */       StringBuilder stringBuilder = (new StringBuilder()).append("X'");
/* 134 */       if (paramArrayOfbyte.length > 40) {
/* 135 */         StringUtils.convertBytesToHex(stringBuilder, paramArrayOfbyte, 40).append("...");
/*     */       } else {
/* 137 */         StringUtils.convertBytesToHex(stringBuilder, paramArrayOfbyte);
/*     */       } 
/* 139 */       throw DbException.get(22018, stringBuilder.append('\'').toString());
/*     */     } 
/* 141 */     return getInternal(paramArrayOfbyte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueJson get(boolean paramBoolean) {
/* 152 */     return paramBoolean ? TRUE : FALSE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueJson get(int paramInt) {
/* 163 */     return (paramInt != 0) ? getNumber(Integer.toString(paramInt)) : ZERO;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueJson get(long paramLong) {
/* 174 */     return (paramLong != 0L) ? getNumber(Long.toString(paramLong)) : ZERO;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueJson get(BigDecimal paramBigDecimal) {
/* 185 */     if (paramBigDecimal.signum() == 0 && paramBigDecimal.scale() == 0) {
/* 186 */       return ZERO;
/*     */     }
/* 188 */     String str = paramBigDecimal.toString();
/* 189 */     int i = str.indexOf('E');
/* 190 */     if (i >= 0 && str.charAt(++i) == '+') {
/* 191 */       int j = str.length();
/* 192 */       str = (new StringBuilder(j - 1)).append(str, 0, i).append(str, i + 1, j).toString();
/*     */     } 
/* 194 */     return getNumber(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueJson get(String paramString) {
/* 205 */     return new ValueJson(JSONByteArrayTarget.encodeString(new ByteArrayOutputStream(paramString
/* 206 */             .length() + 2), paramString).toByteArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueJson getInternal(byte[] paramArrayOfbyte) {
/* 217 */     int i = paramArrayOfbyte.length;
/* 218 */     switch (i) {
/*     */       case 1:
/* 220 */         if (paramArrayOfbyte[0] == 48) {
/* 221 */           return ZERO;
/*     */         }
/*     */         break;
/*     */       case 4:
/* 225 */         if (Arrays.equals(TRUE_BYTES, paramArrayOfbyte))
/* 226 */           return TRUE; 
/* 227 */         if (Arrays.equals(NULL_BYTES, paramArrayOfbyte)) {
/* 228 */           return NULL;
/*     */         }
/*     */         break;
/*     */       case 5:
/* 232 */         if (Arrays.equals(FALSE_BYTES, paramArrayOfbyte))
/* 233 */           return FALSE; 
/*     */         break;
/*     */     } 
/* 236 */     return new ValueJson(paramArrayOfbyte);
/*     */   }
/*     */   
/*     */   private static ValueJson getNumber(String paramString) {
/* 240 */     return new ValueJson(paramString.getBytes(StandardCharsets.ISO_8859_1));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueJson.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */