/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GDI32Util
/*     */ {
/*  55 */   private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, 16711680, 65280, 255);
/*  56 */   private static final int[] SCREENSHOT_BAND_MASKS = new int[] { SCREENSHOT_COLOR_MODEL
/*  57 */       .getRedMask(), SCREENSHOT_COLOR_MODEL
/*  58 */       .getGreenMask(), SCREENSHOT_COLOR_MODEL
/*  59 */       .getBlueMask() };
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
/*     */   public static BufferedImage getScreenshot(WinDef.HWND target) {
/*  76 */     WinDef.RECT rect = new WinDef.RECT();
/*  77 */     if (!User32.INSTANCE.GetWindowRect(target, rect)) {
/*  78 */       throw new Win32Exception(Native.getLastError());
/*     */     }
/*  80 */     Rectangle jRectangle = rect.toRectangle();
/*  81 */     int windowWidth = jRectangle.width;
/*  82 */     int windowHeight = jRectangle.height;
/*     */     
/*  84 */     if (windowWidth == 0 || windowHeight == 0) {
/*  85 */       throw new IllegalStateException("Window width and/or height were 0 even though GetWindowRect did not appear to fail.");
/*     */     }
/*     */     
/*  88 */     WinDef.HDC hdcTarget = User32.INSTANCE.GetDC(target);
/*  89 */     if (hdcTarget == null) {
/*  90 */       throw new Win32Exception(Native.getLastError());
/*     */     }
/*     */     
/*  93 */     Win32Exception we = null;
/*     */ 
/*     */     
/*  96 */     WinDef.HDC hdcTargetMem = null;
/*     */ 
/*     */     
/*  99 */     WinDef.HBITMAP hBitmap = null;
/*     */ 
/*     */     
/* 102 */     WinNT.HANDLE hOriginal = null;
/*     */ 
/*     */     
/* 105 */     BufferedImage image = null;
/*     */     
/*     */     try {
/* 108 */       hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
/* 109 */       if (hdcTargetMem == null) {
/* 110 */         throw new Win32Exception(Native.getLastError());
/*     */       }
/*     */       
/* 113 */       hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, windowWidth, windowHeight);
/* 114 */       if (hBitmap == null) {
/* 115 */         throw new Win32Exception(Native.getLastError());
/*     */       }
/*     */       
/* 118 */       hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);
/* 119 */       if (hOriginal == null) {
/* 120 */         throw new Win32Exception(Native.getLastError());
/*     */       }
/*     */ 
/*     */       
/* 124 */       if (!GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0, windowWidth, windowHeight, hdcTarget, 0, 0, 13369376)) {
/* 125 */         throw new Win32Exception(Native.getLastError());
/*     */       }
/*     */       
/* 128 */       WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
/* 129 */       bmi.bmiHeader.biWidth = windowWidth;
/* 130 */       bmi.bmiHeader.biHeight = -windowHeight;
/* 131 */       bmi.bmiHeader.biPlanes = 1;
/* 132 */       bmi.bmiHeader.biBitCount = 32;
/* 133 */       bmi.bmiHeader.biCompression = 0;
/*     */       
/* 135 */       Memory buffer = new Memory((windowWidth * windowHeight * 4));
/* 136 */       int resultOfDrawing = GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, windowHeight, (Pointer)buffer, bmi, 0);
/*     */       
/* 138 */       if (resultOfDrawing == 0 || resultOfDrawing == 87) {
/* 139 */         throw new Win32Exception(Native.getLastError());
/*     */       }
/*     */       
/* 142 */       int bufferSize = windowWidth * windowHeight;
/* 143 */       DataBuffer dataBuffer = new DataBufferInt(buffer.getIntArray(0L, bufferSize), bufferSize);
/* 144 */       WritableRaster raster = Raster.createPackedRaster(dataBuffer, windowWidth, windowHeight, windowWidth, SCREENSHOT_BAND_MASKS, (Point)null);
/*     */       
/* 146 */       image = new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, null);
/*     */     }
/* 148 */     catch (Win32Exception e) {
/* 149 */       we = e;
/*     */     } finally {
/* 151 */       if (hOriginal != null) {
/*     */         
/* 153 */         WinNT.HANDLE result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
/*     */         
/* 155 */         if (result == null || WinGDI.HGDI_ERROR.equals(result)) {
/* 156 */           Win32Exception ex = new Win32Exception(Native.getLastError());
/* 157 */           if (we != null) {
/* 158 */             ex.addSuppressedReflected((Throwable)we);
/*     */           }
/* 160 */           we = ex;
/*     */         } 
/*     */       } 
/*     */       
/* 164 */       if (hBitmap != null && 
/* 165 */         !GDI32.INSTANCE.DeleteObject(hBitmap)) {
/* 166 */         Win32Exception ex = new Win32Exception(Native.getLastError());
/* 167 */         if (we != null) {
/* 168 */           ex.addSuppressedReflected((Throwable)we);
/*     */         }
/* 170 */         we = ex;
/*     */       } 
/*     */ 
/*     */       
/* 174 */       if (hdcTargetMem != null)
/*     */       {
/* 176 */         if (!GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
/* 177 */           Win32Exception ex = new Win32Exception(Native.getLastError());
/* 178 */           if (we != null) {
/* 179 */             ex.addSuppressedReflected((Throwable)we);
/*     */           }
/* 181 */           we = ex;
/*     */         } 
/*     */       }
/*     */       
/* 185 */       if (hdcTarget != null && 
/* 186 */         0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
/* 187 */         throw new IllegalStateException("Device context did not release properly.");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 192 */     if (we != null) {
/* 193 */       throw we;
/*     */     }
/* 195 */     return image;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\GDI32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */