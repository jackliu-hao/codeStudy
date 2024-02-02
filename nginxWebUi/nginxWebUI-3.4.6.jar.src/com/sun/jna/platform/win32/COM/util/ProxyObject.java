/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.internal.ReflectionUtils;
/*     */ import com.sun.jna.platform.win32.COM.COMException;
/*     */ import com.sun.jna.platform.win32.COM.COMUtils;
/*     */ import com.sun.jna.platform.win32.COM.ConnectionPoint;
/*     */ import com.sun.jna.platform.win32.COM.ConnectionPointContainer;
/*     */ import com.sun.jna.platform.win32.COM.Dispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatchCallback;
/*     */ import com.sun.jna.platform.win32.COM.IUnknown;
/*     */ import com.sun.jna.platform.win32.COM.IUnknownCallback;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.Kernel32Util;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxyObject
/*     */   implements InvocationHandler, IDispatch, IRawDispatchHandle, IConnectionPoint
/*     */ {
/*     */   private long unknownId;
/*     */   private final Class<?> theInterface;
/*     */   private final ObjectFactory factory;
/*     */   private final IDispatch rawDispatch;
/*     */   
/*     */   public ProxyObject(Class<?> theInterface, IDispatch rawDispatch, ObjectFactory factory) {
/*  87 */     this.unknownId = -1L;
/*  88 */     this.rawDispatch = rawDispatch;
/*  89 */     this.theInterface = theInterface;
/*  90 */     this.factory = factory;
/*     */ 
/*     */     
/*  93 */     int n = this.rawDispatch.AddRef();
/*  94 */     getUnknownId();
/*  95 */     factory.register(this);
/*     */   }
/*     */   
/*     */   private long getUnknownId() {
/*  99 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 101 */     if (-1L == this.unknownId) {
/*     */       try {
/* 103 */         PointerByReference ppvObject = new PointerByReference();
/*     */         
/* 105 */         Thread current = Thread.currentThread();
/* 106 */         String tn = current.getName();
/*     */         
/* 108 */         Guid.IID iid = IUnknown.IID_IUNKNOWN;
/* 109 */         WinNT.HRESULT hr = getRawDispatch().QueryInterface(new Guid.REFIID(iid), ppvObject);
/*     */         
/* 111 */         if (WinNT.S_OK.equals(hr)) {
/* 112 */           Dispatch dispatch = new Dispatch(ppvObject.getValue());
/* 113 */           this.unknownId = Pointer.nativeValue(dispatch.getPointer());
/*     */ 
/*     */ 
/*     */           
/* 117 */           int i = dispatch.Release();
/*     */         } else {
/* 119 */           String formatMessageFromHR = Kernel32Util.formatMessage(hr);
/* 120 */           throw new COMException("getUnknownId: " + formatMessageFromHR, hr);
/*     */         } 
/* 122 */       } catch (RuntimeException e) {
/*     */         
/* 124 */         if (e instanceof COMException) {
/* 125 */           throw e;
/*     */         }
/* 127 */         throw new COMException("Error occured when trying get Unknown Id ", e);
/*     */       } 
/*     */     }
/*     */     
/* 131 */     return this.unknownId;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 136 */     dispose();
/* 137 */     super.finalize();
/*     */   }
/*     */   
/*     */   public synchronized void dispose() {
/* 141 */     if (((Dispatch)this.rawDispatch).getPointer() != Pointer.NULL) {
/* 142 */       this.rawDispatch.Release();
/* 143 */       ((Dispatch)this.rawDispatch).setPointer(Pointer.NULL);
/* 144 */       this.factory.unregister(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public IDispatch getRawDispatch() {
/* 150 */     return this.rawDispatch;
/*     */   }
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
/*     */   public boolean equals(Object arg) {
/* 165 */     if (null == arg)
/* 166 */       return false; 
/* 167 */     if (arg instanceof ProxyObject) {
/* 168 */       ProxyObject other = (ProxyObject)arg;
/* 169 */       return (getUnknownId() == other.getUnknownId());
/* 170 */     }  if (Proxy.isProxyClass(arg.getClass())) {
/* 171 */       InvocationHandler handler = Proxy.getInvocationHandler(arg);
/* 172 */       if (handler instanceof ProxyObject) {
/*     */         try {
/* 174 */           ProxyObject other = (ProxyObject)handler;
/* 175 */           return (getUnknownId() == other.getUnknownId());
/* 176 */         } catch (Exception e) {
/*     */ 
/*     */           
/* 179 */           return false;
/*     */         } 
/*     */       }
/* 182 */       return false;
/*     */     } 
/*     */     
/* 185 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 191 */     long id = getUnknownId();
/* 192 */     return (int)(id >>> 32L & 0xFFFFFFFFFFFFFFFFL) + (int)(id & 0xFFFFFFFFFFFFFFFFL);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 197 */     return this.theInterface.getName() + "{unk=" + hashCode() + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 206 */     boolean declaredAsInterface = (method.getAnnotation(ComMethod.class) != null || method.getAnnotation(ComProperty.class) != null);
/*     */     
/* 208 */     if (!declaredAsInterface && (method.getDeclaringClass().equals(Object.class) || method
/* 209 */       .getDeclaringClass().equals(IRawDispatchHandle.class) || method
/* 210 */       .getDeclaringClass().equals(IUnknown.class) || method
/* 211 */       .getDeclaringClass().equals(IDispatch.class) || method
/* 212 */       .getDeclaringClass().equals(IConnectionPoint.class))) {
/*     */       try {
/* 214 */         return method.invoke(this, args);
/* 215 */       } catch (InvocationTargetException ex) {
/* 216 */         throw ex.getCause();
/*     */       } 
/*     */     }
/*     */     
/* 220 */     if (!declaredAsInterface && ReflectionUtils.isDefault(method)) {
/* 221 */       Object methodHandle = ReflectionUtils.getMethodHandle(method);
/* 222 */       return ReflectionUtils.invokeDefaultMethod(proxy, methodHandle, args);
/*     */     } 
/*     */     
/* 225 */     Class<?> returnType = method.getReturnType();
/* 226 */     boolean isVoid = void.class.equals(returnType);
/*     */     
/* 228 */     ComProperty prop = method.<ComProperty>getAnnotation(ComProperty.class);
/* 229 */     if (null != prop) {
/* 230 */       int dispId = prop.dispId();
/* 231 */       Object[] fullLengthArgs = unfoldWhenVarargs(method, args);
/* 232 */       if (isVoid) {
/* 233 */         if (dispId != -1) {
/* 234 */           setProperty(new OaIdl.DISPID(dispId), fullLengthArgs);
/* 235 */           return null;
/*     */         } 
/* 237 */         String str = getMutatorName(method, prop);
/* 238 */         setProperty(str, fullLengthArgs);
/* 239 */         return null;
/*     */       } 
/*     */       
/* 242 */       if (dispId != -1) {
/* 243 */         return getProperty(returnType, new OaIdl.DISPID(dispId), args);
/*     */       }
/* 245 */       String propName = getAccessorName(method, prop);
/* 246 */       return getProperty(returnType, propName, args);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 251 */     ComMethod meth = method.<ComMethod>getAnnotation(ComMethod.class);
/* 252 */     if (null != meth) {
/* 253 */       Object[] fullLengthArgs = unfoldWhenVarargs(method, args);
/* 254 */       int dispId = meth.dispId();
/* 255 */       if (dispId != -1) {
/* 256 */         return invokeMethod(returnType, new OaIdl.DISPID(dispId), fullLengthArgs);
/*     */       }
/* 258 */       String methName = getMethodName(method, meth);
/* 259 */       return invokeMethod(returnType, methName, fullLengthArgs);
/*     */     } 
/*     */ 
/*     */     
/* 263 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private ConnectionPoint fetchRawConnectionPoint(Guid.IID iid) {
/* 268 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */ 
/*     */     
/* 271 */     IConnectionPointContainer cpc = queryInterface(IConnectionPointContainer.class);
/* 272 */     Dispatch rawCpcDispatch = (Dispatch)cpc.getRawDispatch();
/* 273 */     ConnectionPointContainer rawCpc = new ConnectionPointContainer(rawCpcDispatch.getPointer());
/*     */ 
/*     */     
/* 276 */     Guid.REFIID adviseRiid = new Guid.REFIID(iid.getPointer());
/* 277 */     PointerByReference ppCp = new PointerByReference();
/* 278 */     WinNT.HRESULT hr = rawCpc.FindConnectionPoint(adviseRiid, ppCp);
/* 279 */     COMUtils.checkRC(hr);
/* 280 */     ConnectionPoint rawCp = new ConnectionPoint(ppCp.getValue());
/* 281 */     return rawCp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IComEventCallbackCookie advise(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) throws COMException {
/* 288 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*     */     try {
/* 291 */       ComInterface comInterfaceAnnotation = comEventCallbackInterface.<ComInterface>getAnnotation(ComInterface.class);
/* 292 */       if (null == comInterfaceAnnotation) {
/* 293 */         throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation");
/*     */       }
/*     */       
/* 296 */       Guid.IID iid = getIID(comInterfaceAnnotation);
/*     */       
/* 298 */       ConnectionPoint rawCp = fetchRawConnectionPoint(iid);
/*     */ 
/*     */       
/* 301 */       IDispatchCallback rawListener = this.factory.createDispatchCallback(comEventCallbackInterface, comEventCallbackListener);
/*     */ 
/*     */       
/* 304 */       comEventCallbackListener.setDispatchCallbackListener(rawListener);
/*     */ 
/*     */       
/* 307 */       WinDef.DWORDByReference pdwCookie = new WinDef.DWORDByReference();
/* 308 */       WinNT.HRESULT hr = rawCp.Advise((IUnknownCallback)rawListener, pdwCookie);
/* 309 */       int n = rawCp.Release();
/*     */       
/* 311 */       COMUtils.checkRC(hr);
/*     */ 
/*     */       
/* 314 */       return new ComEventCallbackCookie(pdwCookie.getValue());
/*     */     }
/* 316 */     catch (RuntimeException e) {
/*     */       
/* 318 */       if (e instanceof COMException) {
/* 319 */         throw e;
/*     */       }
/* 321 */       throw new COMException("Error occured in advise when trying to connect the listener " + comEventCallbackListener, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void unadvise(Class<?> comEventCallbackInterface, IComEventCallbackCookie cookie) throws COMException {
/* 328 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*     */     try {
/* 331 */       ComInterface comInterfaceAnnotation = comEventCallbackInterface.<ComInterface>getAnnotation(ComInterface.class);
/* 332 */       if (null == comInterfaceAnnotation) {
/* 333 */         throw new COMException("unadvise: Interface must define a value for iid via the ComInterface annotation");
/*     */       }
/*     */       
/* 336 */       Guid.IID iid = getIID(comInterfaceAnnotation);
/*     */       
/* 338 */       ConnectionPoint rawCp = fetchRawConnectionPoint(iid);
/*     */       
/* 340 */       WinNT.HRESULT hr = rawCp.Unadvise(((ComEventCallbackCookie)cookie).getValue());
/*     */       
/* 342 */       rawCp.Release();
/* 343 */       COMUtils.checkRC(hr);
/*     */     }
/* 345 */     catch (RuntimeException e) {
/*     */       
/* 347 */       if (e instanceof COMException) {
/* 348 */         throw e;
/*     */       }
/* 350 */       throw new COMException("Error occured in unadvise when trying to disconnect the listener from " + this, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void setProperty(String name, T value) {
/* 358 */     OaIdl.DISPID dispID = resolveDispId(getRawDispatch(), name);
/* 359 */     setProperty(dispID, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void setProperty(OaIdl.DISPID dispId, T value) {
/* 364 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 366 */     Variant.VARIANT v = Convert.toVariant(value);
/* 367 */     WinNT.HRESULT hr = oleMethod(4, (Variant.VARIANT.ByReference)null, getRawDispatch(), dispId, v);
/* 368 */     Convert.free(v, value);
/* 369 */     COMUtils.checkRC(hr);
/*     */   }
/*     */   
/*     */   private void setProperty(String name, Object... args) {
/* 373 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/* 374 */     OaIdl.DISPID dispID = resolveDispId(getRawDispatch(), name);
/* 375 */     setProperty(dispID, args);
/*     */   }
/*     */   private void setProperty(OaIdl.DISPID dispID, Object... args) {
/*     */     Variant.VARIANT[] vargs;
/* 379 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */ 
/*     */     
/* 382 */     if (null == args) {
/* 383 */       vargs = new Variant.VARIANT[0];
/*     */     } else {
/* 385 */       vargs = new Variant.VARIANT[args.length];
/*     */     } 
/* 387 */     for (int i = 0; i < vargs.length; i++) {
/* 388 */       vargs[i] = Convert.toVariant(args[i]);
/*     */     }
/* 390 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 391 */     WinNT.HRESULT hr = oleMethod(4, (Variant.VARIANT.ByReference)null, getRawDispatch(), dispID, vargs);
/*     */     
/* 393 */     for (int j = 0; j < vargs.length; j++)
/*     */     {
/* 395 */       Convert.free(vargs[j], args[j]);
/*     */     }
/*     */     
/* 398 */     COMUtils.checkRC(hr);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getProperty(Class<T> returnType, String name, Object... args) {
/* 403 */     OaIdl.DISPID dispID = resolveDispId(getRawDispatch(), name);
/* 404 */     return getProperty(returnType, dispID, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getProperty(Class<T> returnType, OaIdl.DISPID dispID, Object... args) {
/*     */     Variant.VARIANT[] vargs;
/* 410 */     if (null == args) {
/* 411 */       vargs = new Variant.VARIANT[0];
/*     */     } else {
/* 413 */       vargs = new Variant.VARIANT[args.length];
/*     */     } 
/* 415 */     for (int i = 0; i < vargs.length; i++) {
/* 416 */       vargs[i] = Convert.toVariant(args[i]);
/*     */     }
/* 418 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 419 */     WinNT.HRESULT hr = oleMethod(2, result, getRawDispatch(), dispID, vargs);
/*     */     
/* 421 */     for (int j = 0; j < vargs.length; j++)
/*     */     {
/* 423 */       Convert.free(vargs[j], args[j]);
/*     */     }
/*     */     
/* 426 */     COMUtils.checkRC(hr);
/*     */     
/* 428 */     return (T)Convert.toJavaObject((Variant.VARIANT)result, returnType, this.factory, false, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T invokeMethod(Class<T> returnType, String name, Object... args) {
/* 433 */     OaIdl.DISPID dispID = resolveDispId(getRawDispatch(), name);
/* 434 */     return invokeMethod(returnType, dispID, args);
/*     */   }
/*     */   
/*     */   public <T> T invokeMethod(Class<T> returnType, OaIdl.DISPID dispID, Object... args) {
/*     */     Variant.VARIANT[] vargs;
/* 439 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */ 
/*     */     
/* 442 */     if (null == args) {
/* 443 */       vargs = new Variant.VARIANT[0];
/*     */     } else {
/* 445 */       vargs = new Variant.VARIANT[args.length];
/*     */     } 
/* 447 */     for (int i = 0; i < vargs.length; i++) {
/* 448 */       vargs[i] = Convert.toVariant(args[i]);
/*     */     }
/* 450 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 451 */     WinNT.HRESULT hr = oleMethod(1, result, getRawDispatch(), dispID, vargs);
/*     */     
/* 453 */     for (int j = 0; j < vargs.length; j++)
/*     */     {
/* 455 */       Convert.free(vargs[j], args[j]);
/*     */     }
/*     */     
/* 458 */     COMUtils.checkRC(hr);
/*     */     
/* 460 */     return (T)Convert.toJavaObject((Variant.VARIANT)result, returnType, this.factory, false, true);
/*     */   }
/*     */   
/*     */   private Object[] unfoldWhenVarargs(Method method, Object[] argParams) {
/* 464 */     if (null == argParams) {
/* 465 */       return null;
/*     */     }
/* 467 */     if (argParams.length == 0 || !method.isVarArgs() || !(argParams[argParams.length - 1] instanceof Object[])) {
/* 468 */       return argParams;
/*     */     }
/*     */     
/* 471 */     Object[] varargs = (Object[])argParams[argParams.length - 1];
/* 472 */     Object[] args = new Object[argParams.length - 1 + varargs.length];
/* 473 */     System.arraycopy(argParams, 0, args, 0, argParams.length - 1);
/* 474 */     System.arraycopy(varargs, 0, args, argParams.length - 1, varargs.length);
/* 475 */     return args;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T queryInterface(Class<T> comInterface) throws COMException {
/* 480 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*     */     try {
/* 483 */       ComInterface comInterfaceAnnotation = comInterface.<ComInterface>getAnnotation(ComInterface.class);
/* 484 */       if (null == comInterfaceAnnotation) {
/* 485 */         throw new COMException("queryInterface: Interface must define a value for iid via the ComInterface annotation");
/*     */       }
/*     */       
/* 488 */       Guid.IID iid = getIID(comInterfaceAnnotation);
/* 489 */       PointerByReference ppvObject = new PointerByReference();
/*     */       
/* 491 */       WinNT.HRESULT hr = getRawDispatch().QueryInterface(new Guid.REFIID(iid), ppvObject);
/*     */       
/* 493 */       if (WinNT.S_OK.equals(hr)) {
/* 494 */         Dispatch dispatch = new Dispatch(ppvObject.getValue());
/* 495 */         T t = this.factory.createProxy(comInterface, (IDispatch)dispatch);
/*     */ 
/*     */ 
/*     */         
/* 499 */         int n = dispatch.Release();
/* 500 */         return t;
/*     */       } 
/* 502 */       String formatMessageFromHR = Kernel32Util.formatMessage(hr);
/* 503 */       throw new COMException("queryInterface: " + formatMessageFromHR, hr);
/*     */     }
/* 505 */     catch (RuntimeException e) {
/*     */       
/* 507 */       if (e instanceof COMException) {
/* 508 */         throw e;
/*     */       }
/* 510 */       throw new COMException("Error occured when trying to query for interface " + comInterface.getName(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Guid.IID getIID(ComInterface annotation) {
/* 516 */     String iidStr = annotation.iid();
/* 517 */     if (null != iidStr && !iidStr.isEmpty()) {
/* 518 */       return new Guid.IID(iidStr);
/*     */     }
/* 520 */     throw new COMException("ComInterface must define a value for iid");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getAccessorName(Method method, ComProperty prop) {
/* 527 */     if (prop.name().isEmpty()) {
/* 528 */       String methName = method.getName();
/* 529 */       if (methName.startsWith("get")) {
/* 530 */         return methName.replaceFirst("get", "");
/*     */       }
/* 532 */       throw new RuntimeException("Property Accessor name must start with 'get', or set the anotation 'name' value");
/*     */     } 
/*     */ 
/*     */     
/* 536 */     return prop.name();
/*     */   }
/*     */ 
/*     */   
/*     */   private String getMutatorName(Method method, ComProperty prop) {
/* 541 */     if (prop.name().isEmpty()) {
/* 542 */       String methName = method.getName();
/* 543 */       if (methName.startsWith("set")) {
/* 544 */         return methName.replaceFirst("set", "");
/*     */       }
/* 546 */       throw new RuntimeException("Property Mutator name must start with 'set', or set the anotation 'name' value");
/*     */     } 
/*     */ 
/*     */     
/* 550 */     return prop.name();
/*     */   }
/*     */ 
/*     */   
/*     */   private String getMethodName(Method method, ComMethod meth) {
/* 555 */     if (meth.name().isEmpty()) {
/* 556 */       String methName = method.getName();
/* 557 */       return methName;
/*     */     } 
/* 559 */     return meth.name();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected OaIdl.DISPID resolveDispId(String name) {
/* 565 */     return resolveDispId(getRawDispatch(), name);
/*     */   }
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT pArg) throws COMException {
/* 570 */     return oleMethod(nType, pvResult, name, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
/* 575 */     return oleMethod(nType, pvResult, dispId, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name) throws COMException {
/* 580 */     return oleMethod(nType, pvResult, name, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId) throws COMException {
/* 586 */     return oleMethod(nType, pvResult, dispId, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT[] pArgs) throws COMException {
/* 592 */     return oleMethod(nType, pvResult, resolveDispId(name), pArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
/* 598 */     return oleMethod(nType, pvResult, getRawDispatch(), dispId, pArgs);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected OaIdl.DISPID resolveDispId(IDispatch pDisp, String name) {
/* 603 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 605 */     if (pDisp == null) {
/* 606 */       throw new COMException("pDisp (IDispatch) parameter is null!");
/*     */     }
/*     */     
/* 609 */     WString[] ptName = { new WString(name) };
/* 610 */     OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
/*     */     
/* 612 */     WinNT.HRESULT hr = pDisp.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, this.factory
/*     */ 
/*     */ 
/*     */         
/* 616 */         .getLCID(), pdispID);
/*     */ 
/*     */     
/* 619 */     COMUtils.checkRC(hr);
/*     */     
/* 621 */     return pdispID.getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name, Variant.VARIANT pArg) throws COMException {
/* 627 */     return oleMethod(nType, pvResult, pDisp, name, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
/* 633 */     return oleMethod(nType, pvResult, pDisp, dispId, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name) throws COMException {
/* 639 */     return oleMethod(nType, pvResult, pDisp, name, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId) throws COMException {
/* 646 */     return oleMethod(nType, pvResult, pDisp, dispId, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name, Variant.VARIANT[] pArgs) throws COMException {
/* 653 */     return oleMethod(nType, pvResult, pDisp, resolveDispId(pDisp, name), pArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
/*     */     int finalNType;
/* 660 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/* 662 */     if (pDisp == null) {
/* 663 */       throw new COMException("pDisp (IDispatch) parameter is null!");
/*     */     }
/*     */ 
/*     */     
/* 667 */     int _argsLen = 0;
/* 668 */     Variant.VARIANT[] _args = null;
/* 669 */     OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
/* 670 */     OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
/* 671 */     IntByReference puArgErr = new IntByReference();
/*     */ 
/*     */     
/* 674 */     if (pArgs != null && pArgs.length > 0) {
/* 675 */       _argsLen = pArgs.length;
/* 676 */       _args = new Variant.VARIANT[_argsLen];
/*     */       
/* 678 */       int revCount = _argsLen;
/* 679 */       for (int i = 0; i < _argsLen; i++) {
/* 680 */         _args[i] = pArgs[--revCount];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 685 */     if (nType == 4) {
/* 686 */       dp.setRgdispidNamedArgs(new OaIdl.DISPID[] { OaIdl.DISPID_PROPERTYPUT });
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 713 */     if (nType == 1 || nType == 2) {
/* 714 */       finalNType = 3;
/*     */     } else {
/* 716 */       finalNType = nType;
/*     */     } 
/*     */ 
/*     */     
/* 720 */     if (_argsLen > 0) {
/* 721 */       dp.setArgs(_args);
/*     */ 
/*     */       
/* 724 */       dp.write();
/*     */     } 
/*     */     
/* 727 */     WinNT.HRESULT hr = pDisp.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), this.factory
/*     */ 
/*     */         
/* 730 */         .getLCID(), new WinDef.WORD(finalNType), dp, pvResult, pExcepInfo, puArgErr);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 737 */     COMUtils.checkRC(hr, (OaIdl.EXCEPINFO)pExcepInfo, puArgErr);
/* 738 */     return hr;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\ProxyObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */