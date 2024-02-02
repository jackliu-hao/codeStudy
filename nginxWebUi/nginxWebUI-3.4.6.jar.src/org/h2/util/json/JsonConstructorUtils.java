/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueJson;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public final class JsonConstructorUtils
/*     */ {
/*     */   public static final int JSON_ABSENT_ON_NULL = 1;
/*     */   public static final int JSON_WITH_UNIQUE_KEYS = 2;
/*     */   
/*     */   public static void jsonObjectAppend(ByteArrayOutputStream paramByteArrayOutputStream, String paramString, Value paramValue) {
/*  45 */     if (paramByteArrayOutputStream.size() > 1) {
/*  46 */       paramByteArrayOutputStream.write(44);
/*     */     }
/*  48 */     JSONByteArrayTarget.encodeString(paramByteArrayOutputStream, paramString).write(58);
/*  49 */     byte[] arrayOfByte = paramValue.convertTo(TypeInfo.TYPE_JSON).getBytesNoCopy();
/*  50 */     paramByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
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
/*     */   public static Value jsonObjectFinish(ByteArrayOutputStream paramByteArrayOutputStream, int paramInt) {
/*  67 */     paramByteArrayOutputStream.write(125);
/*  68 */     byte[] arrayOfByte = paramByteArrayOutputStream.toByteArray();
/*  69 */     if ((paramInt & 0x2) != 0) {
/*     */       try {
/*  71 */         JSONBytesSource.parse(arrayOfByte, new JSONValidationTargetWithUniqueKeys());
/*  72 */       } catch (RuntimeException runtimeException) {
/*  73 */         String str = JSONBytesSource.<String>parse(arrayOfByte, new JSONStringTarget());
/*  74 */         throw DbException.getInvalidValueException("JSON WITH UNIQUE KEYS", 
/*  75 */             (str.length() < 128) ? arrayOfByte : (str.substring(0, 128) + "..."));
/*     */       } 
/*     */     }
/*  78 */     return (Value)ValueJson.getInternal(arrayOfByte);
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
/*     */   public static void jsonArrayAppend(ByteArrayOutputStream paramByteArrayOutputStream, Value paramValue, int paramInt) {
/*     */     ValueJson valueJson;
/*  92 */     if (paramValue == ValueNull.INSTANCE) {
/*  93 */       if ((paramInt & 0x1) != 0) {
/*     */         return;
/*     */       }
/*  96 */       valueJson = ValueJson.NULL;
/*     */     } 
/*  98 */     if (paramByteArrayOutputStream.size() > 1) {
/*  99 */       paramByteArrayOutputStream.write(44);
/*     */     }
/* 101 */     byte[] arrayOfByte = valueJson.convertTo(TypeInfo.TYPE_JSON).getBytesNoCopy();
/* 102 */     paramByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JsonConstructorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */