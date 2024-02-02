package cn.hutool.core.net;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.JNDIUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.DatagramSocket;
import java.net.HttpCookie;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class NetUtil {
   public static final String LOCAL_IP = "127.0.0.1";
   public static String localhostName;
   public static final int PORT_RANGE_MIN = 1024;
   public static final int PORT_RANGE_MAX = 65535;

   public static String longToIpv4(long longIP) {
      return Ipv4Util.longToIpv4(longIP);
   }

   public static long ipv4ToLong(String strIP) {
      return Ipv4Util.ipv4ToLong(strIP);
   }

   public static BigInteger ipv6ToBitInteger(String IPv6Str) {
      try {
         InetAddress address = InetAddress.getByName(IPv6Str);
         if (address instanceof Inet6Address) {
            return new BigInteger(1, address.getAddress());
         }
      } catch (UnknownHostException var2) {
      }

      return null;
   }

   public static String bigIntegerToIPv6(BigInteger bigInteger) {
      try {
         return InetAddress.getByAddress(bigInteger.toByteArray()).toString().substring(1);
      } catch (UnknownHostException var2) {
         return null;
      }
   }

   public static boolean isUsableLocalPort(int port) {
      if (!isValidPort(port)) {
         return false;
      } else {
         Throwable var2;
         try {
            ServerSocket ss = new ServerSocket(port);
            var2 = null;

            try {
               ss.setReuseAddress(true);
            } catch (Throwable var30) {
               var2 = var30;
               throw var30;
            } finally {
               if (ss != null) {
                  if (var2 != null) {
                     try {
                        ss.close();
                     } catch (Throwable var27) {
                        var2.addSuppressed(var27);
                     }
                  } else {
                     ss.close();
                  }
               }

            }
         } catch (IOException var32) {
            return false;
         }

         try {
            DatagramSocket ds = new DatagramSocket(port);
            var2 = null;

            try {
               ds.setReuseAddress(true);
            } catch (Throwable var29) {
               var2 = var29;
               throw var29;
            } finally {
               if (ds != null) {
                  if (var2 != null) {
                     try {
                        ds.close();
                     } catch (Throwable var28) {
                        var2.addSuppressed(var28);
                     }
                  } else {
                     ds.close();
                  }
               }

            }

            return true;
         } catch (IOException var34) {
            return false;
         }
      }
   }

   public static boolean isValidPort(int port) {
      return port >= 0 && port <= 65535;
   }

   public static int getUsableLocalPort() {
      return getUsableLocalPort(1024);
   }

   public static int getUsableLocalPort(int minPort) {
      return getUsableLocalPort(minPort, 65535);
   }

   public static int getUsableLocalPort(int minPort, int maxPort) {
      int maxPortExclude = maxPort + 1;

      for(int i = minPort; i < maxPortExclude; ++i) {
         int randomPort = RandomUtil.randomInt(minPort, maxPortExclude);
         if (isUsableLocalPort(randomPort)) {
            return randomPort;
         }
      }

      throw new UtilException("Could not find an available port in the range [{}, {}] after {} attempts", new Object[]{minPort, maxPort, maxPort - minPort});
   }

   public static TreeSet<Integer> getUsableLocalPorts(int numRequested, int minPort, int maxPort) {
      TreeSet<Integer> availablePorts = new TreeSet();
      int attemptCount = 0;

      while(true) {
         ++attemptCount;
         if (attemptCount > numRequested + 100 || availablePorts.size() >= numRequested) {
            if (availablePorts.size() != numRequested) {
               throw new UtilException("Could not find {} available  ports in the range [{}, {}]", new Object[]{numRequested, minPort, maxPort});
            } else {
               return availablePorts;
            }
         }

         availablePorts.add(getUsableLocalPort(minPort, maxPort));
      }
   }

   public static boolean isInnerIP(String ipAddress) {
      return Ipv4Util.isInnerIP(ipAddress);
   }

   public static String toAbsoluteUrl(String absoluteBasePath, String relativePath) {
      try {
         URL absoluteUrl = new URL(absoluteBasePath);
         return (new URL(absoluteUrl, relativePath)).toString();
      } catch (Exception var3) {
         throw new UtilException(var3, "To absolute url [{}] base [{}] error!", new Object[]{relativePath, absoluteBasePath});
      }
   }

   public static String hideIpPart(String ip) {
      return StrUtil.builder(ip.length()).append(ip, 0, ip.lastIndexOf(".") + 1).append("*").toString();
   }

   public static String hideIpPart(long ip) {
      return hideIpPart(longToIpv4(ip));
   }

   public static InetSocketAddress buildInetSocketAddress(String host, int defaultPort) {
      if (StrUtil.isBlank(host)) {
         host = "127.0.0.1";
      }

      int index = host.indexOf(":");
      String destHost;
      int port;
      if (index != -1) {
         destHost = host.substring(0, index);
         port = Integer.parseInt(host.substring(index + 1));
      } else {
         destHost = host;
         port = defaultPort;
      }

      return new InetSocketAddress(destHost, port);
   }

   public static String getIpByHost(String hostName) {
      try {
         return InetAddress.getByName(hostName).getHostAddress();
      } catch (UnknownHostException var2) {
         return hostName;
      }
   }

   public static NetworkInterface getNetworkInterface(String name) {
      Enumeration networkInterfaces;
      try {
         networkInterfaces = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var3) {
         return null;
      }

      NetworkInterface netInterface;
      do {
         if (!networkInterfaces.hasMoreElements()) {
            return null;
         }

         netInterface = (NetworkInterface)networkInterfaces.nextElement();
      } while(null == netInterface || !name.equals(netInterface.getName()));

      return netInterface;
   }

   public static Collection<NetworkInterface> getNetworkInterfaces() {
      Enumeration networkInterfaces;
      try {
         networkInterfaces = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var2) {
         return null;
      }

      return CollUtil.addAll(new ArrayList(), (Enumeration)networkInterfaces);
   }

   public static LinkedHashSet<String> localIpv4s() {
      LinkedHashSet<InetAddress> localAddressList = localAddressList((t) -> {
         return t instanceof Inet4Address;
      });
      return toIpList(localAddressList);
   }

   public static LinkedHashSet<String> localIpv6s() {
      LinkedHashSet<InetAddress> localAddressList = localAddressList((t) -> {
         return t instanceof Inet6Address;
      });
      return toIpList(localAddressList);
   }

   public static LinkedHashSet<String> toIpList(Set<InetAddress> addressList) {
      LinkedHashSet<String> ipSet = new LinkedHashSet();
      Iterator var2 = addressList.iterator();

      while(var2.hasNext()) {
         InetAddress address = (InetAddress)var2.next();
         ipSet.add(address.getHostAddress());
      }

      return ipSet;
   }

   public static LinkedHashSet<String> localIps() {
      LinkedHashSet<InetAddress> localAddressList = localAddressList((Filter)null);
      return toIpList(localAddressList);
   }

   public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {
      Enumeration networkInterfaces;
      try {
         networkInterfaces = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var6) {
         throw new UtilException(var6);
      }

      if (networkInterfaces == null) {
         throw new UtilException("Get network interface error!");
      } else {
         LinkedHashSet<InetAddress> ipSet = new LinkedHashSet();

         label43:
         while(networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while(true) {
               InetAddress inetAddress;
               do {
                  do {
                     if (!inetAddresses.hasMoreElements()) {
                        continue label43;
                     }

                     inetAddress = (InetAddress)inetAddresses.nextElement();
                  } while(inetAddress == null);
               } while(null != addressFilter && !addressFilter.accept(inetAddress));

               ipSet.add(inetAddress);
            }
         }

         return ipSet;
      }
   }

   public static String getLocalhostStr() {
      InetAddress localhost = getLocalhost();
      return null != localhost ? localhost.getHostAddress() : null;
   }

   public static InetAddress getLocalhost() {
      LinkedHashSet<InetAddress> localAddressList = localAddressList((address) -> {
         return !address.isLoopbackAddress() && address instanceof Inet4Address;
      });
      if (CollUtil.isNotEmpty((Collection)localAddressList)) {
         InetAddress address2 = null;
         Iterator var2 = localAddressList.iterator();

         while(var2.hasNext()) {
            InetAddress inetAddress = (InetAddress)var2.next();
            if (!inetAddress.isSiteLocalAddress()) {
               return inetAddress;
            }

            if (null == address2) {
               address2 = inetAddress;
            }
         }

         if (null != address2) {
            return address2;
         }
      }

      try {
         return InetAddress.getLocalHost();
      } catch (UnknownHostException var4) {
         return null;
      }
   }

   public static String getLocalMacAddress() {
      return getMacAddress(getLocalhost());
   }

   public static String getMacAddress(InetAddress inetAddress) {
      return getMacAddress(inetAddress, "-");
   }

   public static String getMacAddress(InetAddress inetAddress, String separator) {
      if (null == inetAddress) {
         return null;
      } else {
         byte[] mac = getHardwareAddress(inetAddress);
         if (null != mac) {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < mac.length; ++i) {
               if (i != 0) {
                  sb.append(separator);
               }

               String s = Integer.toHexString(mac[i] & 255);
               sb.append(s.length() == 1 ? 0 + s : s);
            }

            return sb.toString();
         } else {
            return null;
         }
      }
   }

   public static byte[] getHardwareAddress(InetAddress inetAddress) {
      if (null == inetAddress) {
         return null;
      } else {
         try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            return null != networkInterface ? networkInterface.getHardwareAddress() : null;
         } catch (SocketException var2) {
            throw new UtilException(var2);
         }
      }
   }

   public static byte[] getLocalHardwareAddress() {
      return getHardwareAddress(getLocalhost());
   }

   public static String getLocalHostName() {
      if (StrUtil.isNotBlank(localhostName)) {
         return localhostName;
      } else {
         InetAddress localhost = getLocalhost();
         if (null != localhost) {
            String name = localhost.getHostName();
            if (StrUtil.isEmpty(name)) {
               name = localhost.getHostAddress();
            }

            localhostName = name;
         }

         return localhostName;
      }
   }

   public static InetSocketAddress createAddress(String host, int port) {
      return StrUtil.isBlank(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
   }

   public static void netCat(String host, int port, boolean isBlock, ByteBuffer data) throws IORuntimeException {
      try {
         SocketChannel channel = SocketChannel.open(createAddress(host, port));
         Throwable var5 = null;

         try {
            channel.configureBlocking(isBlock);
            channel.write(data);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (channel != null) {
               if (var5 != null) {
                  try {
                     channel.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  channel.close();
               }
            }

         }

      } catch (IOException var17) {
         throw new IORuntimeException(var17);
      }
   }

   public static void netCat(String host, int port, byte[] data) throws IORuntimeException {
      OutputStream out = null;

      try {
         Socket socket = new Socket(host, port);
         Throwable var5 = null;

         try {
            out = socket.getOutputStream();
            out.write(data);
            out.flush();
         } catch (Throwable var23) {
            var5 = var23;
            throw var23;
         } finally {
            if (socket != null) {
               if (var5 != null) {
                  try {
                     socket.close();
                  } catch (Throwable var22) {
                     var5.addSuppressed(var22);
                  }
               } else {
                  socket.close();
               }
            }

         }
      } catch (IOException var25) {
         throw new IORuntimeException(var25);
      } finally {
         IoUtil.close(out);
      }

   }

   public static boolean isInRange(String ip, String cidr) {
      int maskSplitMarkIndex = cidr.lastIndexOf("/");
      if (maskSplitMarkIndex < 0) {
         throw new IllegalArgumentException("Invalid cidr: " + cidr);
      } else {
         long mask = -1L << 32 - Integer.parseInt(cidr.substring(maskSplitMarkIndex + 1));
         long cidrIpAddr = ipv4ToLong(cidr.substring(0, maskSplitMarkIndex));
         return (ipv4ToLong(ip) & mask) == (cidrIpAddr & mask);
      }
   }

   public static String idnToASCII(String unicode) {
      return IDN.toASCII(unicode);
   }

   public static String getMultistageReverseProxyIp(String ip) {
      if (ip != null && ip.indexOf(",") > 0) {
         String[] ips = ip.trim().split(",");
         String[] var2 = ips;
         int var3 = ips.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String subIp = var2[var4];
            if (!isUnknown(subIp)) {
               ip = subIp;
               break;
            }
         }
      }

      return ip;
   }

   public static boolean isUnknown(String checkString) {
      return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
   }

   public static boolean ping(String ip) {
      return ping(ip, 200);
   }

   public static boolean ping(String ip, int timeout) {
      try {
         return InetAddress.getByName(ip).isReachable(timeout);
      } catch (Exception var3) {
         return false;
      }
   }

   public static List<HttpCookie> parseCookies(String cookieStr) {
      return StrUtil.isBlank(cookieStr) ? Collections.emptyList() : HttpCookie.parse(cookieStr);
   }

   public static boolean isOpen(InetSocketAddress address, int timeout) {
      try {
         Socket sc = new Socket();
         Throwable var3 = null;

         boolean var4;
         try {
            sc.connect(address, timeout);
            var4 = true;
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (sc != null) {
               if (var3 != null) {
                  try {
                     sc.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  sc.close();
               }
            }

         }

         return var4;
      } catch (Exception var16) {
         return false;
      }
   }

   public static void setGlobalAuthenticator(String user, char[] pass) {
      setGlobalAuthenticator(new UserPassAuthenticator(user, pass));
   }

   public static void setGlobalAuthenticator(Authenticator authenticator) {
      Authenticator.setDefault(authenticator);
   }

   public static List<String> getDnsInfo(String hostName, String... attrNames) {
      String uri = StrUtil.addPrefixIfNot(hostName, "dns:");
      Attributes attributes = JNDIUtil.getAttributes(uri, attrNames);
      List<String> infos = new ArrayList();
      Iterator var5 = (new EnumerationIter(attributes.getAll())).iterator();

      while(var5.hasNext()) {
         Attribute attribute = (Attribute)var5.next();

         try {
            infos.add((String)attribute.get());
         } catch (NamingException var8) {
         }
      }

      return infos;
   }
}
