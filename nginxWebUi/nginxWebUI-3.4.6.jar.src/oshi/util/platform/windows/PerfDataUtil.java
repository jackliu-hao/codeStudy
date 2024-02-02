/*     */ package oshi.util.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.BaseTSD;
/*     */ import com.sun.jna.platform.win32.Pdh;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.FormatUtil;
/*     */ import oshi.util.ParseUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class PerfDataUtil
/*     */ {
/*  53 */   private static final Logger LOG = LoggerFactory.getLogger(PerfDataUtil.class);
/*     */   
/*  55 */   private static final BaseTSD.DWORD_PTR PZERO = new BaseTSD.DWORD_PTR(0L);
/*  56 */   private static final WinDef.DWORDByReference PDH_FMT_RAW = new WinDef.DWORDByReference(new WinDef.DWORD(16L));
/*  57 */   private static final Pdh PDH = Pdh.INSTANCE;
/*     */   
/*  59 */   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
/*     */   
/*     */   public static class PerfCounter {
/*     */     private String object;
/*     */     private String instance;
/*     */     private String counter;
/*     */     
/*     */     public PerfCounter(String objectName, String instanceName, String counterName) {
/*  67 */       this.object = objectName;
/*  68 */       this.instance = instanceName;
/*  69 */       this.counter = counterName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getObject() {
/*  76 */       return this.object;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getInstance() {
/*  83 */       return this.instance;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getCounter() {
/*  90 */       return this.counter;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getCounterPath() {
/*  99 */       StringBuilder sb = new StringBuilder();
/* 100 */       sb.append('\\').append(this.object);
/* 101 */       if (this.instance != null) {
/* 102 */         sb.append('(').append(this.instance).append(')');
/*     */       }
/* 104 */       sb.append('\\').append(this.counter);
/* 105 */       return sb.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PerfCounter createCounter(String object, String instance, String counter) {
/* 124 */     return new PerfCounter(object, instance, counter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long updateQueryTimestamp(WinNT.HANDLEByReference query) {
/* 135 */     WinDef.LONGLONGByReference pllTimeStamp = new WinDef.LONGLONGByReference();
/*     */     
/* 137 */     int ret = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) : PDH.PdhCollectQueryData(query.getValue());
/*     */     
/* 139 */     int retries = 0;
/* 140 */     while (ret == -2147481643 && retries++ < 3) {
/*     */       
/* 142 */       Util.sleep((1 << retries));
/*     */       
/* 144 */       ret = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) : PDH.PdhCollectQueryData(query.getValue());
/*     */     } 
/* 146 */     if (ret != 0) {
/* 147 */       if (LOG.isWarnEnabled()) {
/* 148 */         LOG.warn("Failed to update counter. Error code: {}", String.format(FormatUtil.formatError(ret), new Object[0]));
/*     */       }
/* 150 */       return 0L;
/*     */     } 
/*     */     
/* 153 */     return IS_VISTA_OR_GREATER ? ParseUtil.filetimeToUtcMs(pllTimeStamp.getValue().longValue(), true) : 
/* 154 */       System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean openQuery(WinNT.HANDLEByReference q) {
/* 165 */     int ret = PDH.PdhOpenQuery(null, PZERO, q);
/* 166 */     if (ret != 0) {
/* 167 */       if (LOG.isErrorEnabled()) {
/* 168 */         LOG.error("Failed to open PDH Query. Error code: {}", String.format(FormatUtil.formatError(ret), new Object[0]));
/*     */       }
/* 170 */       return false;
/*     */     } 
/* 172 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean closeQuery(WinNT.HANDLEByReference q) {
/* 183 */     return (0 == PDH.PdhCloseQuery(q.getValue()));
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
/*     */   public static long queryCounter(WinNT.HANDLEByReference counter) {
/* 195 */     Pdh.PDH_RAW_COUNTER counterValue = new Pdh.PDH_RAW_COUNTER();
/* 196 */     int ret = PDH.PdhGetRawCounterValue(counter.getValue(), PDH_FMT_RAW, counterValue);
/* 197 */     if (ret != 0) {
/* 198 */       if (LOG.isWarnEnabled()) {
/* 199 */         LOG.warn("Failed to get counter. Error code: {}", String.format(FormatUtil.formatError(ret), new Object[0]));
/*     */       }
/* 201 */       return ret;
/*     */     } 
/* 203 */     return counterValue.FirstValue;
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
/*     */   public static boolean addCounter(WinNT.HANDLEByReference query, String path, WinNT.HANDLEByReference p) {
/* 219 */     int ret = IS_VISTA_OR_GREATER ? PDH.PdhAddEnglishCounter(query.getValue(), path, PZERO, p) : PDH.PdhAddCounter(query.getValue(), path, PZERO, p);
/* 220 */     if (ret != 0) {
/* 221 */       if (LOG.isWarnEnabled()) {
/* 222 */         LOG.warn("Failed to add PDH Counter: {}, Error code: {}", path, 
/* 223 */             String.format(FormatUtil.formatError(ret), new Object[0]));
/*     */       }
/* 225 */       return false;
/*     */     } 
/* 227 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean removeCounter(WinNT.HANDLEByReference p) {
/* 238 */     return (0 == PDH.PdhRemoveCounter(p.getValue()));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\windows\PerfDataUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */