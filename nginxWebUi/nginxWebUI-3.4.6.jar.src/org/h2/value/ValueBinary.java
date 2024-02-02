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
/*    */ public final class ValueBinary
/*    */   extends ValueBytesBase
/*    */ {
/*    */   private TypeInfo type;
/*    */   
/*    */   protected ValueBinary(byte[] paramArrayOfbyte) {
/* 26 */     super(paramArrayOfbyte);
/* 27 */     int i = paramArrayOfbyte.length;
/* 28 */     if (i > 1048576) {
/* 29 */       throw DbException.getValueTooLongException(getTypeName(getValueType()), 
/* 30 */           StringUtils.convertBytesToHex(paramArrayOfbyte, 41), i);
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
/*    */   public static ValueBinary get(byte[] paramArrayOfbyte) {
/* 42 */     return getNoCopy(Utils.cloneByteArray(paramArrayOfbyte));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueBinary getNoCopy(byte[] paramArrayOfbyte) {
/* 53 */     ValueBinary valueBinary = new ValueBinary(paramArrayOfbyte);
/* 54 */     if (paramArrayOfbyte.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
/* 55 */       return valueBinary;
/*    */     }
/* 57 */     return (ValueBinary)Value.cache(valueBinary);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 62 */     TypeInfo typeInfo = this.type;
/* 63 */     if (typeInfo == null) {
/* 64 */       long l = this.value.length;
/* 65 */       this.type = typeInfo = new TypeInfo(5, l, 0, null);
/*    */     } 
/* 67 */     return typeInfo;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 72 */     return 5;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 77 */     if ((paramInt & 0x4) == 0) {
/* 78 */       int i = this.value.length;
/* 79 */       return super.getSQL(paramStringBuilder.append("CAST("), paramInt).append(" AS BINARY(")
/* 80 */         .append((i > 0) ? i : 1).append("))");
/*    */     } 
/* 82 */     return super.getSQL(paramStringBuilder, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getString() {
/* 87 */     return new String(this.value, StandardCharsets.UTF_8);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueBinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */