/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.Memory;
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Pointer;
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
/*    */ public class VersionUtil
/*    */ {
/*    */   public static VerRsrc.VS_FIXEDFILEINFO getFileVersionInfo(String filePath) {
/* 55 */     IntByReference dwDummy = new IntByReference();
/*    */     
/* 57 */     int versionLength = Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);
/*    */ 
/*    */ 
/*    */     
/* 61 */     if (versionLength == 0) {
/* 62 */       throw new Win32Exception(Native.getLastError());
/*    */     }
/*    */ 
/*    */     
/* 66 */     Memory memory = new Memory(versionLength);
/*    */ 
/*    */     
/* 69 */     PointerByReference lplpBuffer = new PointerByReference();
/*    */     
/* 71 */     if (!Version.INSTANCE.GetFileVersionInfo(filePath, 0, versionLength, (Pointer)memory)) {
/* 72 */       throw new Win32Exception(Native.getLastError());
/*    */     }
/*    */ 
/*    */     
/* 76 */     IntByReference puLen = new IntByReference();
/*    */ 
/*    */     
/* 79 */     if (!Version.INSTANCE.VerQueryValue((Pointer)memory, "\\", lplpBuffer, puLen)) {
/* 80 */       throw new UnsupportedOperationException("Unable to extract version info from the file: \"" + filePath + "\"");
/*    */     }
/*    */     
/* 83 */     VerRsrc.VS_FIXEDFILEINFO fileInfo = new VerRsrc.VS_FIXEDFILEINFO(lplpBuffer.getValue());
/* 84 */     fileInfo.read();
/* 85 */     return fileInfo;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\VersionUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */