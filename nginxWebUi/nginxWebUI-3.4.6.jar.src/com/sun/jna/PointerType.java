/*     */ package com.sun.jna;
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
/*     */ public abstract class PointerType
/*     */   implements NativeMapped
/*     */ {
/*     */   private Pointer pointer;
/*     */   
/*     */   protected PointerType() {
/*  38 */     this.pointer = Pointer.NULL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PointerType(Pointer p) {
/*  45 */     this.pointer = p;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> nativeType() {
/*  51 */     return Pointer.class;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object toNative() {
/*  57 */     return getPointer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pointer getPointer() {
/*  64 */     return this.pointer;
/*     */   }
/*     */   
/*     */   public void setPointer(Pointer p) {
/*  68 */     this.pointer = p;
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
/*     */   public Object fromNative(Object nativeValue, FromNativeContext context) {
/*  80 */     if (nativeValue == null) {
/*  81 */       return null;
/*     */     }
/*  83 */     PointerType pt = (PointerType)Klass.newInstance(getClass());
/*  84 */     pt.pointer = (Pointer)nativeValue;
/*  85 */     return pt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  93 */     return (this.pointer != null) ? this.pointer.hashCode() : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 101 */     if (o == this) {
/* 102 */       return true;
/*     */     }
/* 104 */     if (o instanceof PointerType) {
/* 105 */       Pointer p = ((PointerType)o).getPointer();
/* 106 */       if (this.pointer == null) {
/* 107 */         return (p == null);
/*     */       }
/* 109 */       return this.pointer.equals(p);
/*     */     } 
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 116 */     return (this.pointer == null) ? "NULL" : (this.pointer.toString() + " (" + super.toString() + ")");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\PointerType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */