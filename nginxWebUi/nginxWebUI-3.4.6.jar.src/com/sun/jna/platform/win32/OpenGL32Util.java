/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.Function;
/*    */ import com.sun.jna.Pointer;
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
/*    */ public abstract class OpenGL32Util
/*    */ {
/*    */   public static Function wglGetProcAddress(String procName) {
/* 44 */     Pointer funcPointer = OpenGL32.INSTANCE.wglGetProcAddress("wglEnumGpusNV");
/* 45 */     return (funcPointer == null) ? null : Function.getFunction(funcPointer);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int countGpusNV() {
/* 54 */     WinDef.HWND hWnd = User32Util.createWindow("Message", null, 0, 0, 0, 0, 0, null, null, null, null);
/* 55 */     WinDef.HDC hdc = User32.INSTANCE.GetDC(hWnd);
/*    */ 
/*    */     
/* 58 */     WinGDI.PIXELFORMATDESCRIPTOR.ByReference pfd = new WinGDI.PIXELFORMATDESCRIPTOR.ByReference();
/* 59 */     pfd.nVersion = 1;
/* 60 */     pfd.dwFlags = 37;
/* 61 */     pfd.iPixelType = 0;
/* 62 */     pfd.cColorBits = 24;
/* 63 */     pfd.cDepthBits = 16;
/* 64 */     pfd.iLayerType = 0;
/* 65 */     GDI32.INSTANCE.SetPixelFormat(hdc, GDI32.INSTANCE.ChoosePixelFormat(hdc, pfd), pfd);
/*    */ 
/*    */     
/* 68 */     WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
/* 69 */     OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
/* 70 */     Pointer funcPointer = OpenGL32.INSTANCE.wglGetProcAddress("wglEnumGpusNV");
/* 71 */     Function fncEnumGpusNV = (funcPointer == null) ? null : Function.getFunction(funcPointer);
/* 72 */     OpenGL32.INSTANCE.wglDeleteContext(hGLRC);
/*    */ 
/*    */     
/* 75 */     User32.INSTANCE.ReleaseDC(hWnd, hdc);
/* 76 */     User32Util.destroyWindow(hWnd);
/*    */ 
/*    */     
/* 79 */     if (fncEnumGpusNV == null) return 0;
/*    */ 
/*    */     
/* 82 */     WinDef.HGLRCByReference hGPU = new WinDef.HGLRCByReference();
/* 83 */     for (int i = 0; i < 16; i++) {
/* 84 */       Boolean ok = (Boolean)fncEnumGpusNV.invoke(Boolean.class, new Object[] { Integer.valueOf(i), hGPU });
/* 85 */       if (!ok.booleanValue()) return i;
/*    */     
/*    */     } 
/* 88 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\OpenGL32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */