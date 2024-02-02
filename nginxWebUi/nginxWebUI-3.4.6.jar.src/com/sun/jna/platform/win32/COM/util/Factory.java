/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.COMException;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatchCallback;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeoutException;
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
/*     */ public class Factory
/*     */   extends ObjectFactory
/*     */ {
/*     */   private ComThread comThread;
/*     */   
/*     */   public Factory() {
/*  61 */     this(new ComThread("Default Factory COM Thread", 5000L, new Thread.UncaughtExceptionHandler()
/*     */           {
/*     */             public void uncaughtException(Thread t, Throwable e) {}
/*     */           }));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Factory(ComThread comThread) {
/*  70 */     this.comThread = comThread;
/*     */   }
/*     */   
/*     */   private class ProxyObject2
/*     */     implements InvocationHandler {
/*     */     private final Object delegate;
/*     */     
/*     */     public ProxyObject2(Object delegate) {
/*  78 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
/*  83 */       if (args != null) {
/*  84 */         for (int i = 0; i < args.length; i++) {
/*  85 */           if (args[i] != null && 
/*  86 */             Proxy.isProxyClass(args[i].getClass())) {
/*  87 */             InvocationHandler ih = Proxy.getInvocationHandler(args[i]);
/*  88 */             if (ih instanceof ProxyObject2) {
/*  89 */               args[i] = ((ProxyObject2)ih).delegate;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/*  95 */       return Factory.this.runInComThread(new Callable()
/*     */           {
/*     */             public Object call() throws Exception {
/*  98 */               return method.invoke(Factory.ProxyObject2.this.delegate, args);
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   private class CallbackProxy2
/*     */     extends CallbackProxy {
/*     */     public CallbackProxy2(ObjectFactory factory, Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
/* 107 */       super(factory, comEventCallbackInterface, comEventCallbackListener);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
/* 114 */       ComThread.setComThread(true);
/*     */       try {
/* 116 */         return super.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
/*     */       } finally {
/* 118 */         ComThread.setComThread(false);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T createProxy(Class<T> comInterface, IDispatch dispatch) {
/* 125 */     T result = super.createProxy(comInterface, dispatch);
/* 126 */     ProxyObject2 po2 = new ProxyObject2(result);
/* 127 */     Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class[] { comInterface }, po2);
/* 128 */     return (T)proxy;
/*     */   }
/*     */ 
/*     */   
/*     */   Guid.GUID discoverClsId(final ComObject annotation) {
/* 133 */     return runInComThread(new Callable<Guid.GUID>() {
/*     */           public Guid.GUID call() throws Exception {
/* 135 */             return Factory.this.discoverClsId(annotation);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T fetchObject(final Class<T> comInterface) throws COMException {
/* 143 */     return runInComThread(new Callable<T>() {
/*     */           public T call() throws Exception {
/* 145 */             return Factory.this.fetchObject(comInterface);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T createObject(final Class<T> comInterface) {
/* 153 */     return runInComThread(new Callable<T>() {
/*     */           public T call() throws Exception {
/* 155 */             return Factory.this.createObject(comInterface);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
/* 162 */     return new CallbackProxy2(this, comEventCallbackInterface, comEventCallbackListener);
/*     */   }
/*     */ 
/*     */   
/*     */   public IRunningObjectTable getRunningObjectTable() {
/* 167 */     return super.getRunningObjectTable();
/*     */   }
/*     */   
/*     */   private <T> T runInComThread(Callable<T> callable) {
/*     */     try {
/* 172 */       return this.comThread.execute(callable);
/* 173 */     } catch (TimeoutException ex) {
/* 174 */       throw new RuntimeException(ex);
/* 175 */     } catch (InterruptedException ex) {
/* 176 */       throw new RuntimeException(ex);
/* 177 */     } catch (ExecutionException ex) {
/* 178 */       Throwable cause = ex.getCause();
/* 179 */       if (cause instanceof RuntimeException) {
/* 180 */         appendStacktrace(ex, cause);
/* 181 */         throw (RuntimeException)cause;
/* 182 */       }  if (cause instanceof InvocationTargetException) {
/* 183 */         cause = ((InvocationTargetException)cause).getTargetException();
/* 184 */         if (cause instanceof RuntimeException) {
/* 185 */           appendStacktrace(ex, cause);
/* 186 */           throw (RuntimeException)cause;
/*     */         } 
/*     */       } 
/* 189 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void appendStacktrace(Exception caughtException, Throwable toBeThrown) {
/* 198 */     StackTraceElement[] upperTrace = caughtException.getStackTrace();
/* 199 */     StackTraceElement[] lowerTrace = toBeThrown.getStackTrace();
/* 200 */     StackTraceElement[] trace = new StackTraceElement[upperTrace.length + lowerTrace.length];
/* 201 */     System.arraycopy(upperTrace, 0, trace, lowerTrace.length, upperTrace.length);
/* 202 */     System.arraycopy(lowerTrace, 0, trace, 0, lowerTrace.length);
/* 203 */     toBeThrown.setStackTrace(trace);
/*     */   }
/*     */   
/*     */   public ComThread getComThread() {
/* 207 */     return this.comThread;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\Factory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */