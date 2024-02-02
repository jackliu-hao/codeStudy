/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.win32.W32APITypeMapper;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ 
/*     */ public final class User32Util
/*     */ {
/*     */   public static final int registerWindowMessage(String lpString) {
/*  66 */     int messageId = User32.INSTANCE.RegisterWindowMessage(lpString);
/*  67 */     if (messageId == 0)
/*  68 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
/*  69 */     return messageId;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final WinDef.HWND createWindow(String className, String windowName, int style, int x, int y, int width, int height, WinDef.HWND parent, WinDef.HMENU menu, WinDef.HINSTANCE instance, WinDef.LPVOID param) {
/*  74 */     return createWindowEx(0, className, windowName, style, x, y, width, height, parent, menu, instance, param);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final WinDef.HWND createWindowEx(int exStyle, String className, String windowName, int style, int x, int y, int width, int height, WinDef.HWND parent, WinDef.HMENU menu, WinDef.HINSTANCE instance, WinDef.LPVOID param) {
/*  80 */     WinDef.HWND hWnd = User32.INSTANCE.CreateWindowEx(exStyle, className, windowName, style, x, y, width, height, parent, menu, instance, param);
/*  81 */     if (hWnd == null)
/*  82 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
/*  83 */     return hWnd;
/*     */   }
/*     */   
/*     */   public static final void destroyWindow(WinDef.HWND hWnd) {
/*  87 */     if (!User32.INSTANCE.DestroyWindow(hWnd))
/*  88 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
/*     */   }
/*     */   
/*     */   public static final List<WinUser.RAWINPUTDEVICELIST> GetRawInputDeviceList() {
/*  92 */     IntByReference puiNumDevices = new IntByReference(0);
/*  93 */     WinUser.RAWINPUTDEVICELIST placeholder = new WinUser.RAWINPUTDEVICELIST();
/*  94 */     int cbSize = placeholder.sizeof();
/*     */     
/*  96 */     int returnValue = User32.INSTANCE.GetRawInputDeviceList(null, puiNumDevices, cbSize);
/*  97 */     if (returnValue != 0) {
/*  98 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 101 */     int deviceCount = puiNumDevices.getValue();
/* 102 */     WinUser.RAWINPUTDEVICELIST[] records = (WinUser.RAWINPUTDEVICELIST[])placeholder.toArray(deviceCount);
/* 103 */     returnValue = User32.INSTANCE.GetRawInputDeviceList(records, puiNumDevices, cbSize);
/* 104 */     if (returnValue == -1) {
/* 105 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 108 */     if (returnValue != records.length) {
/* 109 */       throw new IllegalStateException("Mismatched allocated (" + records.length + ") vs. received devices count (" + returnValue + ")");
/*     */     }
/*     */     
/* 112 */     return Arrays.asList(records);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class MessageLoopThread
/*     */     extends Thread
/*     */   {
/*     */     public class Handler
/*     */       implements InvocationHandler
/*     */     {
/*     */       private final Object delegate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public Handler(Object delegate) {
/* 135 */         this.delegate = delegate;
/*     */       }
/*     */       
/*     */       public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
/*     */         try {
/* 140 */           return User32Util.MessageLoopThread.this.runOnThread(new Callable() {
/*     */                 public Object call() throws Exception {
/* 142 */                   return method.invoke(User32Util.MessageLoopThread.Handler.this.delegate, args);
/*     */                 }
/*     */               });
/* 145 */         } catch (InvocationTargetException ex) {
/* 146 */           Throwable cause = ex.getCause();
/* 147 */           if (cause instanceof Exception) {
/* 148 */             StackTraceElement[] hiddenStack = cause.getStackTrace();
/* 149 */             cause.fillInStackTrace();
/* 150 */             StackTraceElement[] currentStack = cause.getStackTrace();
/* 151 */             StackTraceElement[] fullStack = new StackTraceElement[currentStack.length + hiddenStack.length];
/* 152 */             System.arraycopy(hiddenStack, 0, fullStack, 0, hiddenStack.length);
/* 153 */             System.arraycopy(currentStack, 0, fullStack, hiddenStack.length, currentStack.length);
/* 154 */             cause.setStackTrace(fullStack);
/* 155 */             throw (Exception)cause;
/*     */           } 
/* 157 */           throw ex;
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 163 */     private volatile int nativeThreadId = 0;
/* 164 */     private volatile long javaThreadId = 0L;
/* 165 */     private final List<FutureTask> workQueue = Collections.synchronizedList(new ArrayList<FutureTask>());
/* 166 */     private static long messageLoopId = 0L;
/*     */     
/*     */     public MessageLoopThread() {
/* 169 */       setName("JNA User32 MessageLoop " + ++messageLoopId);
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 174 */       WinUser.MSG msg = new WinUser.MSG();
/*     */ 
/*     */       
/* 177 */       User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);
/* 178 */       this.javaThreadId = Thread.currentThread().getId();
/* 179 */       this.nativeThreadId = Kernel32.INSTANCE.GetCurrentThreadId();
/*     */       
/*     */       int getMessageReturn;
/* 182 */       while ((getMessageReturn = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
/* 183 */         if (getMessageReturn != -1) {
/*     */           
/* 185 */           while (!this.workQueue.isEmpty()) {
/*     */             try {
/* 187 */               FutureTask ft = this.workQueue.remove(0);
/* 188 */               ft.run();
/* 189 */             } catch (IndexOutOfBoundsException ex) {
/*     */               break;
/*     */             } 
/*     */           } 
/* 193 */           User32.INSTANCE.TranslateMessage(msg);
/* 194 */           User32.INSTANCE.DispatchMessage(msg);
/*     */           continue;
/*     */         } 
/* 197 */         if (getMessageFailed()) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 203 */       while (!this.workQueue.isEmpty()) {
/* 204 */         ((FutureTask)this.workQueue.remove(0)).cancel(false);
/*     */       }
/*     */     }
/*     */     
/*     */     public <V> Future<V> runAsync(Callable<V> command) {
/* 209 */       while (this.nativeThreadId == 0) {
/*     */         try {
/* 211 */           Thread.sleep(20L);
/* 212 */         } catch (InterruptedException ex) {
/* 213 */           Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */         } 
/*     */       } 
/* 216 */       FutureTask<V> futureTask = new FutureTask<V>(command);
/* 217 */       this.workQueue.add(futureTask);
/* 218 */       User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 1024, null, null);
/* 219 */       return futureTask;
/*     */     }
/*     */     
/*     */     public <V> V runOnThread(Callable<V> callable) throws Exception {
/* 223 */       while (this.javaThreadId == 0L) {
/*     */         try {
/* 225 */           Thread.sleep(20L);
/* 226 */         } catch (InterruptedException ex) {
/* 227 */           Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */         } 
/*     */       } 
/*     */       
/* 231 */       if (this.javaThreadId == Thread.currentThread().getId()) {
/* 232 */         return callable.call();
/*     */       }
/*     */       
/* 235 */       Future<V> ft = runAsync(callable);
/*     */       try {
/* 237 */         return ft.get();
/* 238 */       } catch (InterruptedException ex) {
/* 239 */         throw ex;
/* 240 */       } catch (ExecutionException ex) {
/* 241 */         Throwable cause = ex.getCause();
/* 242 */         if (cause instanceof Exception) {
/* 243 */           throw (Exception)cause;
/*     */         }
/* 245 */         throw ex;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void exit() {
/* 252 */       User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 18, null, null);
/*     */     }
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
/*     */     protected boolean getMessageFailed() {
/* 271 */       int lastError = Kernel32.INSTANCE.GetLastError();
/* 272 */       Logger.getLogger("com.sun.jna.platform.win32.User32Util.MessageLoopThread")
/* 273 */         .log(Level.WARNING, "Message loop was interrupted by an error. [lastError: {0}]", 
/*     */           
/* 275 */           Integer.valueOf(lastError));
/* 276 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String loadString(String location) throws UnsupportedEncodingException {
/* 288 */     int x = location.lastIndexOf(',');
/* 289 */     String moduleName = location.substring(0, x);
/* 290 */     int index = Math.abs(Integer.parseInt(location.substring(x + 1)));
/* 291 */     String path = Kernel32Util.expandEnvironmentStrings(moduleName);
/* 292 */     WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, 2);
/* 293 */     if (target == null) {
/* 294 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 296 */     Memory memory = new Memory(Native.POINTER_SIZE);
/* 297 */     x = User32.INSTANCE.LoadString(target, index, (Pointer)memory, 0);
/* 298 */     if (0 == x) {
/* 299 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 302 */     if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 303 */       return new String(memory.getPointer(0L).getCharArray(0L, x));
/*     */     }
/* 305 */     return new String(memory.getPointer(0L).getByteArray(0L, x), Native.getDefaultStringEncoding());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 313 */   public static final EnumSet<Win32VK> WIN32VK_MAPPABLE = EnumSet.of(Win32VK.VK_BACK, new Win32VK[] { Win32VK.VK_TAB, Win32VK.VK_CLEAR, Win32VK.VK_RETURN, Win32VK.VK_ESCAPE, Win32VK.VK_SPACE, Win32VK.VK_SELECT, Win32VK.VK_EXECUTE, Win32VK.VK_0, Win32VK.VK_1, Win32VK.VK_2, Win32VK.VK_3, Win32VK.VK_4, Win32VK.VK_5, Win32VK.VK_6, Win32VK.VK_7, Win32VK.VK_8, Win32VK.VK_9, Win32VK.VK_A, Win32VK.VK_B, Win32VK.VK_C, Win32VK.VK_D, Win32VK.VK_E, Win32VK.VK_F, Win32VK.VK_G, Win32VK.VK_H, Win32VK.VK_I, Win32VK.VK_J, Win32VK.VK_K, Win32VK.VK_L, Win32VK.VK_M, Win32VK.VK_N, Win32VK.VK_O, Win32VK.VK_P, Win32VK.VK_Q, Win32VK.VK_R, Win32VK.VK_S, Win32VK.VK_T, Win32VK.VK_U, Win32VK.VK_V, Win32VK.VK_W, Win32VK.VK_X, Win32VK.VK_Y, Win32VK.VK_Z, Win32VK.VK_NUMPAD0, Win32VK.VK_NUMPAD1, Win32VK.VK_NUMPAD2, Win32VK.VK_NUMPAD3, Win32VK.VK_NUMPAD4, Win32VK.VK_NUMPAD5, Win32VK.VK_NUMPAD6, Win32VK.VK_NUMPAD7, Win32VK.VK_NUMPAD8, Win32VK.VK_NUMPAD9, Win32VK.VK_MULTIPLY, Win32VK.VK_ADD, Win32VK.VK_SEPARATOR, Win32VK.VK_SUBTRACT, Win32VK.VK_DECIMAL, Win32VK.VK_DIVIDE, Win32VK.VK_OEM_NEC_EQUAL, Win32VK.VK_OEM_FJ_MASSHOU, Win32VK.VK_OEM_FJ_TOUROKU, Win32VK.VK_OEM_FJ_LOYA, Win32VK.VK_OEM_FJ_ROYA, Win32VK.VK_OEM_1, Win32VK.VK_OEM_PLUS, Win32VK.VK_OEM_COMMA, Win32VK.VK_OEM_MINUS, Win32VK.VK_OEM_PERIOD, Win32VK.VK_OEM_2, Win32VK.VK_OEM_3, Win32VK.VK_RESERVED_C1, Win32VK.VK_RESERVED_C2, Win32VK.VK_OEM_4, Win32VK.VK_OEM_5, Win32VK.VK_OEM_6, Win32VK.VK_OEM_7, Win32VK.VK_OEM_8, Win32VK.VK_OEM_AX, Win32VK.VK_OEM_102, Win32VK.VK_ICO_HELP, Win32VK.VK_PROCESSKEY, Win32VK.VK_ICO_CLEAR, Win32VK.VK_PACKET, Win32VK.VK_OEM_RESET, Win32VK.VK_OEM_JUMP, Win32VK.VK_OEM_PA1, Win32VK.VK_OEM_PA2, Win32VK.VK_OEM_PA3, Win32VK.VK_OEM_WSCTRL, Win32VK.VK_OEM_CUSEL, Win32VK.VK_OEM_ATTN, Win32VK.VK_OEM_FINISH, Win32VK.VK_OEM_COPY, Win32VK.VK_OEM_AUTO, Win32VK.VK_OEM_ENLW, Win32VK.VK_OEM_BACKTAB, Win32VK.VK_ATTN, Win32VK.VK_CRSEL, Win32VK.VK_EXSEL, Win32VK.VK_EREOF, Win32VK.VK_PLAY, Win32VK.VK_ZOOM, Win32VK.VK_NONAME, Win32VK.VK_PA1, Win32VK.VK_OEM_CLEAR });
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\User32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */