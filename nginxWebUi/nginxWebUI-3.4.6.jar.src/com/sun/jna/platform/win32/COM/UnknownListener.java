/*    */ package com.sun.jna.platform.win32.COM;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.Structure;
/*    */ import com.sun.jna.Structure.FieldOrder;
/*    */ import com.sun.jna.platform.win32.Guid;
/*    */ import com.sun.jna.platform.win32.WinNT;
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
/*    */ @FieldOrder({"vtbl"})
/*    */ public class UnknownListener
/*    */   extends Structure
/*    */ {
/*    */   public UnknownVTable.ByReference vtbl;
/*    */   
/*    */   public UnknownListener(IUnknownCallback callback) {
/* 38 */     this.vtbl = constructVTable();
/* 39 */     initVTable(callback);
/* 40 */     write();
/*    */   }
/*    */   
/*    */   protected UnknownVTable.ByReference constructVTable() {
/* 44 */     return new UnknownVTable.ByReference();
/*    */   }
/*    */   
/*    */   protected void initVTable(final IUnknownCallback callback) {
/* 48 */     this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback()
/*    */       {
/*    */         public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID refid, PointerByReference ppvObject) {
/* 51 */           return callback.QueryInterface(refid, ppvObject);
/*    */         }
/*    */       };
/* 54 */     this.vtbl.AddRefCallback = new UnknownVTable.AddRefCallback()
/*    */       {
/*    */         public int invoke(Pointer thisPointer) {
/* 57 */           return callback.AddRef();
/*    */         }
/*    */       };
/* 60 */     this.vtbl.ReleaseCallback = new UnknownVTable.ReleaseCallback()
/*    */       {
/*    */         public int invoke(Pointer thisPointer) {
/* 63 */           return callback.Release();
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\UnknownListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */