/*     */ package com.sun.jna.platform;
/*     */ 
/*     */ import com.sun.jna.Platform;
/*     */ import com.sun.jna.platform.unix.X11;
/*     */ import com.sun.jna.platform.win32.User32;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.HeadlessException;
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
/*     */ public class KeyboardUtils
/*     */ {
/*     */   static final NativeKeyboardUtils INSTANCE;
/*     */   
/*     */   static {
/*  48 */     if (GraphicsEnvironment.isHeadless()) {
/*  49 */       throw new HeadlessException("KeyboardUtils requires a keyboard");
/*     */     }
/*  51 */     if (Platform.isWindows()) {
/*  52 */       INSTANCE = new W32KeyboardUtils();
/*     */     } else {
/*  54 */       if (Platform.isMac()) {
/*  55 */         INSTANCE = new MacKeyboardUtils();
/*  56 */         throw new UnsupportedOperationException("No support (yet) for " + 
/*  57 */             System.getProperty("os.name"));
/*     */       } 
/*     */       
/*  60 */       INSTANCE = new X11KeyboardUtils();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean isPressed(int keycode, int location) {
/*  65 */     return INSTANCE.isPressed(keycode, location);
/*     */   }
/*     */   public static boolean isPressed(int keycode) {
/*  68 */     return INSTANCE.isPressed(keycode);
/*     */   }
/*     */   
/*     */   private static abstract class NativeKeyboardUtils { private NativeKeyboardUtils() {}
/*     */     
/*     */     public boolean isPressed(int keycode) {
/*  74 */       return isPressed(keycode, 0);
/*     */     }
/*     */     
/*     */     public abstract boolean isPressed(int param1Int1, int param1Int2); }
/*     */   
/*     */   private static class W32KeyboardUtils extends NativeKeyboardUtils { private int toNative(int code, int loc) {
/*  80 */       if ((code >= 65 && code <= 90) || (code >= 48 && code <= 57))
/*     */       {
/*  82 */         return code;
/*     */       }
/*  84 */       if (code == 16) {
/*  85 */         if ((loc & 0x3) != 0) {
/*  86 */           return 161;
/*     */         }
/*  88 */         if ((loc & 0x2) != 0) {
/*  89 */           return 160;
/*     */         }
/*  91 */         return 16;
/*     */       } 
/*  93 */       if (code == 17) {
/*  94 */         if ((loc & 0x3) != 0) {
/*  95 */           return 163;
/*     */         }
/*  97 */         if ((loc & 0x2) != 0) {
/*  98 */           return 162;
/*     */         }
/* 100 */         return 17;
/*     */       } 
/* 102 */       if (code == 18) {
/* 103 */         if ((loc & 0x3) != 0) {
/* 104 */           return 165;
/*     */         }
/* 106 */         if ((loc & 0x2) != 0) {
/* 107 */           return 164;
/*     */         }
/* 109 */         return 18;
/*     */       } 
/* 111 */       return 0;
/*     */     } private W32KeyboardUtils() {}
/*     */     public boolean isPressed(int keycode, int location) {
/* 114 */       User32 lib = User32.INSTANCE;
/* 115 */       return ((lib.GetAsyncKeyState(toNative(keycode, location)) & 0x8000) != 0);
/*     */     } }
/*     */   private static class MacKeyboardUtils extends NativeKeyboardUtils { private MacKeyboardUtils() {}
/*     */     
/*     */     public boolean isPressed(int keycode, int location) {
/* 120 */       return false;
/*     */     } }
/*     */   
/*     */   private static class X11KeyboardUtils extends NativeKeyboardUtils {
/*     */     private X11KeyboardUtils() {}
/*     */     
/*     */     private int toKeySym(int code, int location) {
/* 127 */       if (code >= 65 && code <= 90)
/* 128 */         return 97 + code - 65; 
/* 129 */       if (code >= 48 && code <= 57)
/* 130 */         return 48 + code - 48; 
/* 131 */       if (code == 16) {
/* 132 */         if ((location & 0x3) != 0)
/* 133 */           return 65505; 
/* 134 */         return 65505;
/*     */       } 
/* 136 */       if (code == 17) {
/* 137 */         if ((location & 0x3) != 0)
/* 138 */           return 65508; 
/* 139 */         return 65507;
/*     */       } 
/* 141 */       if (code == 18) {
/* 142 */         if ((location & 0x3) != 0)
/* 143 */           return 65514; 
/* 144 */         return 65513;
/*     */       } 
/* 146 */       if (code == 157) {
/* 147 */         if ((location & 0x3) != 0)
/* 148 */           return 65512; 
/* 149 */         return 65511;
/*     */       } 
/* 151 */       return 0;
/*     */     }
/*     */     public boolean isPressed(int keycode, int location) {
/* 154 */       X11 lib = X11.INSTANCE;
/* 155 */       X11.Display dpy = lib.XOpenDisplay(null);
/* 156 */       if (dpy == null) {
/* 157 */         throw new Error("Can't open X Display");
/*     */       }
/*     */       try {
/* 160 */         byte[] keys = new byte[32];
/*     */         
/* 162 */         lib.XQueryKeymap(dpy, keys);
/* 163 */         int keysym = toKeySym(keycode, location);
/* 164 */         for (int code = 5; code < 256; code++) {
/* 165 */           int idx = code / 8;
/* 166 */           int shift = code % 8;
/* 167 */           if ((keys[idx] & 1 << shift) != 0) {
/* 168 */             int sym = lib.XKeycodeToKeysym(dpy, (byte)code, 0).intValue();
/* 169 */             if (sym == keysym) {
/* 170 */               return true;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } finally {
/* 175 */         lib.XCloseDisplay(dpy);
/*     */       } 
/* 177 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\KeyboardUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */