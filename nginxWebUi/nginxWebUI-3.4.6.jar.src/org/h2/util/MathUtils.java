/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.concurrent.ThreadLocalRandom;
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
/*     */ public class MathUtils
/*     */ {
/*     */   static SecureRandom secureRandom;
/*     */   static volatile boolean seeded;
/*     */   
/*     */   public static int roundUpInt(int paramInt1, int paramInt2) {
/*  47 */     return paramInt1 + paramInt2 - 1 & -paramInt2;
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
/*     */   public static long roundUpLong(long paramLong1, long paramLong2) {
/*  61 */     return paramLong1 + paramLong2 - 1L & -paramLong2;
/*     */   }
/*     */   
/*     */   private static synchronized SecureRandom getSecureRandom() {
/*  65 */     if (secureRandom != null) {
/*  66 */       return secureRandom;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  73 */       secureRandom = SecureRandom.getInstance("SHA1PRNG");
/*     */ 
/*     */ 
/*     */       
/*  77 */       Runnable runnable = () -> {
/*     */           try {
/*     */             SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
/*     */             byte[] arrayOfByte = secureRandom.generateSeed(20);
/*     */             synchronized (secureRandom) {
/*     */               secureRandom.setSeed(arrayOfByte);
/*     */               seeded = true;
/*     */             } 
/*  85 */           } catch (Exception exception) {
/*     */             warn("SecureRandom", exception);
/*     */           } 
/*     */         };
/*     */ 
/*     */       
/*     */       try {
/*  92 */         Thread thread = new Thread(runnable, "Generate Seed");
/*     */ 
/*     */         
/*  95 */         thread.setDaemon(true);
/*  96 */         thread.start();
/*  97 */         Thread.yield();
/*     */         
/*     */         try {
/* 100 */           thread.join(400L);
/* 101 */         } catch (InterruptedException interruptedException) {
/* 102 */           warn("InterruptedException", interruptedException);
/*     */         } 
/* 104 */         if (!seeded) {
/* 105 */           byte[] arrayOfByte = generateAlternativeSeed();
/*     */           
/* 107 */           synchronized (secureRandom) {
/* 108 */             secureRandom.setSeed(arrayOfByte);
/*     */           } 
/*     */         } 
/* 111 */       } catch (SecurityException securityException) {
/*     */         
/* 113 */         runnable.run();
/* 114 */         generateAlternativeSeed();
/*     */       }
/*     */     
/* 117 */     } catch (Exception exception) {
/*     */       
/* 119 */       warn("SecureRandom", exception);
/* 120 */       secureRandom = new SecureRandom();
/*     */     } 
/* 122 */     return secureRandom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] generateAlternativeSeed() {
/*     */     try {
/* 132 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 133 */       DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
/*     */ 
/*     */       
/* 136 */       dataOutputStream.writeLong(System.currentTimeMillis());
/* 137 */       dataOutputStream.writeLong(System.nanoTime());
/*     */ 
/*     */       
/* 140 */       dataOutputStream.writeInt((new Object()).hashCode());
/* 141 */       Runtime runtime = Runtime.getRuntime();
/* 142 */       dataOutputStream.writeLong(runtime.freeMemory());
/* 143 */       dataOutputStream.writeLong(runtime.maxMemory());
/* 144 */       dataOutputStream.writeLong(runtime.totalMemory());
/*     */ 
/*     */       
/*     */       try {
/* 148 */         String str = System.getProperties().toString();
/*     */ 
/*     */         
/* 151 */         dataOutputStream.writeInt(str.length());
/* 152 */         dataOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
/* 153 */       } catch (Exception exception) {
/* 154 */         warn("generateAlternativeSeed", exception);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 160 */         Class<?> clazz = Class.forName("java.net.InetAddress");
/*     */ 
/*     */         
/* 163 */         Object object = clazz.getMethod("getLocalHost", new Class[0]).invoke(null, new Object[0]);
/*     */         
/* 165 */         String str = clazz.getMethod("getHostName", new Class[0]).invoke(object, new Object[0]).toString();
/* 166 */         dataOutputStream.writeUTF(str);
/*     */         
/* 168 */         Object[] arrayOfObject = (Object[])clazz.getMethod("getAllByName", new Class[] { String.class }).invoke(null, new Object[] { str });
/* 169 */         Method method = clazz.getMethod("getAddress", new Class[0]);
/*     */         
/* 171 */         for (Object object1 : arrayOfObject) {
/* 172 */           dataOutputStream.write((byte[])method.invoke(object1, new Object[0]));
/*     */         }
/* 174 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 181 */       for (byte b = 0; b < 16; b++) {
/* 182 */         byte b1 = 0;
/* 183 */         long l = System.currentTimeMillis();
/* 184 */         while (l == System.currentTimeMillis()) {
/* 185 */           b1++;
/*     */         }
/* 187 */         dataOutputStream.writeInt(b1);
/*     */       } 
/*     */       
/* 190 */       dataOutputStream.close();
/* 191 */       return byteArrayOutputStream.toByteArray();
/* 192 */     } catch (IOException iOException) {
/* 193 */       warn("generateAlternativeSeed", iOException);
/* 194 */       return new byte[1];
/*     */     } 
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
/*     */   static void warn(String paramString, Throwable paramThrowable) {
/* 207 */     System.out.println("Warning: " + paramString);
/* 208 */     if (paramThrowable != null) {
/* 209 */       paramThrowable.printStackTrace();
/*     */     }
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
/*     */   public static int nextPowerOf2(int paramInt) throws IllegalArgumentException {
/* 222 */     if (paramInt + Integer.MIN_VALUE > -1073741824) {
/* 223 */       throw new IllegalArgumentException("Argument out of range [0x0-0x40000000]. Argument was: " + paramInt);
/*     */     }
/*     */     
/* 226 */     return (paramInt <= 1) ? 1 : ((-1 >>> Integer.numberOfLeadingZeros(paramInt - 1)) + 1);
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
/*     */   public static int convertLongToInt(long paramLong) {
/* 238 */     if (paramLong <= -2147483648L)
/* 239 */       return Integer.MIN_VALUE; 
/* 240 */     if (paramLong >= 2147483647L) {
/* 241 */       return Integer.MAX_VALUE;
/*     */     }
/* 243 */     return (int)paramLong;
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
/*     */   public static short convertIntToShort(int paramInt) {
/* 256 */     if (paramInt <= -32768)
/* 257 */       return Short.MIN_VALUE; 
/* 258 */     if (paramInt >= 32767) {
/* 259 */       return Short.MAX_VALUE;
/*     */     }
/* 261 */     return (short)paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long secureRandomLong() {
/* 271 */     return getSecureRandom().nextLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void randomBytes(byte[] paramArrayOfbyte) {
/* 280 */     ThreadLocalRandom.current().nextBytes(paramArrayOfbyte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] secureRandomBytes(int paramInt) {
/* 290 */     if (paramInt <= 0) {
/* 291 */       paramInt = 1;
/*     */     }
/* 293 */     byte[] arrayOfByte = new byte[paramInt];
/* 294 */     getSecureRandom().nextBytes(arrayOfByte);
/* 295 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int randomInt(int paramInt) {
/* 306 */     return ThreadLocalRandom.current().nextInt(paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int secureRandomInt(int paramInt) {
/* 317 */     return getSecureRandom().nextInt(paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\MathUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */