/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.LastErrorException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Win32Exception
/*     */   extends LastErrorException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private WinNT.HRESULT _hr;
/*     */   
/*     */   public WinNT.HRESULT getHR() {
/*  48 */     return this._hr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Win32Exception(int code) {
/*  56 */     this(code, W32Errors.HRESULT_FROM_WIN32(code));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Win32Exception(WinNT.HRESULT hr) {
/*  65 */     this(W32Errors.HRESULT_CODE(hr.intValue()), hr);
/*     */   }
/*     */   
/*     */   protected Win32Exception(int code, WinNT.HRESULT hr) {
/*  69 */     this(code, hr, Kernel32Util.formatMessage(hr));
/*     */   }
/*     */   
/*     */   protected Win32Exception(int code, WinNT.HRESULT hr, String msg) {
/*  73 */     super(code, msg);
/*  74 */     this._hr = hr;
/*     */   }
/*     */   
/*  77 */   private static Method addSuppressedMethod = null;
/*     */   static {
/*     */     try {
/*  80 */       addSuppressedMethod = Throwable.class.getMethod("addSuppressed", new Class[] { Throwable.class });
/*  81 */     } catch (NoSuchMethodException noSuchMethodException) {
/*     */     
/*  83 */     } catch (SecurityException ex) {
/*  84 */       Logger.getLogger(Win32Exception.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   void addSuppressedReflected(Throwable exception) {
/*  89 */     if (addSuppressedMethod == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*  94 */       addSuppressedMethod.invoke(this, new Object[] { exception });
/*  95 */     } catch (IllegalAccessException ex) {
/*  96 */       throw new RuntimeException("Failed to call addSuppressedMethod", ex);
/*  97 */     } catch (IllegalArgumentException ex) {
/*  98 */       throw new RuntimeException("Failed to call addSuppressedMethod", ex);
/*  99 */     } catch (InvocationTargetException ex) {
/* 100 */       throw new RuntimeException("Failed to call addSuppressedMethod", ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Win32Exception.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */