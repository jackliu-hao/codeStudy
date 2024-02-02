package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

interface XAttr extends Library {
   XAttr INSTANCE = (XAttr)Native.load((String)null, (Class)XAttr.class);
   int XATTR_NOFOLLOW = 1;
   int XATTR_CREATE = 2;
   int XATTR_REPLACE = 4;
   int XATTR_NOSECURITY = 8;
   int XATTR_NODEFAULT = 16;
   int XATTR_SHOWCOMPRESSION = 32;
   int XATTR_MAXNAMELEN = 127;
   String XATTR_FINDERINFO_NAME = "com.apple.FinderInfo";
   String XATTR_RESOURCEFORK_NAME = "com.apple.ResourceFork";

   long getxattr(String var1, String var2, Pointer var3, long var4, int var6, int var7);

   int setxattr(String var1, String var2, Pointer var3, long var4, int var6, int var7);

   int removexattr(String var1, String var2, int var3);

   long listxattr(String var1, Pointer var2, long var3, int var5);
}
