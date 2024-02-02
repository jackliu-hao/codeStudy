/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.COMException;
/*     */ import com.sun.jna.platform.win32.COM.COMUtils;
/*     */ import com.sun.jna.platform.win32.COM.Dispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatchCallback;
/*     */ import com.sun.jna.platform.win32.COM.RunningObjectTable;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class ObjectFactory
/*     */ {
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/*  60 */       disposeAll();
/*     */     } finally {
/*  62 */       super.finalize();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IRunningObjectTable getRunningObjectTable() {
/*  73 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*  75 */     PointerByReference rotPtr = new PointerByReference();
/*     */     
/*  77 */     WinNT.HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new WinDef.DWORD(0L), rotPtr);
/*     */     
/*  79 */     COMUtils.checkRC(hr);
/*     */     
/*  81 */     RunningObjectTable raw = new RunningObjectTable(rotPtr.getValue());
/*  82 */     IRunningObjectTable rot = new RunningObjectTable(raw, this);
/*  83 */     return rot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T createProxy(Class<T> comInterface, IDispatch dispatch) {
/*  91 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*  93 */     ProxyObject jop = new ProxyObject(comInterface, dispatch, this);
/*  94 */     Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class[] { comInterface }, jop);
/*  95 */     T result = comInterface.cast(proxy);
/*  96 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T createObject(Class<T> comInterface) {
/* 104 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 106 */     ComObject comObectAnnotation = comInterface.<ComObject>getAnnotation(ComObject.class);
/* 107 */     if (null == comObectAnnotation) {
/* 108 */       throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
/*     */     }
/*     */     
/* 111 */     Guid.GUID guid = discoverClsId(comObectAnnotation);
/*     */     
/* 113 */     PointerByReference ptrDisp = new PointerByReference();
/* 114 */     WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateInstance(guid, null, 21, (Guid.GUID)IDispatch.IID_IDISPATCH, ptrDisp);
/*     */ 
/*     */     
/* 117 */     COMUtils.checkRC(hr);
/* 118 */     Dispatch d = new Dispatch(ptrDisp.getValue());
/* 119 */     T t = createProxy(comInterface, (IDispatch)d);
/*     */ 
/*     */     
/* 122 */     int n = d.Release();
/* 123 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T fetchObject(Class<T> comInterface) throws COMException {
/* 131 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 133 */     ComObject comObectAnnotation = comInterface.<ComObject>getAnnotation(ComObject.class);
/* 134 */     if (null == comObectAnnotation) {
/* 135 */       throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
/*     */     }
/*     */     
/* 138 */     Guid.GUID guid = discoverClsId(comObectAnnotation);
/*     */     
/* 140 */     PointerByReference ptrDisp = new PointerByReference();
/* 141 */     WinNT.HRESULT hr = OleAuto.INSTANCE.GetActiveObject(guid, null, ptrDisp);
/*     */     
/* 143 */     COMUtils.checkRC(hr);
/* 144 */     Dispatch d = new Dispatch(ptrDisp.getValue());
/* 145 */     T t = createProxy(comInterface, (IDispatch)d);
/*     */ 
/*     */     
/* 148 */     d.Release();
/*     */     
/* 150 */     return t;
/*     */   }
/*     */   
/*     */   Guid.GUID discoverClsId(ComObject annotation) {
/* 154 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 156 */     String clsIdStr = annotation.clsId();
/* 157 */     String progIdStr = annotation.progId();
/* 158 */     if (null != clsIdStr && !clsIdStr.isEmpty())
/* 159 */       return (Guid.GUID)new Guid.CLSID(clsIdStr); 
/* 160 */     if (null != progIdStr && !progIdStr.isEmpty()) {
/* 161 */       Guid.CLSID.ByReference rclsid = new Guid.CLSID.ByReference();
/*     */       
/* 163 */       WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progIdStr, rclsid);
/*     */       
/* 165 */       COMUtils.checkRC(hr);
/* 166 */       return (Guid.GUID)rclsid;
/*     */     } 
/* 168 */     throw new COMException("ComObject must define a value for either clsId or progId");
/*     */   }
/*     */ 
/*     */   
/*     */   IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
/* 173 */     return new CallbackProxy(this, comEventCallbackInterface, comEventCallbackListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 183 */   private final List<WeakReference<ProxyObject>> registeredObjects = new LinkedList<WeakReference<ProxyObject>>();
/*     */   
/*     */   public void register(ProxyObject proxyObject) {
/* 186 */     synchronized (this.registeredObjects) {
/* 187 */       this.registeredObjects.add(new WeakReference<ProxyObject>(proxyObject));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void unregister(ProxyObject proxyObject) {
/* 192 */     synchronized (this.registeredObjects) {
/* 193 */       Iterator<WeakReference<ProxyObject>> iterator = this.registeredObjects.iterator();
/* 194 */       while (iterator.hasNext()) {
/* 195 */         WeakReference<ProxyObject> weakRef = iterator.next();
/* 196 */         ProxyObject po = weakRef.get();
/* 197 */         if (po == null || po == proxyObject) {
/* 198 */           iterator.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void disposeAll() {
/* 205 */     synchronized (this.registeredObjects) {
/* 206 */       List<WeakReference<ProxyObject>> s = new ArrayList<WeakReference<ProxyObject>>(this.registeredObjects);
/* 207 */       for (WeakReference<ProxyObject> weakRef : s) {
/* 208 */         ProxyObject po = weakRef.get();
/* 209 */         if (po != null) {
/* 210 */           po.dispose();
/*     */         }
/*     */       } 
/* 213 */       this.registeredObjects.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 220 */   private static final WinDef.LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private WinDef.LCID LCID;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinDef.LCID getLCID() {
/* 231 */     if (this.LCID != null) {
/* 232 */       return this.LCID;
/*     */     }
/* 234 */     return LOCALE_USER_DEFAULT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLCID(WinDef.LCID value) {
/* 244 */     this.LCID = value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\ObjectFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */