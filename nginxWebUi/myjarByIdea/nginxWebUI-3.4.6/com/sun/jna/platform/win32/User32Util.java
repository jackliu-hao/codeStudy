package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class User32Util {
   public static final EnumSet<Win32VK> WIN32VK_MAPPABLE;

   public static final int registerWindowMessage(String lpString) {
      int messageId = User32.INSTANCE.RegisterWindowMessage(lpString);
      if (messageId == 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return messageId;
      }
   }

   public static final WinDef.HWND createWindow(String className, String windowName, int style, int x, int y, int width, int height, WinDef.HWND parent, WinDef.HMENU menu, WinDef.HINSTANCE instance, WinDef.LPVOID param) {
      return createWindowEx(0, className, windowName, style, x, y, width, height, parent, menu, instance, param);
   }

   public static final WinDef.HWND createWindowEx(int exStyle, String className, String windowName, int style, int x, int y, int width, int height, WinDef.HWND parent, WinDef.HMENU menu, WinDef.HINSTANCE instance, WinDef.LPVOID param) {
      WinDef.HWND hWnd = User32.INSTANCE.CreateWindowEx(exStyle, className, windowName, style, x, y, width, height, parent, menu, instance, param);
      if (hWnd == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return hWnd;
      }
   }

   public static final void destroyWindow(WinDef.HWND hWnd) {
      if (!User32.INSTANCE.DestroyWindow(hWnd)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static final List<WinUser.RAWINPUTDEVICELIST> GetRawInputDeviceList() {
      IntByReference puiNumDevices = new IntByReference(0);
      WinUser.RAWINPUTDEVICELIST placeholder = new WinUser.RAWINPUTDEVICELIST();
      int cbSize = placeholder.sizeof();
      int returnValue = User32.INSTANCE.GetRawInputDeviceList((WinUser.RAWINPUTDEVICELIST[])null, puiNumDevices, cbSize);
      if (returnValue != 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         int deviceCount = puiNumDevices.getValue();
         WinUser.RAWINPUTDEVICELIST[] records = (WinUser.RAWINPUTDEVICELIST[])((WinUser.RAWINPUTDEVICELIST[])placeholder.toArray(deviceCount));
         returnValue = User32.INSTANCE.GetRawInputDeviceList(records, puiNumDevices, cbSize);
         if (returnValue == -1) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else if (returnValue != records.length) {
            throw new IllegalStateException("Mismatched allocated (" + records.length + ") vs. received devices count (" + returnValue + ")");
         } else {
            return Arrays.asList(records);
         }
      }
   }

   public static String loadString(String location) throws UnsupportedEncodingException {
      int x = location.lastIndexOf(44);
      String moduleName = location.substring(0, x);
      int index = Math.abs(Integer.parseInt(location.substring(x + 1)));
      String path = Kernel32Util.expandEnvironmentStrings(moduleName);
      WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, (WinNT.HANDLE)null, 2);
      if (target == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         Pointer lpBuffer = new Memory((long)Native.POINTER_SIZE);
         x = User32.INSTANCE.LoadString(target, index, lpBuffer, 0);
         if (0 == x) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? new String(lpBuffer.getPointer(0L).getCharArray(0L, x)) : new String(lpBuffer.getPointer(0L).getByteArray(0L, x), Native.getDefaultStringEncoding());
         }
      }
   }

   static {
      WIN32VK_MAPPABLE = EnumSet.of(Win32VK.VK_BACK, Win32VK.VK_TAB, Win32VK.VK_CLEAR, Win32VK.VK_RETURN, Win32VK.VK_ESCAPE, Win32VK.VK_SPACE, Win32VK.VK_SELECT, Win32VK.VK_EXECUTE, Win32VK.VK_0, Win32VK.VK_1, Win32VK.VK_2, Win32VK.VK_3, Win32VK.VK_4, Win32VK.VK_5, Win32VK.VK_6, Win32VK.VK_7, Win32VK.VK_8, Win32VK.VK_9, Win32VK.VK_A, Win32VK.VK_B, Win32VK.VK_C, Win32VK.VK_D, Win32VK.VK_E, Win32VK.VK_F, Win32VK.VK_G, Win32VK.VK_H, Win32VK.VK_I, Win32VK.VK_J, Win32VK.VK_K, Win32VK.VK_L, Win32VK.VK_M, Win32VK.VK_N, Win32VK.VK_O, Win32VK.VK_P, Win32VK.VK_Q, Win32VK.VK_R, Win32VK.VK_S, Win32VK.VK_T, Win32VK.VK_U, Win32VK.VK_V, Win32VK.VK_W, Win32VK.VK_X, Win32VK.VK_Y, Win32VK.VK_Z, Win32VK.VK_NUMPAD0, Win32VK.VK_NUMPAD1, Win32VK.VK_NUMPAD2, Win32VK.VK_NUMPAD3, Win32VK.VK_NUMPAD4, Win32VK.VK_NUMPAD5, Win32VK.VK_NUMPAD6, Win32VK.VK_NUMPAD7, Win32VK.VK_NUMPAD8, Win32VK.VK_NUMPAD9, Win32VK.VK_MULTIPLY, Win32VK.VK_ADD, Win32VK.VK_SEPARATOR, Win32VK.VK_SUBTRACT, Win32VK.VK_DECIMAL, Win32VK.VK_DIVIDE, Win32VK.VK_OEM_NEC_EQUAL, Win32VK.VK_OEM_FJ_MASSHOU, Win32VK.VK_OEM_FJ_TOUROKU, Win32VK.VK_OEM_FJ_LOYA, Win32VK.VK_OEM_FJ_ROYA, Win32VK.VK_OEM_1, Win32VK.VK_OEM_PLUS, Win32VK.VK_OEM_COMMA, Win32VK.VK_OEM_MINUS, Win32VK.VK_OEM_PERIOD, Win32VK.VK_OEM_2, Win32VK.VK_OEM_3, Win32VK.VK_RESERVED_C1, Win32VK.VK_RESERVED_C2, Win32VK.VK_OEM_4, Win32VK.VK_OEM_5, Win32VK.VK_OEM_6, Win32VK.VK_OEM_7, Win32VK.VK_OEM_8, Win32VK.VK_OEM_AX, Win32VK.VK_OEM_102, Win32VK.VK_ICO_HELP, Win32VK.VK_PROCESSKEY, Win32VK.VK_ICO_CLEAR, Win32VK.VK_PACKET, Win32VK.VK_OEM_RESET, Win32VK.VK_OEM_JUMP, Win32VK.VK_OEM_PA1, Win32VK.VK_OEM_PA2, Win32VK.VK_OEM_PA3, Win32VK.VK_OEM_WSCTRL, Win32VK.VK_OEM_CUSEL, Win32VK.VK_OEM_ATTN, Win32VK.VK_OEM_FINISH, Win32VK.VK_OEM_COPY, Win32VK.VK_OEM_AUTO, Win32VK.VK_OEM_ENLW, Win32VK.VK_OEM_BACKTAB, Win32VK.VK_ATTN, Win32VK.VK_CRSEL, Win32VK.VK_EXSEL, Win32VK.VK_EREOF, Win32VK.VK_PLAY, Win32VK.VK_ZOOM, Win32VK.VK_NONAME, Win32VK.VK_PA1, Win32VK.VK_OEM_CLEAR);
   }

   public static class MessageLoopThread extends Thread {
      private volatile int nativeThreadId = 0;
      private volatile long javaThreadId = 0L;
      private final List<FutureTask> workQueue = Collections.synchronizedList(new ArrayList());
      private static long messageLoopId = 0L;

      public MessageLoopThread() {
         this.setName("JNA User32 MessageLoop " + ++messageLoopId);
      }

      public void run() {
         WinUser.MSG msg = new WinUser.MSG();
         User32.INSTANCE.PeekMessage(msg, (WinDef.HWND)null, 0, 0, 0);
         this.javaThreadId = Thread.currentThread().getId();
         this.nativeThreadId = Kernel32.INSTANCE.GetCurrentThreadId();

         int getMessageReturn;
         while((getMessageReturn = User32.INSTANCE.GetMessage(msg, (WinDef.HWND)null, 0, 0)) != 0) {
            if (getMessageReturn == -1) {
               if (this.getMessageFailed()) {
                  break;
               }
            } else {
               while(!this.workQueue.isEmpty()) {
                  try {
                     FutureTask ft = (FutureTask)this.workQueue.remove(0);
                     ft.run();
                  } catch (IndexOutOfBoundsException var4) {
                     break;
                  }
               }

               User32.INSTANCE.TranslateMessage(msg);
               User32.INSTANCE.DispatchMessage(msg);
            }
         }

         while(!this.workQueue.isEmpty()) {
            ((FutureTask)this.workQueue.remove(0)).cancel(false);
         }

      }

      public <V> Future<V> runAsync(Callable<V> command) {
         while(this.nativeThreadId == 0) {
            try {
               Thread.sleep(20L);
            } catch (InterruptedException var3) {
               Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, (String)null, var3);
            }
         }

         FutureTask<V> futureTask = new FutureTask(command);
         this.workQueue.add(futureTask);
         User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 1024, (WinDef.WPARAM)null, (WinDef.LPARAM)null);
         return futureTask;
      }

      public <V> V runOnThread(Callable<V> callable) throws Exception {
         while(this.javaThreadId == 0L) {
            try {
               Thread.sleep(20L);
            } catch (InterruptedException var5) {
               Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, (String)null, var5);
            }
         }

         if (this.javaThreadId == Thread.currentThread().getId()) {
            return callable.call();
         } else {
            Future<V> ft = this.runAsync(callable);

            try {
               return ft.get();
            } catch (InterruptedException var6) {
               throw var6;
            } catch (ExecutionException var7) {
               Throwable cause = var7.getCause();
               if (cause instanceof Exception) {
                  throw (Exception)cause;
               } else {
                  throw var7;
               }
            }
         }
      }

      public void exit() {
         User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 18, (WinDef.WPARAM)null, (WinDef.LPARAM)null);
      }

      protected boolean getMessageFailed() {
         int lastError = Kernel32.INSTANCE.GetLastError();
         Logger.getLogger("com.sun.jna.platform.win32.User32Util.MessageLoopThread").log(Level.WARNING, "Message loop was interrupted by an error. [lastError: {0}]", lastError);
         return true;
      }

      public class Handler implements InvocationHandler {
         private final Object delegate;

         public Handler(Object delegate) {
            this.delegate = delegate;
         }

         public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
               return MessageLoopThread.this.runOnThread(new Callable<Object>() {
                  public Object call() throws Exception {
                     return method.invoke(Handler.this.delegate, args);
                  }
               });
            } catch (InvocationTargetException var9) {
               Throwable cause = var9.getCause();
               if (cause instanceof Exception) {
                  StackTraceElement[] hiddenStack = cause.getStackTrace();
                  cause.fillInStackTrace();
                  StackTraceElement[] currentStack = cause.getStackTrace();
                  StackTraceElement[] fullStack = new StackTraceElement[currentStack.length + hiddenStack.length];
                  System.arraycopy(hiddenStack, 0, fullStack, 0, hiddenStack.length);
                  System.arraycopy(currentStack, 0, fullStack, hiddenStack.length, currentStack.length);
                  cause.setStackTrace(fullStack);
                  throw (Exception)cause;
               } else {
                  throw var9;
               }
            }
         }
      }
   }
}
