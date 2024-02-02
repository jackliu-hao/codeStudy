/*     */ package META-INF.versions.9.org.h2.util;
/*     */ 
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.invoke.VarHandle;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ import java.util.UUID;
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
/*     */ public final class Bits
/*     */ {
/*  24 */   private static final VarHandle INT_VH_BE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  30 */   private static final VarHandle INT_VH_LE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   private static final VarHandle LONG_VH_BE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final VarHandle LONG_VH_LE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final VarHandle DOUBLE_VH_BE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.BIG_ENDIAN);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final VarHandle DOUBLE_VH_LE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);
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
/*     */   public static int compareNotNull(char[] paramArrayOfchar1, char[] paramArrayOfchar2) {
/*  74 */     return Integer.signum(Arrays.compare(paramArrayOfchar1, paramArrayOfchar2));
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
/*     */   public static int compareNotNullSigned(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  94 */     return Integer.signum(Arrays.compare(paramArrayOfbyte1, paramArrayOfbyte2));
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
/*     */   public static int compareNotNullUnsigned(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/* 114 */     return Integer.signum(Arrays.compareUnsigned(paramArrayOfbyte1, paramArrayOfbyte2));
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
/*     */   public static int readInt(byte[] paramArrayOfbyte, int paramInt) {
/* 128 */     return INT_VH_BE.get(paramArrayOfbyte, paramInt);
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
/*     */   public static int readIntLE(byte[] paramArrayOfbyte, int paramInt) {
/* 142 */     return INT_VH_LE.get(paramArrayOfbyte, paramInt);
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
/*     */   public static long readLong(byte[] paramArrayOfbyte, int paramInt) {
/* 156 */     return LONG_VH_BE.get(paramArrayOfbyte, paramInt);
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
/*     */   public static long readLongLE(byte[] paramArrayOfbyte, int paramInt) {
/* 170 */     return LONG_VH_LE.get(paramArrayOfbyte, paramInt);
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
/*     */   public static double readDouble(byte[] paramArrayOfbyte, int paramInt) {
/* 184 */     return DOUBLE_VH_BE.get(paramArrayOfbyte, paramInt);
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
/*     */   public static double readDoubleLE(byte[] paramArrayOfbyte, int paramInt) {
/* 198 */     return DOUBLE_VH_LE.get(paramArrayOfbyte, paramInt);
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
/*     */   public static byte[] uuidToBytes(long paramLong1, long paramLong2) {
/* 211 */     byte[] arrayOfByte = new byte[16];
/* 212 */     LONG_VH_BE.set(arrayOfByte, 0, paramLong1);
/* 213 */     LONG_VH_BE.set(arrayOfByte, 8, paramLong2);
/* 214 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] uuidToBytes(UUID paramUUID) {
/* 225 */     return uuidToBytes(paramUUID.getMostSignificantBits(), paramUUID.getLeastSignificantBits());
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
/*     */   public static void writeInt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 240 */     INT_VH_BE.set(paramArrayOfbyte, paramInt1, paramInt2);
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
/*     */   public static void writeIntLE(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 255 */     INT_VH_LE.set(paramArrayOfbyte, paramInt1, paramInt2);
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
/*     */   public static void writeLong(byte[] paramArrayOfbyte, int paramInt, long paramLong) {
/* 270 */     LONG_VH_BE.set(paramArrayOfbyte, paramInt, paramLong);
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
/*     */   public static void writeLongLE(byte[] paramArrayOfbyte, int paramInt, long paramLong) {
/* 285 */     LONG_VH_LE.set(paramArrayOfbyte, paramInt, paramLong);
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
/*     */   public static void writeDouble(byte[] paramArrayOfbyte, int paramInt, double paramDouble) {
/* 300 */     DOUBLE_VH_BE.set(paramArrayOfbyte, paramInt, paramDouble);
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
/*     */   public static void writeDoubleLE(byte[] paramArrayOfbyte, int paramInt, double paramDouble) {
/* 315 */     DOUBLE_VH_LE.set(paramArrayOfbyte, paramInt, paramDouble);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\9\org\h\\util\Bits.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */