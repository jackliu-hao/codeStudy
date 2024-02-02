/*     */ package oshi.util.platform.unix.freebsd;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
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
/*     */ public final class BsdSysctlUtil
/*     */ {
/*  45 */   private static final Logger LOG = LoggerFactory.getLogger(BsdSysctlUtil.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String SYSCTL_FAIL = "Failed syctl call: {}, Error code: {}";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int sysctl(String name, int def) {
/*  62 */     IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
/*  63 */     Memory memory = new Memory(size.getValue());
/*  64 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/*  65 */       LOG.error("Failed sysctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/*  66 */       return def;
/*     */     } 
/*  68 */     return memory.getInt(0L);
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
/*     */   public static long sysctl(String name, long def) {
/*  81 */     IntByReference size = new IntByReference(FreeBsdLibc.UINT64_SIZE);
/*  82 */     Memory memory = new Memory(size.getValue());
/*  83 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/*  84 */       LOG.warn("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/*  85 */       return def;
/*     */     } 
/*  87 */     return memory.getLong(0L);
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
/*     */   public static String sysctl(String name, String def) {
/* 101 */     IntByReference size = new IntByReference();
/* 102 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, null, size, null, 0)) {
/* 103 */       LOG.warn("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 104 */       return def;
/*     */     } 
/*     */     
/* 107 */     Memory memory = new Memory(size.getValue() + 1L);
/* 108 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/* 109 */       LOG.warn("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 110 */       return def;
/*     */     } 
/* 112 */     return memory.getString(0L);
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
/*     */   public static boolean sysctl(String name, Structure struct) {
/* 125 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, struct.getPointer(), new IntByReference(struct.size()), null, 0)) {
/*     */       
/* 127 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 128 */       return false;
/*     */     } 
/* 130 */     struct.read();
/* 131 */     return true;
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
/*     */   public static Memory sysctl(String name) {
/* 143 */     IntByReference size = new IntByReference();
/* 144 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, null, size, null, 0)) {
/* 145 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 146 */       return null;
/*     */     } 
/* 148 */     Memory m = new Memory(size.getValue());
/* 149 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)m, size, null, 0)) {
/* 150 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 151 */       return null;
/*     */     } 
/* 153 */     return m;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platfor\\unix\freebsd\BsdSysctlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */