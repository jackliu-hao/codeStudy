/*     */ package oshi.driver.mac.net;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class NetStat
/*     */ {
/*  48 */   private static final Logger LOG = LoggerFactory.getLogger(NetStat.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CTL_NET = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int PF_ROUTE = 17;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int NET_RT_IFLIST2 = 6;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int RTM_IFINFO2 = 18;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<Integer, IFdata> queryIFdata(int index) {
/*  71 */     Map<Integer, IFdata> data = new HashMap<>();
/*     */     
/*  73 */     int[] mib = { 4, 17, 0, 0, 6, 0 };
/*  74 */     IntByReference len = new IntByReference();
/*  75 */     if (0 != SystemB.INSTANCE.sysctl(mib, 6, null, len, null, 0)) {
/*  76 */       LOG.error("Didn't get buffer length for IFLIST2");
/*  77 */       return data;
/*     */     } 
/*  79 */     Memory buf = new Memory(len.getValue());
/*  80 */     if (0 != SystemB.INSTANCE.sysctl(mib, 6, (Pointer)buf, len, null, 0)) {
/*  81 */       LOG.error("Didn't get buffer for IFLIST2");
/*  82 */       return data;
/*     */     } 
/*  84 */     long now = System.currentTimeMillis();
/*     */ 
/*     */     
/*  87 */     int lim = (int)(buf.size() - (new SystemB.IFmsgHdr()).size());
/*  88 */     int offset = 0;
/*  89 */     while (offset < lim) {
/*     */       
/*  91 */       Pointer p = buf.share(offset);
/*     */       
/*  93 */       SystemB.IFmsgHdr ifm = new SystemB.IFmsgHdr(p);
/*  94 */       ifm.read();
/*     */       
/*  96 */       offset += ifm.ifm_msglen;
/*     */       
/*  98 */       if (ifm.ifm_type == 18) {
/*     */         
/* 100 */         SystemB.IFmsgHdr2 if2m = new SystemB.IFmsgHdr2(p);
/* 101 */         if2m.read();
/* 102 */         if (index < 0 || index == if2m.ifm_index) {
/* 103 */           data.put(Integer.valueOf(if2m.ifm_index), new IFdata(if2m.ifm_data.ifi_type, if2m.ifm_data.ifi_opackets, if2m.ifm_data.ifi_ipackets, if2m.ifm_data.ifi_obytes, if2m.ifm_data.ifi_ibytes, if2m.ifm_data.ifi_oerrors, if2m.ifm_data.ifi_ierrors, if2m.ifm_data.ifi_collisions, if2m.ifm_data.ifi_iqdrops, if2m.ifm_data.ifi_baudrate, now));
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 108 */           if (index >= 0) {
/* 109 */             return data;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 114 */     return data;
/*     */   }
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static class IFdata
/*     */   {
/*     */     private final int ifType;
/*     */     
/*     */     private final long oPackets;
/*     */     
/*     */     private final long iPackets;
/*     */     
/*     */     private final long oBytes;
/*     */     private final long iBytes;
/*     */     private final long oErrors;
/*     */     private final long iErrors;
/*     */     private final long collisions;
/*     */     private final long iDrops;
/*     */     private final long speed;
/*     */     private final long timeStamp;
/*     */     
/*     */     IFdata(int ifType, long oPackets, long iPackets, long oBytes, long iBytes, long oErrors, long iErrors, long collisions, long iDrops, long speed, long timeStamp) {
/* 137 */       this.ifType = ifType;
/* 138 */       this.oPackets = oPackets & 0xFFFFFFFFL;
/* 139 */       this.iPackets = iPackets & 0xFFFFFFFFL;
/* 140 */       this.oBytes = oBytes & 0xFFFFFFFFL;
/* 141 */       this.iBytes = iBytes & 0xFFFFFFFFL;
/* 142 */       this.oErrors = oErrors & 0xFFFFFFFFL;
/* 143 */       this.iErrors = iErrors & 0xFFFFFFFFL;
/* 144 */       this.collisions = collisions & 0xFFFFFFFFL;
/* 145 */       this.iDrops = iDrops & 0xFFFFFFFFL;
/* 146 */       this.speed = speed & 0xFFFFFFFFL;
/* 147 */       this.timeStamp = timeStamp;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getIfType() {
/* 154 */       return this.ifType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getOPackets() {
/* 161 */       return this.oPackets;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getIPackets() {
/* 168 */       return this.iPackets;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getOBytes() {
/* 175 */       return this.oBytes;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getIBytes() {
/* 182 */       return this.iBytes;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getOErrors() {
/* 189 */       return this.oErrors;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getIErrors() {
/* 196 */       return this.iErrors;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getCollisions() {
/* 203 */       return this.collisions;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getIDrops() {
/* 210 */       return this.iDrops;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getSpeed() {
/* 217 */       return this.speed;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getTimeStamp() {
/* 224 */       return this.timeStamp;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\mac\net\NetStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */