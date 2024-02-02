package org.h2.util;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.security.CipherFactory;

public class NetUtils {
   private static final int CACHE_MILLIS = 1000;
   private static InetAddress cachedBindAddress;
   private static String cachedLocalAddress;
   private static long cachedLocalAddressTime;

   private NetUtils() {
   }

   public static Socket createLoopbackSocket(int var0, boolean var1) throws IOException {
      String var2 = getLocalAddress();

      try {
         return createSocket(var2, var0, var1);
      } catch (IOException var6) {
         try {
            return createSocket("localhost", var0, var1);
         } catch (IOException var5) {
            throw var6;
         }
      }
   }

   public static Socket createSocket(String var0, int var1, boolean var2) throws IOException {
      return createSocket((String)var0, var1, var2, 0);
   }

   public static Socket createSocket(String var0, int var1, boolean var2, int var3) throws IOException {
      int var4 = var1;
      int var5 = var0.startsWith("[") ? var0.indexOf(93) : 0;
      int var6 = var0.indexOf(58, var5);
      if (var6 >= 0) {
         var4 = Integer.decode(var0.substring(var6 + 1));
         var0 = var0.substring(0, var6);
      }

      InetAddress var7 = InetAddress.getByName(var0);
      return createSocket(var7, var4, var2, var3);
   }

   public static Socket createSocket(InetAddress var0, int var1, boolean var2) throws IOException {
      return createSocket((InetAddress)var0, var1, var2, 0);
   }

   public static Socket createSocket(InetAddress var0, int var1, boolean var2, int var3) throws IOException {
      long var4 = System.nanoTime();
      int var6 = 0;

      while(true) {
         try {
            if (var2) {
               return CipherFactory.createSocket(var0, var1);
            }

            Socket var7 = new Socket();
            var7.setSoTimeout(var3);
            var7.connect(new InetSocketAddress(var0, var1), SysProperties.SOCKET_CONNECT_TIMEOUT);
            return var7;
         } catch (IOException var11) {
            if (System.nanoTime() - var4 >= (long)SysProperties.SOCKET_CONNECT_TIMEOUT * 1000000L) {
               throw var11;
            }

            if (var6 >= SysProperties.SOCKET_CONNECT_RETRY) {
               throw var11;
            }

            try {
               long var8 = (long)Math.min(256, var6 * var6);
               Thread.sleep(var8);
            } catch (InterruptedException var10) {
            }

            ++var6;
         }
      }
   }

   public static ServerSocket createServerSocket(int var0, boolean var1) {
      try {
         return createServerSocketTry(var0, var1);
      } catch (Exception var3) {
         return createServerSocketTry(var0, var1);
      }
   }

   private static InetAddress getBindAddress() throws UnknownHostException {
      String var0 = SysProperties.BIND_ADDRESS;
      if (var0 != null && !var0.isEmpty()) {
         Class var1 = NetUtils.class;
         synchronized(NetUtils.class) {
            if (cachedBindAddress == null) {
               cachedBindAddress = InetAddress.getByName(var0);
            }
         }

         return cachedBindAddress;
      } else {
         return null;
      }
   }

   private static ServerSocket createServerSocketTry(int var0, boolean var1) {
      try {
         InetAddress var2 = getBindAddress();
         if (var1) {
            return CipherFactory.createServerSocket(var0, var2);
         } else {
            return var2 == null ? new ServerSocket(var0) : new ServerSocket(var0, 0, var2);
         }
      } catch (BindException var3) {
         throw DbException.get(90061, var3, Integer.toString(var0), var3.toString());
      } catch (IOException var4) {
         throw DbException.convertIOException(var4, "port: " + var0 + " ssl: " + var1);
      }
   }

   public static boolean isLocalAddress(Socket var0) throws UnknownHostException {
      InetAddress var1 = var0.getInetAddress();
      if (var1.isLoopbackAddress()) {
         return true;
      } else {
         InetAddress var2 = InetAddress.getLocalHost();
         String var3 = var2.getHostAddress();
         InetAddress[] var4 = InetAddress.getAllByName(var3);
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            InetAddress var7 = var4[var6];
            if (var1.equals(var7)) {
               return true;
            }
         }

         return false;
      }
   }

   public static ServerSocket closeSilently(ServerSocket var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }

      return null;
   }

   public static synchronized String getLocalAddress() {
      long var0 = System.nanoTime();
      if (cachedLocalAddress != null && var0 - cachedLocalAddressTime < 1000000000L) {
         return cachedLocalAddress;
      } else {
         InetAddress var2 = null;
         boolean var3 = false;

         try {
            var2 = getBindAddress();
            if (var2 == null) {
               var3 = true;
            }
         } catch (UnknownHostException var6) {
         }

         if (var3) {
            try {
               var2 = InetAddress.getLocalHost();
            } catch (UnknownHostException var5) {
               throw DbException.convert(var5);
            }
         }

         String var4;
         if (var2 == null) {
            var4 = "localhost";
         } else {
            var4 = var2.getHostAddress();
            if (var2 instanceof Inet6Address) {
               if (var4.indexOf(37) >= 0) {
                  var4 = "localhost";
               } else if (var4.indexOf(58) >= 0 && !var4.startsWith("[")) {
                  var4 = "[" + var4 + "]";
               }
            }
         }

         if (var4.equals("127.0.0.1")) {
            var4 = "localhost";
         }

         cachedLocalAddress = var4;
         cachedLocalAddressTime = var0;
         return var4;
      }
   }

   public static String getHostName(String var0) {
      try {
         InetAddress var1 = InetAddress.getByName(var0);
         return var1.getHostName();
      } catch (Exception var2) {
         return "unknown";
      }
   }

   public static StringBuilder ipToShortForm(StringBuilder var0, byte[] var1, boolean var2) {
      switch (var1.length) {
         case 4:
            if (var0 == null) {
               var0 = new StringBuilder(15);
            }

            var0.append(var1[0] & 255).append('.').append(var1[1] & 255).append('.').append(var1[2] & 255).append('.').append(var1[3] & 255);
            break;
         case 16:
            short[] var3 = new short[8];
            int var4 = 0;
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;

            int var8;
            for(var8 = 0; var7 < 8; ++var7) {
               if ((var3[var7] = (short)((var1[var8++] & 255) << 8 | var1[var8++] & 255)) == 0) {
                  ++var6;
                  if (var6 > var5) {
                     var5 = var6;
                     var4 = var7 - var6 + 1;
                  }
               } else {
                  var6 = 0;
               }
            }

            if (var0 == null) {
               var0 = new StringBuilder(var2 ? 41 : 39);
            }

            if (var2) {
               var0.append('[');
            }

            if (var5 <= 1) {
               var7 = 0;
            } else {
               for(var8 = 0; var8 < var4; ++var8) {
                  var0.append(Integer.toHexString(var3[var8] & '\uffff')).append(':');
               }

               if (var4 == 0) {
                  var0.append(':');
               }

               var0.append(':');
               var7 = var4 + var5;
            }

            for(var8 = var7; var8 < 8; ++var8) {
               var0.append(Integer.toHexString(var3[var8] & '\uffff'));
               if (var8 < 7) {
                  var0.append(':');
               }
            }

            if (var2) {
               var0.append(']');
            }
            break;
         default:
            StringUtils.convertBytesToHex(var0, var1);
      }

      return var0;
   }
}
