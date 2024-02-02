package com.sun.jna.platform.linux;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class XAttrUtil {
   private XAttrUtil() {
   }

   public static void setXAttr(String path, String name, String value) throws IOException {
      setXAttr(path, name, value, Native.getDefaultStringEncoding());
   }

   public static void setXAttr(String path, String name, String value, String encoding) throws IOException {
      setXAttr(path, name, value.getBytes(encoding));
   }

   public static void setXAttr(String path, String name, byte[] value) throws IOException {
      int retval = XAttr.INSTANCE.setxattr(path, name, (byte[])value, new XAttr.size_t((long)value.length), 0);
      if (retval != 0) {
         int eno = Native.getLastError();
         throw new IOException("errno: " + eno);
      }
   }

   public static void lSetXAttr(String path, String name, String value) throws IOException {
      lSetXAttr(path, name, value, Native.getDefaultStringEncoding());
   }

   public static void lSetXAttr(String path, String name, String value, String encoding) throws IOException {
      lSetXAttr(path, name, value.getBytes(encoding));
   }

   public static void lSetXAttr(String path, String name, byte[] value) throws IOException {
      int retval = XAttr.INSTANCE.lsetxattr(path, name, (byte[])value, new XAttr.size_t((long)value.length), 0);
      if (retval != 0) {
         int eno = Native.getLastError();
         throw new IOException("errno: " + eno);
      }
   }

   public static void fSetXAttr(int fd, String name, String value) throws IOException {
      fSetXAttr(fd, name, value, Native.getDefaultStringEncoding());
   }

   public static void fSetXAttr(int fd, String name, String value, String encoding) throws IOException {
      fSetXAttr(fd, name, value.getBytes(encoding));
   }

   public static void fSetXAttr(int fd, String name, byte[] value) throws IOException {
      int retval = XAttr.INSTANCE.fsetxattr(fd, name, (byte[])value, new XAttr.size_t((long)value.length), 0);
      if (retval != 0) {
         int eno = Native.getLastError();
         throw new IOException("errno: " + eno);
      }
   }

   public static String getXAttr(String path, String name) throws IOException {
      return getXAttr(path, name, Native.getDefaultStringEncoding());
   }

   public static String getXAttr(String path, String name, String encoding) throws IOException {
      byte[] valueMem = getXAttrBytes(path, name);
      return new String(valueMem, Charset.forName(encoding));
   }

   public static byte[] getXAttrBytes(String path, String name) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      byte[] valueMem;
      do {
         retval = XAttr.INSTANCE.getxattr(path, name, (byte[])null, XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         valueMem = new byte[retval.intValue()];
         retval = XAttr.INSTANCE.getxattr(path, name, valueMem, new XAttr.size_t((long)valueMem.length));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return valueMem;
   }

   public static Memory getXAttrAsMemory(String path, String name) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      Memory valueMem;
      do {
         retval = XAttr.INSTANCE.getxattr(path, name, (Pointer)((Memory)null), XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         if (retval.longValue() == 0L) {
            return null;
         }

         valueMem = new Memory(retval.longValue());
         retval = XAttr.INSTANCE.getxattr(path, name, (Pointer)valueMem, new XAttr.size_t(valueMem.size()));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return valueMem;
   }

   public static String lGetXAttr(String path, String name) throws IOException {
      return lGetXAttr(path, name, Native.getDefaultStringEncoding());
   }

   public static String lGetXAttr(String path, String name, String encoding) throws IOException {
      byte[] valueMem = lGetXAttrBytes(path, name);
      return new String(valueMem, Charset.forName(encoding));
   }

   public static byte[] lGetXAttrBytes(String path, String name) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      byte[] valueMem;
      do {
         retval = XAttr.INSTANCE.lgetxattr(path, name, (byte[])null, XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         valueMem = new byte[retval.intValue()];
         retval = XAttr.INSTANCE.lgetxattr(path, name, valueMem, new XAttr.size_t((long)valueMem.length));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return valueMem;
   }

   public static Memory lGetXAttrAsMemory(String path, String name) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      Memory valueMem;
      do {
         retval = XAttr.INSTANCE.lgetxattr(path, name, (Pointer)((Memory)null), XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         if (retval.longValue() == 0L) {
            return null;
         }

         valueMem = new Memory(retval.longValue());
         retval = XAttr.INSTANCE.lgetxattr(path, name, (Pointer)valueMem, new XAttr.size_t(valueMem.size()));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return valueMem;
   }

   public static String fGetXAttr(int fd, String name) throws IOException {
      return fGetXAttr(fd, name, Native.getDefaultStringEncoding());
   }

   public static String fGetXAttr(int fd, String name, String encoding) throws IOException {
      byte[] valueMem = fGetXAttrBytes(fd, name);
      return new String(valueMem, Charset.forName(encoding));
   }

   public static byte[] fGetXAttrBytes(int fd, String name) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      byte[] valueMem;
      do {
         retval = XAttr.INSTANCE.fgetxattr(fd, name, (byte[])null, XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         valueMem = new byte[retval.intValue()];
         retval = XAttr.INSTANCE.fgetxattr(fd, name, valueMem, new XAttr.size_t((long)valueMem.length));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return valueMem;
   }

   public static Memory fGetXAttrAsMemory(int fd, String name) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      Memory valueMem;
      do {
         retval = XAttr.INSTANCE.fgetxattr(fd, name, (Pointer)((Memory)null), XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         if (retval.longValue() == 0L) {
            return null;
         }

         valueMem = new Memory(retval.longValue());
         retval = XAttr.INSTANCE.fgetxattr(fd, name, (Pointer)valueMem, new XAttr.size_t(valueMem.size()));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return valueMem;
   }

   public static Collection<String> listXAttr(String path) throws IOException {
      return listXAttr(path, Native.getDefaultStringEncoding());
   }

   public static Collection<String> listXAttr(String path, String encoding) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      byte[] listMem;
      do {
         retval = XAttr.INSTANCE.listxattr(path, (byte[])null, XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         listMem = new byte[retval.intValue()];
         retval = XAttr.INSTANCE.listxattr(path, listMem, new XAttr.size_t((long)listMem.length));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return splitBufferToStrings(listMem, encoding);
   }

   public static Collection<String> lListXAttr(String path) throws IOException {
      return lListXAttr(path, Native.getDefaultStringEncoding());
   }

   public static Collection<String> lListXAttr(String path, String encoding) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      byte[] listMem;
      do {
         retval = XAttr.INSTANCE.llistxattr(path, (byte[])null, XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         listMem = new byte[retval.intValue()];
         retval = XAttr.INSTANCE.llistxattr(path, listMem, new XAttr.size_t((long)listMem.length));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return splitBufferToStrings(listMem, encoding);
   }

   public static Collection<String> fListXAttr(int fd) throws IOException {
      return fListXAttr(fd, Native.getDefaultStringEncoding());
   }

   public static Collection<String> fListXAttr(int fd, String encoding) throws IOException {
      int eno = 0;

      XAttr.ssize_t retval;
      byte[] listMem;
      do {
         retval = XAttr.INSTANCE.flistxattr(fd, (byte[])null, XAttr.size_t.ZERO);
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            throw new IOException("errno: " + eno);
         }

         listMem = new byte[retval.intValue()];
         retval = XAttr.INSTANCE.flistxattr(fd, listMem, new XAttr.size_t((long)listMem.length));
         if (retval.longValue() < 0L) {
            eno = Native.getLastError();
            if (eno != 34) {
               throw new IOException("errno: " + eno);
            }
         }
      } while(retval.longValue() < 0L && eno == 34);

      return splitBufferToStrings(listMem, encoding);
   }

   public static void removeXAttr(String path, String name) throws IOException {
      int retval = XAttr.INSTANCE.removexattr(path, name);
      if (retval != 0) {
         int eno = Native.getLastError();
         throw new IOException("errno: " + eno);
      }
   }

   public static void lRemoveXAttr(String path, String name) throws IOException {
      int retval = XAttr.INSTANCE.lremovexattr(path, name);
      if (retval != 0) {
         int eno = Native.getLastError();
         throw new IOException("errno: " + eno);
      }
   }

   public static void fRemoveXAttr(int fd, String name) throws IOException {
      int retval = XAttr.INSTANCE.fremovexattr(fd, name);
      if (retval != 0) {
         int eno = Native.getLastError();
         throw new IOException("errno: " + eno);
      }
   }

   private static Collection<String> splitBufferToStrings(byte[] valueMem, String encoding) throws IOException {
      Charset charset = Charset.forName(encoding);
      Set<String> attributesList = new LinkedHashSet(1);
      int offset = 0;

      for(int i = 0; i < valueMem.length; ++i) {
         if (valueMem[i] == 0) {
            String name = new String(valueMem, offset, i - offset, charset);
            attributesList.add(name);
            offset = i + 1;
         }
      }

      return attributesList;
   }
}
