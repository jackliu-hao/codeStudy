package com.sun.jna.platform.win32.COM;

public interface IPersistStream extends IPersist {
   boolean IsDirty();

   void Load(IStream var1);

   void Save(IStream var1);

   void GetSizeMax();
}
