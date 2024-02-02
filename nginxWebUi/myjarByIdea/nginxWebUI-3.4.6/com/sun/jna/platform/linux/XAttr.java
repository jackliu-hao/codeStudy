package com.sun.jna.platform.linux;

import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface XAttr extends Library {
   XAttr INSTANCE = (XAttr)Native.load(XAttr.class);
   int XATTR_CREATE = 1;
   int XATTR_REPLACE = 2;
   int EPERM = 1;
   int E2BIG = 7;
   int EEXIST = 17;
   int ENOSPC = 28;
   int ERANGE = 34;
   int ENODATA = 61;
   int ENOATTR = 61;
   int ENOTSUP = 95;
   int EDQUOT = 122;

   int setxattr(String var1, String var2, Pointer var3, size_t var4, int var5);

   int setxattr(String var1, String var2, byte[] var3, size_t var4, int var5);

   int lsetxattr(String var1, String var2, Pointer var3, size_t var4, int var5);

   int lsetxattr(String var1, String var2, byte[] var3, size_t var4, int var5);

   int fsetxattr(int var1, String var2, Pointer var3, size_t var4, int var5);

   int fsetxattr(int var1, String var2, byte[] var3, size_t var4, int var5);

   ssize_t getxattr(String var1, String var2, Pointer var3, size_t var4);

   ssize_t getxattr(String var1, String var2, byte[] var3, size_t var4);

   ssize_t lgetxattr(String var1, String var2, Pointer var3, size_t var4);

   ssize_t lgetxattr(String var1, String var2, byte[] var3, size_t var4);

   ssize_t fgetxattr(int var1, String var2, Pointer var3, size_t var4);

   ssize_t fgetxattr(int var1, String var2, byte[] var3, size_t var4);

   ssize_t listxattr(String var1, Pointer var2, size_t var3);

   ssize_t listxattr(String var1, byte[] var2, size_t var3);

   ssize_t llistxattr(String var1, Pointer var2, size_t var3);

   ssize_t llistxattr(String var1, byte[] var2, size_t var3);

   ssize_t flistxattr(int var1, Pointer var2, size_t var3);

   ssize_t flistxattr(int var1, byte[] var2, size_t var3);

   int removexattr(String var1, String var2);

   int lremovexattr(String var1, String var2);

   int fremovexattr(int var1, String var2);

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
