package com.sun.jna.platform.unix;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface LibCAPI extends Reboot, Resource {
   int HOST_NAME_MAX = 255;

   int getuid();

   int geteuid();

   int getgid();

   int getegid();

   int setuid(int var1);

   int seteuid(int var1);

   int setgid(int var1);

   int setegid(int var1);

   int gethostname(byte[] var1, int var2);

   int sethostname(String var1, int var2);

   int getdomainname(byte[] var1, int var2);

   int setdomainname(String var1, int var2);

   String getenv(String var1);

   int setenv(String var1, String var2, int var3);

   int unsetenv(String var1);

   int getloadavg(double[] var1, int var2);

   int close(int var1);

   int msync(Pointer var1, size_t var2, int var3);

   int munmap(Pointer var1, size_t var2);

   public static class ssize_t extends IntegerType {
      public static final ssize_t ZERO = new ssize_t();
      private static final long serialVersionUID = 1L;

      public ssize_t() {
         this(0L);
      }

      public ssize_t(long value) {
         super(Native.SIZE_T_SIZE, value, false);
      }
   }

   public static class size_t extends IntegerType {
      public static final size_t ZERO = new size_t();
      private static final long serialVersionUID = 1L;

      public size_t() {
         this(0L);
      }

      public size_t(long value) {
         super(Native.SIZE_T_SIZE, value, true);
      }
   }
}
