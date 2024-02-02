/*    */ package org.h2.value;
/*    */ 
/*    */ import org.h2.engine.SysProperties;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.util.StringUtils;
/*    */ import org.h2.util.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ValueJavaObject
/*    */   extends ValueBytesBase
/*    */ {
/* 20 */   private static final ValueJavaObject EMPTY = new ValueJavaObject(Utils.EMPTY_BYTES);
/*    */   
/*    */   protected ValueJavaObject(byte[] paramArrayOfbyte) {
/* 23 */     super(paramArrayOfbyte);
/* 24 */     int i = this.value.length;
/* 25 */     if (i > 1048576) {
/* 26 */       throw DbException.getValueTooLongException(getTypeName(getValueType()), 
/* 27 */           StringUtils.convertBytesToHex(this.value, 41), i);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueJavaObject getNoCopy(byte[] paramArrayOfbyte) {
/* 39 */     int i = paramArrayOfbyte.length;
/* 40 */     if (i == 0) {
/* 41 */       return EMPTY;
/*    */     }
/* 43 */     ValueJavaObject valueJavaObject = new ValueJavaObject(paramArrayOfbyte);
/* 44 */     if (i > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
/* 45 */       return valueJavaObject;
/*    */     }
/* 47 */     return (ValueJavaObject)Value.cache(valueJavaObject);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 52 */     return TypeInfo.TYPE_JAVA_OBJECT;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 57 */     return 35;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 62 */     if ((paramInt & 0x4) == 0) {
/* 63 */       return super.getSQL(paramStringBuilder.append("CAST("), 0).append(" AS JAVA_OBJECT)");
/*    */     }
/* 65 */     return super.getSQL(paramStringBuilder, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getString() {
/* 70 */     throw DbException.get(22018, "JAVA_OBJECT to CHARACTER VARYING");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueJavaObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */