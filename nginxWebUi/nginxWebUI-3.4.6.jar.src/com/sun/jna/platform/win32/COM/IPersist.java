package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;

public interface IPersist extends IUnknown {
  Guid.CLSID GetClassID();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\IPersist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */