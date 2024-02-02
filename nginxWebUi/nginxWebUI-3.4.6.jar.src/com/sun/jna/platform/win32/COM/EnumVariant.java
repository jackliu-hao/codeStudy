/*    */ package com.sun.jna.platform.win32.COM;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.platform.win32.Guid;
/*    */ import com.sun.jna.platform.win32.Variant;
/*    */ import com.sun.jna.platform.win32.WinNT;
/*    */ import com.sun.jna.ptr.IntByReference;
/*    */ import com.sun.jna.ptr.PointerByReference;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnumVariant
/*    */   extends Unknown
/*    */   implements IEnumVariant
/*    */ {
/* 36 */   public static final Guid.IID IID = new Guid.IID("{00020404-0000-0000-C000-000000000046}");
/* 37 */   public static final Guid.REFIID REFIID = new Guid.REFIID(IID);
/*    */ 
/*    */   
/*    */   public EnumVariant() {}
/*    */   
/*    */   public EnumVariant(Pointer p) {
/* 43 */     setPointer(p);
/*    */   }
/*    */ 
/*    */   
/*    */   public Variant.VARIANT[] Next(int count) {
/* 48 */     Variant.VARIANT[] resultStaging = new Variant.VARIANT[count];
/* 49 */     IntByReference resultCount = new IntByReference();
/* 50 */     WinNT.HRESULT hresult = (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), Integer.valueOf(resultStaging.length), resultStaging, resultCount }, WinNT.HRESULT.class);
/* 51 */     COMUtils.checkRC(hresult);
/* 52 */     Variant.VARIANT[] result = new Variant.VARIANT[resultCount.getValue()];
/* 53 */     System.arraycopy(resultStaging, 0, result, 0, resultCount.getValue());
/* 54 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public void Skip(int count) {
/* 59 */     WinNT.HRESULT hresult = (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), Integer.valueOf(count) }, WinNT.HRESULT.class);
/* 60 */     COMUtils.checkRC(hresult);
/*    */   }
/*    */ 
/*    */   
/*    */   public void Reset() {
/* 65 */     WinNT.HRESULT hresult = (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer() }, WinNT.HRESULT.class);
/* 66 */     COMUtils.checkRC(hresult);
/*    */   }
/*    */ 
/*    */   
/*    */   public EnumVariant Clone() {
/* 71 */     PointerByReference pbr = new PointerByReference();
/* 72 */     WinNT.HRESULT hresult = (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), pbr }, WinNT.HRESULT.class);
/* 73 */     COMUtils.checkRC(hresult);
/* 74 */     return new EnumVariant(pbr.getValue());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\EnumVariant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */