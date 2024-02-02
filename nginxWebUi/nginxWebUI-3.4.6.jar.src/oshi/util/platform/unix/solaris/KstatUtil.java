/*     */ package oshi.util.platform.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.FormatUtil;
/*     */ import oshi.util.Util;
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
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class KstatUtil
/*     */ {
/*  51 */   private static final Logger LOG = LoggerFactory.getLogger(KstatUtil.class);
/*     */   
/*  53 */   private static final LibKstat KS = LibKstat.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final LibKstat.KstatCtl KC = KS.kstat_open();
/*  59 */   private static final ReentrantLock CHAIN = new ReentrantLock();
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
/*     */   public static final class KstatChain
/*     */     implements AutoCloseable
/*     */   {
/*     */     private KstatChain() {
/*  77 */       KstatUtil.CHAIN.lock();
/*  78 */       update();
/*     */     }
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
/*     */     public boolean read(LibKstat.Kstat ksp) {
/*  95 */       int retry = 0;
/*  96 */       while (0 > KstatUtil.KS.kstat_read(KstatUtil.KC, ksp, null)) {
/*  97 */         if (11 != Native.getLastError() || 5 <= ++retry) {
/*  98 */           if (KstatUtil.LOG.isErrorEnabled()) {
/*  99 */             KstatUtil.LOG.error("Failed to read kstat {}:{}:{}", new Object[] { (new String(ksp.ks_module, StandardCharsets.US_ASCII))
/* 100 */                   .trim(), Integer.valueOf(ksp.ks_instance), (new String(ksp.ks_name, StandardCharsets.US_ASCII))
/* 101 */                   .trim() });
/*     */           }
/* 103 */           return false;
/*     */         } 
/* 105 */         Util.sleep((8 << retry));
/*     */       } 
/* 107 */       return true;
/*     */     }
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
/*     */     public LibKstat.Kstat lookup(String module, int instance, String name) {
/* 127 */       return KstatUtil.KS.kstat_lookup(KstatUtil.KC, module, instance, name);
/*     */     }
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
/*     */     public List<LibKstat.Kstat> lookupAll(String module, int instance, String name) {
/* 148 */       List<LibKstat.Kstat> kstats = new ArrayList<>();
/* 149 */       for (LibKstat.Kstat ksp = KstatUtil.KS.kstat_lookup(KstatUtil.KC, module, instance, name); ksp != null; ksp = ksp.next()) {
/* 150 */         if ((module == null || module.equals((new String(ksp.ks_module, StandardCharsets.US_ASCII)).trim())) && (instance < 0 || instance == ksp.ks_instance) && (name == null || name
/*     */           
/* 152 */           .equals((new String(ksp.ks_name, StandardCharsets.US_ASCII)).trim()))) {
/* 153 */           kstats.add(ksp);
/*     */         }
/*     */       } 
/* 156 */       return kstats;
/*     */     }
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
/*     */     public int update() {
/* 170 */       return KstatUtil.KS.kstat_chain_update(KstatUtil.KC);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() {
/* 178 */       KstatUtil.CHAIN.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static KstatChain openChain() {
/* 189 */     return new KstatChain();
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
/*     */   public static String dataLookupString(LibKstat.Kstat ksp, String name) {
/* 207 */     if (ksp.ks_type != 1 && ksp.ks_type != 4) {
/* 208 */       throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
/*     */     }
/* 210 */     Pointer p = KS.kstat_data_lookup(ksp, name);
/* 211 */     if (p == null) {
/* 212 */       LOG.error("Failed lo lookup kstat value for key {}", name);
/* 213 */       return "";
/*     */     } 
/* 215 */     LibKstat.KstatNamed data = new LibKstat.KstatNamed(p);
/* 216 */     switch (data.data_type) {
/*     */       case 0:
/* 218 */         return (new String(data.value.charc, StandardCharsets.UTF_8)).trim();
/*     */       case 1:
/* 220 */         return Integer.toString(data.value.i32);
/*     */       case 2:
/* 222 */         return FormatUtil.toUnsignedString(data.value.ui32);
/*     */       case 3:
/* 224 */         return Long.toString(data.value.i64);
/*     */       case 4:
/* 226 */         return FormatUtil.toUnsignedString(data.value.ui64);
/*     */       case 9:
/* 228 */         return data.value.str.addr.getString(0L);
/*     */     } 
/* 230 */     LOG.error("Unimplemented kstat data type {}", Byte.valueOf(data.data_type));
/* 231 */     return "";
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
/*     */   
/*     */   public static long dataLookupLong(LibKstat.Kstat ksp, String name) {
/* 251 */     if (ksp.ks_type != 1 && ksp.ks_type != 4) {
/* 252 */       throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
/*     */     }
/* 254 */     Pointer p = KS.kstat_data_lookup(ksp, name);
/* 255 */     if (p == null) {
/* 256 */       if (LOG.isErrorEnabled()) {
/* 257 */         LOG.error("Failed lo lookup kstat value on {}:{}:{} for key {}", new Object[] { (new String(ksp.ks_module, StandardCharsets.US_ASCII))
/* 258 */               .trim(), Integer.valueOf(ksp.ks_instance), (new String(ksp.ks_name, StandardCharsets.US_ASCII))
/* 259 */               .trim(), name });
/*     */       }
/* 261 */       return 0L;
/*     */     } 
/* 263 */     LibKstat.KstatNamed data = new LibKstat.KstatNamed(p);
/* 264 */     switch (data.data_type) {
/*     */       case 1:
/* 266 */         return data.value.i32;
/*     */       case 2:
/* 268 */         return FormatUtil.getUnsignedInt(data.value.ui32);
/*     */       case 3:
/* 270 */         return data.value.i64;
/*     */       case 4:
/* 272 */         return data.value.ui64;
/*     */     } 
/* 274 */     LOG.error("Unimplemented or non-numeric kstat data type {}", Byte.valueOf(data.data_type));
/* 275 */     return 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platfor\\unix\solaris\KstatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */