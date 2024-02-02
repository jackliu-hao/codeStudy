package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.internal.ReflectionUtils;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ConnectionPoint;
import com.sun.jna.platform.win32.COM.ConnectionPointContainer;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyObject implements InvocationHandler, IDispatch, IRawDispatchHandle, IConnectionPoint {
   private long unknownId = -1L;
   private final Class<?> theInterface;
   private final ObjectFactory factory;
   private final com.sun.jna.platform.win32.COM.IDispatch rawDispatch;

   public ProxyObject(Class<?> theInterface, com.sun.jna.platform.win32.COM.IDispatch rawDispatch, ObjectFactory factory) {
      this.rawDispatch = rawDispatch;
      this.theInterface = theInterface;
      this.factory = factory;
      int n = this.rawDispatch.AddRef();
      this.getUnknownId();
      factory.register(this);
   }

   private long getUnknownId() {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      if (-1L == this.unknownId) {
         try {
            PointerByReference ppvObject = new PointerByReference();
            Thread current = Thread.currentThread();
            String tn = current.getName();
            Guid.IID iid = com.sun.jna.platform.win32.COM.IUnknown.IID_IUNKNOWN;
            WinNT.HRESULT hr = this.getRawDispatch().QueryInterface(new Guid.REFIID(iid), ppvObject);
            if (!WinNT.S_OK.equals(hr)) {
               String formatMessageFromHR = Kernel32Util.formatMessage(hr);
               throw new COMException("getUnknownId: " + formatMessageFromHR, hr);
            }

            Dispatch dispatch = new Dispatch(ppvObject.getValue());
            this.unknownId = Pointer.nativeValue(dispatch.getPointer());
            int var7 = dispatch.Release();
         } catch (RuntimeException var8) {
            if (var8 instanceof COMException) {
               throw var8;
            }

            throw new COMException("Error occured when trying get Unknown Id ", var8);
         }
      }

      return this.unknownId;
   }

   protected void finalize() throws Throwable {
      this.dispose();
      super.finalize();
   }

   public synchronized void dispose() {
      if (((Dispatch)this.rawDispatch).getPointer() != Pointer.NULL) {
         this.rawDispatch.Release();
         ((Dispatch)this.rawDispatch).setPointer(Pointer.NULL);
         this.factory.unregister(this);
      }

   }

   public com.sun.jna.platform.win32.COM.IDispatch getRawDispatch() {
      return this.rawDispatch;
   }

   public boolean equals(Object arg) {
      if (null == arg) {
         return false;
      } else if (arg instanceof ProxyObject) {
         ProxyObject other = (ProxyObject)arg;
         return this.getUnknownId() == other.getUnknownId();
      } else if (Proxy.isProxyClass(arg.getClass())) {
         InvocationHandler handler = Proxy.getInvocationHandler(arg);
         if (handler instanceof ProxyObject) {
            try {
               ProxyObject other = (ProxyObject)handler;
               return this.getUnknownId() == other.getUnknownId();
            } catch (Exception var4) {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      long id = this.getUnknownId();
      return (int)(id >>> 32 & -1L) + (int)(id & -1L);
   }

   public String toString() {
      return this.theInterface.getName() + "{unk=" + this.hashCode() + "}";
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      boolean declaredAsInterface = method.getAnnotation(ComMethod.class) != null || method.getAnnotation(ComProperty.class) != null;
      if (!declaredAsInterface && (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().equals(IRawDispatchHandle.class) || method.getDeclaringClass().equals(IUnknown.class) || method.getDeclaringClass().equals(IDispatch.class) || method.getDeclaringClass().equals(IConnectionPoint.class))) {
         try {
            return method.invoke(this, args);
         } catch (InvocationTargetException var12) {
            throw var12.getCause();
         }
      } else if (!declaredAsInterface && ReflectionUtils.isDefault(method)) {
         Object methodHandle = ReflectionUtils.getMethodHandle(method);
         return ReflectionUtils.invokeDefaultMethod(proxy, methodHandle, args);
      } else {
         Class<?> returnType = method.getReturnType();
         boolean isVoid = Void.TYPE.equals(returnType);
         ComProperty prop = (ComProperty)method.getAnnotation(ComProperty.class);
         Object[] fullLengthArgs;
         if (null != prop) {
            int dispId = prop.dispId();
            fullLengthArgs = this.unfoldWhenVarargs(method, args);
            String propName;
            if (isVoid) {
               if (dispId != -1) {
                  this.setProperty(new OaIdl.DISPID(dispId), fullLengthArgs);
                  return null;
               } else {
                  propName = this.getMutatorName(method, prop);
                  this.setProperty(propName, fullLengthArgs);
                  return null;
               }
            } else if (dispId != -1) {
               return this.getProperty(returnType, new OaIdl.DISPID(dispId), args);
            } else {
               propName = this.getAccessorName(method, prop);
               return this.getProperty(returnType, propName, args);
            }
         } else {
            ComMethod meth = (ComMethod)method.getAnnotation(ComMethod.class);
            if (null != meth) {
               fullLengthArgs = this.unfoldWhenVarargs(method, args);
               int dispId = meth.dispId();
               if (dispId != -1) {
                  return this.invokeMethod(returnType, new OaIdl.DISPID(dispId), fullLengthArgs);
               } else {
                  String methName = this.getMethodName(method, meth);
                  return this.invokeMethod(returnType, methName, fullLengthArgs);
               }
            } else {
               return null;
            }
         }
      }
   }

   private ConnectionPoint fetchRawConnectionPoint(Guid.IID iid) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      IConnectionPointContainer cpc = (IConnectionPointContainer)this.queryInterface(IConnectionPointContainer.class);
      Dispatch rawCpcDispatch = (Dispatch)cpc.getRawDispatch();
      ConnectionPointContainer rawCpc = new ConnectionPointContainer(rawCpcDispatch.getPointer());
      Guid.REFIID adviseRiid = new Guid.REFIID(iid.getPointer());
      PointerByReference ppCp = new PointerByReference();
      WinNT.HRESULT hr = rawCpc.FindConnectionPoint(adviseRiid, ppCp);
      COMUtils.checkRC(hr);
      ConnectionPoint rawCp = new ConnectionPoint(ppCp.getValue());
      return rawCp;
   }

   public IComEventCallbackCookie advise(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) throws COMException {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      try {
         ComInterface comInterfaceAnnotation = (ComInterface)comEventCallbackInterface.getAnnotation(ComInterface.class);
         if (null == comInterfaceAnnotation) {
            throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation");
         } else {
            Guid.IID iid = this.getIID(comInterfaceAnnotation);
            ConnectionPoint rawCp = this.fetchRawConnectionPoint(iid);
            IDispatchCallback rawListener = this.factory.createDispatchCallback(comEventCallbackInterface, comEventCallbackListener);
            comEventCallbackListener.setDispatchCallbackListener(rawListener);
            WinDef.DWORDByReference pdwCookie = new WinDef.DWORDByReference();
            WinNT.HRESULT hr = rawCp.Advise(rawListener, pdwCookie);
            int n = rawCp.Release();
            COMUtils.checkRC(hr);
            return new ComEventCallbackCookie(pdwCookie.getValue());
         }
      } catch (RuntimeException var10) {
         if (var10 instanceof COMException) {
            throw var10;
         } else {
            throw new COMException("Error occured in advise when trying to connect the listener " + comEventCallbackListener, var10);
         }
      }
   }

   public void unadvise(Class<?> comEventCallbackInterface, IComEventCallbackCookie cookie) throws COMException {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      try {
         ComInterface comInterfaceAnnotation = (ComInterface)comEventCallbackInterface.getAnnotation(ComInterface.class);
         if (null == comInterfaceAnnotation) {
            throw new COMException("unadvise: Interface must define a value for iid via the ComInterface annotation");
         } else {
            Guid.IID iid = this.getIID(comInterfaceAnnotation);
            ConnectionPoint rawCp = this.fetchRawConnectionPoint(iid);
            WinNT.HRESULT hr = rawCp.Unadvise(((ComEventCallbackCookie)cookie).getValue());
            rawCp.Release();
            COMUtils.checkRC(hr);
         }
      } catch (RuntimeException var7) {
         if (var7 instanceof COMException) {
            throw var7;
         } else {
            throw new COMException("Error occured in unadvise when trying to disconnect the listener from " + this, var7);
         }
      }
   }

   public <T> void setProperty(String name, T value) {
      OaIdl.DISPID dispID = this.resolveDispId(this.getRawDispatch(), name);
      this.setProperty(dispID, value);
   }

   public <T> void setProperty(OaIdl.DISPID dispId, T value) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      Variant.VARIANT v = Convert.toVariant(value);
      WinNT.HRESULT hr = this.oleMethod(4, (Variant.VARIANT.ByReference)null, this.getRawDispatch(), (OaIdl.DISPID)dispId, (Variant.VARIANT)v);
      Convert.free(v, value);
      COMUtils.checkRC(hr);
   }

   private void setProperty(String name, Object... args) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      OaIdl.DISPID dispID = this.resolveDispId(this.getRawDispatch(), name);
      this.setProperty(dispID, args);
   }

   private void setProperty(OaIdl.DISPID dispID, Object... args) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      Variant.VARIANT[] vargs;
      if (null == args) {
         vargs = new Variant.VARIANT[0];
      } else {
         vargs = new Variant.VARIANT[args.length];
      }

      for(int i = 0; i < vargs.length; ++i) {
         vargs[i] = Convert.toVariant(args[i]);
      }

      new Variant.VARIANT.ByReference();
      WinNT.HRESULT hr = this.oleMethod(4, (Variant.VARIANT.ByReference)null, this.getRawDispatch(), (OaIdl.DISPID)dispID, (Variant.VARIANT[])vargs);

      for(int i = 0; i < vargs.length; ++i) {
         Convert.free(vargs[i], args[i]);
      }

      COMUtils.checkRC(hr);
   }

   public <T> T getProperty(Class<T> returnType, String name, Object... args) {
      OaIdl.DISPID dispID = this.resolveDispId(this.getRawDispatch(), name);
      return this.getProperty(returnType, dispID, args);
   }

   public <T> T getProperty(Class<T> returnType, OaIdl.DISPID dispID, Object... args) {
      Variant.VARIANT[] vargs;
      if (null == args) {
         vargs = new Variant.VARIANT[0];
      } else {
         vargs = new Variant.VARIANT[args.length];
      }

      for(int i = 0; i < vargs.length; ++i) {
         vargs[i] = Convert.toVariant(args[i]);
      }

      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      WinNT.HRESULT hr = this.oleMethod(2, result, this.getRawDispatch(), (OaIdl.DISPID)dispID, (Variant.VARIANT[])vargs);

      for(int i = 0; i < vargs.length; ++i) {
         Convert.free(vargs[i], args[i]);
      }

      COMUtils.checkRC(hr);
      return Convert.toJavaObject(result, returnType, this.factory, false, true);
   }

   public <T> T invokeMethod(Class<T> returnType, String name, Object... args) {
      OaIdl.DISPID dispID = this.resolveDispId(this.getRawDispatch(), name);
      return this.invokeMethod(returnType, dispID, args);
   }

   public <T> T invokeMethod(Class<T> returnType, OaIdl.DISPID dispID, Object... args) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      Variant.VARIANT[] vargs;
      if (null == args) {
         vargs = new Variant.VARIANT[0];
      } else {
         vargs = new Variant.VARIANT[args.length];
      }

      for(int i = 0; i < vargs.length; ++i) {
         vargs[i] = Convert.toVariant(args[i]);
      }

      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      WinNT.HRESULT hr = this.oleMethod(1, result, this.getRawDispatch(), (OaIdl.DISPID)dispID, (Variant.VARIANT[])vargs);

      for(int i = 0; i < vargs.length; ++i) {
         Convert.free(vargs[i], args[i]);
      }

      COMUtils.checkRC(hr);
      return Convert.toJavaObject(result, returnType, this.factory, false, true);
   }

   private Object[] unfoldWhenVarargs(Method method, Object[] argParams) {
      if (null == argParams) {
         return null;
      } else if (argParams.length != 0 && method.isVarArgs() && argParams[argParams.length - 1] instanceof Object[]) {
         Object[] varargs = (Object[])((Object[])argParams[argParams.length - 1]);
         Object[] args = new Object[argParams.length - 1 + varargs.length];
         System.arraycopy(argParams, 0, args, 0, argParams.length - 1);
         System.arraycopy(varargs, 0, args, argParams.length - 1, varargs.length);
         return args;
      } else {
         return argParams;
      }
   }

   public <T> T queryInterface(Class<T> comInterface) throws COMException {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      try {
         ComInterface comInterfaceAnnotation = (ComInterface)comInterface.getAnnotation(ComInterface.class);
         if (null == comInterfaceAnnotation) {
            throw new COMException("queryInterface: Interface must define a value for iid via the ComInterface annotation");
         } else {
            Guid.IID iid = this.getIID(comInterfaceAnnotation);
            PointerByReference ppvObject = new PointerByReference();
            WinNT.HRESULT hr = this.getRawDispatch().QueryInterface(new Guid.REFIID(iid), ppvObject);
            if (WinNT.S_OK.equals(hr)) {
               Dispatch dispatch = new Dispatch(ppvObject.getValue());
               T t = this.factory.createProxy(comInterface, dispatch);
               int n = dispatch.Release();
               return t;
            } else {
               String formatMessageFromHR = Kernel32Util.formatMessage(hr);
               throw new COMException("queryInterface: " + formatMessageFromHR, hr);
            }
         }
      } catch (RuntimeException var9) {
         if (var9 instanceof COMException) {
            throw var9;
         } else {
            throw new COMException("Error occured when trying to query for interface " + comInterface.getName(), var9);
         }
      }
   }

   private Guid.IID getIID(ComInterface annotation) {
      String iidStr = annotation.iid();
      if (null != iidStr && !iidStr.isEmpty()) {
         return new Guid.IID(iidStr);
      } else {
         throw new COMException("ComInterface must define a value for iid");
      }
   }

   private String getAccessorName(Method method, ComProperty prop) {
      if (prop.name().isEmpty()) {
         String methName = method.getName();
         if (methName.startsWith("get")) {
            return methName.replaceFirst("get", "");
         } else {
            throw new RuntimeException("Property Accessor name must start with 'get', or set the anotation 'name' value");
         }
      } else {
         return prop.name();
      }
   }

   private String getMutatorName(Method method, ComProperty prop) {
      if (prop.name().isEmpty()) {
         String methName = method.getName();
         if (methName.startsWith("set")) {
            return methName.replaceFirst("set", "");
         } else {
            throw new RuntimeException("Property Mutator name must start with 'set', or set the anotation 'name' value");
         }
      } else {
         return prop.name();
      }
   }

   private String getMethodName(Method method, ComMethod meth) {
      if (meth.name().isEmpty()) {
         String methName = method.getName();
         return methName;
      } else {
         return meth.name();
      }
   }

   protected OaIdl.DISPID resolveDispId(String name) {
      return this.resolveDispId(this.getRawDispatch(), name);
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, name, new Variant.VARIANT[]{pArg});
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, dispId, new Variant.VARIANT[]{pArg});
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name) throws COMException {
      return this.oleMethod(nType, pvResult, name, (Variant.VARIANT[])null);
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId) throws COMException {
      return this.oleMethod(nType, pvResult, dispId, (Variant.VARIANT[])null);
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT[] pArgs) throws COMException {
      return this.oleMethod(nType, pvResult, this.resolveDispId(name), pArgs);
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
      return this.oleMethod(nType, pvResult, this.getRawDispatch(), dispId, pArgs);
   }

   /** @deprecated */
   @Deprecated
   protected OaIdl.DISPID resolveDispId(com.sun.jna.platform.win32.COM.IDispatch pDisp, String name) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      if (pDisp == null) {
         throw new COMException("pDisp (IDispatch) parameter is null!");
      } else {
         WString[] ptName = new WString[]{new WString(name)};
         OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
         WinNT.HRESULT hr = pDisp.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, this.factory.getLCID(), pdispID);
         COMUtils.checkRC(hr);
         return pdispID.getValue();
      }
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, String name, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, name, new Variant.VARIANT[]{pArg});
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, dispId, new Variant.VARIANT[]{pArg});
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, String name) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, name, (Variant.VARIANT[])null);
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, OaIdl.DISPID dispId) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, dispId, (Variant.VARIANT[])null);
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, String name, Variant.VARIANT[] pArgs) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, this.resolveDispId(pDisp, name), pArgs);
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      if (pDisp == null) {
         throw new COMException("pDisp (IDispatch) parameter is null!");
      } else {
         int _argsLen = 0;
         Variant.VARIANT[] _args = null;
         OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
         OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
         IntByReference puArgErr = new IntByReference();
         int finalNType;
         if (pArgs != null && pArgs.length > 0) {
            _argsLen = pArgs.length;
            _args = new Variant.VARIANT[_argsLen];
            finalNType = _argsLen;

            for(int i = 0; i < _argsLen; ++i) {
               --finalNType;
               _args[i] = pArgs[finalNType];
            }
         }

         if (nType == 4) {
            dp.setRgdispidNamedArgs(new OaIdl.DISPID[]{OaIdl.DISPID_PROPERTYPUT});
         }

         if (nType != 1 && nType != 2) {
            finalNType = nType;
         } else {
            finalNType = 3;
         }

         if (_argsLen > 0) {
            dp.setArgs(_args);
            dp.write();
         }

         WinNT.HRESULT hr = pDisp.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), this.factory.getLCID(), new WinDef.WORD((long)finalNType), dp, pvResult, pExcepInfo, puArgErr);
         COMUtils.checkRC(hr, pExcepInfo, puArgErr);
         return hr;
      }
   }
}
