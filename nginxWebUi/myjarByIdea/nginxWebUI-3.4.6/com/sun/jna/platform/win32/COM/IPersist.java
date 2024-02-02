package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;

public interface IPersist extends IUnknown {
   Guid.CLSID GetClassID();
}
