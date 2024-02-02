/*     */ package oshi.util.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.NotThreadSafe;
/*     */ import oshi.util.FormatUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NotThreadSafe
/*     */ public final class PerfCounterQueryHandler
/*     */   implements AutoCloseable
/*     */ {
/*  47 */   private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQueryHandler.class);
/*     */ 
/*     */   
/*  50 */   private Map<PerfDataUtil.PerfCounter, WinNT.HANDLEByReference> counterHandleMap = new HashMap<>();
/*     */   
/*  52 */   private WinNT.HANDLEByReference queryHandle = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addCounterToQuery(PerfDataUtil.PerfCounter counter) {
/*  63 */     if (this.queryHandle == null) {
/*  64 */       this.queryHandle = new WinNT.HANDLEByReference();
/*  65 */       if (!PerfDataUtil.openQuery(this.queryHandle)) {
/*  66 */         LOG.warn("Failed to open a query for PDH object: {}", counter.getObject());
/*  67 */         this.queryHandle = null;
/*  68 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/*  72 */     WinNT.HANDLEByReference p = new WinNT.HANDLEByReference();
/*  73 */     if (!PerfDataUtil.addCounter(this.queryHandle, counter.getCounterPath(), p)) {
/*  74 */       LOG.warn("Failed to add counter for PDH object: {}", counter.getObject());
/*  75 */       return false;
/*     */     } 
/*  77 */     this.counterHandleMap.put(counter, p);
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeCounterFromQuery(PerfDataUtil.PerfCounter counter) {
/*  89 */     boolean success = false;
/*  90 */     WinNT.HANDLEByReference href = this.counterHandleMap.remove(counter);
/*     */     
/*  92 */     if (href != null) {
/*  93 */       success = PerfDataUtil.removeCounter(href);
/*     */     }
/*  95 */     if (this.counterHandleMap.isEmpty()) {
/*  96 */       PerfDataUtil.closeQuery(this.queryHandle);
/*  97 */       this.queryHandle = null;
/*     */     } 
/*  99 */     return success;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAllCounters() {
/* 107 */     for (WinNT.HANDLEByReference href : this.counterHandleMap.values()) {
/* 108 */       PerfDataUtil.removeCounter(href);
/*     */     }
/* 110 */     this.counterHandleMap.clear();
/*     */     
/* 112 */     if (this.queryHandle != null) {
/* 113 */       PerfDataUtil.closeQuery(this.queryHandle);
/*     */     }
/* 115 */     this.queryHandle = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long updateQuery() {
/* 125 */     if (this.queryHandle == null) {
/* 126 */       LOG.warn("Query does not exist to update.");
/* 127 */       return 0L;
/*     */     } 
/* 129 */     return PerfDataUtil.updateQueryTimestamp(this.queryHandle);
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
/*     */   public long queryCounter(PerfDataUtil.PerfCounter counter) {
/* 141 */     if (!this.counterHandleMap.containsKey(counter)) {
/* 142 */       if (LOG.isWarnEnabled()) {
/* 143 */         LOG.warn("Counter {} does not exist to query.", counter.getCounterPath());
/*     */       }
/* 145 */       return 0L;
/*     */     } 
/* 147 */     long value = PerfDataUtil.queryCounter(this.counterHandleMap.get(counter));
/* 148 */     if (value < 0L) {
/* 149 */       if (LOG.isWarnEnabled()) {
/* 150 */         LOG.warn("Error querying counter {}: {}", counter.getCounterPath(), 
/* 151 */             String.format(FormatUtil.formatError((int)value), new Object[0]));
/*     */       }
/* 153 */       return 0L;
/*     */     } 
/* 155 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 160 */     removeAllCounters();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\windows\PerfCounterQueryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */