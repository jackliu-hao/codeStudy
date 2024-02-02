/*    */ package com.sun.jna.platform.win32.COM;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.platform.win32.Guid;
/*    */ import com.sun.jna.platform.win32.WinDef;
/*    */ import com.sun.jna.platform.win32.WinNT;
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
/*    */ public class ConnectionPoint
/*    */   extends Unknown
/*    */   implements IConnectionPoint
/*    */ {
/*    */   public ConnectionPoint(Pointer pointer) {
/* 35 */     super(pointer);
/*    */   }
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT GetConnectionInterface(Guid.IID iid) {
/* 40 */     int vTableId = 3;
/* 41 */     return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), iid }, WinNT.HRESULT.class);
/*    */   }
/*    */   
/*    */   void GetConnectionPointContainer() {
/* 45 */     int vTableId = 4;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT Advise(IUnknownCallback pUnkSink, WinDef.DWORDByReference pdwCookie) {
/* 51 */     int vTableId = 5;
/*    */     
/* 53 */     return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), pUnkSink.getPointer(), pdwCookie }, WinNT.HRESULT.class);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT Unadvise(WinDef.DWORD dwCookie) {
/* 59 */     int vTableId = 6;
/*    */     
/* 61 */     return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), dwCookie }, WinNT.HRESULT.class);
/*    */   }
/*    */   
/*    */   void EnumConnections() {
/* 65 */     int vTableId = 7;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\ConnectionPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */