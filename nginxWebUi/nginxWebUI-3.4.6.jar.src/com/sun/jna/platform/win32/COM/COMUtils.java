/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.LastErrorException;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Advapi32;
/*     */ import com.sun.jna.platform.win32.Advapi32Util;
/*     */ import com.sun.jna.platform.win32.Kernel32Util;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.W32Errors;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.platform.win32.WinReg;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class COMUtils
/*     */ {
/*     */   public static final int S_OK = 0;
/*     */   public static final int S_FALSE = 1;
/*     */   public static final int E_UNEXPECTED = -2147418113;
/*     */   
/*     */   public static boolean SUCCEEDED(WinNT.HRESULT hr) {
/*  66 */     return SUCCEEDED(hr.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean SUCCEEDED(int hr) {
/*  77 */     return (hr >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean FAILED(WinNT.HRESULT hr) {
/*  88 */     return FAILED(hr.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean FAILED(int hr) {
/*  99 */     return (hr < 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkRC(WinNT.HRESULT hr) {
/* 109 */     if (FAILED(hr)) {
/*     */       String formatMessage;
/*     */       try {
/* 112 */         formatMessage = Kernel32Util.formatMessage(hr) + "(HRESULT: " + Integer.toHexString(hr.intValue()) + ")";
/* 113 */       } catch (LastErrorException ex) {
/*     */         
/* 115 */         formatMessage = "(HRESULT: " + Integer.toHexString(hr.intValue()) + ")";
/*     */       } 
/* 117 */       throw new COMException(formatMessage, hr);
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
/*     */   public static void checkRC(WinNT.HRESULT hr, OaIdl.EXCEPINFO pExcepInfo, IntByReference puArgErr) {
/* 139 */     COMException resultException = null;
/*     */     
/* 141 */     if (FAILED(hr)) {
/* 142 */       StringBuilder formatMessage = new StringBuilder();
/*     */       
/* 144 */       Integer errorArg = null;
/* 145 */       Integer wCode = null;
/* 146 */       Integer scode = null;
/* 147 */       String description = null;
/* 148 */       String helpFile = null;
/* 149 */       Integer helpCtx = null;
/* 150 */       String source = null;
/*     */       
/* 152 */       if (puArgErr != null) {
/* 153 */         errorArg = Integer.valueOf(puArgErr.getValue());
/*     */       }
/*     */       
/*     */       try {
/* 157 */         formatMessage.append(Kernel32Util.formatMessage(hr));
/* 158 */       } catch (LastErrorException lastErrorException) {}
/*     */ 
/*     */ 
/*     */       
/* 162 */       formatMessage.append("(HRESULT: ");
/* 163 */       formatMessage.append(Integer.toHexString(hr.intValue()));
/* 164 */       formatMessage.append(")");
/*     */       
/* 166 */       if (pExcepInfo != null) {
/* 167 */         wCode = Integer.valueOf(pExcepInfo.wCode.intValue());
/* 168 */         scode = Integer.valueOf(pExcepInfo.scode.intValue());
/* 169 */         helpCtx = Integer.valueOf(pExcepInfo.dwHelpContext.intValue());
/*     */         
/* 171 */         if (pExcepInfo.bstrSource != null) {
/* 172 */           source = pExcepInfo.bstrSource.getValue();
/* 173 */           formatMessage.append("\nSource:      ");
/* 174 */           formatMessage.append(source);
/*     */         } 
/* 176 */         if (pExcepInfo.bstrDescription != null) {
/* 177 */           description = pExcepInfo.bstrDescription.getValue();
/* 178 */           formatMessage.append("\nDescription: ");
/* 179 */           formatMessage.append(description);
/*     */         } 
/* 181 */         if (pExcepInfo.bstrHelpFile != null) {
/* 182 */           helpFile = pExcepInfo.bstrHelpFile.getValue();
/*     */         }
/*     */       } 
/*     */       
/* 186 */       throw new COMInvokeException(formatMessage
/* 187 */           .toString(), hr, errorArg, description, helpCtx, helpFile, scode, source, wCode);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 199 */     if (pExcepInfo != null) {
/* 200 */       if (pExcepInfo.bstrSource != null) {
/* 201 */         OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrSource);
/*     */       }
/* 203 */       if (pExcepInfo.bstrDescription != null) {
/* 204 */         OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrDescription);
/*     */       }
/* 206 */       if (pExcepInfo.bstrHelpFile != null) {
/* 207 */         OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrHelpFile);
/*     */       }
/*     */     } 
/*     */     
/* 211 */     if (resultException != null) {
/* 212 */       throw resultException;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ArrayList<COMInfo> getAllCOMInfoOnSystem() {
/* 222 */     WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
/* 223 */     WinReg.HKEYByReference phkResult2 = new WinReg.HKEYByReference();
/*     */     
/* 225 */     ArrayList<COMInfo> comInfos = new ArrayList<COMInfo>();
/*     */ 
/*     */     
/*     */     try {
/* 229 */       phkResult = Advapi32Util.registryGetKey(WinReg.HKEY_CLASSES_ROOT, "CLSID", 131097);
/*     */ 
/*     */       
/* 232 */       Advapi32Util.InfoKey infoKey = Advapi32Util.registryQueryInfoKey(phkResult
/* 233 */           .getValue(), 131097);
/*     */       
/* 235 */       for (int i = 0; i < infoKey.lpcSubKeys.getValue(); i++) {
/* 236 */         Advapi32Util.EnumKey enumKey = Advapi32Util.registryRegEnumKey(phkResult
/* 237 */             .getValue(), i);
/* 238 */         String subKey = Native.toString(enumKey.lpName);
/*     */         
/* 240 */         COMInfo comInfo = new COMInfo(subKey);
/*     */         
/* 242 */         phkResult2 = Advapi32Util.registryGetKey(phkResult.getValue(), subKey, 131097);
/*     */         
/* 244 */         Advapi32Util.InfoKey infoKey2 = Advapi32Util.registryQueryInfoKey(phkResult2
/* 245 */             .getValue(), 131097);
/*     */         
/* 247 */         for (int y = 0; y < infoKey2.lpcSubKeys.getValue(); y++) {
/* 248 */           Advapi32Util.EnumKey enumKey2 = Advapi32Util.registryRegEnumKey(phkResult2
/* 249 */               .getValue(), y);
/* 250 */           String subKey2 = Native.toString(enumKey2.lpName);
/*     */           
/* 252 */           if (subKey2.equals("InprocHandler32")) {
/* 253 */             comInfo
/* 254 */               .inprocHandler32 = (String)Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
/*     */           }
/* 256 */           else if (subKey2.equals("InprocServer32")) {
/* 257 */             comInfo
/* 258 */               .inprocServer32 = (String)Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
/*     */           }
/* 260 */           else if (subKey2.equals("LocalServer32")) {
/* 261 */             comInfo
/* 262 */               .localServer32 = (String)Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
/*     */           }
/* 264 */           else if (subKey2.equals("ProgID")) {
/* 265 */             comInfo
/* 266 */               .progID = (String)Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
/*     */           }
/* 268 */           else if (subKey2.equals("TypeLib")) {
/* 269 */             comInfo
/* 270 */               .typeLib = (String)Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 275 */         Advapi32.INSTANCE.RegCloseKey(phkResult2.getValue());
/* 276 */         comInfos.add(comInfo);
/*     */       } 
/*     */     } finally {
/* 279 */       Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
/* 280 */       Advapi32.INSTANCE.RegCloseKey(phkResult2.getValue());
/*     */     } 
/*     */     
/* 283 */     return comInfos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean comIsInitialized() {
/* 294 */     WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, 0);
/* 295 */     if (hr.equals(W32Errors.S_OK)) {
/*     */       
/* 297 */       Ole32.INSTANCE.CoUninitialize();
/* 298 */       return false;
/* 299 */     }  if (hr.equals(W32Errors.S_FALSE)) {
/*     */ 
/*     */ 
/*     */       
/* 303 */       Ole32.INSTANCE.CoUninitialize();
/* 304 */       return true;
/* 305 */     }  if (hr.intValue() == -2147417850) {
/* 306 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 311 */     checkRC(hr);
/*     */     
/* 313 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class COMInfo
/*     */   {
/*     */     public String clsid;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String inprocHandler32;
/*     */ 
/*     */ 
/*     */     
/*     */     public String inprocServer32;
/*     */ 
/*     */ 
/*     */     
/*     */     public String localServer32;
/*     */ 
/*     */ 
/*     */     
/*     */     public String progID;
/*     */ 
/*     */ 
/*     */     
/*     */     public String typeLib;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public COMInfo() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public COMInfo(String clsid) {
/* 354 */       this.clsid = clsid;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */