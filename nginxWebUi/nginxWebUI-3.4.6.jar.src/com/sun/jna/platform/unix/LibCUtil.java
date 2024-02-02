/*     */ package com.sun.jna.platform.unix;
/*     */ 
/*     */ import com.sun.jna.Function;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.NativeLibrary;
/*     */ import com.sun.jna.Pointer;
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
/*     */ public class LibCUtil
/*     */ {
/*  39 */   private static final NativeLibrary LIBC = NativeLibrary.getInstance("c");
/*     */   
/*  41 */   private static Function mmap = null;
/*     */   private static boolean mmap64 = false;
/*  43 */   private static Function ftruncate = null;
/*     */   
/*     */   static {
/*     */     try {
/*  47 */       mmap = LIBC.getFunction("mmap64", 64);
/*  48 */       mmap64 = true;
/*  49 */     } catch (UnsatisfiedLinkError ex) {
/*  50 */       mmap = LIBC.getFunction("mmap", 64);
/*     */     } 
/*     */     try {
/*  53 */       ftruncate = LIBC.getFunction("ftruncate64", 64);
/*  54 */       ftruncate64 = true;
/*  55 */     } catch (UnsatisfiedLinkError ex) {
/*  56 */       ftruncate = LIBC.getFunction("ftruncate", 64);
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
/*     */   private static boolean ftruncate64 = false;
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
/*     */   public static Pointer mmap(Pointer addr, long length, int prot, int flags, int fd, long offset) {
/* 109 */     Object[] params = new Object[6];
/* 110 */     params[0] = addr;
/* 111 */     if (Native.SIZE_T_SIZE == 4) {
/* 112 */       require32Bit(length, "length");
/* 113 */       params[1] = Integer.valueOf((int)length);
/*     */     } else {
/* 115 */       params[1] = Long.valueOf(length);
/*     */     } 
/* 117 */     params[2] = Integer.valueOf(prot);
/* 118 */     params[3] = Integer.valueOf(flags);
/* 119 */     params[4] = Integer.valueOf(fd);
/* 120 */     if (mmap64 || Native.LONG_SIZE > 4) {
/* 121 */       params[5] = Long.valueOf(offset);
/*     */     } else {
/* 123 */       require32Bit(offset, "offset");
/* 124 */       params[5] = Integer.valueOf((int)offset);
/*     */     } 
/* 126 */     return mmap.invokePointer(params);
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
/*     */   
/*     */   public static int ftruncate(int fd, long length) {
/* 147 */     Object[] params = new Object[2];
/* 148 */     params[0] = Integer.valueOf(fd);
/* 149 */     if (ftruncate64 || Native.LONG_SIZE > 4) {
/* 150 */       params[1] = Long.valueOf(length);
/*     */     } else {
/* 152 */       require32Bit(length, "length");
/* 153 */       params[1] = Integer.valueOf((int)length);
/*     */     } 
/* 155 */     return ftruncate.invokeInt(params);
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
/*     */   public static void require32Bit(long val, String value) {
/* 170 */     if (val > 2147483647L)
/* 171 */       throw new IllegalArgumentException(value + " exceeds 32bit"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platfor\\unix\LibCUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */