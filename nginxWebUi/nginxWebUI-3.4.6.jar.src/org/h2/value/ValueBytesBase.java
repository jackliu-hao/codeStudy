/*    */ package org.h2.value;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.util.Bits;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class ValueBytesBase
/*    */   extends Value
/*    */ {
/*    */   byte[] value;
/*    */   int hash;
/*    */   
/*    */   ValueBytesBase(byte[] paramArrayOfbyte) {
/* 31 */     this.value = paramArrayOfbyte;
/*    */   }
/*    */ 
/*    */   
/*    */   public final byte[] getBytes() {
/* 36 */     return Utils.cloneByteArray(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public final byte[] getBytesNoCopy() {
/* 41 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public final int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 46 */     return Bits.compareNotNullUnsigned(this.value, ((ValueBytesBase)paramValue).value);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 51 */     return StringUtils.convertBytesToHex(paramStringBuilder.append("X'"), this.value).append('\'');
/*    */   }
/*    */ 
/*    */   
/*    */   public final int hashCode() {
/* 56 */     int i = this.hash;
/* 57 */     if (i == 0) {
/* 58 */       i = getClass().hashCode() ^ Utils.getByteArrayHash(this.value);
/* 59 */       if (i == 0) {
/* 60 */         i = 1234570417;
/*    */       }
/* 62 */       this.hash = i;
/*    */     } 
/* 64 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMemory() {
/* 69 */     return this.value.length + 24;
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean equals(Object paramObject) {
/* 74 */     return (paramObject != null && getClass() == paramObject.getClass() && Arrays.equals(this.value, ((ValueBytesBase)paramObject).value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueBytesBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */