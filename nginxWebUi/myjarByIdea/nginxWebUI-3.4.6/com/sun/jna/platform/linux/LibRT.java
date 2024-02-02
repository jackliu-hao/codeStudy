package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LibRT extends Library {
   LibRT INSTANCE = (LibRT)Native.load("rt", LibRT.class);

   int shm_open(String var1, int var2, int var3);

   int shm_unlink(String var1);
}
