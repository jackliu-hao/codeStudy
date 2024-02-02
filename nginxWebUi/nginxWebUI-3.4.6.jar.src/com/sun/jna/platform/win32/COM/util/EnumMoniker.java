/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.COMUtils;
/*     */ import com.sun.jna.platform.win32.COM.Dispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.IEnumMoniker;
/*     */ import com.sun.jna.platform.win32.COM.IRunningObjectTable;
/*     */ import com.sun.jna.platform.win32.COM.Moniker;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EnumMoniker
/*     */   implements Iterable<IDispatch>
/*     */ {
/*     */   ObjectFactory factory;
/*     */   IRunningObjectTable rawRot;
/*     */   IEnumMoniker raw;
/*     */   Moniker rawNext;
/*     */   
/*     */   protected EnumMoniker(IEnumMoniker raw, IRunningObjectTable rawRot, ObjectFactory factory) {
/*  49 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*  51 */     this.rawRot = rawRot;
/*  52 */     this.raw = raw;
/*  53 */     this.factory = factory;
/*     */     
/*  55 */     WinNT.HRESULT hr = raw.Reset();
/*  56 */     COMUtils.checkRC(hr);
/*     */     
/*  58 */     cacheNext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void cacheNext() {
/*  67 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*  68 */     PointerByReference rgelt = new PointerByReference();
/*  69 */     WinDef.ULONGByReference pceltFetched = new WinDef.ULONGByReference();
/*     */     
/*  71 */     WinNT.HRESULT hr = this.raw.Next(new WinDef.ULONG(1L), rgelt, pceltFetched);
/*     */     
/*  73 */     if (WinNT.S_OK.equals(hr) && pceltFetched.getValue().intValue() > 0) {
/*  74 */       this.rawNext = new Moniker(rgelt.getValue());
/*     */     } else {
/*  76 */       if (!WinNT.S_FALSE.equals(hr)) {
/*  77 */         COMUtils.checkRC(hr);
/*     */       }
/*  79 */       this.rawNext = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<IDispatch> iterator() {
/*  85 */     return new Iterator<IDispatch>()
/*     */       {
/*     */         public boolean hasNext()
/*     */         {
/*  89 */           return (null != EnumMoniker.this.rawNext);
/*     */         }
/*     */ 
/*     */         
/*     */         public IDispatch next() {
/*  94 */           assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */           
/*  96 */           Moniker moniker = EnumMoniker.this.rawNext;
/*  97 */           PointerByReference ppunkObject = new PointerByReference();
/*  98 */           WinNT.HRESULT hr = EnumMoniker.this.rawRot.GetObject(moniker.getPointer(), ppunkObject);
/*  99 */           COMUtils.checkRC(hr);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 117 */           Dispatch dispatch = new Dispatch(ppunkObject.getValue());
/* 118 */           EnumMoniker.this.cacheNext();
/* 119 */           IDispatch d = EnumMoniker.this.factory.<IDispatch>createProxy(IDispatch.class, (IDispatch)dispatch);
/*     */           
/* 121 */           int n = dispatch.Release();
/* 122 */           return d;
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 127 */           throw new UnsupportedOperationException("remove");
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\EnumMoniker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */