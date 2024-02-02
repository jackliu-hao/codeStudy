/*     */ package oshi.hardware.common;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InterfaceAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.FormatUtil;
/*     */ import oshi.util.Memoizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public abstract class AbstractNetworkIF
/*     */   implements NetworkIF
/*     */ {
/*  55 */   private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkIF.class);
/*     */   
/*     */   private static final String OSHI_VM_MAC_ADDR_PROPERTIES = "oshi.vmmacaddr.properties";
/*     */   
/*     */   private NetworkInterface networkInterface;
/*     */   
/*     */   private int mtu;
/*     */   private String mac;
/*     */   private String[] ipv4;
/*     */   private Short[] subnetMasks;
/*     */   private String[] ipv6;
/*     */   private Short[] prefixLengths;
/*  67 */   private final Supplier<Properties> vmMacAddrProps = Memoizer.memoize(AbstractNetworkIF::queryVmMacAddrProps);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractNetworkIF(NetworkInterface netint) {
/*  77 */     this.networkInterface = netint;
/*     */     
/*     */     try {
/*  80 */       this.mtu = this.networkInterface.getMTU();
/*     */       
/*  82 */       byte[] hwmac = this.networkInterface.getHardwareAddress();
/*  83 */       if (hwmac != null) {
/*  84 */         List<String> octets = new ArrayList<>(6);
/*  85 */         for (byte b : hwmac) {
/*  86 */           octets.add(String.format("%02x", new Object[] { Byte.valueOf(b) }));
/*     */         } 
/*  88 */         this.mac = String.join(":", (Iterable)octets);
/*     */       } else {
/*  90 */         this.mac = "Unknown";
/*     */       } 
/*     */       
/*  93 */       ArrayList<String> ipv4list = new ArrayList<>();
/*  94 */       ArrayList<Short> subnetMaskList = new ArrayList<>();
/*  95 */       ArrayList<String> ipv6list = new ArrayList<>();
/*  96 */       ArrayList<Short> prefixLengthList = new ArrayList<>();
/*     */       
/*  98 */       for (InterfaceAddress interfaceAddress : this.networkInterface.getInterfaceAddresses()) {
/*  99 */         InetAddress address = interfaceAddress.getAddress();
/* 100 */         if (address.getHostAddress().length() > 0) {
/* 101 */           if (address.getHostAddress().contains(":")) {
/* 102 */             ipv6list.add(address.getHostAddress().split("%")[0]);
/* 103 */             prefixLengthList.add(Short.valueOf(interfaceAddress.getNetworkPrefixLength())); continue;
/*     */           } 
/* 105 */           ipv4list.add(address.getHostAddress());
/* 106 */           subnetMaskList.add(Short.valueOf(interfaceAddress.getNetworkPrefixLength()));
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 111 */       this.ipv4 = ipv4list.<String>toArray(new String[0]);
/* 112 */       this.subnetMasks = subnetMaskList.<Short>toArray(new Short[0]);
/* 113 */       this.ipv6 = ipv6list.<String>toArray(new String[0]);
/* 114 */       this.prefixLengths = prefixLengthList.<Short>toArray(new Short[0]);
/* 115 */     } catch (SocketException e) {
/* 116 */       LOG.error("Socket exception: {}", e.getMessage());
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
/*     */   protected static List<NetworkInterface> getNetworkInterfaces(boolean includeLocalInterfaces) {
/* 128 */     List<NetworkInterface> interfaces = getAllNetworkInterfaces();
/*     */     
/* 130 */     return includeLocalInterfaces ? interfaces : 
/* 131 */       (List<NetworkInterface>)getAllNetworkInterfaces().stream().filter(networkInterface1 -> !isLocalInterface(networkInterface1))
/* 132 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<NetworkInterface> getAllNetworkInterfaces() {
/*     */     try {
/* 142 */       Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
/* 143 */       return (interfaces == null) ? Collections.<NetworkInterface>emptyList() : Collections.<NetworkInterface>list(interfaces);
/* 144 */     } catch (SocketException ex) {
/* 145 */       LOG.error("Socket exception when retrieving interfaces: {}", ex.getMessage());
/*     */       
/* 147 */       return Collections.emptyList();
/*     */     } 
/*     */   }
/*     */   private static boolean isLocalInterface(NetworkInterface networkInterface) {
/*     */     try {
/* 152 */       return (networkInterface.isLoopback() || networkInterface.getHardwareAddress() == null);
/* 153 */     } catch (SocketException e) {
/* 154 */       LOG.error("Socket exception when retrieving interface information for {}: {}", networkInterface, e
/* 155 */           .getMessage());
/*     */       
/* 157 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public NetworkInterface queryNetworkInterface() {
/* 162 */     return this.networkInterface;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 167 */     return this.networkInterface.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayName() {
/* 172 */     return this.networkInterface.getDisplayName();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMTU() {
/* 177 */     return this.mtu;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMacaddr() {
/* 182 */     return this.mac;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getIPv4addr() {
/* 187 */     return Arrays.<String>copyOf(this.ipv4, this.ipv4.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short[] getSubnetMasks() {
/* 192 */     return Arrays.<Short>copyOf(this.subnetMasks, this.subnetMasks.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getIPv6addr() {
/* 197 */     return Arrays.<String>copyOf(this.ipv6, this.ipv6.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short[] getPrefixLengths() {
/* 202 */     return Arrays.<Short>copyOf(this.prefixLengths, this.prefixLengths.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isKnownVmMacAddr() {
/* 207 */     String oui = (getMacaddr().length() > 7) ? getMacaddr().substring(0, 8) : getMacaddr();
/* 208 */     return ((Properties)this.vmMacAddrProps.get()).containsKey(oui.toUpperCase());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIfType() {
/* 214 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNdisPhysicalMediumType() {
/* 220 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectorPresent() {
/* 226 */     return false;
/*     */   }
/*     */   
/*     */   private static Properties queryVmMacAddrProps() {
/* 230 */     return FileUtil.readPropertiesFromFilename("oshi.vmmacaddr.properties");
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 235 */     StringBuilder sb = new StringBuilder();
/* 236 */     sb.append("Name: ").append(getName()).append(" ").append("(").append(getDisplayName()).append(")").append("\n");
/* 237 */     sb.append("  MAC Address: ").append(getMacaddr()).append("\n");
/* 238 */     sb.append("  MTU: ").append(getMTU()).append(", ").append("Speed: ").append(getSpeed()).append("\n");
/* 239 */     String[] ipv4withmask = getIPv4addr();
/* 240 */     if (this.ipv4.length == this.subnetMasks.length) {
/* 241 */       for (int i = 0; i < this.subnetMasks.length; i++) {
/* 242 */         ipv4withmask[i] = ipv4withmask[i] + "/" + this.subnetMasks[i];
/*     */       }
/*     */     }
/* 245 */     sb.append("  IPv4: ").append(Arrays.toString((Object[])ipv4withmask)).append("\n");
/* 246 */     String[] ipv6withprefixlength = getIPv6addr();
/* 247 */     if (this.ipv6.length == this.prefixLengths.length) {
/* 248 */       for (int j = 0; j < this.prefixLengths.length; j++) {
/* 249 */         ipv6withprefixlength[j] = ipv6withprefixlength[j] + "/" + this.prefixLengths[j];
/*     */       }
/*     */     }
/* 252 */     sb.append("  IPv6: ").append(Arrays.toString((Object[])ipv6withprefixlength)).append("\n");
/* 253 */     sb.append("  Traffic: received ").append(getPacketsRecv()).append(" packets/")
/* 254 */       .append(FormatUtil.formatBytes(getBytesRecv())).append(" (" + getInErrors() + " err, ")
/* 255 */       .append(getInDrops() + " drop);");
/* 256 */     sb.append(" transmitted ").append(getPacketsSent()).append(" packets/")
/* 257 */       .append(FormatUtil.formatBytes(getBytesSent())).append(" (" + getOutErrors() + " err, ")
/* 258 */       .append(getCollisions() + " coll);");
/* 259 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */