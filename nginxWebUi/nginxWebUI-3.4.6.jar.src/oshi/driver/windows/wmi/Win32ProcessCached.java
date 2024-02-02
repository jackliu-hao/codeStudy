/*     */ package oshi.driver.windows.wmi;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.GuardedBy;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.platform.windows.WmiUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ public final class Win32ProcessCached
/*     */ {
/*  47 */   private static final Supplier<Win32ProcessCached> INSTANCE = Memoizer.memoize(Win32ProcessCached::createInstance);
/*     */   
/*     */   @GuardedBy("commandLineCacheLock")
/*  50 */   private final Map<Integer, Pair<Long, String>> commandLineCache = new HashMap<>();
/*     */   
/*  52 */   private final ReentrantLock commandLineCacheLock = new ReentrantLock();
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
/*     */   public static Win32ProcessCached getInstance() {
/*  64 */     return INSTANCE.get();
/*     */   }
/*     */   
/*     */   private static Win32ProcessCached createInstance() {
/*  68 */     return new Win32ProcessCached();
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
/*     */   public String getCommandLine(int processId, long startTime) {
/*  98 */     this.commandLineCacheLock.lock();
/*     */     
/*     */     try {
/* 101 */       Pair<Long, String> pair = this.commandLineCache.get(Integer.valueOf(processId));
/*     */       
/* 103 */       if (pair != null && startTime < ((Long)pair.getA()).longValue())
/*     */       {
/* 105 */         return (String)pair.getB();
/*     */       }
/*     */ 
/*     */       
/* 109 */       long now = System.currentTimeMillis();
/*     */       
/* 111 */       WbemcliUtil.WmiResult<Win32Process.CommandLineProperty> commandLineAllProcs = Win32Process.queryCommandLines(null);
/*     */ 
/*     */       
/* 114 */       if (this.commandLineCache.size() > commandLineAllProcs.getResultCount() * 2) {
/* 115 */         this.commandLineCache.clear();
/*     */       }
/*     */       
/* 118 */       String result = "";
/* 119 */       for (int i = 0; i < commandLineAllProcs.getResultCount(); i++) {
/* 120 */         int pid = WmiUtil.getUint32(commandLineAllProcs, Win32Process.CommandLineProperty.PROCESSID, i);
/* 121 */         String cl = WmiUtil.getString(commandLineAllProcs, Win32Process.CommandLineProperty.COMMANDLINE, i);
/* 122 */         this.commandLineCache.put(Integer.valueOf(pid), new Pair(Long.valueOf(now), cl));
/* 123 */         if (pid == processId) {
/* 124 */           result = cl;
/*     */         }
/*     */       } 
/* 127 */       return result;
/*     */     } finally {
/*     */       
/* 130 */       this.commandLineCacheLock.unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32ProcessCached.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */