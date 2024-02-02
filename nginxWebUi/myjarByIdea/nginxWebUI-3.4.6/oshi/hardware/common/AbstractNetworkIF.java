package oshi.hardware.common;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.util.FileUtil;
import oshi.util.FormatUtil;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractNetworkIF implements NetworkIF {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkIF.class);
   private static final String OSHI_VM_MAC_ADDR_PROPERTIES = "oshi.vmmacaddr.properties";
   private NetworkInterface networkInterface;
   private int mtu;
   private String mac;
   private String[] ipv4;
   private Short[] subnetMasks;
   private String[] ipv6;
   private Short[] prefixLengths;
   private final Supplier<Properties> vmMacAddrProps = Memoizer.memoize(AbstractNetworkIF::queryVmMacAddrProps);

   protected AbstractNetworkIF(NetworkInterface netint) {
      this.networkInterface = netint;

      try {
         this.mtu = this.networkInterface.getMTU();
         byte[] hwmac = this.networkInterface.getHardwareAddress();
         ArrayList ipv4list;
         if (hwmac == null) {
            this.mac = "Unknown";
         } else {
            ipv4list = new ArrayList(6);
            byte[] var4 = hwmac;
            int var5 = hwmac.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               byte b = var4[var6];
               ipv4list.add(String.format("%02x", b));
            }

            this.mac = String.join(":", ipv4list);
         }

         ipv4list = new ArrayList();
         ArrayList<Short> subnetMaskList = new ArrayList();
         ArrayList<String> ipv6list = new ArrayList();
         ArrayList<Short> prefixLengthList = new ArrayList();
         Iterator var14 = this.networkInterface.getInterfaceAddresses().iterator();

         while(var14.hasNext()) {
            InterfaceAddress interfaceAddress = (InterfaceAddress)var14.next();
            InetAddress address = interfaceAddress.getAddress();
            if (address.getHostAddress().length() > 0) {
               if (address.getHostAddress().contains(":")) {
                  ipv6list.add(address.getHostAddress().split("%")[0]);
                  prefixLengthList.add(interfaceAddress.getNetworkPrefixLength());
               } else {
                  ipv4list.add(address.getHostAddress());
                  subnetMaskList.add(interfaceAddress.getNetworkPrefixLength());
               }
            }
         }

         this.ipv4 = (String[])ipv4list.toArray(new String[0]);
         this.subnetMasks = (Short[])subnetMaskList.toArray(new Short[0]);
         this.ipv6 = (String[])ipv6list.toArray(new String[0]);
         this.prefixLengths = (Short[])prefixLengthList.toArray(new Short[0]);
      } catch (SocketException var10) {
         LOG.error((String)"Socket exception: {}", (Object)var10.getMessage());
      }

   }

   protected static List<NetworkInterface> getNetworkInterfaces(boolean includeLocalInterfaces) {
      List<NetworkInterface> interfaces = getAllNetworkInterfaces();
      return includeLocalInterfaces ? interfaces : (List)getAllNetworkInterfaces().stream().filter((networkInterface1) -> {
         return !isLocalInterface(networkInterface1);
      }).collect(Collectors.toList());
   }

   private static List<NetworkInterface> getAllNetworkInterfaces() {
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
         return (List)(interfaces == null ? Collections.emptyList() : Collections.list(interfaces));
      } catch (SocketException var1) {
         LOG.error((String)"Socket exception when retrieving interfaces: {}", (Object)var1.getMessage());
         return Collections.emptyList();
      }
   }

   private static boolean isLocalInterface(NetworkInterface networkInterface) {
      try {
         return networkInterface.isLoopback() || networkInterface.getHardwareAddress() == null;
      } catch (SocketException var2) {
         LOG.error((String)"Socket exception when retrieving interface information for {}: {}", (Object)networkInterface, (Object)var2.getMessage());
         return false;
      }
   }

   public NetworkInterface queryNetworkInterface() {
      return this.networkInterface;
   }

   public String getName() {
      return this.networkInterface.getName();
   }

   public String getDisplayName() {
      return this.networkInterface.getDisplayName();
   }

   public int getMTU() {
      return this.mtu;
   }

   public String getMacaddr() {
      return this.mac;
   }

   public String[] getIPv4addr() {
      return (String[])Arrays.copyOf(this.ipv4, this.ipv4.length);
   }

   public Short[] getSubnetMasks() {
      return (Short[])Arrays.copyOf(this.subnetMasks, this.subnetMasks.length);
   }

   public String[] getIPv6addr() {
      return (String[])Arrays.copyOf(this.ipv6, this.ipv6.length);
   }

   public Short[] getPrefixLengths() {
      return (Short[])Arrays.copyOf(this.prefixLengths, this.prefixLengths.length);
   }

   public boolean isKnownVmMacAddr() {
      String oui = this.getMacaddr().length() > 7 ? this.getMacaddr().substring(0, 8) : this.getMacaddr();
      return ((Properties)this.vmMacAddrProps.get()).containsKey(oui.toUpperCase());
   }

   public int getIfType() {
      return 0;
   }

   public int getNdisPhysicalMediumType() {
      return 0;
   }

   public boolean isConnectorPresent() {
      return false;
   }

   private static Properties queryVmMacAddrProps() {
      return FileUtil.readPropertiesFromFilename("oshi.vmmacaddr.properties");
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Name: ").append(this.getName()).append(" ").append("(").append(this.getDisplayName()).append(")").append("\n");
      sb.append("  MAC Address: ").append(this.getMacaddr()).append("\n");
      sb.append("  MTU: ").append(this.getMTU()).append(", ").append("Speed: ").append(this.getSpeed()).append("\n");
      String[] ipv4withmask = this.getIPv4addr();
      if (this.ipv4.length == this.subnetMasks.length) {
         for(int i = 0; i < this.subnetMasks.length; ++i) {
            ipv4withmask[i] = ipv4withmask[i] + "/" + this.subnetMasks[i];
         }
      }

      sb.append("  IPv4: ").append(Arrays.toString(ipv4withmask)).append("\n");
      String[] ipv6withprefixlength = this.getIPv6addr();
      if (this.ipv6.length == this.prefixLengths.length) {
         for(int j = 0; j < this.prefixLengths.length; ++j) {
            ipv6withprefixlength[j] = ipv6withprefixlength[j] + "/" + this.prefixLengths[j];
         }
      }

      sb.append("  IPv6: ").append(Arrays.toString(ipv6withprefixlength)).append("\n");
      sb.append("  Traffic: received ").append(this.getPacketsRecv()).append(" packets/").append(FormatUtil.formatBytes(this.getBytesRecv())).append(" (" + this.getInErrors() + " err, ").append(this.getInDrops() + " drop);");
      sb.append(" transmitted ").append(this.getPacketsSent()).append(" packets/").append(FormatUtil.formatBytes(this.getBytesSent())).append(" (" + this.getOutErrors() + " err, ").append(this.getCollisions() + " coll);");
      return sb.toString();
   }
}
