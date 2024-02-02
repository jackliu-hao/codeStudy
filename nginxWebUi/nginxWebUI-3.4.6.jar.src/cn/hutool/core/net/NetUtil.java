/*     */ package cn.hutool.core.net;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.EnumerationIter;
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.JNDIUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.net.Authenticator;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.HttpCookie;
/*     */ import java.net.IDN;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attribute;
/*     */ import javax.naming.directory.Attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetUtil
/*     */ {
/*     */   public static final String LOCAL_IP = "127.0.0.1";
/*     */   public static String localhostName;
/*     */   public static final int PORT_RANGE_MIN = 1024;
/*     */   public static final int PORT_RANGE_MAX = 65535;
/*     */   
/*     */   public static String longToIpv4(long longIP) {
/*  72 */     return Ipv4Util.longToIpv4(longIP);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long ipv4ToLong(String strIP) {
/*  83 */     return Ipv4Util.ipv4ToLong(strIP);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigInteger ipv6ToBitInteger(String IPv6Str) {
/*     */     try {
/*  95 */       InetAddress address = InetAddress.getByName(IPv6Str);
/*  96 */       if (address instanceof java.net.Inet6Address) {
/*  97 */         return new BigInteger(1, address.getAddress());
/*     */       }
/*  99 */     } catch (UnknownHostException unknownHostException) {}
/*     */     
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String bigIntegerToIPv6(BigInteger bigInteger) {
/*     */     try {
/* 113 */       return InetAddress.getByAddress(bigInteger.toByteArray()).toString().substring(1);
/* 114 */     } catch (UnknownHostException ignore) {
/* 115 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isUsableLocalPort(int port) {
/* 127 */     if (false == isValidPort(port))
/*     */     {
/* 129 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 133 */     try (ServerSocket ss = new ServerSocket(port)) {
/* 134 */       ss.setReuseAddress(true);
/* 135 */     } catch (IOException ignored) {
/* 136 */       return false;
/*     */     } 
/*     */     
/* 139 */     try (DatagramSocket ds = new DatagramSocket(port)) {
/* 140 */       ds.setReuseAddress(true);
/* 141 */     } catch (IOException ignored) {
/* 142 */       return false;
/*     */     } 
/*     */     
/* 145 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidPort(int port) {
/* 157 */     return (port >= 0 && port <= 65535);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getUsableLocalPort() {
/* 169 */     return getUsableLocalPort(1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getUsableLocalPort(int minPort) {
/* 182 */     return getUsableLocalPort(minPort, 65535);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getUsableLocalPort(int minPort, int maxPort) {
/* 196 */     int maxPortExclude = maxPort + 1;
/*     */     
/* 198 */     for (int i = minPort; i < maxPortExclude; i++) {
/* 199 */       int randomPort = RandomUtil.randomInt(minPort, maxPortExclude);
/* 200 */       if (isUsableLocalPort(randomPort)) {
/* 201 */         return randomPort;
/*     */       }
/*     */     } 
/*     */     
/* 205 */     throw new UtilException("Could not find an available port in the range [{}, {}] after {} attempts", new Object[] { Integer.valueOf(minPort), Integer.valueOf(maxPort), Integer.valueOf(maxPort - minPort) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TreeSet<Integer> getUsableLocalPorts(int numRequested, int minPort, int maxPort) {
/* 219 */     TreeSet<Integer> availablePorts = new TreeSet<>();
/* 220 */     int attemptCount = 0;
/* 221 */     while (++attemptCount <= numRequested + 100 && availablePorts.size() < numRequested) {
/* 222 */       availablePorts.add(Integer.valueOf(getUsableLocalPort(minPort, maxPort)));
/*     */     }
/*     */     
/* 225 */     if (availablePorts.size() != numRequested) {
/* 226 */       throw new UtilException("Could not find {} available  ports in the range [{}, {}]", new Object[] { Integer.valueOf(numRequested), Integer.valueOf(minPort), Integer.valueOf(maxPort) });
/*     */     }
/*     */     
/* 229 */     return availablePorts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInnerIP(String ipAddress) {
/* 247 */     return Ipv4Util.isInnerIP(ipAddress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toAbsoluteUrl(String absoluteBasePath, String relativePath) {
/*     */     try {
/* 259 */       URL absoluteUrl = new URL(absoluteBasePath);
/* 260 */       return (new URL(absoluteUrl, relativePath)).toString();
/* 261 */     } catch (Exception e) {
/* 262 */       throw new UtilException(e, "To absolute url [{}] base [{}] error!", new Object[] { relativePath, absoluteBasePath });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String hideIpPart(String ip) {
/* 273 */     return StrUtil.builder(ip.length()).append(ip, 0, ip.lastIndexOf(".") + 1).append("*").toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String hideIpPart(long ip) {
/* 283 */     return hideIpPart(longToIpv4(ip));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InetSocketAddress buildInetSocketAddress(String host, int defaultPort) {
/*     */     String destHost;
/*     */     int port;
/* 296 */     if (StrUtil.isBlank(host)) {
/* 297 */       host = "127.0.0.1";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 302 */     int index = host.indexOf(":");
/* 303 */     if (index != -1) {
/*     */       
/* 305 */       destHost = host.substring(0, index);
/* 306 */       port = Integer.parseInt(host.substring(index + 1));
/*     */     } else {
/* 308 */       destHost = host;
/* 309 */       port = defaultPort;
/*     */     } 
/*     */     
/* 312 */     return new InetSocketAddress(destHost, port);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getIpByHost(String hostName) {
/*     */     try {
/* 323 */       return InetAddress.getByName(hostName).getHostAddress();
/* 324 */     } catch (UnknownHostException e) {
/* 325 */       return hostName;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NetworkInterface getNetworkInterface(String name) {
/*     */     Enumeration<NetworkInterface> networkInterfaces;
/*     */     try {
/* 339 */       networkInterfaces = NetworkInterface.getNetworkInterfaces();
/* 340 */     } catch (SocketException e) {
/* 341 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 345 */     while (networkInterfaces.hasMoreElements()) {
/* 346 */       NetworkInterface netInterface = networkInterfaces.nextElement();
/* 347 */       if (null != netInterface && name.equals(netInterface.getName())) {
/* 348 */         return netInterface;
/*     */       }
/*     */     } 
/*     */     
/* 352 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<NetworkInterface> getNetworkInterfaces() {
/*     */     Enumeration<NetworkInterface> networkInterfaces;
/*     */     try {
/* 364 */       networkInterfaces = NetworkInterface.getNetworkInterfaces();
/* 365 */     } catch (SocketException e) {
/* 366 */       return null;
/*     */     } 
/*     */     
/* 369 */     return CollUtil.addAll(new ArrayList(), networkInterfaces);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LinkedHashSet<String> localIpv4s() {
/* 379 */     LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof java.net.Inet4Address);
/*     */     
/* 381 */     return toIpList(localAddressList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LinkedHashSet<String> localIpv6s() {
/* 392 */     LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof java.net.Inet6Address);
/*     */     
/* 394 */     return toIpList(localAddressList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LinkedHashSet<String> toIpList(Set<InetAddress> addressList) {
/* 405 */     LinkedHashSet<String> ipSet = new LinkedHashSet<>();
/* 406 */     for (InetAddress address : addressList) {
/* 407 */       ipSet.add(address.getHostAddress());
/*     */     }
/*     */     
/* 410 */     return ipSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LinkedHashSet<String> localIps() {
/* 420 */     LinkedHashSet<InetAddress> localAddressList = localAddressList(null);
/* 421 */     return toIpList(localAddressList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {
/*     */     Enumeration<NetworkInterface> networkInterfaces;
/*     */     try {
/* 434 */       networkInterfaces = NetworkInterface.getNetworkInterfaces();
/* 435 */     } catch (SocketException e) {
/* 436 */       throw new UtilException(e);
/*     */     } 
/*     */     
/* 439 */     if (networkInterfaces == null) {
/* 440 */       throw new UtilException("Get network interface error!");
/*     */     }
/*     */     
/* 443 */     LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();
/*     */     
/* 445 */     while (networkInterfaces.hasMoreElements()) {
/* 446 */       NetworkInterface networkInterface = networkInterfaces.nextElement();
/* 447 */       Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
/* 448 */       while (inetAddresses.hasMoreElements()) {
/* 449 */         InetAddress inetAddress = inetAddresses.nextElement();
/* 450 */         if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
/* 451 */           ipSet.add(inetAddress);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 456 */     return ipSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getLocalhostStr() {
/* 470 */     InetAddress localhost = getLocalhost();
/* 471 */     if (null != localhost) {
/* 472 */       return localhost.getHostAddress();
/*     */     }
/* 474 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InetAddress getLocalhost() {
/* 493 */     LinkedHashSet<InetAddress> localAddressList = localAddressList(address -> 
/*     */         
/* 495 */         (false == address.isLoopbackAddress() && address instanceof java.net.Inet4Address));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 500 */     if (CollUtil.isNotEmpty(localAddressList)) {
/* 501 */       InetAddress address2 = null;
/* 502 */       for (InetAddress inetAddress : localAddressList) {
/* 503 */         if (false == inetAddress.isSiteLocalAddress())
/*     */         {
/* 505 */           return inetAddress; } 
/* 506 */         if (null == address2) {
/* 507 */           address2 = inetAddress;
/*     */         }
/*     */       } 
/*     */       
/* 511 */       if (null != address2) {
/* 512 */         return address2;
/*     */       }
/*     */     } 
/*     */     
/*     */     try {
/* 517 */       return InetAddress.getLocalHost();
/* 518 */     } catch (UnknownHostException unknownHostException) {
/*     */ 
/*     */ 
/*     */       
/* 522 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getLocalMacAddress() {
/* 531 */     return getMacAddress(getLocalhost());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMacAddress(InetAddress inetAddress) {
/* 541 */     return getMacAddress(inetAddress, "-");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMacAddress(InetAddress inetAddress, String separator) {
/* 552 */     if (null == inetAddress) {
/* 553 */       return null;
/*     */     }
/*     */     
/* 556 */     byte[] mac = getHardwareAddress(inetAddress);
/* 557 */     if (null != mac) {
/* 558 */       StringBuilder sb = new StringBuilder();
/*     */       
/* 560 */       for (int i = 0; i < mac.length; i++) {
/* 561 */         if (i != 0) {
/* 562 */           sb.append(separator);
/*     */         }
/*     */         
/* 565 */         String s = Integer.toHexString(mac[i] & 0xFF);
/* 566 */         sb.append((s.length() == 1) ? (Character.MIN_VALUE + s) : s);
/*     */       } 
/* 568 */       return sb.toString();
/*     */     } 
/*     */     
/* 571 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getHardwareAddress(InetAddress inetAddress) {
/* 582 */     if (null == inetAddress) {
/* 583 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 587 */       NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
/* 588 */       if (null != networkInterface) {
/* 589 */         return networkInterface.getHardwareAddress();
/*     */       }
/* 591 */     } catch (SocketException e) {
/* 592 */       throw new UtilException(e);
/*     */     } 
/* 594 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getLocalHardwareAddress() {
/* 604 */     return getHardwareAddress(getLocalhost());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getLocalHostName() {
/* 614 */     if (StrUtil.isNotBlank(localhostName)) {
/* 615 */       return localhostName;
/*     */     }
/*     */     
/* 618 */     InetAddress localhost = getLocalhost();
/* 619 */     if (null != localhost) {
/* 620 */       String name = localhost.getHostName();
/* 621 */       if (StrUtil.isEmpty(name)) {
/* 622 */         name = localhost.getHostAddress();
/*     */       }
/* 624 */       localhostName = name;
/*     */     } 
/*     */     
/* 627 */     return localhostName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InetSocketAddress createAddress(String host, int port) {
/* 639 */     if (StrUtil.isBlank(host)) {
/* 640 */       return new InetSocketAddress(port);
/*     */     }
/* 642 */     return new InetSocketAddress(host, port);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void netCat(String host, int port, boolean isBlock, ByteBuffer data) throws IORuntimeException {
/* 656 */     try (SocketChannel channel = SocketChannel.open(createAddress(host, port))) {
/* 657 */       channel.configureBlocking(isBlock);
/* 658 */       channel.write(data);
/* 659 */     } catch (IOException e) {
/* 660 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void netCat(String host, int port, byte[] data) throws IORuntimeException {
/* 674 */     OutputStream out = null;
/* 675 */     try (Socket socket = new Socket(host, port)) {
/* 676 */       out = socket.getOutputStream();
/* 677 */       out.write(data);
/* 678 */       out.flush();
/* 679 */     } catch (IOException e) {
/* 680 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 682 */       IoUtil.close(out);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInRange(String ip, String cidr) {
/* 696 */     int maskSplitMarkIndex = cidr.lastIndexOf("/");
/* 697 */     if (maskSplitMarkIndex < 0) {
/* 698 */       throw new IllegalArgumentException("Invalid cidr: " + cidr);
/*     */     }
/*     */     
/* 701 */     long mask = -1L << 32 - Integer.parseInt(cidr.substring(maskSplitMarkIndex + 1));
/* 702 */     long cidrIpAddr = ipv4ToLong(cidr.substring(0, maskSplitMarkIndex));
/*     */     
/* 704 */     return ((ipv4ToLong(ip) & mask) == (cidrIpAddr & mask));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String idnToASCII(String unicode) {
/* 715 */     return IDN.toASCII(unicode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMultistageReverseProxyIp(String ip) {
/* 727 */     if (ip != null && ip.indexOf(",") > 0) {
/* 728 */       String[] ips = ip.trim().split(",");
/* 729 */       for (String subIp : ips) {
/* 730 */         if (false == isUnknown(subIp)) {
/* 731 */           ip = subIp;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 736 */     return ip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isUnknown(String checkString) {
/* 747 */     return (StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean ping(String ip) {
/* 757 */     return ping(ip, 200);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean ping(String ip, int timeout) {
/*     */     try {
/* 769 */       return InetAddress.getByName(ip).isReachable(timeout);
/* 770 */     } catch (Exception ex) {
/* 771 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HttpCookie> parseCookies(String cookieStr) {
/* 783 */     if (StrUtil.isBlank(cookieStr)) {
/* 784 */       return Collections.emptyList();
/*     */     }
/* 786 */     return HttpCookie.parse(cookieStr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isOpen(InetSocketAddress address, int timeout) {
/* 798 */     try (Socket sc = new Socket()) {
/* 799 */       sc.connect(address, timeout);
/* 800 */       return true;
/* 801 */     } catch (Exception e) {
/* 802 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setGlobalAuthenticator(String user, char[] pass) {
/* 814 */     setGlobalAuthenticator(new UserPassAuthenticator(user, pass));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setGlobalAuthenticator(Authenticator authenticator) {
/* 824 */     Authenticator.setDefault(authenticator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> getDnsInfo(String hostName, String... attrNames) {
/* 840 */     String uri = StrUtil.addPrefixIfNot(hostName, "dns:");
/* 841 */     Attributes attributes = JNDIUtil.getAttributes(uri, attrNames);
/*     */     
/* 843 */     List<String> infos = new ArrayList<>();
/* 844 */     for (Attribute attribute : new EnumerationIter(attributes.getAll())) {
/*     */       try {
/* 846 */         infos.add((String)attribute.get());
/* 847 */       } catch (NamingException namingException) {}
/*     */     } 
/*     */ 
/*     */     
/* 851 */     return infos;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\NetUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */