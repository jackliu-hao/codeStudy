package com.sun.jna.platform.win32.COM.util;

import java.util.List;

public interface IRunningObjectTable {
   Iterable<IDispatch> enumRunning();

   <T> List<T> getActiveObjectsByInterface(Class<T> var1);
}
