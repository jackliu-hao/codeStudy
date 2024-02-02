package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class Moniker extends Unknown implements IMoniker {
   static final int vTableIdStart = 7;

   public Moniker() {
   }

   public Moniker(Pointer pointer) {
      super(pointer);
   }

   public void BindToObject() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void BindToStorage() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void Reduce() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void ComposeWith() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void Enum() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void IsEqual() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void Hash() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void IsRunning() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void GetTimeOfLastChange() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void Inverse() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void CommonPrefixWith() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void RelativePathTo() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public String GetDisplayName(Pointer pbc, Pointer pmkToLeft) {
      int vTableId = true;
      PointerByReference ppszDisplayNameRef = new PointerByReference();
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(20, new Object[]{this.getPointer(), pbc, pmkToLeft, ppszDisplayNameRef}, WinNT.HRESULT.class);
      COMUtils.checkRC(hr);
      Pointer ppszDisplayName = ppszDisplayNameRef.getValue();
      if (ppszDisplayName == null) {
         return null;
      } else {
         WTypes.LPOLESTR oleStr = new WTypes.LPOLESTR(ppszDisplayName);
         String name = oleStr.getValue();
         Ole32.INSTANCE.CoTaskMemFree(ppszDisplayName);
         return name;
      }
   }

   public void ParseDisplayName() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public void IsSystemMoniker() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public boolean IsDirty() {
      throw new UnsupportedOperationException();
   }

   public void Load(IStream stm) {
      throw new UnsupportedOperationException();
   }

   public void Save(IStream stm) {
      throw new UnsupportedOperationException();
   }

   public void GetSizeMax() {
      throw new UnsupportedOperationException();
   }

   public Guid.CLSID GetClassID() {
      throw new UnsupportedOperationException();
   }

   public static class ByReference extends Moniker implements Structure.ByReference {
   }
}
