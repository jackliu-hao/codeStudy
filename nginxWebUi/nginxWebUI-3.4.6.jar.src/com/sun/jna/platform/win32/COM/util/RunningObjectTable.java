/*    */ package com.sun.jna.platform.win32.COM.util;
/*    */ 
/*    */ import com.sun.jna.platform.win32.COM.COMException;
/*    */ import com.sun.jna.platform.win32.COM.COMUtils;
/*    */ import com.sun.jna.platform.win32.COM.EnumMoniker;
/*    */ import com.sun.jna.platform.win32.COM.IEnumMoniker;
/*    */ import com.sun.jna.platform.win32.COM.IRunningObjectTable;
/*    */ import com.sun.jna.platform.win32.WinNT;
/*    */ import com.sun.jna.ptr.PointerByReference;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class RunningObjectTable
/*    */   implements IRunningObjectTable
/*    */ {
/*    */   ObjectFactory factory;
/*    */   com.sun.jna.platform.win32.COM.RunningObjectTable raw;
/*    */   
/*    */   protected RunningObjectTable(com.sun.jna.platform.win32.COM.RunningObjectTable raw, ObjectFactory factory) {
/* 37 */     this.raw = raw;
/* 38 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Iterable<IDispatch> enumRunning() {
/* 46 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*    */     
/* 48 */     PointerByReference ppenumMoniker = new PointerByReference();
/*    */     
/* 50 */     WinNT.HRESULT hr = this.raw.EnumRunning(ppenumMoniker);
/*    */     
/* 52 */     COMUtils.checkRC(hr);
/*    */     
/* 54 */     EnumMoniker raw = new EnumMoniker(ppenumMoniker.getValue());
/*    */     
/* 56 */     return new EnumMoniker((IEnumMoniker)raw, (IRunningObjectTable)this.raw, this.factory);
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> List<T> getActiveObjectsByInterface(Class<T> comInterface) {
/* 61 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*    */     
/* 63 */     List<T> result = new ArrayList<T>();
/*    */     
/* 65 */     for (IDispatch obj : enumRunning()) {
/*    */       try {
/* 67 */         T dobj = obj.queryInterface(comInterface);
/*    */         
/* 69 */         result.add(dobj);
/* 70 */       } catch (COMException cOMException) {}
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 75 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\RunningObjectTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */