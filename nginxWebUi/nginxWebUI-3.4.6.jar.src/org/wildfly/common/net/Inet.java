/*      */ package org.wildfly.common.net;
/*      */ 
/*      */ import java.lang.reflect.Array;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.net.SocketException;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.AccessController;
/*      */ import java.util.Enumeration;
/*      */ import java.util.regex.Pattern;
/*      */ import org.wildfly.common.Assert;
/*      */ import org.wildfly.common._private.CommonMessages;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Inet
/*      */ {
/*   52 */   public static final Inet4Address INET4_ANY = getInet4Address(0, 0, 0, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   57 */   public static final Inet4Address INET4_LOOPBACK = getInet4Address(127, 0, 0, 1);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   62 */   public static final Inet4Address INET4_BROADCAST = getInet4Address(255, 255, 255, 255);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   67 */   public static final Inet6Address INET6_ANY = getInet6Address(0, 0, 0, 0, 0, 0, 0, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   72 */   public static final Inet6Address INET6_LOOPBACK = getInet6Address(0, 0, 0, 0, 0, 0, 0, 1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toOptimalString(InetAddress inetAddress) {
/*   82 */     Assert.checkNotNullParam("inetAddress", inetAddress);
/*   83 */     return (inetAddress instanceof Inet6Address) ? toOptimalStringV6(inetAddress.getAddress()) : inetAddress.getHostAddress();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toOptimalString(byte[] addressBytes) {
/*   93 */     Assert.checkNotNullParam("addressBytes", addressBytes);
/*   94 */     if (addressBytes.length == 4)
/*   95 */       return (addressBytes[0] & 0xFF) + "." + (addressBytes[1] & 0xFF) + "." + (addressBytes[2] & 0xFF) + "." + (addressBytes[3] & 0xFF); 
/*   96 */     if (addressBytes.length == 16) {
/*   97 */       return toOptimalStringV6(addressBytes);
/*      */     }
/*   99 */     throw CommonMessages.msg.invalidAddressBytes(addressBytes.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toURLString(InetAddress inetAddress, boolean useHostNameIfPresent) {
/*  112 */     Assert.checkNotNullParam("inetAddress", inetAddress);
/*  113 */     if (useHostNameIfPresent) {
/*  114 */       String hostName = getHostNameIfResolved(inetAddress);
/*  115 */       if (hostName != null) {
/*  116 */         if (inetAddress instanceof Inet6Address && isInet6Address(hostName)) {
/*  117 */           return "[" + hostName + "]";
/*      */         }
/*      */         
/*  120 */         return hostName;
/*      */       } 
/*      */     } 
/*      */     
/*  124 */     if (inetAddress instanceof Inet6Address) {
/*  125 */       return "[" + toOptimalString(inetAddress) + "]";
/*      */     }
/*  127 */     return toOptimalString(inetAddress);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toURLString(byte[] addressBytes) {
/*  138 */     Assert.checkNotNullParam("addressBytes", addressBytes);
/*  139 */     if (addressBytes.length == 4)
/*  140 */       return (addressBytes[0] & 0xFF) + "." + (addressBytes[1] & 0xFF) + "." + (addressBytes[2] & 0xFF) + "." + (addressBytes[3] & 0xFF); 
/*  141 */     if (addressBytes.length == 16) {
/*  142 */       return "[" + toOptimalStringV6(addressBytes) + "]";
/*      */     }
/*  144 */     throw CommonMessages.msg.invalidAddressBytes(addressBytes.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet6Address toInet6Address(InetAddress inetAddress) {
/*  156 */     if (inetAddress instanceof Inet6Address) {
/*  157 */       return (Inet6Address)inetAddress;
/*      */     }
/*  159 */     assert inetAddress instanceof Inet4Address;
/*  160 */     byte[] addr = new byte[16];
/*  161 */     addr[11] = -1; addr[10] = -1;
/*  162 */     System.arraycopy(inetAddress.getAddress(), 0, addr, 12, 4);
/*      */     
/*      */     try {
/*  165 */       return Inet6Address.getByAddress(getHostNameIfResolved(inetAddress), addr, 0);
/*  166 */     } catch (UnknownHostException e) {
/*      */       
/*  168 */       throw new IllegalStateException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getHostNameIfResolved(InetAddress inetAddress) {
/*  180 */     Assert.checkNotNullParam("inetAddress", inetAddress);
/*  181 */     return getHostNameIfResolved(new InetSocketAddress(inetAddress, 0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getHostNameIfResolved(InetSocketAddress socketAddress) {
/*  191 */     Assert.checkNotNullParam("socketAddress", socketAddress);
/*  192 */     String hostString = socketAddress.getHostString();
/*  193 */     String toString = socketAddress.toString();
/*  194 */     int slash = toString.lastIndexOf('/');
/*  195 */     if (slash == 0)
/*      */     {
/*  197 */       return hostString.isEmpty() ? "" : null;
/*      */     }
/*  199 */     return hostString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetSocketAddress getResolved(URI uri, int defaultPort, Class<? extends InetAddress> addressType) throws UnknownHostException {
/*  212 */     Assert.checkNotNullParam("uri", uri);
/*  213 */     Assert.checkMinimumParameter("defaultPort", 1, defaultPort);
/*  214 */     Assert.checkMaximumParameter("defaultPort", 65535, defaultPort);
/*  215 */     Assert.checkNotNullParam("addressType", addressType);
/*  216 */     InetAddress resolved = getResolvedInetAddress(uri, (Class)addressType);
/*  217 */     if (resolved == null) {
/*  218 */       return null;
/*      */     }
/*  220 */     int uriPort = uri.getPort();
/*  221 */     return (uriPort != -1) ? new InetSocketAddress(resolved, uriPort) : new InetSocketAddress(resolved, defaultPort);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetSocketAddress getResolved(URI uri, int defaultPort) throws UnknownHostException {
/*  233 */     return getResolved(uri, defaultPort, InetAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends InetAddress> T getResolvedInetAddress(URI uri, Class<T> addressType) throws UnknownHostException {
/*  245 */     String uriHost = uri.getHost();
/*  246 */     if (uriHost == null) {
/*  247 */       return null;
/*      */     }
/*  249 */     int length = uriHost.length();
/*  250 */     if (length == 0) {
/*  251 */       return null;
/*      */     }
/*  253 */     return getAddressByNameAndType(uriHost, addressType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetAddress getResolvedInetAddress(URI uri) throws UnknownHostException {
/*  264 */     return getResolvedInetAddress(uri, InetAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetSocketAddress getResolved(InetSocketAddress address) throws UnknownHostException {
/*  275 */     return getResolved(address, InetAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetSocketAddress getResolved(InetSocketAddress address, Class<? extends InetAddress> addressType) throws UnknownHostException {
/*  288 */     Assert.checkNotNullParam("address", address);
/*  289 */     Assert.checkNotNullParam("addressType", addressType);
/*  290 */     if (!address.isUnresolved()) {
/*  291 */       if (!addressType.isInstance(address.getAddress()))
/*      */       {
/*  293 */         throw new UnknownHostException(address.getHostString());
/*      */       }
/*  295 */       return address;
/*      */     } 
/*  297 */     return new InetSocketAddress(getAddressByNameAndType(address.getHostString(), (Class)addressType), address.getPort());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends InetAddress> T getAddressByNameAndType(String hostName, Class<T> addressType) throws UnknownHostException {
/*  310 */     Assert.checkNotNullParam("hostName", hostName);
/*  311 */     Assert.checkNotNullParam("addressType", addressType);
/*  312 */     if (addressType == InetAddress.class) {
/*  313 */       return addressType.cast(InetAddress.getByName(hostName));
/*      */     }
/*  315 */     for (InetAddress inetAddress : InetAddress.getAllByName(hostName)) {
/*  316 */       if (addressType.isInstance(inetAddress)) {
/*  317 */         return addressType.cast(inetAddress);
/*      */       }
/*      */     } 
/*      */     
/*  321 */     throw new UnknownHostException(hostName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends InetAddress> T[] getAllAddressesByNameAndType(String hostName, Class<T> addressType) throws UnknownHostException {
/*  335 */     Assert.checkNotNullParam("hostName", hostName);
/*  336 */     Assert.checkNotNullParam("addressType", addressType);
/*  337 */     if (addressType == InetAddress.class)
/*      */     {
/*  339 */       return (T[])InetAddress.getAllByName(hostName);
/*      */     }
/*  341 */     InetAddress[] addresses = InetAddress.getAllByName(hostName);
/*  342 */     int length = addresses.length;
/*  343 */     int count = 0;
/*  344 */     for (InetAddress inetAddress : addresses) {
/*  345 */       if (addressType.isInstance(inetAddress)) {
/*  346 */         count++;
/*      */       }
/*      */     } 
/*  349 */     if (count == 0)
/*      */     {
/*  351 */       throw new UnknownHostException(hostName);
/*      */     }
/*  353 */     InetAddress[] arrayOfInetAddress1 = (InetAddress[])Array.newInstance(addressType, count);
/*  354 */     if (count == length) {
/*      */       
/*  356 */       System.arraycopy(addresses, 0, arrayOfInetAddress1, 0, length);
/*      */     } else {
/*  358 */       int idx = 0;
/*  359 */       for (InetAddress inetAddress : addresses) {
/*  360 */         if (addressType.isInstance(inetAddress)) {
/*  361 */           arrayOfInetAddress1[idx] = (InetAddress)addressType.cast(inetAddress);
/*      */         }
/*      */       } 
/*      */     } 
/*  365 */     return (T[])arrayOfInetAddress1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet4Address getInet4Address(int s1, int s2, int s3, int s4) {
/*  378 */     byte[] bytes = new byte[4];
/*  379 */     Assert.checkMinimumParameter("s1", 0, s1);
/*  380 */     Assert.checkMaximumParameter("s1", 255, s1);
/*  381 */     Assert.checkMinimumParameter("s2", 0, s2);
/*  382 */     Assert.checkMaximumParameter("s2", 255, s2);
/*  383 */     Assert.checkMinimumParameter("s3", 0, s3);
/*  384 */     Assert.checkMaximumParameter("s3", 255, s3);
/*  385 */     Assert.checkMinimumParameter("s4", 0, s4);
/*  386 */     Assert.checkMaximumParameter("s4", 255, s4);
/*  387 */     bytes[0] = (byte)s1;
/*  388 */     bytes[1] = (byte)s2;
/*  389 */     bytes[2] = (byte)s3;
/*  390 */     bytes[3] = (byte)s4;
/*      */     try {
/*  392 */       return (Inet4Address)InetAddress.getByAddress(s1 + "." + s2 + "." + s3 + "." + s4, bytes);
/*  393 */     } catch (UnknownHostException e) {
/*      */       
/*  395 */       throw new IllegalStateException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet6Address getInet6Address(int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8) {
/*  413 */     byte[] bytes = new byte[16];
/*  414 */     Assert.checkMinimumParameter("s1", 0, s1);
/*  415 */     Assert.checkMaximumParameter("s1", 65535, s1);
/*  416 */     Assert.checkMinimumParameter("s2", 0, s2);
/*  417 */     Assert.checkMaximumParameter("s2", 65535, s2);
/*  418 */     Assert.checkMinimumParameter("s3", 0, s3);
/*  419 */     Assert.checkMaximumParameter("s3", 65535, s3);
/*  420 */     Assert.checkMinimumParameter("s4", 0, s4);
/*  421 */     Assert.checkMaximumParameter("s4", 65535, s4);
/*  422 */     Assert.checkMinimumParameter("s5", 0, s5);
/*  423 */     Assert.checkMaximumParameter("s5", 65535, s5);
/*  424 */     Assert.checkMinimumParameter("s6", 0, s6);
/*  425 */     Assert.checkMaximumParameter("s6", 65535, s6);
/*  426 */     Assert.checkMinimumParameter("s7", 0, s7);
/*  427 */     Assert.checkMaximumParameter("s7", 65535, s7);
/*  428 */     Assert.checkMinimumParameter("s8", 0, s8);
/*  429 */     Assert.checkMaximumParameter("s8", 65535, s8);
/*  430 */     bytes[0] = (byte)(s1 >> 8);
/*  431 */     bytes[1] = (byte)s1;
/*  432 */     bytes[2] = (byte)(s2 >> 8);
/*  433 */     bytes[3] = (byte)s2;
/*  434 */     bytes[4] = (byte)(s3 >> 8);
/*  435 */     bytes[5] = (byte)s3;
/*  436 */     bytes[6] = (byte)(s4 >> 8);
/*  437 */     bytes[7] = (byte)s4;
/*  438 */     bytes[8] = (byte)(s5 >> 8);
/*  439 */     bytes[9] = (byte)s5;
/*  440 */     bytes[10] = (byte)(s6 >> 8);
/*  441 */     bytes[11] = (byte)s6;
/*  442 */     bytes[12] = (byte)(s7 >> 8);
/*  443 */     bytes[13] = (byte)s7;
/*  444 */     bytes[14] = (byte)(s8 >> 8);
/*  445 */     bytes[15] = (byte)s8;
/*      */     try {
/*  447 */       return Inet6Address.getByAddress(toOptimalStringV6(bytes), bytes, 0);
/*  448 */     } catch (UnknownHostException e) {
/*      */       
/*  450 */       throw new IllegalStateException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isInet6Address(String address) {
/*  461 */     return (parseInet6AddressToBytes(address) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet6Address parseInet6Address(String address) {
/*  471 */     return parseInet6Address(address, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet6Address parseInet6Address(String address, String hostName) {
/*      */     Inet6Address inetAddress;
/*  483 */     byte[] bytes = parseInet6AddressToBytes(address);
/*  484 */     if (bytes == null) {
/*  485 */       return null;
/*      */     }
/*  487 */     int scopeId = 0;
/*      */     
/*      */     try {
/*  490 */       inetAddress = Inet6Address.getByAddress((hostName == null) ? toOptimalStringV6(bytes) : hostName, bytes, 0);
/*  491 */     } catch (UnknownHostException e) {
/*      */       
/*  493 */       throw new IllegalStateException(e);
/*      */     } 
/*  495 */     int pctIdx = address.indexOf('%');
/*  496 */     if (pctIdx != -1) {
/*  497 */       scopeId = getScopeId(address.substring(pctIdx + 1), inetAddress);
/*  498 */       if (scopeId == 0)
/*      */       {
/*  500 */         return null;
/*      */       }
/*      */       try {
/*  503 */         inetAddress = Inet6Address.getByAddress((hostName == null) ? toOptimalStringV6(bytes) : hostName, bytes, scopeId);
/*  504 */       } catch (UnknownHostException e) {
/*      */         
/*  506 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     } 
/*  509 */     return inetAddress;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet6Address parseInet6AddressOrFail(String address) {
/*  520 */     Inet6Address result = parseInet6Address(address, null);
/*  521 */     if (result == null) throw CommonMessages.msg.invalidAddress(address); 
/*  522 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet6Address parseInet6AddressOrFail(String address, String hostName) {
/*  535 */     Inet6Address result = parseInet6Address(address, hostName);
/*  536 */     if (result == null) throw CommonMessages.msg.invalidAddress(address); 
/*  537 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isInet4Address(String address) {
/*  547 */     return (parseInet4AddressToBytes(address) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet4Address parseInet4Address(String address) {
/*  557 */     return parseInet4Address(address, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet4Address parseInet4Address(String address, String hostName) {
/*  569 */     byte[] bytes = parseInet4AddressToBytes(address);
/*  570 */     if (bytes == null) {
/*  571 */       return null;
/*      */     }
/*      */     try {
/*  574 */       return (Inet4Address)Inet4Address.getByAddress((hostName == null) ? toOptimalString(bytes) : hostName, bytes);
/*  575 */     } catch (UnknownHostException e) {
/*      */       
/*  577 */       throw new IllegalStateException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet4Address parseInet4AddressOrFail(String address) {
/*  589 */     Inet4Address result = parseInet4Address(address, null);
/*  590 */     if (result == null) throw CommonMessages.msg.invalidAddress(address); 
/*  591 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Inet4Address parseInet4AddressOrFail(String address, String hostName) {
/*  604 */     Inet4Address result = parseInet4Address(address, hostName);
/*  605 */     if (result == null) throw CommonMessages.msg.invalidAddress(address); 
/*  606 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetAddress parseInetAddress(String address) {
/*  616 */     return parseInetAddress(address, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetAddress parseInetAddress(String address, String hostName) {
/*  629 */     if (address.indexOf(':') != -1) {
/*  630 */       return parseInet6Address(address, hostName);
/*      */     }
/*  632 */     return parseInet4Address(address, hostName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetAddress parseInetAddressOrFail(String address) {
/*  644 */     InetAddress result = parseInetAddress(address, null);
/*  645 */     if (result == null) throw CommonMessages.msg.invalidAddress(address); 
/*  646 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InetAddress parseInetAddressOrFail(String address, String hostName) {
/*  659 */     InetAddress result = parseInetAddress(address, hostName);
/*  660 */     if (result == null) throw CommonMessages.msg.invalidAddress(address); 
/*  661 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CidrAddress parseCidrAddress(String address) {
/*  671 */     int mask, idx = address.indexOf('/');
/*  672 */     if (idx == -1) {
/*  673 */       return null;
/*      */     }
/*      */     
/*      */     try {
/*  677 */       mask = Integer.parseInt(address.substring(idx + 1));
/*  678 */     } catch (NumberFormatException e) {
/*  679 */       return null;
/*      */     } 
/*  681 */     byte[] addressBytes = parseInetAddressToBytes(address.substring(0, idx));
/*  682 */     if (addressBytes == null) {
/*  683 */       return null;
/*      */     }
/*      */     try {
/*  686 */       return CidrAddress.create(addressBytes, mask, false);
/*  687 */     } catch (IllegalArgumentException e) {
/*  688 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] parseInet6AddressToBytes(String address) {
/*      */     int skipIndex, skippedSegments;
/*  701 */     if (address == null || address.isEmpty()) {
/*  702 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  706 */     if (address.startsWith("[") && address.endsWith("]")) {
/*  707 */       address = address.substring(1, address.length() - 1);
/*      */     }
/*      */     
/*  710 */     int pctIdx = address.indexOf('%');
/*  711 */     if (pctIdx != -1) {
/*  712 */       address = address.substring(0, pctIdx);
/*      */     }
/*      */     
/*  715 */     String[] segments = address.split(":", 10);
/*      */ 
/*      */     
/*  718 */     if (segments.length > 9 || segments.length < 3) {
/*  719 */       return null;
/*      */     }
/*      */     
/*  722 */     if (segments[0].length() == 0 && segments[1].length() != 0) {
/*  723 */       return null;
/*      */     }
/*      */     
/*  726 */     if (segments[segments.length - 1].length() == 0 && segments[segments.length - 2].length() != 0) {
/*  727 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  731 */     for (int i = 0; i < segments.length; i++) {
/*  732 */       for (int charIdx = 0; charIdx < segments[i].length(); charIdx++) {
/*  733 */         char c = segments[i].charAt(charIdx);
/*  734 */         if (c == '.' && i != segments.length - 1)
/*  735 */           return null; 
/*  736 */         if (c != '.' && c != ':' && Character.digit(c, 16) == -1) {
/*  737 */           return null;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  743 */     int emptyIndex = -1;
/*  744 */     for (int j = 0; j < segments.length - 1; j++) {
/*  745 */       if (segments[j].length() == 0) {
/*  746 */         if (emptyIndex > 0)
/*  747 */           return null; 
/*  748 */         if (emptyIndex != 0) {
/*  749 */           emptyIndex = j;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  754 */     boolean containsIPv4 = segments[segments.length - 1].contains(".");
/*  755 */     int totalSegments = containsIPv4 ? 7 : 8;
/*  756 */     if (emptyIndex == -1 && segments.length != totalSegments) {
/*  757 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  763 */     boolean isDefaultRoute = (segments.length == 3 && segments[0].isEmpty() && segments[1].isEmpty() && segments[2].isEmpty());
/*  764 */     if (isDefaultRoute) {
/*  765 */       skipIndex = 0;
/*  766 */       skippedSegments = 8;
/*  767 */     } else if (segments[0].isEmpty() || segments[segments.length - 1].isEmpty()) {
/*      */       
/*  769 */       skipIndex = emptyIndex;
/*  770 */       skippedSegments = totalSegments - segments.length + 2;
/*  771 */     } else if (emptyIndex > -1) {
/*      */       
/*  773 */       skipIndex = emptyIndex;
/*  774 */       skippedSegments = totalSegments - segments.length + 1;
/*      */     } else {
/*      */       
/*  777 */       skipIndex = 0;
/*  778 */       skippedSegments = 0;
/*      */     } 
/*      */     
/*  781 */     ByteBuffer bytes = ByteBuffer.allocate(16);
/*      */     
/*      */     try {
/*      */       int k;
/*  785 */       for (k = 0; k < skipIndex; k++) {
/*  786 */         bytes.putShort(parseHexadecimal(segments[k]));
/*      */       }
/*      */       
/*  789 */       for (k = skipIndex; k < skipIndex + skippedSegments; k++) {
/*  790 */         bytes.putShort((short)0);
/*      */       }
/*      */       
/*  793 */       for (k = skipIndex + skippedSegments; k < totalSegments; k++) {
/*  794 */         int segmentIdx = segments.length - totalSegments - k;
/*  795 */         if (containsIPv4 && k == totalSegments - 1) {
/*      */           
/*  797 */           String[] ipV4Segments = segments[segmentIdx].split("\\.");
/*  798 */           if (ipV4Segments.length != 4) {
/*  799 */             return null;
/*      */           }
/*  801 */           for (int idxV4 = 0; idxV4 < 4; idxV4++) {
/*  802 */             bytes.put(parseDecimal(ipV4Segments[idxV4]));
/*      */           }
/*      */         } else {
/*  805 */           bytes.putShort(parseHexadecimal(segments[segmentIdx]));
/*      */         } 
/*      */       } 
/*      */       
/*  809 */       return bytes.array();
/*  810 */     } catch (NumberFormatException e) {
/*  811 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] parseInet4AddressToBytes(String address) {
/*  826 */     String[] segments = address.split("\\.", 5);
/*  827 */     if (segments.length != 4) {
/*  828 */       return null;
/*      */     }
/*      */     
/*  831 */     for (int i = 0; i < segments.length; i++) {
/*  832 */       if (segments[i].length() < 1) {
/*  833 */         return null;
/*      */       }
/*  835 */       for (int cidx = 0; cidx < segments[i].length(); cidx++) {
/*  836 */         if (Character.digit(segments[i].charAt(cidx), 10) < 0) {
/*  837 */           return null;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  842 */     byte[] bytes = new byte[4];
/*      */     try {
/*  844 */       for (int j = 0; j < segments.length; j++) {
/*  845 */         bytes[j] = parseDecimal(segments[j]);
/*      */       }
/*  847 */       return bytes;
/*  848 */     } catch (NumberFormatException e) {
/*  849 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] parseInetAddressToBytes(String address) {
/*  863 */     if (address.indexOf(':') != -1) {
/*  864 */       return parseInet6AddressToBytes(address);
/*      */     }
/*  866 */     return parseInet4AddressToBytes(address);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getScopeId(InetAddress address) {
/*  876 */     return (address instanceof Inet6Address) ? ((Inet6Address)address).getScopeId() : 0;
/*      */   }
/*      */   
/*  879 */   private static final Pattern NUMERIC = Pattern.compile("\\d+");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getScopeId(String scopeName) {
/*  889 */     return getScopeId(scopeName, (InetAddress)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getScopeId(String scopeName, InetAddress compareWith) {
/*  901 */     Assert.checkNotNullParam("scopeName", scopeName);
/*  902 */     if (NUMERIC.matcher(scopeName).matches())
/*  903 */       try { return Integer.parseInt(scopeName); }
/*  904 */       catch (NumberFormatException ignored)
/*  905 */       { return 0; }
/*      */        
/*  907 */     NetworkInterface ni = findInterfaceWithScopeId(scopeName);
/*  908 */     if (ni == null) return 0; 
/*  909 */     return getScopeId(ni, compareWith);
/*      */   }
/*      */   
/*      */   public static NetworkInterface findInterfaceWithScopeId(String scopeName) {
/*      */     Enumeration<NetworkInterface> enumeration;
/*      */     try {
/*  915 */       enumeration = NetworkInterface.getNetworkInterfaces();
/*  916 */     } catch (SocketException ignored) {
/*  917 */       return null;
/*      */     } 
/*  919 */     while (enumeration.hasMoreElements()) {
/*  920 */       NetworkInterface net = enumeration.nextElement();
/*  921 */       if (net.getName().equals(scopeName)) {
/*  922 */         return net;
/*      */       }
/*      */     } 
/*  925 */     return null;
/*      */   }
/*      */   
/*      */   public static int getScopeId(NetworkInterface networkInterface) {
/*  929 */     return getScopeId(networkInterface, (InetAddress)null);
/*      */   }
/*      */   
/*      */   public static int getScopeId(NetworkInterface networkInterface, InetAddress compareWith) {
/*  933 */     Assert.checkNotNullParam("networkInterface", networkInterface);
/*  934 */     Inet6Address cw6 = (compareWith instanceof Inet6Address) ? (Inet6Address)compareWith : null;
/*  935 */     Inet6Address address = AccessController.<Inet6Address>doPrivileged(() -> {
/*      */           Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
/*      */           
/*      */           while (addresses.hasMoreElements()) {
/*      */             InetAddress a = addresses.nextElement();
/*      */             
/*      */             if (a instanceof Inet6Address) {
/*      */               Inet6Address a6 = (Inet6Address)a;
/*      */               
/*      */               if (cw6 == null || (a6.isLinkLocalAddress() == cw6.isLinkLocalAddress() && a6.isSiteLocalAddress() == cw6.isSiteLocalAddress())) {
/*      */                 return a6;
/*      */               }
/*      */             } 
/*      */           } 
/*      */           return null;
/*      */         });
/*  951 */     return (address == null) ? 0 : address.getScopeId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static URI getURIFromAddress(String scheme, InetSocketAddress socketAddress, int defaultPort) throws URISyntaxException {
/*  964 */     String host = getHostNameIfResolved(socketAddress);
/*  965 */     if (isInet6Address(host)) {
/*  966 */       host = '[' + host + ']';
/*      */     }
/*  968 */     int port = socketAddress.getPort();
/*  969 */     return new URI(scheme, null, host, (port == defaultPort) ? -1 : port, null, null, null);
/*      */   }
/*      */   
/*      */   private static byte parseDecimal(String number) {
/*  973 */     int i = Integer.parseInt(number);
/*  974 */     if (i < 0 || i > 255) {
/*  975 */       throw new NumberFormatException();
/*      */     }
/*  977 */     return (byte)i;
/*      */   }
/*      */   
/*      */   private static short parseHexadecimal(String hexNumber) {
/*  981 */     int i = Integer.parseInt(hexNumber, 16);
/*  982 */     if (i > 65535) {
/*  983 */       throw new NumberFormatException();
/*      */     }
/*  985 */     return (short)i;
/*      */   }
/*      */   
/*      */   private static String toOptimalStringV6(byte[] bytes) {
/*  989 */     int[] segments = new int[8];
/*  990 */     for (int i = 0; i < 8; i++) {
/*  991 */       segments[i] = (bytes[i << 1] & 0xFF) << 8 | bytes[(i << 1) + 1] & 0xFF;
/*      */     }
/*      */     
/*  994 */     StringBuilder b = new StringBuilder();
/*  995 */     for (int j = 0; j < 8; j++) {
/*  996 */       if (segments[j] == 0) {
/*  997 */         if (j == 7) {
/*  998 */           b.append('0');
/*      */         } else {
/*      */           
/* 1001 */           j++;
/* 1002 */           if (segments[j] == 0) {
/*      */             
/* 1004 */             b.append(':').append(':');
/* 1005 */             for (; ++j < 8; j++) {
/* 1006 */               if (segments[j] == 65535 && b.length() == 2) {
/* 1007 */                 b.append("ffff");
/* 1008 */                 if (j == 5) {
/*      */                   
/* 1010 */                   b.append(':').append(bytes[12] & 0xFF).append('.').append(bytes[13] & 0xFF).append('.').append(bytes[14] & 0xFF).append('.').append(bytes[15] & 0xFF);
/* 1011 */                   j = 8;
/* 1012 */                 } else if (j == 4 && segments[5] == 0) {
/*      */                   
/* 1014 */                   b.append(":0:").append(bytes[12] & 0xFF).append('.').append(bytes[13] & 0xFF).append('.').append(bytes[14] & 0xFF).append('.').append(bytes[15] & 0xFF);
/* 1015 */                   j = 8;
/*      */                 } else {
/*      */                   
/* 1018 */                   for (; ++j < 8; j++) {
/* 1019 */                     b.append(':').append(Integer.toHexString(segments[j]));
/*      */                   }
/*      */                 } 
/* 1022 */               } else if (segments[j] != 0) {
/*      */                 
/* 1024 */                 b.append(Integer.toHexString(segments[j]));
/* 1025 */                 for (; ++j < 8; j++) {
/* 1026 */                   b.append(':').append(Integer.toHexString(segments[j]));
/*      */                 }
/*      */               } 
/*      */             } 
/*      */           } else {
/*      */             
/* 1032 */             if (j > 1) b.append(':'); 
/* 1033 */             b.append('0').append(':').append(Integer.toHexString(segments[j]));
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1037 */         if (j > 0) b.append(':'); 
/* 1038 */         b.append(Integer.toHexString(segments[j]));
/*      */       } 
/*      */     } 
/* 1041 */     return b.toString();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\Inet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */