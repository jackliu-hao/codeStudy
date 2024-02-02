/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.platform.win32.COM.COMException;
/*     */ import com.sun.jna.platform.win32.COM.COMUtils;
/*     */ import com.sun.jna.platform.win32.COM.Dispatch;
/*     */ import com.sun.jna.platform.win32.COM.DispatchListener;
/*     */ import com.sun.jna.platform.win32.COM.IDispatchCallback;
/*     */ import com.sun.jna.platform.win32.COM.Unknown;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
/*     */ import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinError;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class CallbackProxy
/*     */   implements IDispatchCallback
/*     */ {
/*     */   private static boolean DEFAULT_BOOLEAN;
/*     */   private static byte DEFAULT_BYTE;
/*     */   private static short DEFAULT_SHORT;
/*     */   private static int DEFAULT_INT;
/*     */   private static long DEFAULT_LONG;
/*     */   private static float DEFAULT_FLOAT;
/*     */   private static double DEFAULT_DOUBLE;
/*     */   ObjectFactory factory;
/*     */   Class<?> comEventCallbackInterface;
/*     */   IComEventCallbackListener comEventCallbackListener;
/*     */   Guid.REFIID listenedToRiid;
/*     */   public DispatchListener dispatchListener;
/*     */   Map<OaIdl.DISPID, Method> dsipIdMap;
/*     */   
/*     */   public CallbackProxy(ObjectFactory factory, Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
/*  73 */     this.factory = factory;
/*  74 */     this.comEventCallbackInterface = comEventCallbackInterface;
/*  75 */     this.comEventCallbackListener = comEventCallbackListener;
/*  76 */     this.listenedToRiid = createRIID(comEventCallbackInterface);
/*  77 */     this.dsipIdMap = createDispIdMap(comEventCallbackInterface);
/*  78 */     this.dispatchListener = new DispatchListener(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Guid.REFIID createRIID(Class<?> comEventCallbackInterface) {
/*  89 */     ComInterface comInterfaceAnnotation = comEventCallbackInterface.<ComInterface>getAnnotation(ComInterface.class);
/*  90 */     if (null == comInterfaceAnnotation) {
/*  91 */       throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation");
/*     */     }
/*     */     
/*  94 */     String iidStr = comInterfaceAnnotation.iid();
/*  95 */     if (null == iidStr || iidStr.isEmpty()) {
/*  96 */       throw new COMException("ComInterface must define a value for iid");
/*     */     }
/*  98 */     return new Guid.REFIID((new Guid.IID(iidStr)).getPointer());
/*     */   }
/*     */   
/*     */   Map<OaIdl.DISPID, Method> createDispIdMap(Class<?> comEventCallbackInterface) {
/* 102 */     Map<OaIdl.DISPID, Method> map = new HashMap<OaIdl.DISPID, Method>();
/*     */     
/* 104 */     for (Method meth : comEventCallbackInterface.getMethods()) {
/* 105 */       ComEventCallback callbackAnnotation = meth.<ComEventCallback>getAnnotation(ComEventCallback.class);
/* 106 */       ComMethod methodAnnotation = meth.<ComMethod>getAnnotation(ComMethod.class);
/* 107 */       if (methodAnnotation != null) {
/* 108 */         int dispId = methodAnnotation.dispId();
/* 109 */         if (-1 == dispId) {
/* 110 */           dispId = fetchDispIdFromName(callbackAnnotation);
/*     */         }
/* 112 */         if (dispId == -1) {
/* 113 */           this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + meth
/* 114 */               .getName() + " not found", null);
/*     */         }
/*     */         
/* 117 */         map.put(new OaIdl.DISPID(dispId), meth);
/* 118 */       } else if (null != callbackAnnotation) {
/* 119 */         int dispId = callbackAnnotation.dispid();
/* 120 */         if (-1 == dispId) {
/* 121 */           dispId = fetchDispIdFromName(callbackAnnotation);
/*     */         }
/* 123 */         if (dispId == -1) {
/* 124 */           this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + meth
/* 125 */               .getName() + " not found", null);
/*     */         }
/*     */         
/* 128 */         map.put(new OaIdl.DISPID(dispId), meth);
/*     */       } 
/*     */     } 
/*     */     
/* 132 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   int fetchDispIdFromName(ComEventCallback annotation) {
/* 137 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void invokeOnThread(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams) {
/* 143 */     Variant.VARIANT[] arguments = pDispParams.getArgs();
/*     */     
/* 145 */     Method eventMethod = this.dsipIdMap.get(dispIdMember);
/* 146 */     if (eventMethod == null) {
/* 147 */       this.comEventCallbackListener.errorReceivingCallbackEvent("No method found with dispId = " + dispIdMember, null);
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
/*     */       return;
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
/* 185 */     OaIdl.DISPID[] positionMap = pDispParams.getRgdispidNamedArgs();
/*     */     
/* 187 */     Class<?>[] paramTypes = eventMethod.getParameterTypes();
/* 188 */     Object[] params = new Object[paramTypes.length];
/*     */     
/*     */     int i;
/* 191 */     for (i = 0; i < params.length && arguments.length - positionMap.length - i > 0; i++) {
/* 192 */       Class<?> targetClass = paramTypes[i];
/* 193 */       Variant.VARIANT varg = arguments[arguments.length - i - 1];
/* 194 */       params[i] = Convert.toJavaObject(varg, targetClass, this.factory, true, false);
/*     */     } 
/*     */     
/* 197 */     for (i = 0; i < positionMap.length; i++) {
/* 198 */       int targetPosition = positionMap[i].intValue();
/* 199 */       if (targetPosition < params.length) {
/*     */ 
/*     */ 
/*     */         
/* 203 */         Class<?> targetClass = paramTypes[targetPosition];
/* 204 */         Variant.VARIANT varg = arguments[i];
/* 205 */         params[targetPosition] = Convert.toJavaObject(varg, targetClass, this.factory, true, false);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 211 */     for (i = 0; i < params.length; i++) {
/* 212 */       if (params[i] == null && paramTypes[i].isPrimitive()) {
/* 213 */         if (paramTypes[i].equals(boolean.class)) {
/* 214 */           params[i] = Boolean.valueOf(DEFAULT_BOOLEAN);
/* 215 */         } else if (paramTypes[i].equals(byte.class)) {
/* 216 */           params[i] = Byte.valueOf(DEFAULT_BYTE);
/* 217 */         } else if (paramTypes[i].equals(short.class)) {
/* 218 */           params[i] = Short.valueOf(DEFAULT_SHORT);
/* 219 */         } else if (paramTypes[i].equals(int.class)) {
/* 220 */           params[i] = Integer.valueOf(DEFAULT_INT);
/* 221 */         } else if (paramTypes[i].equals(long.class)) {
/* 222 */           params[i] = Long.valueOf(DEFAULT_LONG);
/* 223 */         } else if (paramTypes[i].equals(float.class)) {
/* 224 */           params[i] = Float.valueOf(DEFAULT_FLOAT);
/* 225 */         } else if (paramTypes[i].equals(double.class)) {
/* 226 */           params[i] = Double.valueOf(DEFAULT_DOUBLE);
/*     */         } else {
/* 228 */           throw new IllegalArgumentException("Class type " + paramTypes[i].getName() + " not mapped to primitive default value.");
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/*     */     try {
/* 234 */       eventMethod.invoke(this.comEventCallbackListener, params);
/* 235 */     } catch (Exception e) {
/* 236 */       List<String> decodedClassNames = new ArrayList<String>(params.length);
/* 237 */       for (Object o : params) {
/* 238 */         if (o == null) {
/* 239 */           decodedClassNames.add("NULL");
/*     */         } else {
/* 241 */           decodedClassNames.add(o.getClass().getName());
/*     */         } 
/*     */       } 
/* 244 */       this.comEventCallbackListener.errorReceivingCallbackEvent("Exception invoking method " + eventMethod + " supplied: " + decodedClassNames
/* 245 */           .toString(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Pointer getPointer() {
/* 251 */     return this.dispatchListener.getPointer();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
/* 257 */     return new WinNT.HRESULT(-2147467263);
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
/* 262 */     return new WinNT.HRESULT(-2147467263);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
/* 268 */     return new WinNT.HRESULT(-2147467263);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
/* 276 */     assert COMUtils.comIsInitialized() : "Assumption about COM threading broken.";
/*     */     
/* 278 */     invokeOnThread(dispIdMember, riid, lcid, wFlags, pDispParams);
/*     */     
/* 280 */     return WinError.S_OK;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT QueryInterface(Guid.REFIID refid, PointerByReference ppvObject) {
/* 286 */     if (null == ppvObject)
/* 287 */       return new WinNT.HRESULT(-2147467261); 
/* 288 */     if (refid.equals(this.listenedToRiid)) {
/* 289 */       ppvObject.setValue(getPointer());
/* 290 */       return WinError.S_OK;
/* 291 */     }  if (refid.getValue().equals(Unknown.IID_IUNKNOWN)) {
/* 292 */       ppvObject.setValue(getPointer());
/* 293 */       return WinError.S_OK;
/* 294 */     }  if (refid.getValue().equals(Dispatch.IID_IDISPATCH)) {
/* 295 */       ppvObject.setValue(getPointer());
/* 296 */       return WinError.S_OK;
/*     */     } 
/*     */     
/* 299 */     return new WinNT.HRESULT(-2147467262);
/*     */   }
/*     */   
/*     */   public int AddRef() {
/* 303 */     return 0;
/*     */   }
/*     */   
/*     */   public int Release() {
/* 307 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\CallbackProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */