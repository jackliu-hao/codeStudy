/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Native;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Shell32Util
/*     */ {
/*     */   public static String getFolderPath(WinDef.HWND hwnd, int nFolder, WinDef.DWORD dwFlags) {
/*  51 */     char[] pszPath = new char[260];
/*  52 */     WinNT.HRESULT hr = Shell32.INSTANCE.SHGetFolderPath(hwnd, nFolder, null, dwFlags, pszPath);
/*     */     
/*  54 */     if (!hr.equals(W32Errors.S_OK)) {
/*  55 */       throw new Win32Exception(hr);
/*     */     }
/*  57 */     return Native.toString(pszPath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getFolderPath(int nFolder) {
/*  68 */     return getFolderPath(null, nFolder, ShlObj.SHGFP_TYPE_CURRENT);
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
/*     */   public static String getKnownFolderPath(Guid.GUID guid) throws Win32Exception {
/*  83 */     int flags = ShlObj.KNOWN_FOLDER_FLAG.NONE.getFlag();
/*  84 */     PointerByReference outPath = new PointerByReference();
/*  85 */     WinNT.HANDLE token = null;
/*  86 */     WinNT.HRESULT hr = Shell32.INSTANCE.SHGetKnownFolderPath(guid, flags, token, outPath);
/*     */     
/*  88 */     if (!W32Errors.SUCCEEDED(hr.intValue()))
/*     */     {
/*  90 */       throw new Win32Exception(hr);
/*     */     }
/*     */     
/*  93 */     String result = outPath.getValue().getWideString(0L);
/*  94 */     Ole32.INSTANCE.CoTaskMemFree(outPath.getValue());
/*     */     
/*  96 */     return result;
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
/*     */   public static final String getSpecialFolderPath(int csidl, boolean create) {
/* 110 */     char[] pszPath = new char[260];
/* 111 */     if (!Shell32.INSTANCE.SHGetSpecialFolderPath(null, pszPath, csidl, create))
/* 112 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
/* 113 */     return Native.toString(pszPath);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Shell32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */