/*     */ package oshi.util.platform.mac;
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
/*     */ public final class SysctlUtil
/*     */ {
/*  44 */   private static final Logger LOG = LoggerFactory.getLogger(SysctlUtil.class);
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
/*  61 */     IntByReference size = new IntByReference(SystemB.INT_SIZE);
/*  62 */     Memory memory = new Memory(size.getValue());
/*  63 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/*  64 */       LOG.error("Failed sysctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/*  65 */       return def;
/*     */     } 
/*  67 */     return memory.getInt(0L);
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
/*  80 */     IntByReference size = new IntByReference(SystemB.UINT64_SIZE);
/*  81 */     Memory memory = new Memory(size.getValue());
/*  82 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/*  83 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/*  84 */       return def;
/*     */     } 
/*  86 */     return memory.getLong(0L);
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
/* 100 */     IntByReference size = new IntByReference();
/* 101 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, null, size, null, 0)) {
/* 102 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 103 */       return def;
/*     */     } 
/*     */     
/* 106 */     Memory memory = new Memory(size.getValue() + 1L);
/* 107 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/* 108 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 109 */       return def;
/*     */     } 
/* 111 */     return memory.getString(0L);
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
/* 124 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, struct.getPointer(), new IntByReference(struct.size()), null, 0)) {
/* 125 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 126 */       return false;
/*     */     } 
/* 128 */     struct.read();
/* 129 */     return true;
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
/* 141 */     IntByReference size = new IntByReference();
/* 142 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, null, size, null, 0)) {
/* 143 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 144 */       return null;
/*     */     } 
/* 146 */     Memory m = new Memory(size.getValue());
/* 147 */     if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)m, size, null, 0)) {
/* 148 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 149 */       return null;
/*     */     } 
/* 151 */     return m;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\mac\SysctlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */