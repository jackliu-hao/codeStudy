/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Function;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
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
/*     */ public interface IEnumIDList
/*     */ {
/*  43 */   public static final Guid.IID IID_IEnumIDList = new Guid.IID("{000214F2-0000-0000-C000-000000000046}");
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
/*     */   WinNT.HRESULT QueryInterface(Guid.REFIID paramREFIID, PointerByReference paramPointerByReference);
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
/*     */   int AddRef();
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
/*     */   int Release();
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
/*     */   WinNT.HRESULT Next(int paramInt, PointerByReference paramPointerByReference, IntByReference paramIntByReference);
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
/*     */   WinNT.HRESULT Skip(int paramInt);
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
/*     */   WinNT.HRESULT Reset();
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
/*     */   WinNT.HRESULT Clone(PointerByReference paramPointerByReference);
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
/*     */   public static class Converter
/*     */   {
/*     */     public static IEnumIDList PointerToIEnumIDList(PointerByReference ptr) {
/* 200 */       final Pointer interfacePointer = ptr.getValue();
/* 201 */       Pointer vTablePointer = interfacePointer.getPointer(0L);
/* 202 */       final Pointer[] vTable = new Pointer[7];
/* 203 */       vTablePointer.read(0L, vTable, 0, 7);
/* 204 */       return new IEnumIDList()
/*     */         {
/*     */           public WinNT.HRESULT QueryInterface(Guid.REFIID byValue, PointerByReference pointerByReference)
/*     */           {
/* 208 */             Function f = Function.getFunction(vTable[0], 63);
/* 209 */             return new WinNT.HRESULT(f.invokeInt(new Object[] { this.val$interfacePointer, byValue, pointerByReference }));
/*     */           }
/*     */ 
/*     */           
/*     */           public int AddRef() {
/* 214 */             Function f = Function.getFunction(vTable[1], 63);
/* 215 */             return f.invokeInt(new Object[] { this.val$interfacePointer });
/*     */           }
/*     */           
/*     */           public int Release() {
/* 219 */             Function f = Function.getFunction(vTable[2], 63);
/* 220 */             return f.invokeInt(new Object[] { this.val$interfacePointer });
/*     */           }
/*     */ 
/*     */           
/*     */           public WinNT.HRESULT Next(int celt, PointerByReference rgelt, IntByReference pceltFetched) {
/* 225 */             Function f = Function.getFunction(vTable[3], 63);
/* 226 */             return new WinNT.HRESULT(f.invokeInt(new Object[] { this.val$interfacePointer, Integer.valueOf(celt), rgelt, pceltFetched }));
/*     */           }
/*     */ 
/*     */           
/*     */           public WinNT.HRESULT Skip(int celt) {
/* 231 */             Function f = Function.getFunction(vTable[4], 63);
/* 232 */             return new WinNT.HRESULT(f.invokeInt(new Object[] { this.val$interfacePointer, Integer.valueOf(celt) }));
/*     */           }
/*     */ 
/*     */           
/*     */           public WinNT.HRESULT Reset() {
/* 237 */             Function f = Function.getFunction(vTable[5], 63);
/* 238 */             return new WinNT.HRESULT(f.invokeInt(new Object[] { this.val$interfacePointer }));
/*     */           }
/*     */ 
/*     */           
/*     */           public WinNT.HRESULT Clone(PointerByReference ppenum) {
/* 243 */             Function f = Function.getFunction(vTable[6], 63);
/* 244 */             return new WinNT.HRESULT(f.invokeInt(new Object[] { this.val$interfacePointer, ppenum }));
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\IEnumIDList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */