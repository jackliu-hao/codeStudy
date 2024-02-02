package com.sun.jna.platform;

import com.sun.jna.Platform;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.User32;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

public class KeyboardUtils {
   static final NativeKeyboardUtils INSTANCE;

   public static boolean isPressed(int keycode, int location) {
      return INSTANCE.isPressed(keycode, location);
   }

   public static boolean isPressed(int keycode) {
      return INSTANCE.isPressed(keycode);
   }

   static {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException("KeyboardUtils requires a keyboard");
      } else {
         if (Platform.isWindows()) {
            INSTANCE = new W32KeyboardUtils();
         } else {
            if (Platform.isMac()) {
               INSTANCE = new MacKeyboardUtils();
               throw new UnsupportedOperationException("No support (yet) for " + System.getProperty("os.name"));
            }

            INSTANCE = new X11KeyboardUtils();
         }

      }
   }

   private static class X11KeyboardUtils extends NativeKeyboardUtils {
      private X11KeyboardUtils() {
         super(null);
      }

      private int toKeySym(int code, int location) {
         if (code >= 65 && code <= 90) {
            return 97 + (code - 65);
         } else if (code >= 48 && code <= 57) {
            return 48 + (code - 48);
         } else if (code == 16) {
            return (location & 3) != 0 ? '￡' : '￡';
         } else if (code == 17) {
            return (location & 3) != 0 ? '￤' : '￣';
         } else if (code == 18) {
            return (location & 3) != 0 ? '￪' : '￩';
         } else if (code == 157) {
            return (location & 3) != 0 ? '￨' : '\uffe7';
         } else {
            return 0;
         }
      }

      public boolean isPressed(int keycode, int location) {
         X11 lib = X11.INSTANCE;
         X11.Display dpy = lib.XOpenDisplay((String)null);
         if (dpy == null) {
            throw new Error("Can't open X Display");
         } else {
            try {
               byte[] keys = new byte[32];
               lib.XQueryKeymap(dpy, keys);
               int keysym = this.toKeySym(keycode, location);

               for(int code = 5; code < 256; ++code) {
                  int idx = code / 8;
                  int shift = code % 8;
                  if ((keys[idx] & 1 << shift) != 0) {
                     int sym = lib.XKeycodeToKeysym(dpy, (byte)code, 0).intValue();
                     if (sym == keysym) {
                        boolean var11 = true;
                        return var11;
                     }
                  }
               }

               return false;
            } finally {
               lib.XCloseDisplay(dpy);
            }
         }
      }

      // $FF: synthetic method
      X11KeyboardUtils(Object x0) {
         this();
      }
   }

   private static class MacKeyboardUtils extends NativeKeyboardUtils {
      private MacKeyboardUtils() {
         super(null);
      }

      public boolean isPressed(int keycode, int location) {
         return false;
      }

      // $FF: synthetic method
      MacKeyboardUtils(Object x0) {
         this();
      }
   }

   private static class W32KeyboardUtils extends NativeKeyboardUtils {
      private W32KeyboardUtils() {
         super(null);
      }

      private int toNative(int code, int loc) {
         if ((code < 65 || code > 90) && (code < 48 || code > 57)) {
            if (code == 16) {
               if ((loc & 3) != 0) {
                  return 161;
               } else {
                  return (loc & 2) != 0 ? 160 : 16;
               }
            } else if (code == 17) {
               if ((loc & 3) != 0) {
                  return 163;
               } else {
                  return (loc & 2) != 0 ? 162 : 17;
               }
            } else if (code == 18) {
               if ((loc & 3) != 0) {
                  return 165;
               } else {
                  return (loc & 2) != 0 ? 164 : 18;
               }
            } else {
               return 0;
            }
         } else {
            return code;
         }
      }

      public boolean isPressed(int keycode, int location) {
         User32 lib = User32.INSTANCE;
         return (lib.GetAsyncKeyState(this.toNative(keycode, location)) & '耀') != 0;
      }

      // $FF: synthetic method
      W32KeyboardUtils(Object x0) {
         this();
      }
   }

   private abstract static class NativeKeyboardUtils {
      private NativeKeyboardUtils() {
      }

      public abstract boolean isPressed(int var1, int var2);

      public boolean isPressed(int keycode) {
         return this.isPressed(keycode, 0);
      }

      // $FF: synthetic method
      NativeKeyboardUtils(Object x0) {
         this();
      }
   }
}
