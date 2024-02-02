/*     */ package com.sun.jna.platform.win32;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class W32Errors
/*     */   implements WinError
/*     */ {
/*     */   public static final boolean SUCCEEDED(int hr) {
/*  42 */     return (hr >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean FAILED(int hr) {
/*  53 */     return (hr < 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean SUCCEEDED(WinNT.HRESULT hr) {
/*  64 */     return (hr == null || SUCCEEDED(hr.intValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean FAILED(WinNT.HRESULT hr) {
/*  75 */     return (hr != null && FAILED(hr.intValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int HRESULT_CODE(int hr) {
/*  86 */     return hr & 0xFFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SCODE_CODE(int sc) {
/*  97 */     return sc & 0xFFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int HRESULT_FACILITY(int hr) {
/* 108 */     return (hr >>= 16) & 0x1FFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SCODE_FACILITY(short sc) {
/* 119 */     return (sc = (short)(sc >> 16)) & 0x1FFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short HRESULT_SEVERITY(int hr) {
/* 130 */     return (short)((hr >>= 31) & 0x1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short SCODE_SEVERITY(short sc) {
/* 141 */     return (short)((sc = (short)(sc >> 31)) & 0x1);
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
/*     */   public static int MAKE_HRESULT(short sev, short fac, short code) {
/* 154 */     return sev << 31 | fac << 16 | code;
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
/*     */   public static final int MAKE_SCODE(short sev, short fac, short code) {
/* 167 */     return sev << 31 | fac << 16 | code;
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
/*     */   public static final WinNT.HRESULT HRESULT_FROM_WIN32(int x) {
/* 179 */     int f = 7;
/* 180 */     return new WinNT.HRESULT((x <= 0) ? x : (x & 0xFFFF | (f <<= 16) | Integer.MIN_VALUE));
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
/*     */   public static final int FILTER_HRESULT_FROM_FLT_NTSTATUS(int x) {
/* 194 */     int f = 31;
/* 195 */     return x & 0x8000FFFF | (f <<= 16);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\W32Errors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */