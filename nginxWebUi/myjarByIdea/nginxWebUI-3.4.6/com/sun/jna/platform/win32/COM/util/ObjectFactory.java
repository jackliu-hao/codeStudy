package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.ptr.PointerByReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ObjectFactory {
   private final List<WeakReference<ProxyObject>> registeredObjects = new LinkedList();
   private static final WinDef.LCID LOCALE_USER_DEFAULT;
   private WinDef.LCID LCID;

   protected void finalize() throws Throwable {
      try {
         this.disposeAll();
      } finally {
         super.finalize();
      }

   }

   public IRunningObjectTable getRunningObjectTable() {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      PointerByReference rotPtr = new PointerByReference();
      WinNT.HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new WinDef.DWORD(0L), rotPtr);
      COMUtils.checkRC(hr);
      com.sun.jna.platform.win32.COM.RunningObjectTable raw = new com.sun.jna.platform.win32.COM.RunningObjectTable(rotPtr.getValue());
      IRunningObjectTable rot = new RunningObjectTable(raw, this);
      return rot;
   }

   public <T> T createProxy(Class<T> comInterface, com.sun.jna.platform.win32.COM.IDispatch dispatch) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      ProxyObject jop = new ProxyObject(comInterface, dispatch, this);
      Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class[]{comInterface}, jop);
      T result = comInterface.cast(proxy);
      return result;
   }

   public <T> T createObject(Class<T> comInterface) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      ComObject comObectAnnotation = (ComObject)comInterface.getAnnotation(ComObject.class);
      if (null == comObectAnnotation) {
         throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
      } else {
         Guid.GUID guid = this.discoverClsId(comObectAnnotation);
         PointerByReference ptrDisp = new PointerByReference();
         WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateInstance(guid, (Pointer)null, 21, com.sun.jna.platform.win32.COM.IDispatch.IID_IDISPATCH, ptrDisp);
         COMUtils.checkRC(hr);
         Dispatch d = new Dispatch(ptrDisp.getValue());
         T t = this.createProxy(comInterface, d);
         int n = d.Release();
         return t;
      }
   }

   public <T> T fetchObject(Class<T> comInterface) throws COMException {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      ComObject comObectAnnotation = (ComObject)comInterface.getAnnotation(ComObject.class);
      if (null == comObectAnnotation) {
         throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
      } else {
         Guid.GUID guid = this.discoverClsId(comObectAnnotation);
         PointerByReference ptrDisp = new PointerByReference();
         WinNT.HRESULT hr = OleAuto.INSTANCE.GetActiveObject(guid, (WinDef.PVOID)null, ptrDisp);
         COMUtils.checkRC(hr);
         Dispatch d = new Dispatch(ptrDisp.getValue());
         T t = this.createProxy(comInterface, d);
         d.Release();
         return t;
      }
   }

   Guid.GUID discoverClsId(ComObject annotation) {
      assert COMUtils.comIsInitialized() : "COM not initialized";

      String clsIdStr = annotation.clsId();
      String progIdStr = annotation.progId();
      if (null != clsIdStr && !clsIdStr.isEmpty()) {
         return new Guid.CLSID(clsIdStr);
      } else if (null != progIdStr && !progIdStr.isEmpty()) {
         Guid.CLSID.ByReference rclsid = new Guid.CLSID.ByReference();
         WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progIdStr, rclsid);
         COMUtils.checkRC(hr);
         return rclsid;
      } else {
         throw new COMException("ComObject must define a value for either clsId or progId");
      }
   }

   IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
      return new CallbackProxy(this, comEventCallbackInterface, comEventCallbackListener);
   }

   public void register(ProxyObject proxyObject) {
      synchronized(this.registeredObjects) {
         this.registeredObjects.add(new WeakReference(proxyObject));
      }
   }

   public void unregister(ProxyObject proxyObject) {
      synchronized(this.registeredObjects) {
         Iterator<WeakReference<ProxyObject>> iterator = this.registeredObjects.iterator();

         while(true) {
            ProxyObject po;
            do {
               if (!iterator.hasNext()) {
                  return;
               }

               WeakReference<ProxyObject> weakRef = (WeakReference)iterator.next();
               po = (ProxyObject)weakRef.get();
            } while(po != null && po != proxyObject);

            iterator.remove();
         }
      }
   }

   public void disposeAll() {
      synchronized(this.registeredObjects) {
         List<WeakReference<ProxyObject>> s = new ArrayList(this.registeredObjects);
         Iterator var3 = s.iterator();

         while(var3.hasNext()) {
            WeakReference<ProxyObject> weakRef = (WeakReference)var3.next();
            ProxyObject po = (ProxyObject)weakRef.get();
            if (po != null) {
               po.dispose();
            }
         }

         this.registeredObjects.clear();
      }
   }

   public WinDef.LCID getLCID() {
      return this.LCID != null ? this.LCID : LOCALE_USER_DEFAULT;
   }

   public void setLCID(WinDef.LCID value) {
      this.LCID = value;
   }

   static {
      LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
   }
}
