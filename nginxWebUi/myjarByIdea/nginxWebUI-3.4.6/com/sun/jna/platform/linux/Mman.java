package com.sun.jna.platform.linux;

import com.sun.jna.Pointer;

public interface Mman {
   int PROT_READ = 1;
   int PROT_WRITE = 2;
   int PROT_EXEC = 4;
   int PROT_NONE = 0;
   int PROT_GROWSDOWN = 16777216;
   int PROT_GROWSUP = 33554432;
   int MAP_SHARED = 1;
   int MAP_PRIVATE = 2;
   int MAP_SHARED_VALIDATE = 3;
   int MAP_TYPE = 15;
   int MAP_FILE = 0;
   int MAP_FIXED = 16;
   int MAP_ANONYMOUS = 32;
   int MAP_ANON = 32;
   int MAP_32BIT = 64;
   int MAP_GROWSDOWN = 256;
   int MAP_DENYWRITE = 2048;
   int MAP_EXECUTABLE = 4096;
   int MAP_LOCKED = 8192;
   int MAP_NORESERVE = 16384;
   int MAP_POPULATE = 32768;
   int MAP_NONBLOCK = 65536;
   int MAP_STACK = 131072;
   int MAP_HUGETLB = 262144;
   int MAP_SYNC = 524288;
   int MAP_FIXED_NOREPLACE = 1048576;
   Pointer MAP_FAILED = new Pointer(-1L);
   int MS_ASYNC = 1;
   int MS_SYNC = 2;
   int MS_INVALIDATE = 4;
}
