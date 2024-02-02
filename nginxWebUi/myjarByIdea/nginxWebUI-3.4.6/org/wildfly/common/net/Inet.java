package org.wildfly.common.net;

import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.regex.Pattern;
import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

public final class Inet {
   public static final Inet4Address INET4_ANY = getInet4Address(0, 0, 0, 0);
   public static final Inet4Address INET4_LOOPBACK = getInet4Address(127, 0, 0, 1);
   public static final Inet4Address INET4_BROADCAST = getInet4Address(255, 255, 255, 255);
   public static final Inet6Address INET6_ANY = getInet6Address(0, 0, 0, 0, 0, 0, 0, 0);
   public static final Inet6Address INET6_LOOPBACK = getInet6Address(0, 0, 0, 0, 0, 0, 0, 1);
   private static final Pattern NUMERIC = Pattern.compile("\\d+");

   private Inet() {
   }

   public static String toOptimalString(InetAddress inetAddress) {
      Assert.checkNotNullParam("inetAddress", inetAddress);
      return inetAddress instanceof Inet6Address ? toOptimalStringV6(inetAddress.getAddress()) : inetAddress.getHostAddress();
   }

   public static String toOptimalString(byte[] addressBytes) {
      Assert.checkNotNullParam("addressBytes", addressBytes);
      if (addressBytes.length == 4) {
         return (addressBytes[0] & 255) + "." + (addressBytes[1] & 255) + "." + (addressBytes[2] & 255) + "." + (addressBytes[3] & 255);
      } else if (addressBytes.length == 16) {
         return toOptimalStringV6(addressBytes);
      } else {
         throw CommonMessages.msg.invalidAddressBytes(addressBytes.length);
      }
   }

   public static String toURLString(InetAddress inetAddress, boolean useHostNameIfPresent) {
      Assert.checkNotNullParam("inetAddress", inetAddress);
      if (useHostNameIfPresent) {
         String hostName = getHostNameIfResolved(inetAddress);
         if (hostName != null) {
            if (inetAddress instanceof Inet6Address && isInet6Address(hostName)) {
               return "[" + hostName + "]";
            }

            return hostName;
         }
      }

      return inetAddress instanceof Inet6Address ? "[" + toOptimalString(inetAddress) + "]" : toOptimalString(inetAddress);
   }

   public static String toURLString(byte[] addressBytes) {
      Assert.checkNotNullParam("addressBytes", addressBytes);
      if (addressBytes.length == 4) {
         return (addressBytes[0] & 255) + "." + (addressBytes[1] & 255) + "." + (addressBytes[2] & 255) + "." + (addressBytes[3] & 255);
      } else if (addressBytes.length == 16) {
         return "[" + toOptimalStringV6(addressBytes) + "]";
      } else {
         throw CommonMessages.msg.invalidAddressBytes(addressBytes.length);
      }
   }

   public static Inet6Address toInet6Address(InetAddress inetAddress) {
      if (inetAddress instanceof Inet6Address) {
         return (Inet6Address)inetAddress;
      } else {
         assert inetAddress instanceof Inet4Address;

         byte[] addr = new byte[16];
         addr[10] = addr[11] = -1;
         System.arraycopy(inetAddress.getAddress(), 0, addr, 12, 4);

         try {
            return Inet6Address.getByAddress(getHostNameIfResolved(inetAddress), addr, 0);
         } catch (UnknownHostException var3) {
            throw new IllegalStateException(var3);
         }
      }
   }

   public static String getHostNameIfResolved(InetAddress inetAddress) {
      Assert.checkNotNullParam("inetAddress", inetAddress);
      return getHostNameIfResolved(new InetSocketAddress(inetAddress, 0));
   }

   public static String getHostNameIfResolved(InetSocketAddress socketAddress) {
      Assert.checkNotNullParam("socketAddress", socketAddress);
      String hostString = socketAddress.getHostString();
      String toString = socketAddress.toString();
      int slash = toString.lastIndexOf(47);
      if (slash == 0) {
         return hostString.isEmpty() ? "" : null;
      } else {
         return hostString;
      }
   }

   public static InetSocketAddress getResolved(URI uri, int defaultPort, Class<? extends InetAddress> addressType) throws UnknownHostException {
      Assert.checkNotNullParam("uri", uri);
      Assert.checkMinimumParameter("defaultPort", 1, defaultPort);
      Assert.checkMaximumParameter("defaultPort", 65535, defaultPort);
      Assert.checkNotNullParam("addressType", addressType);
      InetAddress resolved = getResolvedInetAddress(uri, addressType);
      if (resolved == null) {
         return null;
      } else {
         int uriPort = uri.getPort();
         return uriPort != -1 ? new InetSocketAddress(resolved, uriPort) : new InetSocketAddress(resolved, defaultPort);
      }
   }

   public static InetSocketAddress getResolved(URI uri, int defaultPort) throws UnknownHostException {
      return getResolved(uri, defaultPort, InetAddress.class);
   }

   public static <T extends InetAddress> T getResolvedInetAddress(URI uri, Class<T> addressType) throws UnknownHostException {
      String uriHost = uri.getHost();
      if (uriHost == null) {
         return null;
      } else {
         int length = uriHost.length();
         return length == 0 ? null : getAddressByNameAndType(uriHost, addressType);
      }
   }

   public static InetAddress getResolvedInetAddress(URI uri) throws UnknownHostException {
      return getResolvedInetAddress(uri, InetAddress.class);
   }

   public static InetSocketAddress getResolved(InetSocketAddress address) throws UnknownHostException {
      return getResolved(address, InetAddress.class);
   }

   public static InetSocketAddress getResolved(InetSocketAddress address, Class<? extends InetAddress> addressType) throws UnknownHostException {
      Assert.checkNotNullParam("address", address);
      Assert.checkNotNullParam("addressType", addressType);
      if (!address.isUnresolved()) {
         if (!addressType.isInstance(address.getAddress())) {
            throw new UnknownHostException(address.getHostString());
         } else {
            return address;
         }
      } else {
         return new InetSocketAddress(getAddressByNameAndType(address.getHostString(), addressType), address.getPort());
      }
   }

   public static <T extends InetAddress> T getAddressByNameAndType(String hostName, Class<T> addressType) throws UnknownHostException {
      Assert.checkNotNullParam("hostName", hostName);
      Assert.checkNotNullParam("addressType", addressType);
      if (addressType == InetAddress.class) {
         return (InetAddress)addressType.cast(InetAddress.getByName(hostName));
      } else {
         InetAddress[] var2 = InetAddress.getAllByName(hostName);
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            InetAddress inetAddress = var2[var4];
            if (addressType.isInstance(inetAddress)) {
               return (InetAddress)addressType.cast(inetAddress);
            }
         }

         throw new UnknownHostException(hostName);
      }
   }

   public static <T extends InetAddress> T[] getAllAddressesByNameAndType(String hostName, Class<T> addressType) throws UnknownHostException {
      Assert.checkNotNullParam("hostName", hostName);
      Assert.checkNotNullParam("addressType", addressType);
      if (addressType == InetAddress.class) {
         return InetAddress.getAllByName(hostName);
      } else {
         InetAddress[] addresses = InetAddress.getAllByName(hostName);
         int length = addresses.length;
         int count = 0;
         InetAddress[] newArray = addresses;
         int var6 = addresses.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            InetAddress inetAddress = newArray[var7];
            if (addressType.isInstance(inetAddress)) {
               ++count;
            }
         }

         if (count == 0) {
            throw new UnknownHostException(hostName);
         } else {
            newArray = (InetAddress[])Array.newInstance(addressType, count);
            if (count == length) {
               System.arraycopy(addresses, 0, newArray, 0, length);
            } else {
               int idx = 0;
               InetAddress[] var12 = addresses;
               int var13 = addresses.length;

               for(int var9 = 0; var9 < var13; ++var9) {
                  InetAddress inetAddress = var12[var9];
                  if (addressType.isInstance(inetAddress)) {
                     newArray[idx] = (InetAddress)addressType.cast(inetAddress);
                  }
               }
            }

            return newArray;
         }
      }
   }

   public static Inet4Address getInet4Address(int s1, int s2, int s3, int s4) {
      byte[] bytes = new byte[4];
      Assert.checkMinimumParameter("s1", 0, s1);
      Assert.checkMaximumParameter("s1", 255, s1);
      Assert.checkMinimumParameter("s2", 0, s2);
      Assert.checkMaximumParameter("s2", 255, s2);
      Assert.checkMinimumParameter("s3", 0, s3);
      Assert.checkMaximumParameter("s3", 255, s3);
      Assert.checkMinimumParameter("s4", 0, s4);
      Assert.checkMaximumParameter("s4", 255, s4);
      bytes[0] = (byte)s1;
      bytes[1] = (byte)s2;
      bytes[2] = (byte)s3;
      bytes[3] = (byte)s4;

      try {
         return (Inet4Address)InetAddress.getByAddress(s1 + "." + s2 + "." + s3 + "." + s4, bytes);
      } catch (UnknownHostException var6) {
         throw new IllegalStateException(var6);
      }
   }

   public static Inet6Address getInet6Address(int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8) {
      byte[] bytes = new byte[16];
      Assert.checkMinimumParameter("s1", 0, s1);
      Assert.checkMaximumParameter("s1", 65535, s1);
      Assert.checkMinimumParameter("s2", 0, s2);
      Assert.checkMaximumParameter("s2", 65535, s2);
      Assert.checkMinimumParameter("s3", 0, s3);
      Assert.checkMaximumParameter("s3", 65535, s3);
      Assert.checkMinimumParameter("s4", 0, s4);
      Assert.checkMaximumParameter("s4", 65535, s4);
      Assert.checkMinimumParameter("s5", 0, s5);
      Assert.checkMaximumParameter("s5", 65535, s5);
      Assert.checkMinimumParameter("s6", 0, s6);
      Assert.checkMaximumParameter("s6", 65535, s6);
      Assert.checkMinimumParameter("s7", 0, s7);
      Assert.checkMaximumParameter("s7", 65535, s7);
      Assert.checkMinimumParameter("s8", 0, s8);
      Assert.checkMaximumParameter("s8", 65535, s8);
      bytes[0] = (byte)(s1 >> 8);
      bytes[1] = (byte)s1;
      bytes[2] = (byte)(s2 >> 8);
      bytes[3] = (byte)s2;
      bytes[4] = (byte)(s3 >> 8);
      bytes[5] = (byte)s3;
      bytes[6] = (byte)(s4 >> 8);
      bytes[7] = (byte)s4;
      bytes[8] = (byte)(s5 >> 8);
      bytes[9] = (byte)s5;
      bytes[10] = (byte)(s6 >> 8);
      bytes[11] = (byte)s6;
      bytes[12] = (byte)(s7 >> 8);
      bytes[13] = (byte)s7;
      bytes[14] = (byte)(s8 >> 8);
      bytes[15] = (byte)s8;

      try {
         return Inet6Address.getByAddress(toOptimalStringV6(bytes), bytes, 0);
      } catch (UnknownHostException var10) {
         throw new IllegalStateException(var10);
      }
   }

   public static boolean isInet6Address(String address) {
      return parseInet6AddressToBytes(address) != null;
   }

   public static Inet6Address parseInet6Address(String address) {
      return parseInet6Address(address, (String)null);
   }

   public static Inet6Address parseInet6Address(String address, String hostName) {
      byte[] bytes = parseInet6AddressToBytes(address);
      if (bytes == null) {
         return null;
      } else {
         int scopeId = false;

         Inet6Address inetAddress;
         try {
            inetAddress = Inet6Address.getByAddress(hostName == null ? toOptimalStringV6(bytes) : hostName, bytes, 0);
         } catch (UnknownHostException var8) {
            throw new IllegalStateException(var8);
         }

         int pctIdx = address.indexOf(37);
         if (pctIdx != -1) {
            int scopeId = getScopeId((String)address.substring(pctIdx + 1), inetAddress);
            if (scopeId == 0) {
               return null;
            }

            try {
               inetAddress = Inet6Address.getByAddress(hostName == null ? toOptimalStringV6(bytes) : hostName, bytes, scopeId);
            } catch (UnknownHostException var7) {
               throw new IllegalStateException(var7);
            }
         }

         return inetAddress;
      }
   }

   public static Inet6Address parseInet6AddressOrFail(String address) {
      Inet6Address result = parseInet6Address(address, (String)null);
      if (result == null) {
         throw CommonMessages.msg.invalidAddress(address);
      } else {
         return result;
      }
   }

   public static Inet6Address parseInet6AddressOrFail(String address, String hostName) {
      Inet6Address result = parseInet6Address(address, hostName);
      if (result == null) {
         throw CommonMessages.msg.invalidAddress(address);
      } else {
         return result;
      }
   }

   public static boolean isInet4Address(String address) {
      return parseInet4AddressToBytes(address) != null;
   }

   public static Inet4Address parseInet4Address(String address) {
      return parseInet4Address(address, (String)null);
   }

   public static Inet4Address parseInet4Address(String address, String hostName) {
      byte[] bytes = parseInet4AddressToBytes(address);
      if (bytes == null) {
         return null;
      } else {
         try {
            return (Inet4Address)Inet4Address.getByAddress(hostName == null ? toOptimalString(bytes) : hostName, bytes);
         } catch (UnknownHostException var4) {
            throw new IllegalStateException(var4);
         }
      }
   }

   public static Inet4Address parseInet4AddressOrFail(String address) {
      Inet4Address result = parseInet4Address(address, (String)null);
      if (result == null) {
         throw CommonMessages.msg.invalidAddress(address);
      } else {
         return result;
      }
   }

   public static Inet4Address parseInet4AddressOrFail(String address, String hostName) {
      Inet4Address result = parseInet4Address(address, hostName);
      if (result == null) {
         throw CommonMessages.msg.invalidAddress(address);
      } else {
         return result;
      }
   }

   public static InetAddress parseInetAddress(String address) {
      return parseInetAddress(address, (String)null);
   }

   public static InetAddress parseInetAddress(String address, String hostName) {
      return (InetAddress)(address.indexOf(58) != -1 ? parseInet6Address(address, hostName) : parseInet4Address(address, hostName));
   }

   public static InetAddress parseInetAddressOrFail(String address) {
      InetAddress result = parseInetAddress(address, (String)null);
      if (result == null) {
         throw CommonMessages.msg.invalidAddress(address);
      } else {
         return result;
      }
   }

   public static InetAddress parseInetAddressOrFail(String address, String hostName) {
      InetAddress result = parseInetAddress(address, hostName);
      if (result == null) {
         throw CommonMessages.msg.invalidAddress(address);
      } else {
         return result;
      }
   }

   public static CidrAddress parseCidrAddress(String address) {
      int idx = address.indexOf(47);
      if (idx == -1) {
         return null;
      } else {
         int mask;
         try {
            mask = Integer.parseInt(address.substring(idx + 1));
         } catch (NumberFormatException var6) {
            return null;
         }

         byte[] addressBytes = parseInetAddressToBytes(address.substring(0, idx));
         if (addressBytes == null) {
            return null;
         } else {
            try {
               return CidrAddress.create(addressBytes, mask, false);
            } catch (IllegalArgumentException var5) {
               return null;
            }
         }
      }
   }

   public static byte[] parseInet6AddressToBytes(String address) {
      if (address != null && !address.isEmpty()) {
         if (address.startsWith("[") && address.endsWith("]")) {
            address = address.substring(1, address.length() - 1);
         }

         int pctIdx = address.indexOf(37);
         if (pctIdx != -1) {
            address = address.substring(0, pctIdx);
         }

         String[] segments = address.split(":", 10);
         if (segments.length <= 9 && segments.length >= 3) {
            if (segments[0].length() == 0 && segments[1].length() != 0) {
               return null;
            } else if (segments[segments.length - 1].length() == 0 && segments[segments.length - 2].length() != 0) {
               return null;
            } else {
               int emptyIndex;
               int i;
               int totalSegments;
               for(emptyIndex = 0; emptyIndex < segments.length; ++emptyIndex) {
                  for(i = 0; i < segments[emptyIndex].length(); ++i) {
                     totalSegments = segments[emptyIndex].charAt(i);
                     if (totalSegments == 46 && emptyIndex != segments.length - 1) {
                        return null;
                     }

                     if (totalSegments != 46 && totalSegments != 58 && Character.digit((char)totalSegments, 16) == -1) {
                        return null;
                     }
                  }
               }

               emptyIndex = -1;

               for(i = 0; i < segments.length - 1; ++i) {
                  if (segments[i].length() == 0) {
                     if (emptyIndex > 0) {
                        return null;
                     }

                     if (emptyIndex != 0) {
                        emptyIndex = i;
                     }
                  }
               }

               boolean containsIPv4 = segments[segments.length - 1].contains(".");
               totalSegments = containsIPv4 ? 7 : 8;
               if (emptyIndex == -1 && segments.length != totalSegments) {
                  return null;
               } else {
                  boolean isDefaultRoute = segments.length == 3 && segments[0].isEmpty() && segments[1].isEmpty() && segments[2].isEmpty();
                  int skipIndex;
                  int skippedSegments;
                  if (isDefaultRoute) {
                     skipIndex = 0;
                     skippedSegments = 8;
                  } else if (!segments[0].isEmpty() && !segments[segments.length - 1].isEmpty()) {
                     if (emptyIndex > -1) {
                        skipIndex = emptyIndex;
                        skippedSegments = totalSegments - segments.length + 1;
                     } else {
                        skipIndex = 0;
                        skippedSegments = 0;
                     }
                  } else {
                     skipIndex = emptyIndex;
                     skippedSegments = totalSegments - segments.length + 2;
                  }

                  ByteBuffer bytes = ByteBuffer.allocate(16);

                  try {
                     int i;
                     for(i = 0; i < skipIndex; ++i) {
                        bytes.putShort(parseHexadecimal(segments[i]));
                     }

                     for(i = skipIndex; i < skipIndex + skippedSegments; ++i) {
                        bytes.putShort((short)0);
                     }

                     for(i = skipIndex + skippedSegments; i < totalSegments; ++i) {
                        int segmentIdx = segments.length - (totalSegments - i);
                        if (containsIPv4 && i == totalSegments - 1) {
                           String[] ipV4Segments = segments[segmentIdx].split("\\.");
                           if (ipV4Segments.length != 4) {
                              return null;
                           }

                           for(int idxV4 = 0; idxV4 < 4; ++idxV4) {
                              bytes.put(parseDecimal(ipV4Segments[idxV4]));
                           }
                        } else {
                           bytes.putShort(parseHexadecimal(segments[segmentIdx]));
                        }
                     }

                     return bytes.array();
                  } catch (NumberFormatException var14) {
                     return null;
                  }
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static byte[] parseInet4AddressToBytes(String address) {
      String[] segments = address.split("\\.", 5);
      if (segments.length != 4) {
         return null;
      } else {
         int cidx;
         for(int i = 0; i < segments.length; ++i) {
            if (segments[i].length() < 1) {
               return null;
            }

            for(cidx = 0; cidx < segments[i].length(); ++cidx) {
               if (Character.digit(segments[i].charAt(cidx), 10) < 0) {
                  return null;
               }
            }
         }

         byte[] bytes = new byte[4];

         try {
            for(cidx = 0; cidx < segments.length; ++cidx) {
               bytes[cidx] = parseDecimal(segments[cidx]);
            }

            return bytes;
         } catch (NumberFormatException var4) {
            return null;
         }
      }
   }

   public static byte[] parseInetAddressToBytes(String address) {
      return address.indexOf(58) != -1 ? parseInet6AddressToBytes(address) : parseInet4AddressToBytes(address);
   }

   public static int getScopeId(InetAddress address) {
      return address instanceof Inet6Address ? ((Inet6Address)address).getScopeId() : 0;
   }

   public static int getScopeId(String scopeName) {
      return getScopeId((String)scopeName, (InetAddress)null);
   }

   public static int getScopeId(String scopeName, InetAddress compareWith) {
      Assert.checkNotNullParam("scopeName", scopeName);
      if (NUMERIC.matcher(scopeName).matches()) {
         try {
            return Integer.parseInt(scopeName);
         } catch (NumberFormatException var3) {
            return 0;
         }
      } else {
         NetworkInterface ni = findInterfaceWithScopeId(scopeName);
         return ni == null ? 0 : getScopeId(ni, compareWith);
      }
   }

   public static NetworkInterface findInterfaceWithScopeId(String scopeName) {
      Enumeration enumeration;
      try {
         enumeration = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var3) {
         return null;
      }

      NetworkInterface net;
      do {
         if (!enumeration.hasMoreElements()) {
            return null;
         }

         net = (NetworkInterface)enumeration.nextElement();
      } while(!net.getName().equals(scopeName));

      return net;
   }

   public static int getScopeId(NetworkInterface networkInterface) {
      return getScopeId((NetworkInterface)networkInterface, (InetAddress)null);
   }

   public static int getScopeId(NetworkInterface networkInterface, InetAddress compareWith) {
      Assert.checkNotNullParam("networkInterface", networkInterface);
      Inet6Address cw6 = compareWith instanceof Inet6Address ? (Inet6Address)compareWith : null;
      Inet6Address address = (Inet6Address)AccessController.doPrivileged(() -> {
         Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

         Inet6Address a6;
         do {
            InetAddress a;
            do {
               if (!addresses.hasMoreElements()) {
                  return null;
               }

               a = (InetAddress)addresses.nextElement();
            } while(!(a instanceof Inet6Address));

            a6 = (Inet6Address)a;
         } while(cw6 != null && (a6.isLinkLocalAddress() != cw6.isLinkLocalAddress() || a6.isSiteLocalAddress() != cw6.isSiteLocalAddress()));

         return a6;
      });
      return address == null ? 0 : address.getScopeId();
   }

   public static URI getURIFromAddress(String scheme, InetSocketAddress socketAddress, int defaultPort) throws URISyntaxException {
      String host = getHostNameIfResolved(socketAddress);
      if (isInet6Address(host)) {
         host = '[' + host + ']';
      }

      int port = socketAddress.getPort();
      return new URI(scheme, (String)null, host, port == defaultPort ? -1 : port, (String)null, (String)null, (String)null);
   }

   private static byte parseDecimal(String number) {
      int i = Integer.parseInt(number);
      if (i >= 0 && i <= 255) {
         return (byte)i;
      } else {
         throw new NumberFormatException();
      }
   }

   private static short parseHexadecimal(String hexNumber) {
      int i = Integer.parseInt(hexNumber, 16);
      if (i > 65535) {
         throw new NumberFormatException();
      } else {
         return (short)i;
      }
   }

   private static String toOptimalStringV6(byte[] bytes) {
      int[] segments = new int[8];

      for(int i = 0; i < 8; ++i) {
         segments[i] = (bytes[i << 1] & 255) << 8 | bytes[(i << 1) + 1] & 255;
      }

      StringBuilder b = new StringBuilder();

      for(int i = 0; i < 8; ++i) {
         if (segments[i] != 0) {
            if (i > 0) {
               b.append(':');
            }

            b.append(Integer.toHexString(segments[i]));
         } else if (i == 7) {
            b.append('0');
         } else {
            ++i;
            if (segments[i] != 0) {
               if (i > 1) {
                  b.append(':');
               }

               b.append('0').append(':').append(Integer.toHexString(segments[i]));
            } else {
               b.append(':').append(':');
               ++i;

               for(; i < 8; ++i) {
                  if (segments[i] == 65535 && b.length() == 2) {
                     b.append("ffff");
                     if (i == 5) {
                        b.append(':').append(bytes[12] & 255).append('.').append(bytes[13] & 255).append('.').append(bytes[14] & 255).append('.').append(bytes[15] & 255);
                        i = 8;
                     } else if (i == 4 && segments[5] == 0) {
                        b.append(":0:").append(bytes[12] & 255).append('.').append(bytes[13] & 255).append('.').append(bytes[14] & 255).append('.').append(bytes[15] & 255);
                        i = 8;
                     } else {
                        ++i;

                        while(i < 8) {
                           b.append(':').append(Integer.toHexString(segments[i]));
                           ++i;
                        }
                     }
                  } else if (segments[i] != 0) {
                     b.append(Integer.toHexString(segments[i]));
                     ++i;

                     while(i < 8) {
                        b.append(':').append(Integer.toHexString(segments[i]));
                        ++i;
                     }
                  }
               }
            }
         }
      }

      return b.toString();
   }
}
