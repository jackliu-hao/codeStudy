/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.IntegerType;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.ptr.ByReference;
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
/*     */ public interface BaseTSD
/*     */ {
/*     */   public static class LONG_PTR
/*     */     extends IntegerType
/*     */   {
/*     */     public LONG_PTR() {
/*  44 */       this(0L);
/*     */     }
/*     */     
/*     */     public LONG_PTR(long value) {
/*  48 */       super(Native.POINTER_SIZE, value);
/*     */     }
/*     */     
/*     */     public Pointer toPointer() {
/*  52 */       return Pointer.createConstant(longValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class SSIZE_T
/*     */     extends LONG_PTR
/*     */   {
/*     */     public SSIZE_T() {
/*  61 */       this(0L);
/*     */     }
/*     */     
/*     */     public SSIZE_T(long value) {
/*  65 */       super(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ULONG_PTR
/*     */     extends IntegerType
/*     */   {
/*     */     public ULONG_PTR() {
/*  74 */       this(0L);
/*     */     }
/*     */     
/*     */     public ULONG_PTR(long value) {
/*  78 */       super(Native.POINTER_SIZE, value, true);
/*     */     }
/*     */     
/*     */     public Pointer toPointer() {
/*  82 */       return Pointer.createConstant(longValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ULONG_PTRByReference
/*     */     extends ByReference
/*     */   {
/*     */     public ULONG_PTRByReference() {
/*  91 */       this(new BaseTSD.ULONG_PTR(0L));
/*     */     }
/*     */     public ULONG_PTRByReference(BaseTSD.ULONG_PTR value) {
/*  94 */       super(Native.POINTER_SIZE);
/*  95 */       setValue(value);
/*     */     }
/*     */     public void setValue(BaseTSD.ULONG_PTR value) {
/*  98 */       if (Native.POINTER_SIZE == 4) {
/*  99 */         getPointer().setInt(0L, value.intValue());
/*     */       } else {
/*     */         
/* 102 */         getPointer().setLong(0L, value.longValue());
/*     */       } 
/*     */     }
/*     */     public BaseTSD.ULONG_PTR getValue() {
/* 106 */       return new BaseTSD.ULONG_PTR((Native.POINTER_SIZE == 4) ? 
/* 107 */           getPointer().getInt(0L) : 
/* 108 */           getPointer().getLong(0L));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class DWORD_PTR
/*     */     extends IntegerType
/*     */   {
/*     */     public DWORD_PTR() {
/* 118 */       this(0L);
/*     */     }
/*     */     
/*     */     public DWORD_PTR(long value) {
/* 122 */       super(Native.POINTER_SIZE, value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class SIZE_T
/*     */     extends ULONG_PTR
/*     */   {
/*     */     public SIZE_T() {
/* 132 */       this(0L);
/*     */     }
/*     */     
/*     */     public SIZE_T(long value) {
/* 136 */       super(value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\BaseTSD.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */