/*    */ package org.h2.value;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ 
/*    */ 
/*    */ public final class ValueVarbinary
/*    */   extends ValueBytesBase
/*    */ {
/* 23 */   public static final ValueVarbinary EMPTY = new ValueVarbinary(Utils.EMPTY_BYTES);
/*    */ 
/*    */   
/*    */   private TypeInfo type;
/*    */ 
/*    */ 
/*    */   
/*    */   protected ValueVarbinary(byte[] paramArrayOfbyte) {
/* 31 */     super(paramArrayOfbyte);
/* 32 */     int i = paramArrayOfbyte.length;
/* 33 */     if (i > 1048576) {
/* 34 */       throw DbException.getValueTooLongException(getTypeName(getValueType()), 
/* 35 */           StringUtils.convertBytesToHex(paramArrayOfbyte, 41), i);
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
/*    */   public static ValueVarbinary get(byte[] paramArrayOfbyte) {
/* 47 */     if (paramArrayOfbyte.length == 0) {
/* 48 */       return EMPTY;
/*    */     }
/* 50 */     paramArrayOfbyte = Utils.cloneByteArray(paramArrayOfbyte);
/* 51 */     return getNoCopy(paramArrayOfbyte);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueVarbinary getNoCopy(byte[] paramArrayOfbyte) {
/* 62 */     if (paramArrayOfbyte.length == 0) {
/* 63 */       return EMPTY;
/*    */     }
/* 65 */     ValueVarbinary valueVarbinary = new ValueVarbinary(paramArrayOfbyte);
/* 66 */     if (paramArrayOfbyte.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
/* 67 */       return valueVarbinary;
/*    */     }
/* 69 */     return (ValueVarbinary)Value.cache(valueVarbinary);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 74 */     TypeInfo typeInfo = this.type;
/* 75 */     if (typeInfo == null) {
/* 76 */       long l = this.value.length;
/* 77 */       this.type = typeInfo = new TypeInfo(6, l, 0, null);
/*    */     } 
/* 79 */     return typeInfo;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 84 */     return 6;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getString() {
/* 89 */     return new String(this.value, StandardCharsets.UTF_8);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueVarbinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */