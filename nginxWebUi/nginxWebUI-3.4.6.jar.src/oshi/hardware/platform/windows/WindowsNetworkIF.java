/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.IPHlpAPI;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.common.AbstractNetworkIF;
/*     */ import oshi.util.ParseUtil;
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
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class WindowsNetworkIF
/*     */   extends AbstractNetworkIF
/*     */ {
/*  50 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkIF.class);
/*     */   
/*  52 */   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
/*     */   
/*     */   private static final byte CONNECTOR_PRESENT_BIT = 32;
/*     */   private int ifType;
/*     */   private int ndisPhysicalMediumType;
/*     */   private boolean connectorPresent;
/*     */   private long bytesRecv;
/*     */   private long bytesSent;
/*     */   private long packetsRecv;
/*     */   private long packetsSent;
/*     */   private long inErrors;
/*     */   private long outErrors;
/*     */   private long inDrops;
/*     */   private long collisions;
/*     */   private long speed;
/*     */   private long timeStamp;
/*     */   
/*     */   public WindowsNetworkIF(NetworkInterface netint) {
/*  70 */     super(netint);
/*  71 */     updateAttributes();
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
/*     */   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
/*  83 */     return Collections.unmodifiableList((List<? extends NetworkIF>)getNetworkInterfaces(includeLocalInterfaces).stream()
/*  84 */         .map(WindowsNetworkIF::new).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIfType() {
/*  89 */     return this.ifType;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNdisPhysicalMediumType() {
/*  94 */     return this.ndisPhysicalMediumType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConnectorPresent() {
/*  99 */     return this.connectorPresent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRecv() {
/* 104 */     return this.bytesRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/* 109 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsRecv() {
/* 114 */     return this.packetsRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsSent() {
/* 119 */     return this.packetsSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInErrors() {
/* 124 */     return this.inErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOutErrors() {
/* 129 */     return this.outErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInDrops() {
/* 134 */     return this.inDrops;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCollisions() {
/* 139 */     return this.collisions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSpeed() {
/* 144 */     return this.speed;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 149 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 155 */     if (IS_VISTA_OR_GREATER) {
/*     */       
/* 157 */       IPHlpAPI.MIB_IF_ROW2 ifRow = new IPHlpAPI.MIB_IF_ROW2();
/* 158 */       ifRow.InterfaceIndex = queryNetworkInterface().getIndex();
/* 159 */       if (0 != IPHlpAPI.INSTANCE.GetIfEntry2(ifRow)) {
/*     */         
/* 161 */         LOG.error("Failed to retrieve data for interface {}, {}", Integer.valueOf(queryNetworkInterface().getIndex()), 
/* 162 */             getName());
/* 163 */         return false;
/*     */       } 
/* 165 */       this.ifType = ifRow.Type;
/* 166 */       this.ndisPhysicalMediumType = ifRow.PhysicalMediumType;
/* 167 */       this.connectorPresent = ((ifRow.InterfaceAndOperStatusFlags & 0x20) > 0);
/* 168 */       this.bytesSent = ifRow.OutOctets;
/* 169 */       this.bytesRecv = ifRow.InOctets;
/* 170 */       this.packetsSent = ifRow.OutUcastPkts;
/* 171 */       this.packetsRecv = ifRow.InUcastPkts;
/* 172 */       this.outErrors = ifRow.OutErrors;
/* 173 */       this.inErrors = ifRow.InErrors;
/* 174 */       this.collisions = ifRow.OutDiscards;
/* 175 */       this.inDrops = ifRow.InDiscards;
/* 176 */       this.speed = ifRow.ReceiveLinkSpeed;
/*     */     } else {
/*     */       
/* 179 */       IPHlpAPI.MIB_IFROW ifRow = new IPHlpAPI.MIB_IFROW();
/* 180 */       ifRow.dwIndex = queryNetworkInterface().getIndex();
/* 181 */       if (0 != IPHlpAPI.INSTANCE.GetIfEntry(ifRow)) {
/*     */         
/* 183 */         LOG.error("Failed to retrieve data for interface {}, {}", Integer.valueOf(queryNetworkInterface().getIndex()), 
/* 184 */             getName());
/* 185 */         return false;
/*     */       } 
/* 187 */       this.ifType = ifRow.dwType;
/*     */       
/* 189 */       this.bytesSent = ParseUtil.unsignedIntToLong(ifRow.dwOutOctets);
/* 190 */       this.bytesRecv = ParseUtil.unsignedIntToLong(ifRow.dwInOctets);
/* 191 */       this.packetsSent = ParseUtil.unsignedIntToLong(ifRow.dwOutUcastPkts);
/* 192 */       this.packetsRecv = ParseUtil.unsignedIntToLong(ifRow.dwInUcastPkts);
/* 193 */       this.outErrors = ParseUtil.unsignedIntToLong(ifRow.dwOutErrors);
/* 194 */       this.inErrors = ParseUtil.unsignedIntToLong(ifRow.dwInErrors);
/* 195 */       this.collisions = ParseUtil.unsignedIntToLong(ifRow.dwOutDiscards);
/* 196 */       this.inDrops = ParseUtil.unsignedIntToLong(ifRow.dwInDiscards);
/* 197 */       this.speed = ParseUtil.unsignedIntToLong(ifRow.dwSpeed);
/*     */     } 
/* 199 */     this.timeStamp = System.currentTimeMillis();
/* 200 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */