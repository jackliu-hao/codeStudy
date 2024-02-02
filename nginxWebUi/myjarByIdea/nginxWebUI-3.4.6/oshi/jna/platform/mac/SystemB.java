package oshi.jna.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import oshi.jna.platform.unix.CLibrary;

public interface SystemB extends com.sun.jna.platform.mac.SystemB, CLibrary {
   SystemB INSTANCE = (SystemB)Native.load("System", SystemB.class);
   int UTX_USERSIZE = 256;
   int UTX_LINESIZE = 32;
   int UTX_IDSIZE = 4;
   int UTX_HOSTSIZE = 256;

   MacUtmpx getutxent();

   @Structure.FieldOrder({"ut_user", "ut_id", "ut_line", "ut_pid", "ut_type", "ut_tv", "ut_host", "ut_pad"})
   public static class MacUtmpx extends Structure {
      public byte[] ut_user = new byte[256];
      public byte[] ut_id = new byte[4];
      public byte[] ut_line = new byte[32];
      public int ut_pid;
      public short ut_type;
      public com.sun.jna.platform.mac.SystemB.Timeval ut_tv;
      public byte[] ut_host = new byte[256];
      public byte[] ut_pad = new byte[16];
   }
}
