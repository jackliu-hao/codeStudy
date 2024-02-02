package oshi.jna.platform.unix.aix;

import com.sun.jna.Native;
import oshi.jna.platform.unix.CLibrary;

public interface AixLibc extends CLibrary {
   AixLibc INSTANCE = (AixLibc)Native.load("c", AixLibc.class);
}
