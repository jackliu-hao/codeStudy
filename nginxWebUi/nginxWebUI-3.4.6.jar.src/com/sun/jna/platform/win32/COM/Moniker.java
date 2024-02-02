/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.WTypes;
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
/*     */ public class Moniker
/*     */   extends Unknown
/*     */   implements IMoniker
/*     */ {
/*     */   static final int vTableIdStart = 7;
/*     */   
/*     */   public static class ByReference
/*     */     extends Moniker
/*     */     implements Structure.ByReference {}
/*     */   
/*     */   public Moniker() {}
/*     */   
/*     */   public Moniker(Pointer pointer) {
/*  43 */     super(pointer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void BindToObject() {
/*  51 */     int vTableId = 8;
/*     */     
/*  53 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void BindToStorage() {
/*  58 */     int vTableId = 9;
/*     */     
/*  60 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Reduce() {
/*  65 */     int vTableId = 10;
/*     */     
/*  67 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void ComposeWith() {
/*  72 */     int vTableId = 11;
/*     */     
/*  74 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Enum() {
/*  79 */     int vTableId = 12;
/*     */     
/*  81 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void IsEqual() {
/*  86 */     int vTableId = 13;
/*     */     
/*  88 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Hash() {
/*  93 */     int vTableId = 14;
/*     */     
/*  95 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void IsRunning() {
/* 100 */     int vTableId = 15;
/*     */     
/* 102 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void GetTimeOfLastChange() {
/* 107 */     int vTableId = 16;
/*     */     
/* 109 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Inverse() {
/* 114 */     int vTableId = 17;
/*     */     
/* 116 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void CommonPrefixWith() {
/* 121 */     int vTableId = 18;
/*     */     
/* 123 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void RelativePathTo() {
/* 128 */     int vTableId = 19;
/*     */     
/* 130 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public String GetDisplayName(Pointer pbc, Pointer pmkToLeft) {
/* 135 */     int vTableId = 20;
/*     */     
/* 137 */     PointerByReference ppszDisplayNameRef = new PointerByReference();
/*     */     
/* 139 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(20, new Object[] { getPointer(), pbc, pmkToLeft, ppszDisplayNameRef }, WinNT.HRESULT.class);
/*     */ 
/*     */     
/* 142 */     COMUtils.checkRC(hr);
/*     */     
/* 144 */     Pointer ppszDisplayName = ppszDisplayNameRef.getValue();
/* 145 */     if (ppszDisplayName == null) {
/* 146 */       return null;
/*     */     }
/*     */     
/* 149 */     WTypes.LPOLESTR oleStr = new WTypes.LPOLESTR(ppszDisplayName);
/* 150 */     String name = oleStr.getValue();
/* 151 */     Ole32.INSTANCE.CoTaskMemFree(ppszDisplayName);
/*     */     
/* 153 */     return name;
/*     */   }
/*     */ 
/*     */   
/*     */   public void ParseDisplayName() {
/* 158 */     int vTableId = 21;
/*     */     
/* 160 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void IsSystemMoniker() {
/* 165 */     int vTableId = 22;
/*     */     
/* 167 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean IsDirty() {
/* 173 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Load(IStream stm) {
/* 178 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Save(IStream stm) {
/* 183 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void GetSizeMax() {
/* 188 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Guid.CLSID GetClassID() {
/* 193 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\Moniker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */