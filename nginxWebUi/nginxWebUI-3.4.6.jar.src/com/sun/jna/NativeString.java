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
/*     */ class NativeString
/*     */   implements CharSequence, Comparable
/*     */ {
/*     */   static final String WIDE_STRING = "--WIDE-STRING--";
/*     */   private Pointer pointer;
/*     */   private String encoding;
/*     */   
/*     */   private class StringMemory
/*     */     extends Memory
/*     */   {
/*     */     public StringMemory(long size) {
/*  40 */       super(size);
/*     */     }
/*     */     public String toString() {
/*  43 */       return NativeString.this.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NativeString(String string) {
/*  51 */     this(string, Native.getDefaultStringEncoding());
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
/*     */   public NativeString(String string, boolean wide) {
/*  63 */     this(string, wide ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NativeString(WString string) {
/*  70 */     this(string.toString(), "--WIDE-STRING--");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NativeString(String string, String encoding) {
/*  77 */     if (string == null) {
/*  78 */       throw new NullPointerException("String must not be null");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  83 */     this.encoding = encoding;
/*  84 */     if ("--WIDE-STRING--".equals(this.encoding)) {
/*  85 */       int len = (string.length() + 1) * Native.WCHAR_SIZE;
/*  86 */       this.pointer = new StringMemory(len);
/*  87 */       this.pointer.setWideString(0L, string);
/*     */     } else {
/*  89 */       byte[] data = Native.getBytes(string, encoding);
/*  90 */       this.pointer = new StringMemory((data.length + 1));
/*  91 */       this.pointer.write(0L, data, 0, data.length);
/*  92 */       this.pointer.setByte(data.length, (byte)0);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  98 */     return toString().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 103 */     if (other instanceof CharSequence) {
/* 104 */       return (compareTo(other) == 0);
/*     */     }
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 111 */     boolean wide = "--WIDE-STRING--".equals(this.encoding);
/* 112 */     return wide ? this.pointer.getWideString(0L) : this.pointer.getString(0L, this.encoding);
/*     */   }
/*     */   
/*     */   public Pointer getPointer() {
/* 116 */     return this.pointer;
/*     */   }
/*     */ 
/*     */   
/*     */   public char charAt(int index) {
/* 121 */     return toString().charAt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int length() {
/* 126 */     return toString().length();
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence subSequence(int start, int end) {
/* 131 */     return toString().subSequence(start, end);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(Object other) {
/* 136 */     if (other == null) {
/* 137 */       return 1;
/*     */     }
/* 139 */     return toString().compareTo(other.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\NativeString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */