package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class GDI32Util {
   private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, 16711680, 65280, 255);
   private static final int[] SCREENSHOT_BAND_MASKS;

   public static BufferedImage getScreenshot(WinDef.HWND target) {
      WinDef.RECT rect = new WinDef.RECT();
      if (!User32.INSTANCE.GetWindowRect(target, rect)) {
         throw new Win32Exception(Native.getLastError());
      } else {
         Rectangle jRectangle = rect.toRectangle();
         int windowWidth = jRectangle.width;
         int windowHeight = jRectangle.height;
         if (windowWidth != 0 && windowHeight != 0) {
            WinDef.HDC hdcTarget = User32.INSTANCE.GetDC(target);
            if (hdcTarget == null) {
               throw new Win32Exception(Native.getLastError());
            } else {
               Win32Exception we = null;
               WinDef.HDC hdcTargetMem = null;
               WinDef.HBITMAP hBitmap = null;
               WinNT.HANDLE hOriginal = null;
               BufferedImage image = null;
               boolean var22 = false;

               label394: {
                  WinNT.HANDLE result;
                  Win32Exception ex;
                  Win32Exception ex;
                  label395: {
                     try {
                        var22 = true;
                        hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
                        if (hdcTargetMem == null) {
                           throw new Win32Exception(Native.getLastError());
                        }

                        hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, windowWidth, windowHeight);
                        if (hBitmap == null) {
                           throw new Win32Exception(Native.getLastError());
                        }

                        hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);
                        if (hOriginal == null) {
                           throw new Win32Exception(Native.getLastError());
                        }

                        if (!GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0, windowWidth, windowHeight, hdcTarget, 0, 0, 13369376)) {
                           throw new Win32Exception(Native.getLastError());
                        }

                        WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
                        bmi.bmiHeader.biWidth = windowWidth;
                        bmi.bmiHeader.biHeight = -windowHeight;
                        bmi.bmiHeader.biPlanes = 1;
                        bmi.bmiHeader.biBitCount = 32;
                        bmi.bmiHeader.biCompression = 0;
                        Memory buffer = new Memory((long)(windowWidth * windowHeight * 4));
                        int resultOfDrawing = GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, windowHeight, buffer, bmi, 0);
                        if (resultOfDrawing == 0 || resultOfDrawing == 87) {
                           throw new Win32Exception(Native.getLastError());
                        }

                        int bufferSize = windowWidth * windowHeight;
                        DataBuffer dataBuffer = new DataBufferInt(buffer.getIntArray(0L, bufferSize), bufferSize);
                        WritableRaster raster = Raster.createPackedRaster(dataBuffer, windowWidth, windowHeight, windowWidth, SCREENSHOT_BAND_MASKS, (Point)null);
                        image = new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, (Hashtable)null);
                        var22 = false;
                        break label395;
                     } catch (Win32Exception var23) {
                        we = var23;
                        var22 = false;
                     } finally {
                        if (var22) {
                           if (hOriginal != null) {
                              WinNT.HANDLE result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
                              if (result == null || WinGDI.HGDI_ERROR.equals(result)) {
                                 Win32Exception ex = new Win32Exception(Native.getLastError());
                                 if (we != null) {
                                    ex.addSuppressedReflected(we);
                                 }

                                 we = ex;
                              }
                           }

                           Win32Exception ex;
                           if (hBitmap != null && !GDI32.INSTANCE.DeleteObject(hBitmap)) {
                              ex = new Win32Exception(Native.getLastError());
                              if (we != null) {
                                 ex.addSuppressedReflected(we);
                              }

                              we = ex;
                           }

                           if (hdcTargetMem != null && !GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
                              ex = new Win32Exception(Native.getLastError());
                              if (we != null) {
                                 ex.addSuppressedReflected(we);
                              }
                           }

                           if (hdcTarget != null && 0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
                              throw new IllegalStateException("Device context did not release properly.");
                           }

                        }
                     }

                     if (hOriginal != null) {
                        result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
                        if (result == null || WinGDI.HGDI_ERROR.equals(result)) {
                           ex = new Win32Exception(Native.getLastError());
                           if (we != null) {
                              ex.addSuppressedReflected(we);
                           }

                           we = ex;
                        }
                     }

                     if (hBitmap != null && !GDI32.INSTANCE.DeleteObject(hBitmap)) {
                        ex = new Win32Exception(Native.getLastError());
                        if (we != null) {
                           ex.addSuppressedReflected(we);
                        }

                        we = ex;
                     }

                     if (hdcTargetMem != null && !GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
                        ex = new Win32Exception(Native.getLastError());
                        if (we != null) {
                           ex.addSuppressedReflected(we);
                        }

                        we = ex;
                     }

                     if (hdcTarget != null && 0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
                        throw new IllegalStateException("Device context did not release properly.");
                     }
                     break label394;
                  }

                  if (hOriginal != null) {
                     result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
                     if (result == null || WinGDI.HGDI_ERROR.equals(result)) {
                        ex = new Win32Exception(Native.getLastError());
                        if (we != null) {
                           ex.addSuppressedReflected(we);
                        }

                        we = ex;
                     }
                  }

                  if (hBitmap != null && !GDI32.INSTANCE.DeleteObject(hBitmap)) {
                     ex = new Win32Exception(Native.getLastError());
                     if (we != null) {
                        ex.addSuppressedReflected(we);
                     }

                     we = ex;
                  }

                  if (hdcTargetMem != null && !GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
                     ex = new Win32Exception(Native.getLastError());
                     if (we != null) {
                        ex.addSuppressedReflected(we);
                     }

                     we = ex;
                  }

                  if (hdcTarget != null && 0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
                     throw new IllegalStateException("Device context did not release properly.");
                  }
               }

               if (we != null) {
                  throw we;
               } else {
                  return image;
               }
            }
         } else {
            throw new IllegalStateException("Window width and/or height were 0 even though GetWindowRect did not appear to fail.");
         }
      }
   }

   static {
      SCREENSHOT_BAND_MASKS = new int[]{SCREENSHOT_COLOR_MODEL.getRedMask(), SCREENSHOT_COLOR_MODEL.getGreenMask(), SCREENSHOT_COLOR_MODEL.getBlueMask()};
   }
}
