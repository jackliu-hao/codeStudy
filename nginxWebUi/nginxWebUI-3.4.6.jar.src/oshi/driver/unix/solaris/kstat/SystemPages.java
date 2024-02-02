/*    */ package oshi.driver.unix.solaris.kstat;
/*    */ 
/*    */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.platform.unix.solaris.KstatUtil;
/*    */ import oshi.util.tuples.Pair;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class SystemPages
/*    */ {
/*    */   public static Pair<Long, Long> queryAvailableTotal() {
/* 50 */     long memAvailable = 0L;
/* 51 */     long memTotal = 0L;
/*    */     
/* 53 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 54 */     try { LibKstat.Kstat ksp = kc.lookup(null, -1, "system_pages");
/*    */       
/* 56 */       if (ksp != null && kc.read(ksp)) {
/* 57 */         memAvailable = KstatUtil.dataLookupLong(ksp, "availrmem");
/* 58 */         memTotal = KstatUtil.dataLookupLong(ksp, "physmem");
/*    */       } 
/* 60 */       if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 61 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return new Pair(Long.valueOf(memAvailable), Long.valueOf(memTotal));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\solaris\kstat\SystemPages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */