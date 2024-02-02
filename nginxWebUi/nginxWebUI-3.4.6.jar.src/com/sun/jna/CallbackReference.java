/*     */ package com.sun.jna;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
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
/*     */ public class CallbackReference
/*     */   extends WeakReference<Callback>
/*     */ {
/*  52 */   static final Map<Callback, CallbackReference> callbackMap = new WeakHashMap<Callback, CallbackReference>();
/*  53 */   static final Map<Callback, CallbackReference> directCallbackMap = new WeakHashMap<Callback, CallbackReference>();
/*  54 */   static final Map<Pointer, Reference<Callback>> pointerCallbackMap = new WeakHashMap<Pointer, Reference<Callback>>();
/*     */   
/*  56 */   static final Map<Object, Object> allocations = new WeakHashMap<Object, Object>();
/*     */ 
/*     */   
/*  59 */   private static final Map<CallbackReference, Reference<CallbackReference>> allocatedMemory = Collections.synchronizedMap(new WeakHashMap<CallbackReference, Reference<CallbackReference>>());
/*     */   private static final Method PROXY_CALLBACK_METHOD;
/*     */   
/*     */   static {
/*     */     try {
/*  64 */       PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", new Class[] { Object[].class });
/*  65 */     } catch (Exception e) {
/*  66 */       throw new Error("Error looking up CallbackProxy.callback() method");
/*     */     } 
/*     */   }
/*     */   
/*  70 */   private static final Map<Callback, CallbackThreadInitializer> initializers = new WeakHashMap<Callback, CallbackThreadInitializer>();
/*     */   Pointer cbstruct;
/*     */   Pointer trampoline;
/*     */   CallbackProxy proxy;
/*     */   Method method;
/*     */   int callingConvention;
/*     */   
/*     */   static CallbackThreadInitializer setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
/*  78 */     synchronized (initializers) {
/*  79 */       if (initializer != null) {
/*  80 */         return initializers.put(cb, initializer);
/*     */       }
/*  82 */       return initializers.remove(cb);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class AttachOptions
/*     */     extends Structure
/*     */   {
/*     */     AttachOptions() {
/*  94 */       setStringEncoding("utf8");
/*     */     }
/*     */     public static final List<String> FIELDS = createFieldsOrder(new String[] { "daemon", "detach", "name" }); public boolean daemon;
/*     */     
/*     */     protected List<String> getFieldOrder() {
/*  99 */       return FIELDS;
/*     */     }
/*     */     public boolean detach;
/*     */     public String name; }
/*     */   
/*     */   private static ThreadGroup initializeThread(Callback cb, AttachOptions args) {
/* 105 */     CallbackThreadInitializer init = null;
/* 106 */     if (cb instanceof DefaultCallbackProxy) {
/* 107 */       cb = ((DefaultCallbackProxy)cb).getCallback();
/*     */     }
/* 109 */     synchronized (initializers) {
/* 110 */       init = initializers.get(cb);
/*     */     } 
/* 112 */     ThreadGroup group = null;
/* 113 */     if (init != null) {
/* 114 */       group = init.getThreadGroup(cb);
/* 115 */       args.name = init.getName(cb);
/* 116 */       args.daemon = init.isDaemon(cb);
/* 117 */       args.detach = init.detach(cb);
/* 118 */       args.write();
/*     */     } 
/* 120 */     return group;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Callback getCallback(Class<?> type, Pointer p) {
/* 131 */     return getCallback(type, p, false);
/*     */   }
/*     */   
/*     */   private static Callback getCallback(Class<?> type, Pointer p, boolean direct) {
/* 135 */     if (p == null) {
/* 136 */       return null;
/*     */     }
/*     */     
/* 139 */     if (!type.isInterface())
/* 140 */       throw new IllegalArgumentException("Callback type must be an interface"); 
/* 141 */     Map<Callback, CallbackReference> map = direct ? directCallbackMap : callbackMap;
/* 142 */     synchronized (pointerCallbackMap) {
/* 143 */       Callback cb = null;
/* 144 */       Reference<Callback> ref = pointerCallbackMap.get(p);
/* 145 */       if (ref != null) {
/* 146 */         cb = ref.get();
/* 147 */         if (cb != null && !type.isAssignableFrom(cb.getClass())) {
/* 148 */           throw new IllegalStateException("Pointer " + p + " already mapped to " + cb + ".\nNative code may be re-using a default function pointer, in which case you may need to use a common Callback class wherever the function pointer is reused.");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 153 */         return cb;
/*     */       } 
/* 155 */       int ctype = AltCallingConvention.class.isAssignableFrom(type) ? 63 : 0;
/*     */       
/* 157 */       Map<String, Object> foptions = new HashMap<String, Object>(Native.getLibraryOptions(type));
/* 158 */       foptions.put("invoking-method", getCallbackMethod(type));
/* 159 */       NativeFunctionHandler h = new NativeFunctionHandler(p, ctype, foptions);
/* 160 */       cb = (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, h);
/*     */       
/* 162 */       map.remove(cb);
/* 163 */       pointerCallbackMap.put(p, new WeakReference<Callback>(cb));
/* 164 */       return cb;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CallbackReference(Callback callback, int callingConvention, boolean direct) {
/* 175 */     super(callback);
/* 176 */     TypeMapper mapper = Native.getTypeMapper(callback.getClass());
/* 177 */     this.callingConvention = callingConvention;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     boolean ppc = Platform.isPPC();
/* 184 */     if (direct) {
/* 185 */       Method m = getCallbackMethod(callback);
/* 186 */       Class<?>[] ptypes = m.getParameterTypes();
/* 187 */       for (int i = 0; i < ptypes.length; i++) {
/*     */         
/* 189 */         if (ppc && (ptypes[i] == float.class || ptypes[i] == double.class)) {
/*     */           
/* 191 */           direct = false;
/*     */           
/*     */           break;
/*     */         } 
/* 195 */         if (mapper != null && mapper
/* 196 */           .getFromNativeConverter(ptypes[i]) != null) {
/* 197 */           direct = false;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 202 */       if (mapper != null && mapper
/* 203 */         .getToNativeConverter(m.getReturnType()) != null) {
/* 204 */         direct = false;
/*     */       }
/*     */     } 
/*     */     
/* 208 */     String encoding = Native.getStringEncoding(callback.getClass());
/* 209 */     long peer = 0L;
/* 210 */     if (direct) {
/* 211 */       this.method = getCallbackMethod(callback);
/* 212 */       Class<?>[] nativeParamTypes = this.method.getParameterTypes();
/* 213 */       Class<?> returnType = this.method.getReturnType();
/* 214 */       int flags = 1;
/* 215 */       if (callback instanceof com.sun.jna.win32.DLLCallback) {
/* 216 */         flags |= 0x2;
/*     */       }
/* 218 */       peer = Native.createNativeCallback(callback, this.method, nativeParamTypes, returnType, callingConvention, flags, encoding);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 223 */       if (callback instanceof CallbackProxy) {
/* 224 */         this.proxy = (CallbackProxy)callback;
/*     */       } else {
/*     */         
/* 227 */         this.proxy = new DefaultCallbackProxy(getCallbackMethod(callback), mapper, encoding);
/*     */       } 
/* 229 */       Class<?>[] nativeParamTypes = this.proxy.getParameterTypes();
/* 230 */       Class<?> returnType = this.proxy.getReturnType();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 235 */       if (mapper != null) {
/* 236 */         for (int j = 0; j < nativeParamTypes.length; j++) {
/* 237 */           FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes[j]);
/* 238 */           if (rc != null) {
/* 239 */             nativeParamTypes[j] = rc.nativeType();
/*     */           }
/*     */         } 
/* 242 */         ToNativeConverter tn = mapper.getToNativeConverter(returnType);
/* 243 */         if (tn != null) {
/* 244 */           returnType = tn.nativeType();
/*     */         }
/*     */       } 
/* 247 */       for (int i = 0; i < nativeParamTypes.length; i++) {
/* 248 */         nativeParamTypes[i] = getNativeType(nativeParamTypes[i]);
/* 249 */         if (!isAllowableNativeType(nativeParamTypes[i])) {
/* 250 */           String msg = "Callback argument " + nativeParamTypes[i] + " requires custom type conversion";
/*     */           
/* 252 */           throw new IllegalArgumentException(msg);
/*     */         } 
/*     */       } 
/* 255 */       returnType = getNativeType(returnType);
/* 256 */       if (!isAllowableNativeType(returnType)) {
/* 257 */         String msg = "Callback return type " + returnType + " requires custom type conversion";
/*     */         
/* 259 */         throw new IllegalArgumentException(msg);
/*     */       } 
/* 261 */       int flags = (callback instanceof com.sun.jna.win32.DLLCallback) ? 2 : 0;
/*     */       
/* 263 */       peer = Native.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, nativeParamTypes, returnType, callingConvention, flags, encoding);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 268 */     this.cbstruct = (peer != 0L) ? new Pointer(peer) : null;
/* 269 */     allocatedMemory.put(this, new WeakReference<CallbackReference>(this));
/*     */   }
/*     */   
/*     */   private Class<?> getNativeType(Class<?> cls) {
/* 273 */     if (Structure.class.isAssignableFrom(cls))
/*     */     
/* 275 */     { Structure.validate((Class)cls);
/* 276 */       if (!Structure.ByValue.class.isAssignableFrom(cls))
/* 277 */         return Pointer.class;  }
/* 278 */     else { if (NativeMapped.class.isAssignableFrom(cls))
/* 279 */         return NativeMappedConverter.getInstance(cls).nativeType(); 
/* 280 */       if (cls == String.class || cls == WString.class || cls == String[].class || cls == WString[].class || Callback.class
/*     */ 
/*     */ 
/*     */         
/* 284 */         .isAssignableFrom(cls))
/* 285 */         return Pointer.class;  }
/*     */     
/* 287 */     return cls;
/*     */   }
/*     */   
/*     */   private static Method checkMethod(Method m) {
/* 291 */     if ((m.getParameterTypes()).length > 256) {
/* 292 */       String msg = "Method signature exceeds the maximum parameter count: " + m;
/*     */       
/* 294 */       throw new UnsupportedOperationException(msg);
/*     */     } 
/* 296 */     return m;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Class<?> findCallbackClass(Class<?> type) {
/* 305 */     if (!Callback.class.isAssignableFrom(type)) {
/* 306 */       throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
/*     */     }
/* 308 */     if (type.isInterface()) {
/* 309 */       return type;
/*     */     }
/* 311 */     Class<?>[] ifaces = type.getInterfaces();
/* 312 */     for (int i = 0; i < ifaces.length; i++) {
/* 313 */       if (Callback.class.isAssignableFrom(ifaces[i])) {
/*     */         
/*     */         try {
/* 316 */           getCallbackMethod(ifaces[i]);
/* 317 */           return ifaces[i];
/*     */         }
/* 319 */         catch (IllegalArgumentException e) {
/*     */           break;
/*     */         } 
/*     */       }
/*     */     } 
/* 324 */     if (Callback.class.isAssignableFrom(type.getSuperclass())) {
/* 325 */       return findCallbackClass(type.getSuperclass());
/*     */     }
/* 327 */     return type;
/*     */   }
/*     */   
/*     */   private static Method getCallbackMethod(Callback callback) {
/* 331 */     return getCallbackMethod(findCallbackClass(callback.getClass()));
/*     */   }
/*     */ 
/*     */   
/*     */   private static Method getCallbackMethod(Class<?> cls) {
/* 336 */     Method[] pubMethods = cls.getDeclaredMethods();
/* 337 */     Method[] classMethods = cls.getMethods();
/* 338 */     Set<Method> pmethods = new HashSet<Method>(Arrays.asList(pubMethods));
/* 339 */     pmethods.retainAll(Arrays.asList((Object[])classMethods));
/*     */ 
/*     */     
/* 342 */     for (Iterator<Method> i = pmethods.iterator(); i.hasNext(); ) {
/* 343 */       Method m = i.next();
/* 344 */       if (Callback.FORBIDDEN_NAMES.contains(m.getName())) {
/* 345 */         i.remove();
/*     */       }
/*     */     } 
/*     */     
/* 349 */     Method[] methods = pmethods.<Method>toArray(new Method[0]);
/* 350 */     if (methods.length == 1) {
/* 351 */       return checkMethod(methods[0]);
/*     */     }
/* 353 */     for (int j = 0; j < methods.length; j++) {
/* 354 */       Method m = methods[j];
/* 355 */       if ("callback".equals(m.getName())) {
/* 356 */         return checkMethod(m);
/*     */       }
/*     */     } 
/* 359 */     String msg = "Callback must implement a single public method, or one public method named 'callback'";
/*     */     
/* 361 */     throw new IllegalArgumentException(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   private void setCallbackOptions(int options) {
/* 366 */     this.cbstruct.setInt(Native.POINTER_SIZE, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public Pointer getTrampoline() {
/* 371 */     if (this.trampoline == null) {
/* 372 */       this.trampoline = this.cbstruct.getPointer(0L);
/*     */     }
/* 374 */     return this.trampoline;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() {
/* 380 */     dispose();
/*     */   }
/*     */ 
/*     */   
/*     */   protected synchronized void dispose() {
/* 385 */     if (this.cbstruct != null) {
/*     */       try {
/* 387 */         Native.freeNativeCallback(this.cbstruct.peer);
/*     */       } finally {
/* 389 */         this.cbstruct.peer = 0L;
/* 390 */         this.cbstruct = null;
/* 391 */         allocatedMemory.remove(this);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void disposeAll() {
/* 399 */     Collection<CallbackReference> refs = new LinkedList<CallbackReference>(allocatedMemory.keySet());
/* 400 */     for (CallbackReference r : refs) {
/* 401 */       r.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   private Callback getCallback() {
/* 406 */     return get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Pointer getNativeFunctionPointer(Callback cb) {
/* 413 */     if (Proxy.isProxyClass(cb.getClass())) {
/* 414 */       Object handler = Proxy.getInvocationHandler(cb);
/* 415 */       if (handler instanceof NativeFunctionHandler) {
/* 416 */         return ((NativeFunctionHandler)handler).getPointer();
/*     */       }
/*     */     } 
/* 419 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pointer getFunctionPointer(Callback cb) {
/* 426 */     return getFunctionPointer(cb, false);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Pointer getFunctionPointer(Callback cb, boolean direct) {
/* 431 */     Pointer fp = null;
/* 432 */     if (cb == null) {
/* 433 */       return null;
/*     */     }
/* 435 */     if ((fp = getNativeFunctionPointer(cb)) != null) {
/* 436 */       return fp;
/*     */     }
/* 438 */     Map<String, ?> options = Native.getLibraryOptions(cb.getClass());
/*     */ 
/*     */ 
/*     */     
/* 442 */     int callingConvention = (cb instanceof AltCallingConvention) ? 63 : ((options != null && options.containsKey("calling-convention")) ? ((Integer)options.get("calling-convention")).intValue() : 0);
/*     */ 
/*     */     
/* 445 */     Map<Callback, CallbackReference> map = direct ? directCallbackMap : callbackMap;
/* 446 */     synchronized (pointerCallbackMap) {
/* 447 */       CallbackReference cbref = map.get(cb);
/* 448 */       if (cbref == null) {
/* 449 */         cbref = new CallbackReference(cb, callingConvention, direct);
/* 450 */         map.put(cb, cbref);
/* 451 */         pointerCallbackMap.put(cbref.getTrampoline(), new WeakReference<Callback>(cb));
/* 452 */         if (initializers.containsKey(cb)) {
/* 453 */           cbref.setCallbackOptions(1);
/*     */         }
/*     */       } 
/* 456 */       return cbref.getTrampoline();
/*     */     } 
/*     */   }
/*     */   
/*     */   private class DefaultCallbackProxy implements CallbackProxy { private final Method callbackMethod;
/*     */     private ToNativeConverter toNative;
/*     */     private final FromNativeConverter[] fromNative;
/*     */     private final String encoding;
/*     */     
/*     */     public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper, String encoding) {
/* 466 */       this.callbackMethod = callbackMethod;
/* 467 */       this.encoding = encoding;
/* 468 */       Class<?>[] argTypes = callbackMethod.getParameterTypes();
/* 469 */       Class<?> returnType = callbackMethod.getReturnType();
/* 470 */       this.fromNative = new FromNativeConverter[argTypes.length];
/* 471 */       if (NativeMapped.class.isAssignableFrom(returnType)) {
/* 472 */         this.toNative = NativeMappedConverter.getInstance(returnType);
/*     */       }
/* 474 */       else if (mapper != null) {
/* 475 */         this.toNative = mapper.getToNativeConverter(returnType);
/*     */       } 
/* 477 */       for (int i = 0; i < this.fromNative.length; i++) {
/* 478 */         if (NativeMapped.class.isAssignableFrom(argTypes[i])) {
/* 479 */           this.fromNative[i] = new NativeMappedConverter(argTypes[i]);
/*     */         }
/* 481 */         else if (mapper != null) {
/* 482 */           this.fromNative[i] = mapper.getFromNativeConverter(argTypes[i]);
/*     */         } 
/*     */       } 
/* 485 */       if (!callbackMethod.isAccessible()) {
/*     */         try {
/* 487 */           callbackMethod.setAccessible(true);
/*     */         }
/* 489 */         catch (SecurityException e) {
/* 490 */           throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     public Callback getCallback() {
/* 496 */       return CallbackReference.this.getCallback();
/*     */     }
/*     */     
/*     */     private Object invokeCallback(Object[] args) {
/* 500 */       Class<?>[] paramTypes = this.callbackMethod.getParameterTypes();
/* 501 */       Object[] callbackArgs = new Object[args.length];
/*     */ 
/*     */       
/* 504 */       for (int i = 0; i < args.length; i++) {
/* 505 */         Class<?> type = paramTypes[i];
/* 506 */         Object arg = args[i];
/* 507 */         if (this.fromNative[i] != null) {
/* 508 */           FromNativeContext context = new CallbackParameterContext(type, this.callbackMethod, args, i);
/*     */           
/* 510 */           callbackArgs[i] = this.fromNative[i].fromNative(arg, context);
/*     */         } else {
/* 512 */           callbackArgs[i] = convertArgument(arg, type);
/*     */         } 
/*     */       } 
/*     */       
/* 516 */       Object result = null;
/* 517 */       Callback cb = getCallback();
/* 518 */       if (cb != null) {
/*     */         try {
/* 520 */           result = convertResult(this.callbackMethod.invoke(cb, callbackArgs));
/*     */         }
/* 522 */         catch (IllegalArgumentException e) {
/* 523 */           Native.getCallbackExceptionHandler().uncaughtException(cb, e);
/*     */         }
/* 525 */         catch (IllegalAccessException e) {
/* 526 */           Native.getCallbackExceptionHandler().uncaughtException(cb, e);
/*     */         }
/* 528 */         catch (InvocationTargetException e) {
/* 529 */           Native.getCallbackExceptionHandler().uncaughtException(cb, e.getTargetException());
/*     */         } 
/*     */       }
/*     */       
/* 533 */       for (int j = 0; j < callbackArgs.length; j++) {
/* 534 */         if (callbackArgs[j] instanceof Structure && !(callbackArgs[j] instanceof Structure.ByValue))
/*     */         {
/* 536 */           ((Structure)callbackArgs[j]).autoWrite();
/*     */         }
/*     */       } 
/*     */       
/* 540 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object callback(Object[] args) {
/*     */       try {
/* 551 */         return invokeCallback(args);
/*     */       }
/* 553 */       catch (Throwable t) {
/* 554 */         Native.getCallbackExceptionHandler().uncaughtException(getCallback(), t);
/* 555 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Object convertArgument(Object value, Class<?> dstType) {
/* 563 */       if (value instanceof Pointer) {
/* 564 */         if (dstType == String.class) {
/* 565 */           value = ((Pointer)value).getString(0L, this.encoding);
/*     */         }
/* 567 */         else if (dstType == WString.class) {
/* 568 */           value = new WString(((Pointer)value).getWideString(0L));
/*     */         }
/* 570 */         else if (dstType == String[].class) {
/* 571 */           value = ((Pointer)value).getStringArray(0L, this.encoding);
/*     */         }
/* 573 */         else if (dstType == WString[].class) {
/* 574 */           value = ((Pointer)value).getWideStringArray(0L);
/*     */         }
/* 576 */         else if (Callback.class.isAssignableFrom(dstType)) {
/* 577 */           value = CallbackReference.getCallback(dstType, (Pointer)value);
/*     */         }
/* 579 */         else if (Structure.class.isAssignableFrom(dstType)) {
/*     */ 
/*     */           
/* 582 */           if (Structure.ByValue.class.isAssignableFrom(dstType)) {
/* 583 */             Structure s = (Structure)Structure.newInstance(dstType);
/* 584 */             byte[] buf = new byte[s.size()];
/* 585 */             ((Pointer)value).read(0L, buf, 0, buf.length);
/* 586 */             s.getPointer().write(0L, buf, 0, buf.length);
/* 587 */             s.read();
/* 588 */             value = s;
/*     */           } else {
/* 590 */             Structure s = (Structure)Structure.newInstance(dstType, (Pointer)value);
/* 591 */             s.conditionalAutoRead();
/* 592 */             value = s;
/*     */           }
/*     */         
/*     */         } 
/* 596 */       } else if ((boolean.class == dstType || Boolean.class == dstType) && value instanceof Number) {
/*     */         
/* 598 */         value = Function.valueOf((((Number)value).intValue() != 0));
/*     */       } 
/* 600 */       return value;
/*     */     }
/*     */     
/*     */     private Object convertResult(Object value) {
/* 604 */       if (this.toNative != null) {
/* 605 */         value = this.toNative.toNative(value, new CallbackResultContext(this.callbackMethod));
/*     */       }
/* 607 */       if (value == null) {
/* 608 */         return null;
/*     */       }
/*     */       
/* 611 */       Class<?> cls = value.getClass();
/* 612 */       if (Structure.class.isAssignableFrom(cls)) {
/* 613 */         if (Structure.ByValue.class.isAssignableFrom(cls)) {
/* 614 */           return value;
/*     */         }
/* 616 */         return ((Structure)value).getPointer();
/* 617 */       }  if (cls == boolean.class || cls == Boolean.class) {
/* 618 */         return Boolean.TRUE.equals(value) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
/*     */       }
/* 620 */       if (cls == String.class || cls == WString.class)
/* 621 */         return CallbackReference.getNativeString(value, (cls == WString.class)); 
/* 622 */       if (cls == String[].class || cls == WString.class) {
/* 623 */         StringArray sa = (cls == String[].class) ? new StringArray((String[])value, this.encoding) : new StringArray((WString[])value);
/*     */ 
/*     */ 
/*     */         
/* 627 */         CallbackReference.allocations.put(value, sa);
/* 628 */         return sa;
/* 629 */       }  if (Callback.class.isAssignableFrom(cls)) {
/* 630 */         return CallbackReference.getFunctionPointer((Callback)value);
/*     */       }
/* 632 */       return value;
/*     */     }
/*     */     
/*     */     public Class<?>[] getParameterTypes() {
/* 636 */       return this.callbackMethod.getParameterTypes();
/*     */     }
/*     */     
/*     */     public Class<?> getReturnType() {
/* 640 */       return this.callbackMethod.getReturnType();
/*     */     } }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class NativeFunctionHandler
/*     */     implements InvocationHandler
/*     */   {
/*     */     private final Function function;
/*     */     
/*     */     private final Map<String, ?> options;
/*     */     
/*     */     public NativeFunctionHandler(Pointer address, int callingConvention, Map<String, ?> options) {
/* 653 */       this.options = options;
/* 654 */       this.function = new Function(address, callingConvention, (String)options.get("string-encoding"));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 660 */       if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
/* 661 */         String str = "Proxy interface to " + this.function;
/* 662 */         Method m = (Method)this.options.get("invoking-method");
/* 663 */         Class<?> cls = CallbackReference.findCallbackClass(m.getDeclaringClass());
/* 664 */         str = str + " (" + cls.getName() + ")";
/*     */         
/* 666 */         return str;
/* 667 */       }  if (Library.Handler.OBJECT_HASHCODE.equals(method))
/* 668 */         return Integer.valueOf(hashCode()); 
/* 669 */       if (Library.Handler.OBJECT_EQUALS.equals(method)) {
/* 670 */         Object o = args[0];
/* 671 */         if (o != null && Proxy.isProxyClass(o.getClass())) {
/* 672 */           return Function.valueOf((Proxy.getInvocationHandler(o) == this));
/*     */         }
/* 674 */         return Boolean.FALSE;
/*     */       } 
/* 676 */       if (Function.isVarArgs(method)) {
/* 677 */         args = Function.concatenateVarArgs(args);
/*     */       }
/* 679 */       return this.function.invoke(method.getReturnType(), args, this.options);
/*     */     }
/*     */     
/*     */     public Pointer getPointer() {
/* 683 */       return this.function;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isAllowableNativeType(Class<?> cls) {
/* 691 */     return (cls == void.class || cls == Void.class || cls == boolean.class || cls == Boolean.class || cls == byte.class || cls == Byte.class || cls == short.class || cls == Short.class || cls == char.class || cls == Character.class || cls == int.class || cls == Integer.class || cls == long.class || cls == Long.class || cls == float.class || cls == Float.class || cls == double.class || cls == Double.class || (Structure.ByValue.class
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 700 */       .isAssignableFrom(cls) && Structure.class
/* 701 */       .isAssignableFrom(cls)) || Pointer.class
/* 702 */       .isAssignableFrom(cls));
/*     */   }
/*     */   
/*     */   private static Pointer getNativeString(Object value, boolean wide) {
/* 706 */     if (value != null) {
/* 707 */       NativeString ns = new NativeString(value.toString(), wide);
/*     */       
/* 709 */       allocations.put(value, ns);
/* 710 */       return ns.getPointer();
/*     */     } 
/* 712 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\CallbackReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */