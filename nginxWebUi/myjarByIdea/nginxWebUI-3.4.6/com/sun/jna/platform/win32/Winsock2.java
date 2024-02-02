package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface Winsock2 extends Library {
   Winsock2 INSTANCE = (Winsock2)Native.load("ws2_32", Winsock2.class, W32APIOptions.ASCII_OPTIONS);

   int gethostname(byte[] var1, int var2);
}
