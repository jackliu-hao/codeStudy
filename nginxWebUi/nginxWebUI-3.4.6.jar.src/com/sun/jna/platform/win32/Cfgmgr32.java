/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.Library;
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.ptr.IntByReference;
/*    */ import com.sun.jna.win32.W32APIOptions;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Cfgmgr32
/*    */   extends Library
/*    */ {
/* 38 */   public static final Cfgmgr32 INSTANCE = (Cfgmgr32)Native.load("Cfgmgr32", Cfgmgr32.class, W32APIOptions.DEFAULT_OPTIONS);
/*    */   public static final int CR_SUCCESS = 0;
/*    */   public static final int CR_BUFFER_SMALL = 26;
/*    */   public static final int CM_LOCATE_DEVNODE_NORMAL = 0;
/*    */   public static final int CM_LOCATE_DEVNODE_PHANTOM = 1;
/*    */   public static final int CM_LOCATE_DEVNODE_CANCELREMOVE = 2;
/*    */   public static final int CM_LOCATE_DEVNODE_NOVALIDATION = 4;
/*    */   public static final int CM_LOCATE_DEVNODE_BITS = 7;
/*    */   
/*    */   int CM_Locate_DevNode(IntByReference paramIntByReference, String paramString, int paramInt);
/*    */   
/*    */   int CM_Get_Parent(IntByReference paramIntByReference, int paramInt1, int paramInt2);
/*    */   
/*    */   int CM_Get_Child(IntByReference paramIntByReference, int paramInt1, int paramInt2);
/*    */   
/*    */   int CM_Get_Sibling(IntByReference paramIntByReference, int paramInt1, int paramInt2);
/*    */   
/*    */   int CM_Get_Device_ID(int paramInt1, Pointer paramPointer, int paramInt2, int paramInt3);
/*    */   
/*    */   int CM_Get_Device_ID_Size(IntByReference paramIntByReference, int paramInt1, int paramInt2);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Cfgmgr32.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */