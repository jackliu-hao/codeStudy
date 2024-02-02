package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.DispatchListener;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallbackProxy implements IDispatchCallback {
   private static boolean DEFAULT_BOOLEAN;
   private static byte DEFAULT_BYTE;
   private static short DEFAULT_SHORT;
   private static int DEFAULT_INT;
   private static long DEFAULT_LONG;
   private static float DEFAULT_FLOAT;
   private static double DEFAULT_DOUBLE;
   ObjectFactory factory;
   Class<?> comEventCallbackInterface;
   IComEventCallbackListener comEventCallbackListener;
   Guid.REFIID listenedToRiid;
   public DispatchListener dispatchListener;
   Map<OaIdl.DISPID, Method> dsipIdMap;

   public CallbackProxy(ObjectFactory factory, Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
      this.factory = factory;
      this.comEventCallbackInterface = comEventCallbackInterface;
      this.comEventCallbackListener = comEventCallbackListener;
      this.listenedToRiid = this.createRIID(comEventCallbackInterface);
      this.dsipIdMap = this.createDispIdMap(comEventCallbackInterface);
      this.dispatchListener = new DispatchListener(this);
   }

   Guid.REFIID createRIID(Class<?> comEventCallbackInterface) {
      ComInterface comInterfaceAnnotation = (ComInterface)comEventCallbackInterface.getAnnotation(ComInterface.class);
      if (null == comInterfaceAnnotation) {
         throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation");
      } else {
         String iidStr = comInterfaceAnnotation.iid();
         if (null != iidStr && !iidStr.isEmpty()) {
            return new Guid.REFIID((new Guid.IID(iidStr)).getPointer());
         } else {
            throw new COMException("ComInterface must define a value for iid");
         }
      }
   }

   Map<OaIdl.DISPID, Method> createDispIdMap(Class<?> comEventCallbackInterface) {
      Map<OaIdl.DISPID, Method> map = new HashMap();
      Method[] var3 = comEventCallbackInterface.getMethods();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method meth = var3[var5];
         ComEventCallback callbackAnnotation = (ComEventCallback)meth.getAnnotation(ComEventCallback.class);
         ComMethod methodAnnotation = (ComMethod)meth.getAnnotation(ComMethod.class);
         int dispId;
         if (methodAnnotation != null) {
            dispId = methodAnnotation.dispId();
            if (-1 == dispId) {
               dispId = this.fetchDispIdFromName(callbackAnnotation);
            }

            if (dispId == -1) {
               this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + meth.getName() + " not found", (Exception)null);
            }

            map.put(new OaIdl.DISPID(dispId), meth);
         } else if (null != callbackAnnotation) {
            dispId = callbackAnnotation.dispid();
            if (-1 == dispId) {
               dispId = this.fetchDispIdFromName(callbackAnnotation);
            }

            if (dispId == -1) {
               this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + meth.getName() + " not found", (Exception)null);
            }

            map.put(new OaIdl.DISPID(dispId), meth);
         }
      }

      return map;
   }

   int fetchDispIdFromName(ComEventCallback annotation) {
      return -1;
   }

   void invokeOnThread(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams) {
      Variant.VARIANT[] arguments = pDispParams.getArgs();
      Method eventMethod = (Method)this.dsipIdMap.get(dispIdMember);
      if (eventMethod == null) {
         this.comEventCallbackListener.errorReceivingCallbackEvent("No method found with dispId = " + dispIdMember, (Exception)null);
      } else {
         OaIdl.DISPID[] positionMap = pDispParams.getRgdispidNamedArgs();
         Class<?>[] paramTypes = eventMethod.getParameterTypes();
         Object[] params = new Object[paramTypes.length];

         int i;
         for(i = 0; i < params.length && arguments.length - positionMap.length - i > 0; ++i) {
            Class targetClass = paramTypes[i];
            Variant.VARIANT varg = arguments[arguments.length - i - 1];
            params[i] = Convert.toJavaObject(varg, targetClass, this.factory, true, false);
         }

         for(i = 0; i < positionMap.length; ++i) {
            int targetPosition = positionMap[i].intValue();
            if (targetPosition < params.length) {
               Class targetClass = paramTypes[targetPosition];
               Variant.VARIANT varg = arguments[i];
               params[targetPosition] = Convert.toJavaObject(varg, targetClass, this.factory, true, false);
            }
         }

         for(i = 0; i < params.length; ++i) {
            if (params[i] == null && paramTypes[i].isPrimitive()) {
               if (paramTypes[i].equals(Boolean.TYPE)) {
                  params[i] = DEFAULT_BOOLEAN;
               } else if (paramTypes[i].equals(Byte.TYPE)) {
                  params[i] = DEFAULT_BYTE;
               } else if (paramTypes[i].equals(Short.TYPE)) {
                  params[i] = DEFAULT_SHORT;
               } else if (paramTypes[i].equals(Integer.TYPE)) {
                  params[i] = DEFAULT_INT;
               } else if (paramTypes[i].equals(Long.TYPE)) {
                  params[i] = DEFAULT_LONG;
               } else if (paramTypes[i].equals(Float.TYPE)) {
                  params[i] = DEFAULT_FLOAT;
               } else {
                  if (!paramTypes[i].equals(Double.TYPE)) {
                     throw new IllegalArgumentException("Class type " + paramTypes[i].getName() + " not mapped to primitive default value.");
                  }

                  params[i] = DEFAULT_DOUBLE;
               }
            }
         }

         try {
            eventMethod.invoke(this.comEventCallbackListener, params);
         } catch (Exception var17) {
            List<String> decodedClassNames = new ArrayList(params.length);
            Object[] var21 = params;
            int var22 = params.length;

            for(int var15 = 0; var15 < var22; ++var15) {
               Object o = var21[var15];
               if (o == null) {
                  decodedClassNames.add("NULL");
               } else {
                  decodedClassNames.add(o.getClass().getName());
               }
            }

            this.comEventCallbackListener.errorReceivingCallbackEvent("Exception invoking method " + eventMethod + " supplied: " + decodedClassNames.toString(), var17);
         }

      }
   }

   public Pointer getPointer() {
      return this.dispatchListener.getPointer();
   }

   public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
      return new WinNT.HRESULT(-2147467263);
   }

   public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
      return new WinNT.HRESULT(-2147467263);
   }

   public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
      return new WinNT.HRESULT(-2147467263);
   }

   public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
      assert COMUtils.comIsInitialized() : "Assumption about COM threading broken.";

      this.invokeOnThread(dispIdMember, riid, lcid, wFlags, pDispParams);
      return WinError.S_OK;
   }

   public WinNT.HRESULT QueryInterface(Guid.REFIID refid, PointerByReference ppvObject) {
      if (null == ppvObject) {
         return new WinNT.HRESULT(-2147467261);
      } else if (refid.equals(this.listenedToRiid)) {
         ppvObject.setValue(this.getPointer());
         return WinError.S_OK;
      } else if (refid.getValue().equals(Unknown.IID_IUNKNOWN)) {
         ppvObject.setValue(this.getPointer());
         return WinError.S_OK;
      } else if (refid.getValue().equals(Dispatch.IID_IDISPATCH)) {
         ppvObject.setValue(this.getPointer());
         return WinError.S_OK;
      } else {
         return new WinNT.HRESULT(-2147467262);
      }
   }

   public int AddRef() {
      return 0;
   }

   public int Release() {
      return 0;
   }
}
