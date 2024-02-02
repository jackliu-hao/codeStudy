/*     */ package org.h2.value;
/*     */ 
/*     */ import java.util.UUID;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public final class ValueUuid
/*     */   extends Value
/*     */ {
/*     */   static final int PRECISION = 16;
/*     */   static final int DISPLAY_SIZE = 36;
/*     */   private final long high;
/*     */   private final long low;
/*     */   
/*     */   private ValueUuid(long paramLong1, long paramLong2) {
/*  36 */     this.high = paramLong1;
/*  37 */     this.low = paramLong2;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  42 */     return (int)(this.high >>> 32L ^ this.high ^ this.low >>> 32L ^ this.low);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueUuid getNewRandom() {
/*  51 */     long l1 = MathUtils.secureRandomLong();
/*  52 */     long l2 = MathUtils.secureRandomLong();
/*     */     
/*  54 */     l1 = l1 & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
/*     */     
/*  56 */     l2 = l2 & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
/*  57 */     return new ValueUuid(l1, l2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueUuid get(byte[] paramArrayOfbyte) {
/*  67 */     int i = paramArrayOfbyte.length;
/*  68 */     if (i != 16) {
/*  69 */       throw DbException.get(22018, "UUID requires 16 bytes, got " + i);
/*     */     }
/*  71 */     return get(Bits.readLong(paramArrayOfbyte, 0), Bits.readLong(paramArrayOfbyte, 8));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueUuid get(long paramLong1, long paramLong2) {
/*  82 */     return (ValueUuid)Value.cache(new ValueUuid(paramLong1, paramLong2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueUuid get(UUID paramUUID) {
/*  92 */     return get(paramUUID.getMostSignificantBits(), paramUUID.getLeastSignificantBits());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueUuid get(String paramString) {
/* 102 */     long l1 = 0L, l2 = 0L;
/* 103 */     byte b1 = 0; byte b2; int i;
/* 104 */     for (b2 = 0, i = paramString.length(); b2 < i; b2++) {
/* 105 */       char c = paramString.charAt(b2);
/* 106 */       if (c >= '0' && c <= '9')
/* 107 */       { l1 = l1 << 4L | (c - 48); }
/* 108 */       else if (c >= 'a' && c <= 'f')
/* 109 */       { l1 = l1 << 4L | (c - 87); }
/* 110 */       else { if (c == '-')
/*     */           continue; 
/* 112 */         if (c >= 'A' && c <= 'F')
/* 113 */         { l1 = l1 << 4L | (c - 55); }
/* 114 */         else { if (c > ' ')
/*     */           {
/*     */             
/* 117 */             throw DbException.get(22018, paramString); }  continue; }
/*     */          }
/* 119 */        if (++b1 == 16) {
/* 120 */         l2 = l1;
/* 121 */         l1 = 0L;
/*     */       }  continue;
/*     */     } 
/* 124 */     if (b1 != 32) {
/* 125 */       throw DbException.get(22018, paramString);
/*     */     }
/* 127 */     return get(l2, l1);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 132 */     return addString(paramStringBuilder.append("UUID '")).append('\'');
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 137 */     return TypeInfo.TYPE_UUID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 142 */     return 32;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 147 */     return 39;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 152 */     return addString(new StringBuilder(36)).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 157 */     return Bits.uuidToBytes(this.high, this.low);
/*     */   }
/*     */   
/*     */   private StringBuilder addString(StringBuilder paramStringBuilder) {
/* 161 */     StringUtils.appendHex(paramStringBuilder, this.high >> 32L, 4).append('-');
/* 162 */     StringUtils.appendHex(paramStringBuilder, this.high >> 16L, 2).append('-');
/* 163 */     StringUtils.appendHex(paramStringBuilder, this.high, 2).append('-');
/* 164 */     StringUtils.appendHex(paramStringBuilder, this.low >> 48L, 2).append('-');
/* 165 */     return StringUtils.appendHex(paramStringBuilder, this.low, 6);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 170 */     if (paramValue == this) {
/* 171 */       return 0;
/*     */     }
/* 173 */     ValueUuid valueUuid = (ValueUuid)paramValue;
/* 174 */     int i = Long.compareUnsigned(this.high, valueUuid.high);
/* 175 */     return (i != 0) ? i : Long.compareUnsigned(this.low, valueUuid.low);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 180 */     if (!(paramObject instanceof ValueUuid)) {
/* 181 */       return false;
/*     */     }
/* 183 */     ValueUuid valueUuid = (ValueUuid)paramObject;
/* 184 */     return (this.high == valueUuid.high && this.low == valueUuid.low);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UUID getUuid() {
/* 193 */     return new UUID(this.high, this.low);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getHigh() {
/* 202 */     return this.high;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLow() {
/* 211 */     return this.low;
/*     */   }
/*     */ 
/*     */   
/*     */   public long charLength() {
/* 216 */     return 36L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long octetLength() {
/* 221 */     return 16L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueUuid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */