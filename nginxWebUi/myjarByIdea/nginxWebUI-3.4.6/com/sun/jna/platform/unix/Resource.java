package com.sun.jna.platform.unix;

import com.sun.jna.Structure;

public interface Resource {
   int RLIMIT_CPU = 0;
   int RLIMIT_FSIZE = 1;
   int RLIMIT_DATA = 2;
   int RLIMIT_STACK = 3;
   int RLIMIT_CORE = 4;
   int RLIMIT_RSS = 5;
   int RLIMIT_NOFILE = 7;
   int RLIMIT_AS = 9;
   int RLIMIT_NPROC = 6;
   int RLIMIT_MEMLOCK = 8;
   int RLIMIT_LOCKS = 10;
   int RLIMIT_SIGPENDING = 11;
   int RLIMIT_MSGQUEUE = 12;
   int RLIMIT_NICE = 13;
   int RLIMIT_RTPRIO = 14;
   int RLIMIT_RTTIME = 15;
   int RLIMIT_NLIMITS = 16;

   int getrlimit(int var1, Rlimit var2);

   int setrlimit(int var1, Rlimit var2);

   @Structure.FieldOrder({"rlim_cur", "rlim_max"})
   public static class Rlimit extends Structure {
      public long rlim_cur;
      public long rlim_max;
   }
}
