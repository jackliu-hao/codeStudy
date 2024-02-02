/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RunningObjectTable
/*     */   extends Unknown
/*     */   implements IRunningObjectTable
/*     */ {
/*     */   public static class ByReference
/*     */     extends RunningObjectTable
/*     */     implements Structure.ByReference {}
/*     */   
/*     */   public RunningObjectTable() {}
/*     */   
/*     */   public RunningObjectTable(Pointer pointer) {
/*  44 */     super(pointer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT Register(WinDef.DWORD grfFlags, Pointer punkObject, Pointer pmkObjectName, WinDef.DWORDByReference pdwRegister) {
/*  53 */     int vTableId = 3;
/*     */     
/*  55 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), grfFlags, punkObject, pmkObjectName, pdwRegister }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/*  58 */     return hr;
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT Revoke(WinDef.DWORD dwRegister) {
/*  63 */     int vTableId = 4;
/*     */     
/*  65 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), dwRegister }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/*  68 */     return hr;
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT IsRunning(Pointer pmkObjectName) {
/*  73 */     int vTableId = 5;
/*     */     
/*  75 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), pmkObjectName }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/*  78 */     return hr;
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetObject(Pointer pmkObjectName, PointerByReference ppunkObject) {
/*  83 */     int vTableId = 6;
/*     */     
/*  85 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), pmkObjectName, ppunkObject }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/*  88 */     return hr;
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT NoteChangeTime(WinDef.DWORD dwRegister, WinBase.FILETIME pfiletime) {
/*  93 */     int vTableId = 7;
/*     */     
/*  95 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(7, new Object[] { getPointer(), dwRegister, pfiletime }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/*  98 */     return hr;
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetTimeOfLastChange(Pointer pmkObjectName, WinBase.FILETIME.ByReference pfiletime) {
/* 103 */     int vTableId = 8;
/*     */     
/* 105 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(8, new Object[] { getPointer(), pmkObjectName, pfiletime }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/* 108 */     return hr;
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT EnumRunning(PointerByReference ppenumMoniker) {
/* 113 */     int vTableId = 9;
/*     */     
/* 115 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(9, new Object[] { getPointer(), ppenumMoniker }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/* 118 */     return hr;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\RunningObjectTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */